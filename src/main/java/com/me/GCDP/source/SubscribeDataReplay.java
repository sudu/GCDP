package com.me.GCDP.source;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.SourceMapper;
import com.me.GCDP.model.Source;
import com.me.GCDP.model.SourceSubscribe;
import com.me.GCDP.util.oscache.OSCache;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.script.ScriptService;
import com.me.json.JSONException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-8-23              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SubscribeDataReplay extends Thread {
	
	private static Log log = LogFactory.getLog(SubscribeDataReplay.class);
	
	private SourceMapper<Source> sourceMapper = null;
	
	private ScriptService scriptService = null;
	private FormService formService = null;
	
	private OSCache oscache_sta = null;
	private Integer nodeId = null;
	private Source source = null;
	private SourceSubscribe subscribe = null;
	private Integer startId = null;
	private Integer endId = null;

	@Override
	public void run() {
		Integer formId =  source.getFormId();
		FormConfig fc;
		try {
			fc = FormConfig.getInstance(nodeId, formId);
		} catch (JSONException e1) {
			log.error(e1);
			return;
		} catch (SQLException e1) {
			log.error(e1);
			return;
		}
		log.info("start Data-replay : NodeId : " + nodeId + " | startId : " + startId + " | endId : " + endId + " | forId : " +formId);
		Integer id = 0;
		while(true){
			if(id < endId){
				if(id < startId){
					id = startId;
				}else{
					id ++;
				}
			}else{
				break;
			}
			List<Integer> idList = formService.getFormIdList(fc, id, 100);
			for(int i = 0 ; i < idList.size() ; i++){
				try{
					id = idList.get(i);
					if(id <= endId){
						log.info("Replay : formId-" + formId + "|recordId-" + id);
						Map<String, Object> dataMap = formService.getData(fc, id);
						Map<String, Object> dataPool = new HashMap<String, Object>();
						dataPool.put("data", dataMap);
						dataPool.put("id", dataMap.get("id"));
						
						SubscribeHandler handler = new SubscribeHandler();
						handler.setNodeId(nodeId);
						handler.setFormId(formId);
						handler.setDataPool(dataPool);
						handler.setSourceMapper(sourceMapper);
						handler.setScriptService(scriptService);
						handler.setOscache_sta(oscache_sta);
						handler.processSubscribe(subscribe, true);
					}
					Thread.sleep(10);
				}catch(Exception e){
					log.error("Replay : formId-" + formId + "|recordId-" + id + "|" + e.getMessage());
				}
			}
		}
	}
	
	/*
	 * getter and setter
	 */
	public void setSourceMapper(SourceMapper<Source> sourceMapper) {
		this.sourceMapper = sourceMapper;
	}

	public void setOscache_sta(OSCache oscache_sta) {
		this.oscache_sta = oscache_sta;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void setSubscribe(SourceSubscribe subscribe) {
		this.subscribe = subscribe;
	}

	public void setStartId(Integer startId) {
		this.startId = startId;
	}

	public void setEndId(Integer endId) {
		this.endId = endId;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
}