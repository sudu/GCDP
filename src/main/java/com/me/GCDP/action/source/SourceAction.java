package com.me.GCDP.action.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.DynamicConfigMapper;
import com.me.GCDP.mapper.FormConfigMapper;
import com.me.GCDP.mapper.SourceMapper;
import com.me.GCDP.model.DynamicConfig;
import com.me.GCDP.model.FormConfig;
import com.me.GCDP.model.Source;
import com.me.GCDP.model.SourceSubscribe;
import com.me.GCDP.model.SourceSubscribe.SubscribeStatus;
import com.me.GCDP.model.SourceSubscribeInfo;
import com.me.GCDP.model.TableField;
import com.me.GCDP.source.SubscribeDataReplay;
import com.me.GCDP.util.oscache.OSCache;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-18              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SourceAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(SourceAction.class);
	
	private OSCache oscache_sta = null;
	
	private String msg = null;
	private Boolean hasError = false;

	private List<FormConfig> formList = null;
	private List<Source> sourceList = null;
	private List<TableField> fieldList = null;
	private List<SourceSubscribe> subscribeList = null;
	
	private FormConfigMapper<FormConfig> formConfigMapper = null;
	private SourceMapper<Source> sourceMapper = null;
	
	private ScriptService scriptService = null;
	private FormService formService = null;
	
	private FormConfig form = new FormConfig();
	private Source source = new Source();
	private SourceSubscribe subscribe = new SourceSubscribe();
	private SourceSubscribeInfo info = null;
	
	private Integer nodeId = null;
	
	//用于数据回放
	private Integer startId = null;
	private Integer endId = null;
	
	//动态前端
	private DynamicConfigMapper<DynamicConfig> dynamicConfigMapper = null;
	public void setDynamicConfigMapper(
			DynamicConfigMapper<DynamicConfig> dynamicConfigMapper) {
		this.dynamicConfigMapper = dynamicConfigMapper;
	}

	/**
	 * 数据源列表
	 */
	@Override
	public String execute() throws Exception {
		Source source = new Source();
		source.setNodeid(nodeId);
		sourceList = sourceMapper.get(source);
		return "list";
	}
	
	/**
	 * 发布数据源页面
	 * @return
	 */
	public String add(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		FormConfig form = new FormConfig();
		form.setNodeid(nodeId);
		formList = formConfigMapper.get(form);
		return "add";
	}
	
	/**
	 * 
	 * @return
	 */
	public String update(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(source.getName() == null){
			msg = "name为空";
			hasError = true;
			return "msg";
		}
		try{
			source.setNodeid(nodeId);
			sourceMapper.update(source);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 添加新数据源
	 * @return
	 */
	public String insert(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		if(source.getName() == null){
			msg = "name为空";
			hasError = true;
			return "msg";
		}
		
		try{
			source.setNodeid(nodeId);
			sourceMapper.insert(source);
			msg = source.getId()+"";
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 删除源
	 * @return
	 */
	public String delete(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		
		try{
			source.setNodeid(nodeId);
			SourceSubscribe subscribe = new SourceSubscribe();
			subscribe.setSourceId(source.getId());
			sourceMapper.deleteSubscribe(subscribe);
			sourceMapper.delete(source);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 查看数据源详情
	 * @param formConfigMapper
	 */
	public String viewSource(){
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		List<Source> slist = sourceMapper.get(source);
		source = (slist != null && slist.size()>0) ? slist.get(0) : null;
		return "viewSource";
	}
	
	/**
	 * 字段列表
	 * @return
	 */
	public String fieldList(){
		if(form.getId() == null){
			msg = "form id is null";
			hasError = true;
			return "msg";
		}
		List<FormConfig> flist = formConfigMapper.get(form);
//		form = (flist != null && flist.size()>0) ? flist.get(0) : null;
		FormConfig fconf = null;
		JSONObject confJSON = null;
		JSONArray fieldConfList = null;
		TableField tableConf = null;
		if(form != null){
			fconf = flist.get(0);
			fieldList = new ArrayList<TableField>();
			try {
				fieldConfList = ((JSONObject)(new JSONObject(fconf.getConfig()).get("fieldsConfig"))).getJSONArray("fieldsConfig");
				if(fieldConfList != null){
					for(Object field:fieldConfList.toList()){
						confJSON = (JSONObject)field;
						tableConf = new TableField();
						
						tableConf.setField(confJSON.getString("f_name"));
						tableConf.setType(confJSON.getString("f_type")+"("+confJSON.getString("f_length")+")");
						tableConf.setDesc(confJSON.getString("f_title"));
						
						fieldList.add(tableConf);
					}
				}
			} catch (JSONException e) {
				msg = e.getMessage();
				hasError = true;
				return "msg";
			}
/*			fieldList = formConfigMapper.descTable(form.getTableName());
			
			//获取表单配置信息中的字段名称
			if(fieldList != null && fieldList.size() > 0){
				String form_config = form.getConfig();
				if(form_config != null && !form_config.equals("")){
					try {
						com.me.GCDP.xform.FormConfig formConfig = new com.me.GCDP.xform.FormConfig(form_config);
						for(int i = 0 ; i < fieldList.size() ; i ++){
							TableField field = fieldList.get(i);
							JSONObject fieldJSON = formConfig.getField(field.getField());
							if(fieldJSON != null){
								field.setDesc((String)fieldJSON.get("f_title"));
							}
						}
						
					} catch (JSONException e) {
						log.error(e.getMessage());
					}
				}
			}*/
			
			return "fieldList";
		}else{
			msg = "error";
			return "message";
		}
	}
	
	/**
	 * 订阅源页面
	 * @return
	 */
	public String subscribeList(){
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		SourceSubscribe subscribe = new SourceSubscribe();
		subscribe.setSourceId(source.getId());
		try{
			subscribeList = sourceMapper.getSubscribeList(subscribe);
			if(subscribeList != null && subscribeList.size() > 0){
				for(int i = 0 ; i < subscribeList.size() ; i++){
					subscribe = subscribeList.get(i);
					Object obj = oscache_sta.get("subscribe_" + subscribe.getSourceId() + "_" + subscribe.getId());
					if(obj != null){
						subscribe.setInfo((SourceSubscribeInfo)obj);
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "subscribeList";
	}
	
	/**
	 * 订阅源
	 * @return
	 */
	public String subscribe(){
		if(nodeId == null){
			msg = "nodeId is null";
			hasError = true;
			return "msg";
		}
		if(subscribe.getSourceId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
//		if(subscribe.getCallBackUrl() == null){
//			msg = "CallBackUrl is null";
//			hasError = true;
//			return "msg";
//		}
		
		String[] subUrl = null;
		String[] names = null;
		SubscribeStatus status;
		//动态前端
		if(subscribe.getDynIds() != null && !subscribe.getDynIds().equals("")){			
			subUrl = dynUrlRewrite(subscribe);
			names = subscribe.getName().split(",");
			status = SubscribeStatus.NORMAL;
			subscribe.setMethod("post");
			
		}else{
			subUrl = new String[]{subscribe.getCallBackUrl()};
			names = new String[]{subscribe.getName()};
			status = SubscribeStatus.CHECKING;
		}
		for(int i = 0; i<subUrl.length; i++){
			subscribe.setId(null);
			subscribe.setCallBackUrl(subUrl[i]);
			subscribe.setName(names[i]);
			subscribe.setSubscribeDate(System.currentTimeMillis()/1000);
			subscribe.setStatus(status);
			try{
				sourceMapper.insertSubscribe(subscribe);
			}catch(Exception e){
				log.error(e.getMessage());
				msg = e.getMessage();
				hasError = true;
				return "msg";
			}
			
			String script = subscribe.getScript();
			if(StringUtils.isNotBlank(script)){
				try {
					scriptService.saveDebug(nodeId, script, ScriptType.subscribe, subscribe.getSourceId()+"", subscribe.getId()+"");
				} catch (Exception e) {
					log.error(e.getMessage());
					msg = "保存脚本失败: "+e.getMessage();
					hasError = true;
				}
			}
		}
		
		return "msg";
	}
	
	/**
	 * 更新订阅信息
	 * @return
	 */
	public String updateSubscribe(){
		if(nodeId == null){
			msg = "nodeId is null";
			hasError = true;
			return "msg";
		}
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		try{
			sourceMapper.updateSubscribe(subscribe);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		
		String script = subscribe.getScript();
		if(StringUtils.isNotBlank(script)){
			try {
				scriptService.saveDebug(nodeId, script, ScriptType.subscribe, subscribe.getSourceId()+"", subscribe.getId()+"");
			} catch (Exception e) {
				log.error(e.getMessage());
				msg = "保存脚本失败: "+e.getMessage();
				hasError = true;
			}
		}
		return "msg";
	}
	
	/**
	 * 修改定阅状态
	 * @return
	 */
	public String updateSubscribeStatus(){
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		try{
			sourceMapper.updateSubscribeStatus(subscribe);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "msg";
	}
	
	/**
	 * 删除定阅信息
	 * @return
	 */
	public String deleteSubscribe(){
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		try{
			sourceMapper.deleteSubscribe(subscribe);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "msg";
	}
	
	/**
	 * 查看订阅详细信息
	 * @return
	 */
	public String viewSubscribe(){
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		try{
			List<SourceSubscribe> slist = sourceMapper.getSubscribeList(subscribe);
			subscribe = (slist != null && slist.size()>0) ? slist.get(0) : null;
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		
		if(subscribe != null){
			try {
				String script = scriptService.openLatest(nodeId, ScriptType.subscribe, subscribe.getSourceId()+"", subscribe.getId()+"");
				subscribe.setScript(script);
			} catch (IOException e) {
				log.error(e.getMessage());
				msg = "读取脚本失败: "+e.getMessage();
				hasError = true;
			}
		}
		return "viewSubscribe";
	}
	
	public String replayStatus(){
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		String oscache_key = "subscribe_replay_" + source.getId() + "_" + subscribe.getId();
		info = (SourceSubscribeInfo)oscache_sta.get(oscache_key);
		return "replayStatus";
	}
	
	/**
	 * 数据回放
	 * @return
	 */
	public String dataReplay(){
		if(source.getId() == null){
			msg = "source id is null";
			hasError = true;
			return "msg";
		}
		if(subscribe.getId() == null){
			msg = "subscribe id is null";
			hasError = true;
			return "msg";
		}
		
		List<Source> slist = sourceMapper.get(source);
		List<SourceSubscribe> sublist = sourceMapper.getSubscribeList(subscribe);
		source = (slist != null && slist.size()>0) ? slist.get(0) : null;
		subscribe = (sublist != null && sublist.size()>0) ? sublist.get(0) : null;
		if(source == null){
			msg = "source not found";
			hasError = true;
			return "msg";
		}
		if(startId == null){
			startId = 0;
		}
		if(endId == null){
			try {
				com.me.GCDP.xform.FormConfig fc = com.me.GCDP.xform.FormConfig.getInstance(0, source.getFormId());
				endId = formService.getFormLastId(fc);
			} catch (Exception e) {
				log.error(e);
			}
		}
		
		SubscribeDataReplay replay = new SubscribeDataReplay();
		replay.setSource(source);
		replay.setSubscribe(subscribe);
		replay.setOscache_sta(oscache_sta);
		replay.setSourceMapper(sourceMapper);
		replay.setNodeId(nodeId);
		replay.setScriptService(scriptService);
		replay.setFormService(formService);
		replay.setStartId(startId);
		replay.setEndId(endId);
		replay.start();
		
		msg = "回放开始";
		hasError = false;
		return "msg";
	}
	
	private String[] dynUrlRewrite(SourceSubscribe sub){
		
		String[] id = sub.getDynIds().split(",");
		String[] url = new String[id.length];
		
		for(int i = 0; i < id.length; i++){
			source.setId(sub.getSourceId());
			sourceList = sourceMapper.get(source);
			form.setId(sourceList.get(0).getFormId());
			formList = formConfigMapper.get(form);
			String tName = formList.get(0).getTableName();
			
//			String[] dynId = sub.getCallBackUrl().split(":");
			DynamicConfig dc = new DynamicConfig();
			dc.setId(Integer.parseInt(id[i]));
			dc = dynamicConfigMapper.get(dc).get(0);
			
			String svrPath = dc.getSvrPath().startsWith("/")?dc.getSvrPath():"/" +dc.getSvrPath();
			
			url[i] = "dyn://"+dc.getSvrIp()+ svrPath +"/dynamic/insert.do?tName="+tName;
//			sub.setCallBackUrl(url);
//			sub.setStatus(SourceSubscribe.SubscribeStatus.NORMAL);
		}

		return url;
			
	}
	
	/*
	 * getter and setter
	 */
	public void setFormConfigMapper(FormConfigMapper<FormConfig> formConfigMapper) {
		this.formConfigMapper = formConfigMapper;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setOscache_sta(OSCache oscache_sta) {
		this.oscache_sta = oscache_sta;
	}

	public List<FormConfig> getFormList() {
		return formList;
	}

	public void setSourceMapper(SourceMapper<Source> sourceMapper) {
		this.sourceMapper = sourceMapper;
	}

	public List<Source> getSourceList() {
		return sourceList;
	}
	
	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public List<TableField> getFieldList() {
		return fieldList;
	}

	public FormConfig getForm() {
		return form;
	}

	public void setForm(FormConfig form) {
		this.form = form;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public List<SourceSubscribe> getSubscribeList() {
		return subscribeList;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public SourceSubscribe getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(SourceSubscribe subscribe) {
		this.subscribe = subscribe;
	}

	public Integer getStartId() {
		return startId;
	}

	public void setStartId(Integer startId) {
		this.startId = startId;
	}

	public Integer getEndId() {
		return endId;
	}

	public void setEndId(Integer endId) {
		this.endId = endId;
	}

	public SourceSubscribeInfo getInfo() {
		return info;
	}

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
}
