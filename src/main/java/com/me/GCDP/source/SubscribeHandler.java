package com.me.GCDP.source;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.NativeArray;

import com.me.GCDP.mapper.SourceMapper;
import com.me.GCDP.model.Source;
import com.me.GCDP.model.SourceSubscribe;
import com.me.GCDP.model.SourceSubscribe.SubscribeStatus;
import com.me.GCDP.model.SourceSubscribeInfo;
import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.oscache.OSCache;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.xform.Form2Plugin;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONException;
import com.me.json.JSONObject;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-25              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SubscribeHandler extends Thread {
	
	private static Log log = LogFactory.getLog(SubscribeHandler.class);
	
	private Integer nodeId = null;
	
	private Integer formId = null;
	
	private Map<String,Object> dataPool = null;
	
	private SourceMapper<Source> sourceMapper = null;
	
	private ScriptService scriptService = null;
	
	private OSCache oscache_sta = null;
	
	//subscribe new logic add by pankh
	private ScriptPluginFactory pf = null;
	private Form2Plugin form2 = null;
	
	//推送重试次数
	int retry_count = Integer.parseInt(CmppConfig.getKey("cmpp.subscribe.retry.count"));
	
	//最大连续失败次数
	int error_count = Integer.parseInt(CmppConfig.getKey("cmpp.subscribe.error.count"));
	
	//subscribe new logic add by pankh
	public SubscribeHandler(){
		pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		form2 = (Form2Plugin) pf.getP("form2");
	}
	@Override
	public void run() {
		Source source = new Source();
		source.setFormId(formId);
		source.setNodeid(nodeId);

		try{
			List<Source> slist = sourceMapper.get(source);
			for(int i = 0 ; i < slist.size() ; i ++){
				processSource(slist.get(i));
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 处理数据源
	 * @param source
	 */
	public void processSource(Source source){
		try{
			SourceSubscribe subscribe = new SourceSubscribe();
			subscribe.setSourceId(source.getId());
			List<SourceSubscribe> subList = sourceMapper.getSubscribeList(subscribe);
			if(subList != null && subList.size() > 0){
				for(SourceSubscribe sub : subList){
					try{
						if(sub.getStatus().equals(SubscribeStatus.NORMAL) 
								|| sub.getStatus().equals(SubscribeStatus.SCRIPT_ERROR)
								|| sub.getStatus().equals(SubscribeStatus.PUSH_ERROR)){
							processSubscribe(sub, false);
						}
					}catch(Exception e){
						log.error("processSource : " + e.getMessage());
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	/**
	 * 处理定阅
	 * @param subscribe
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processSubscribe(SourceSubscribe subscribe, boolean isReplay) throws JSONException, SQLException{
		
		try {
			scriptService.run(nodeId, dataPool, 30, ScriptType.subscribe,
					subscribe.getSourceId()+"", subscribe.getId()+"");
		} catch (Exception e) {
			//脚本异常处理
			Integer lastId = null;
			try{
				lastId = (Integer)dataPool.get("id");
			}catch(Exception e2){
				e2.printStackTrace();
				log.error(e2.getMessage());
			}
			log.error("processSubscribe : " + e.getMessage() + "|" + subscribe.getSourceId() + "|" + subscribe.getId() + "|" + lastId);
			processResult(isReplay, subscribe, 1, lastId, e.getMessage());
			return;
		}
		
		Boolean ret = (Boolean)dataPool.get("subscribe_ret");
		if(ret){
			//推送
			String url = subscribe.getCallBackUrl();
			String method = subscribe.getMethod();
			String params = null;
			Map paramMap = new HashMap();
			
			//Map<String, Object> data = (Map<String, Object>)dataPool.get("data");
			
			//subscribe new logic add by pankh
			Map<String, Object> data = form2.getData(nodeId, formId, (Integer)dataPool.get("id"));
			
			//获取要推送的参数列表
			NativeArray na = (NativeArray)dataPool.get("subscribe_plist");
			long size = na.getLength();
			for(int i = 0 ; i < size ; i ++){
				String param = (String)na.get(i, null);
//				if(param.equalsIgnoreCase("id")){
//					data.put("id", (Integer)dataPool.get("id"));
//				}			
				if(data.get(param) != null){
					String value = data.get(param)==null?"":data.get(param).toString();

					paramMap.put(param, value);
					try{
						if(params == null){
							params = param + "=" + URLEncoder.encode(value, "utf-8");
						}else{
							params += "&" + param + "=" + URLEncoder.encode(value, "utf-8");
						}
					}catch(Exception e){
						log.error(e.getMessage());
					}
				}
			}
			String info = null;
			Boolean flag = false;
			for(int count = 0 ; count < retry_count ; count ++){
				try{
					//动态前端参数调整
					if(subscribe.getCallBackUrl().startsWith("dyn://")){
						url = subscribe.getCallBackUrl().replaceFirst("dyn:", "http:");
//						paramMap.put("id", dataPool.get("id"));
						String record= (new JSONObject(paramMap)).toString();
						paramMap.clear();
						paramMap.put("record", record);
					}
					
					Map<String, Object> retMap = null;
					if(method.equalsIgnoreCase("get")){
						retMap = HttpUtil.get(url, params);
					}else{
						retMap = HttpUtil.post(url, paramMap);
					}
					String result = retMap.get("content")!=null?retMap.get("content").toString():null;
					Integer status_code = retMap.get("status_code")!=null?(Integer)retMap.get("status_code"):null;
					if(status_code < 400){
						//推送成功
						flag = true;
					}else{
						//推送失败
						flag = false;
					}
					log.info("processSubscribe1 : " + method + "|" + count + "|" + url + "|" + status_code + "|" + result);
					log.debug("processSubscribe : " + method + "|" + count + "|" + url + "|"+ status_code + "|" + params + "|" + result);
					break;
				}catch(Exception e){
					//推送出异常
					flag = false;
					log.error("processSubscribe : " + e.getMessage() + "|" + method + "|" + count + "|" + url + "|" + params);
					info = e.getMessage();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				}
			}
			
			Integer lastId = null;
			try{
				lastId = (Integer)dataPool.get("id");
			}catch(Exception e2){
				e2.printStackTrace();
				log.error(e2.getMessage());
			}
			log.info("processSubscribe : " + flag + "|" + subscribe.getSourceId() + "|" + subscribe.getId() + "|" + lastId + "|" + info );
			if(flag){
				//推送成功
				processResult(isReplay, subscribe, 0, lastId, null);
			}else{
				//推送失败
				processResult(isReplay, subscribe, 2, lastId, info);
			}
		}else{
			//不推送
			return;
		}
	}
	
	/**
	 * 处理推送结果
	 * @param isReplay 回放标识
	 * @param subscribe 定阅对象
	 * @param result 0-成功 1-脚本失败 2-推送失败
	 * @param lastId 已经处理的ID
	 * @param errInfo 错误信息
	 */
	private void processResult(boolean isReplay, SourceSubscribe subscribe, Integer result, Integer lastId, String errInfo ){
		
		String oscache_key = null;
		if(isReplay){
			oscache_key = "subscribe_replay_" + subscribe.getSourceId() + "_" + subscribe.getId();
		}else{
			oscache_key = "subscribe_" + subscribe.getSourceId() + "_" + subscribe.getId();
		}
		
		Object obj = oscache_sta.get(oscache_key);
		SourceSubscribeInfo info = null;
		if(obj == null){
			info = new SourceSubscribeInfo();
			oscache_sta.put(oscache_key, info);
		}else{
			info = (SourceSubscribeInfo)obj;
		}
		
		info.getTotalCount().incrementAndGet();
		info.setLastId(lastId);
		if(errInfo != null && !errInfo.equals("")){
			info.setLastErrInfo(errInfo);
		}
		
		SubscribeStatus initStatus = subscribe.getStatus();
		
		if(result == 0){
			//成功
			info.getLastPushErrCount().set(0);
			info.getLastScriptErrCount().set(0);
			if(!isReplay){
				subscribe.setStatus(SubscribeStatus.NORMAL);
			}
		}else if(result == 1){
			//脚本失败
			info.getScriptErrCount().incrementAndGet();
			info.getLastScriptErrCount().incrementAndGet();
			if(!isReplay){
				if(info.getLastScriptErrCount().get() >= error_count){
					subscribe.setStatus(SubscribeStatus.ERROR_STOP);
				}else{
					subscribe.setStatus(SubscribeStatus.SCRIPT_ERROR);
				}
			}
		}else if(result == 2){
			//推送失败
			info.getPushErrCount().incrementAndGet();
			info.getLastPushErrCount().incrementAndGet();
			subscribe.setStatus(SubscribeStatus.PUSH_ERROR);
			if(!isReplay){
				if(info.getLastPushErrCount().get() >= error_count){
					subscribe.setStatus(SubscribeStatus.ERROR_STOP);
				}else{
					subscribe.setStatus(SubscribeStatus.PUSH_ERROR);
				}
			}
		}
		
		if(!initStatus.equals(subscribe.getStatus())){
			sourceMapper.updateSubscribe(subscribe);
		}
	} 

	/*
	 * getter and setter 
	 */
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	
	public void setSourceMapper(SourceMapper<Source> sourceMapper) {
		this.sourceMapper = sourceMapper;
	}

	public void setDataPool(Map<String, Object> dataPool) {
		this.dataPool = dataPool;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setOscache_sta(OSCache oscache_sta) {
		this.oscache_sta = oscache_sta;
	}
	
}
