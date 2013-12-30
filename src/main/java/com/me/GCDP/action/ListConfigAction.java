/**
 * 
 */
package com.me.GCDP.action;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Version;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.version.VersionHelper2;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author zhangzy
 *modify by chengds
 */
public class ListConfigAction extends ActionSupport {
	private static final long serialVersionUID = -8569494905531379340L;
	private static Log log = LogFactory.getLog(ListConfigAction.class);
	private Integer formId;
	private int listId;
	private String config;
	private String name;
	private String sourceType="";//列表页数据源类型 default url sql
	private JSONObject listPageJson;
	ScriptService scriptService;
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public String delListConfig(){
		HttpServletResponse response = ServletActionContext.getResponse();
		String isSuccess="false";
		String sql ="delete from cmpp_listConfig where listId=?";
		Object[] parms = new Object[1];
		parms[0]=listId;
		int i;
		try {
			i=MySQLHelper.ExecuteNoQuery(sql, parms);
			if(i>0){
				isSuccess="success";
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("isSuccess", isSuccess);
			response.getWriter().write(jsonObj.toString());
			response.getWriter().flush();
		} catch (SQLException e) {
			log.error(e);
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * 保存一个列表配置的版本信息
	 * @param userName 用户名
	 * @param config 列表页配置(json)
	 * @throws Exception
	 */
	private void saveListVersion(String userName, String config) throws Exception {
		String key = nodeId + "_" + formId + "_" + listId + "_list";
		JSONObject listConfigData = new JSONObject(config);
		VersionHelper2 versionHelper2 = new VersionHelper2(key,userName,listConfigData.toString());
		versionHelper2.setVersionMapper((VersionMapper<Version>) 
				SpringContextUtil.getBean("versionMapper"));
		versionHelper2.save();
	}
	
	public String saveListConfig(){
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean isSuccess=false;
		response.setCharacterEncoding("UTF-8");
		int i=0;
		if(listId==0){
			String sql ="insert into cmpp_listConfig(formId,config,name) value(?,?,?)";
			Object[] parms = new Object[3];
			parms[0]=formId;
			parms[1]=config;
			parms[2]=name;
			try {
				String r=MySQLHelper.InsertAndReturnId(sql, parms);
				saveListVersion(AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]", config);
				if(r!=null){
					isSuccess=true;
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("success", isSuccess);
				jsonObj.put("listId", r);
				response.getWriter().write(jsonObj.toString());
				response.getWriter().flush();
			} catch (SQLException e) {
				log.error(e);
			} catch (JSONException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
			}
		}else{
			String sql ="update cmpp_listConfig set formId=?,name=?,config=? where listId=?";
			Object[] parms = new Object[4];
			parms[0]=formId;
			parms[1]=name;
			parms[2]=config;
			parms[3]=listId;
			try {
				i=MySQLHelper.ExecuteNoQuery(sql, parms);
				if(i>0){
					isSuccess=true;
				}
				saveListVersion(AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]", config);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("success", isSuccess);
				response.getWriter().write(jsonObj.toString());
				response.getWriter().flush();
			JSONObject cfg = new JSONObject(config);
			JSONArray buttons = cfg.getJSONObject("buttons").getJSONArray("ext");
			for(int m=0;m<buttons.length();m++)
			{
				JSONObject btn = buttons.getJSONObject(m);
				String scriptKey = btn.getString("id");
				String script = btn.getString("script");
				scriptService.save(nodeId, script, ScriptType.form,formId.toString(),scriptKey);
				
			}
			} catch (SQLException e) {
				log.error(e);
			} catch (JSONException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
			}
		}
		return null;
	}
	public String copyListConfig(){
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean isSuccess=false;
		String sql ="insert into cmpp_listConfig(name,config,formId)"+
				"select CONCAT(name,'_copy') name,config,formId from cmpp_listConfig where listId=?";
		Object[] parms = new Object[1];
		parms[0] = listId;
		try {
			String r=MySQLHelper.InsertAndReturnId(sql, parms);
			if(r!=null){
				isSuccess=true;
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("success", isSuccess);
			jsonObj.put("listId", r);
			response.getWriter().write(jsonObj.toString());
			response.getWriter().flush();
		} catch (SQLException e) {
			log.error(e);
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}
	public String getListManage() throws JSONException{
		String type="default";
		if(listId!=0){
			String sql_config ="select * from cmpp_listConfig where formId=? and listId=?";
			Object[] parms = new Object[2];
			parms[0]=formId;
			parms[1]=listId;
			Map<String, Object> map_field=null;
			
			try {
				map_field = MySQLHelper.ExecuteSql(sql_config, parms).get(0);
			} catch (SQLException e) {
				log.error(e);
			}
			try {
				listPageJson=new JSONObject(map_field);
				
				String configStr = listPageJson.getString("config");
				if(configStr.isEmpty()){
					configStr="{}";
				}
				JSONObject configJson = new JSONObject(configStr);
				listPageJson.put("config",configJson);
				if(this.sourceType==null || this.sourceType.equals("")){
					if(configJson.has("sourceType")){
						type = configJson.getString("sourceType");
					}
				}
				if(type.equalsIgnoreCase("default")) {
					listPageJson.put("fields", this.getAllFields(formId));
				}

			} catch (JSONException e) {
				log.error(e);
			}
		}else{	
			listPageJson=new JSONObject();
			listPageJson.put("listId",0);
			listPageJson.put("formId",formId);
			listPageJson.put("fields", this.getAllFields(formId));
		}
		if(this.sourceType==null || this.sourceType.equals("")){
			this.sourceType = type;	
		}
		return "listManage";
	}

	private JSONArray getAllFields(int formId){
		JSONArray ret = null;
		String sql_field ="select config from cmpp_formConfig where formId=?";
		Object[] parms = new Object[1];
		parms[0]=formId;
		Map<String, Object> map_config=null;
		
		try {
			map_config = MySQLHelper.ExecuteSql(sql_field, parms).get(0);
		} catch (SQLException e) {
			log.error(e);
		}
		try {
			JSONObject resultJson=new JSONObject(map_config.get("config").toString());
			ret= resultJson.getJSONObject("fieldsConfig").getJSONArray("fields");

		} catch (JSONException e) {
			log.error(e);
		}
		return ret;
	}
	
	/*
	 * 获取formConfig
	 */
	public String getFormConfig() throws SQLException {
		return FormConfig.getConfig(formId);
	}	
	
	/*
	 * 获取viewConfig 
	 * [{name:'图片精编表单1',viewId:148},{name:'图片精编表单2',viewId:149}]
	 */
	public JSONArray getViewConfig() throws SQLException, JSONException {
		String sql ="select viewId,name from cmpp_viewConfig where formId=?";
		Object[] parms = new Object[1];
		parms[0]=formId;
		List<Map<String,Object>> list=null;
		
		try {
			list = MySQLHelper.ExecuteSql(sql, parms);
		} catch (SQLException e) {
			log.error(e);
		}
		//JSONObject ret= new JSONObject(map);
		JSONArray retArr =  new JSONArray(list);
		return  retArr;
	}	
	

	public JSONObject getListPageJson() {
		return listPageJson;
	}

	public void setListPageJson(JSONObject listPageJson) {
		this.listPageJson = listPageJson;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	private Integer nodeId;	
	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getSourceType(){
		return  sourceType;
	}
	public void setSourceType(String value){
		this.sourceType=value;
	}
}
