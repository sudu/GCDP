package com.me.GCDP.action.xform;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.action.xform.XformAction;
import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Permission;
import com.me.GCDP.model.Version;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.version.VersionHelper2;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormPlugin;
import com.me.GCDP.xform.FormSaveException;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.ViewConfig;
import com.me.GCDP.xform.xcontrol.BaseControl;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: xform运行时Action</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :chengds
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-8-2              chengds               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class XformAction extends ActionSupport {
	
	private static Log log = LogFactory.getLog(XformAction.class);
	
	private static final long serialVersionUID = 1L;
	
	private SecurityManager securityManager = null;
	
	private Map<String, String[]> form;
	//节点ID
	private Integer nodeId = 0;
	private Integer formId = 0;
	private Integer id = 0;
	private int viewId;
	private FormConfig fc = null;

	private String msg = "";
	private String errorStep = "";
	private boolean hasError = false;
	
	private VersionHelper2 versionHelper2;
	private String customedTpl="";
	private String templateName="";
	private String dataSource="";
	private String bodyInject="";
	private String headInject="";
	private String ext="";
	ViewConfig viewConfig=null;
	@SuppressWarnings("rawtypes")
	private VersionMapper versionMapper;
	
	
	/**
	 * 获取表单试图配置
	 * 运行时需要
	 * @return
	 */
	public String viewConfig(){
		return "viewConfig";	
	}
	
	/**
	 * 获取表单记录的数据
	 * 运行时需要
	 * @return
	 */
	public String viewData(){
		return "viewData";	
	}	
	
//	/**
//	 * 将表单的数据根据配置保存到数据库或者nosql
//	 * 
//	 * @return
//	 */
//	public String saveData() {
//		String rtn = "saveData";
//		try {
//			ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
//			this.formSvr = (FormPlugin) pf.getP("form");
//			formSvr.init(nodeId, formId, id, form);
//			FormHelper helper = formSvr.getFormHelper();
//
//			String userId = AuthorzationUtil.getUserId();
//			Map<String, Object> xform = helper.getPostData(viewId);
//			String powerPath = helper.getFc().getPowerPath();
//			boolean check = true;
//			if (id == 0) {
//				check = securityManager.checkPermission(powerPath, Permission.ADD, xform, formId, id);
//			} else {
//				check = securityManager.checkPermission(powerPath, Permission.MODIFY, xform, formId, id);
//			}
//			if (!check) {
//				String stra = (id == 0 ? "添加" : "修改");
//				msg = "你没有" + stra + "数据的权限";
//				log.info(userId + ":" + stra + "formId=" + formId + ",id=" + id + " 的数据由于权限验证没通过被阻止.");
//				hasError = true;
//				return rtn;
//			}
//			Map<String, Object> dataPool = helper.save(xform, viewId);
//			id = (Integer) dataPool.get("id");
//			boolean verify = (Boolean) dataPool.get("verify");
//			if (!verify) {
//				hasError = true;
//				msg = (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : "");
//				return rtn;
//			}
//			// 保存历史版本
//			setVersionData(AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]", id, xform);
//			ext = (dataPool.containsKey("data") ? dataPool.get("data").toString() : "");
//			msg = (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : "");
//		} catch (Exception e) {
//			Error(e, "保存数据出错");
//
//		}
//		return rtn;
//	}
	
	/**
	 * 将表单的数据根据配置保存到数据库或者nosql
	 * 
	 * @return
	 */
	public String saveData() {
		String rtn = "saveData";
		try {
			String userId = AuthorzationUtil.getUserId();
			FormConfig fc = FormConfig.getInstance(nodeId, formId);
			Map<String, Object> xform;
			String powerPath = fc.getPowerPath();
			/*
			 * 如果权限路径为空,则意味着所有人都有权限,不需要再进行权限认证
			 * add by chengds at 2013/8/20
			 */
			if(StringUtils.isNotEmpty(powerPath)){	
				xform = FormService.getPostDataAndRecordData(fc, form);
				boolean check = true;
				if (id == 0) {
					check = securityManager.checkPermission(powerPath, Permission.ADD, xform, formId, id);
				} else {
					check = securityManager.checkPermission(powerPath, Permission.MODIFY, xform, formId, id);
				}
				if (!check) {
					String stra = (id == 0 ? "添加" : "修改");
					msg = "你没有" + stra + "数据的权限";
					log.info(userId + ":" + stra + "formId=" + formId + ",id=" + id + " 的数据由于权限验证没通过被阻止.");
					hasError = true;
					return rtn;
				}
			}
			xform = FormService.getPostData(fc, form);
			FormService formService = (FormService) SpringContextUtil.getBean("formService");
			Map<String, Object> injection = new java.util.HashMap<String, Object>();
			injection.put("viewId", viewId);
			// TODO 日后移除form yangbo
			injection.put("form", form);
			/*兼容 保存前后dataPool注入helper yangbo
			 * 2013.2.20修复helper没有初始化导致异常的BUG*/
			ScriptPluginFactory pf = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
			FormPlugin helper = (FormPlugin) pf.getP("form");
			helper.init(nodeId, formId, id);
			injection.put("helper", helper);
			
			Map<String, Object> dataPool = formService.save(fc, id, xform, injection);
			id = (Integer) dataPool.get("dataId");
			// 保存历史版本
			setVersionData(AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]", id, xform);
						
			boolean verify = (Boolean) dataPool.get("verify");
			if (!verify) {
				hasError = true;
				msg = (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : "");
				return rtn;
			}
			
			ext = (dataPool.containsKey("data") ? dataPool.get("data").toString() : "");
			msg = (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : "");
		} catch (FormSaveException e) {
			hasError = true;
			errorStep = e.getErrorStep();
			msg = e.toString();
			if (log.isDebugEnabled()) {
				log.debug(msg, e);
			} else {
				log.error(msg);
			}
		} catch (Exception e) {
			Error(e, "保存数据出错");
		}
		return rtn;
	}
	
	private void setVersionData(String userName, int rid, Map<String, Object> xform) throws Exception {
		String key = nodeId + "_" + formId + "_" + viewId +"_" + rid;
		JSONObject xformDataJson = new JSONObject(xform);
		versionHelper2 = new VersionHelper2(key,userName,xformDataJson.toString());
		versionHelper2.setVersionMapper(getVersionMapper());
		versionHelper2.save();
	}

	/**
	 * 渲染运行时模板
	 * @return
	 */
	public String render(){
		try {
			msg="渲染出错";

			/*
			 * 如果权限路径为空,则意味着所有人都有权限,不需要再进行权限认证
			 * add by chengds at 2013/8/20
			 */
			String powerPath = getFc().getPowerPath();
			if(StringUtils.isNotEmpty(powerPath)){	
				Map<String,Object> xform = FormService.getPostDataAndRecordData(getFc(), form);
				boolean check=true;
				check = securityManager.checkPermission(powerPath, Permission.CHECK, xform, formId, id);
				if(!check){
					msg = "你没有查看数据的权限";
					return "renderError";
				}
			}
			JSONObject tplJson= FormService.getTemplateJson(viewId);
			if(tplJson==null){
				return "renderError";
			}else{
				viewConfig = ViewConfig.getInstance(viewId);
				
				String cusTpl = tplJson.getString("customedTpl");
				if(cusTpl.equals("")){	
					this.templateName = tplJson.getString("defaultTpl");
					return "render";	
				}else{

					Map<String, Object> renderMap = new HashMap<String, Object>();
					renderMap.put("id",this.id);
					renderMap.put("formId",this.formId);
					renderMap.put("viewId",this.viewId);
					renderMap.put("nodeId",this.nodeId);
					renderMap.put("config",this.getConfig());
					renderMap.put("recordData",this.getRecordData());
					renderMap.put("dataSource",this.getDataSource());
					renderMap.put("relyJSList",this.getRelyJSList());
					renderMap.put("relyCSSList",this.getRelyCSSList());
					renderMap.put("headInject",this.getHeadInject());
					renderMap.put("bodyInject",this.getBodyInject());
					this.customedTpl  = FreeMarkerHelper.process2(cusTpl, renderMap);
					return "renderTpl";
				}
			}
		} catch (Exception e) {
			Error(e, "渲染运行时模板出错");
		}
		return "renderError";	
	}

	/**
	 * 获取某记录的数据
	 * @return
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	public String getRecordData() throws JSONException, SQLException {
		if (id == 0) {
			return null;
		}
		FormService formService = (FormService) SpringContextUtil.getBean("formService");
		return new JSONObject(formService.getData(getFc(), id)).toString();
	}
	
	public String getDataSource() {
		this.dataSource=parseDataSource();
		return dataSource;
	}
	/**
	 * 
	 * @return
	 */
	public String parseDataSource() {
		String ControlId="";
		try {
			ViewConfig vc = viewConfig;
			Vector<BaseControl> ctrs = vc.getControl();
			JSONObject config = new JSONObject();
			HttpServletRequest request=ServletActionContext.getRequest();
			String requestUrl = request.getScheme() + "://" + request.getHeader("Host") +request.getRequestURI().replaceFirst(request.getServletPath(), "");
			for (int i = 0; i < ctrs.size(); i++) {
				String[][] render = ctrs.get(i).renderDataSource(requestUrl);
				ControlId = ctrs.get(i).getControlId();
				if (render != null) {
					config.put(ControlId, render);
				}
			}
			return config.toString();
		} catch (Exception e) {
			Error(e, "获取控件数据失败"+ControlId);
			return "{}";
		}
	}	
	public String getConfig()
	{
		try {
			JSONObject config = viewConfig!=null?viewConfig.render():new JSONObject(ViewConfig.getView(viewId));
			//JSONObject config = new JSONObject(ViewConfig.getView(viewId));
			JSONObject tplJson = config.getJSONObject("form").getJSONObject("template");
			if(tplJson.has("headInject")){
				this.headInject = tplJson.getString("headInject");
			}
			if(tplJson.has("bodyInject")){
				this.bodyInject = tplJson.getString("bodyInject");
			}
			if(config.has("relyJS")){
				JSONArray relyJS = config.getJSONArray("relyJS");
				Vector<String> relyJSArr = new Vector<String>();
				for(int i=0;i<relyJS.length();i++){
					relyJSArr.add(relyJS.getString(i));
				}
				config.remove("relyJS");
			}
			if(config.has("relyCSS")){
				JSONArray relyCSS = config.getJSONArray("relyCSS");
				Vector<String> relyCSSArr = new Vector<String>();
				for(int i=0;i<relyCSS.length();i++){
					relyCSSArr.add(relyCSS.getString(i));
				}
				config.remove("relyCSS");
			}
			config.getJSONObject("form").remove("template");
			return config.toString();
		} catch (Exception e) {
			Error(e,"获取控件数据失败");
			return "{}";
		} 		
	}
	
	/**
	 * 异常处理私有方法
	 * @param e
	 * @param msg
	 */
	private void Error(Exception e, String msg) {
		e.printStackTrace();
		this.msg = msg + e.getMessage();
		log.error(msg + "formId=" + formId + ",id=" + id + e);
		this.hasError = true;
	}
	
	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public int getFormId() {
		return formId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() throws UnsupportedEncodingException {
		return msg;
	}
	public String getMessage()
	{
		return msg;
	}
	
	public String getErrorStep()
	{
		return errorStep;
	}
	
	public boolean getHasError() {
		return hasError;
	}

	@SuppressWarnings("unchecked")
	public void setFormId(int formId) {
		this.formId = formId;
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(ServletActionContext.HTTP_REQUEST);
		this.form = request.getParameterMap();
		
	}	
	public Integer getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getCustomedTpl(){
		return customedTpl;
	}
	public String getTemplateName(){
		return templateName;
	}

	@SuppressWarnings("unchecked")
	public VersionMapper<Version> getVersionMapper() {
		return versionMapper;
	}
	public void setVersionMapper(VersionMapper<Version> versionMapper) {
		this.versionMapper = versionMapper;
	}

	public String getHeadInject() throws JSONException
	{
		return headInject;
	}
	public String getBodyInject() throws JSONException
	{
		return bodyInject;
	}

	public String getExt() {
		return ext;
	}
	
	public Vector<String> getRelyJSList(){
		try {
				
			JSONObject config = new JSONObject(ViewConfig.getView(viewId));
			if(config.has("relyJS")){
				Vector<String> relyJSArr = new Vector<String>();
				JSONArray relyJS = config.getJSONArray("relyJS");
				for(int i=0;i<relyJS.length();i++){
					relyJSArr.add(relyJS.getString(i));
				}
				return relyJSArr;
			}
			
		} catch (Exception e) {
			Error(e,"获取依赖的脚本出错");
		} 
		return null;
	}
	public Vector<String> getRelyCSSList(){
		try {
				
			JSONObject config = new JSONObject(ViewConfig.getView(viewId));
			if(config.has("relyCSS")){
				Vector<String> relyCSSArr = new Vector<String>();
				JSONArray relyCSS = config.getJSONArray("relyCSS");
				for(int i=0;i<relyCSS.length();i++){
					relyCSSArr.add(relyCSS.getString(i));
				}
				return relyCSSArr;
			}
			
		} catch (Exception e) {
			Error(e,"获取依赖的脚本出错");
		} 
		return null;
	}

	public FormConfig getFc() throws JSONException, SQLException {
		if (fc == null) {
			fc = FormConfig.getInstance(nodeId, formId);
		}
		return fc;
	}

	public void setFc(FormConfig fc) {
		this.fc = fc;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void setXform(Map<String, Object[]> xform) {
	}
}
