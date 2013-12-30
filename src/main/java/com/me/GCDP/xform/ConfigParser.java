package com.me.GCDP.xform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class ConfigParser {
	private JSONObject json=null;
	private JSONObject searchConfig = null;//搜索服务索引配置
	private JSONObject fieldsConfig = null;//表字段配置
	private JSONObject listConfig = null;//列表配置(运行时和运行时通用)
	private JSONObject formConfig = null;//表单配置(运行时和运行时通用)
	
	private static Log log = LogFactory.getLog(JsonFormConfig.class);
	public ConfigParser(String jsonStr) throws JSONException
	{
		json = new JSONObject(jsonStr);
	}
	public String getConfig()
	{
		return json.toString();
	}
	public JSONObject getJsonConfig()
	{
		return json;
	}
	public String getFormName()
	{
		try {
			return json.getJSONObject("form").getJSONObject("ui").getString("title");
		} catch (JSONException e) {
			log.error(e);
		}
		return "";
	}
	private Boolean isFieldSearchable(int indexType){
		Boolean ret = false;
		if(indexType>0 && indexType<6 ){
			ret = true;
		}	
		return ret;
	}
	private Boolean isFieldSortable(int indexType){
		Boolean ret = false;
		if(indexType==2 ){
			ret = true;
		}	
		return ret;
	}

	
	/*
	 * 解析原始JSON,返回解析结果
	 */
	public JSONObject doParseConfig() throws JSONException
	{

		//需要返回的字段
		JSONArray fieldsArr = new JSONArray();
		fieldsArr.put(new JSONArray("[id,ID]"));
		
		//列描述
		JSONArray colsArr = new JSONArray();
		//默认把ID列添加到列集合中
		//JSONObject idColJson= new JSONObject();
		//idColJson.put("title", "ID").put("tpl", "{id}").put("field", "id").put("width", 85).put("isView", true);
		//colsArr.put(idColJson);
		
		JSONArray dbSearchableArr = new JSONArray();
		JSONArray dbSortableArr = new JSONArray();
		JSONArray svrSearchableArr = new JSONArray();
		JSONArray svrSortableArr = new JSONArray();
		JSONObject svrFieldsCfg = new JSONObject();
		
		JSONObject listPageJson = json.getJSONObject("form").getJSONObject("listPage");
		int pagesize = 10;
		String listTemplate="";
		String cutomListTemplate="";
		try
		{
			pagesize = listPageJson.getInt("l_pagesize");
			listTemplate = listPageJson.getString("l_template");
			cutomListTemplate = listPageJson.getString("l_customedTpl");
		}
		catch (Exception e) {
	
		}
		String listCfgStr= listPageJson.getString("l_listCfg");
		JSONArray columsArr= new JSONArray(listCfgStr);
		JSONObject tempJson;
		int len = columsArr.length();
		for(int i=0;i<len;i++){
			JSONObject col = columsArr.getJSONObject(i);
			JSONObject commonCfg = col.getJSONObject("通用");
			
			String field = commonCfg.getString("f_name");
			//if(!field.trim().equals("")){
				String name = commonCfg.getString("l_columnName");
				fieldsArr.put(new JSONArray("[" + field + "," + name + "]"));
				tempJson= new JSONObject();
				tempJson.put("title", name)
						.put("tpl", commonCfg.getString("l_tpl"))
						.put("field", field)
						.put("isView", commonCfg.getBoolean("l_isView"));
				
				colsArr.put(tempJson);
			//}
		}
		JSONArray ctrlsArr = json.getJSONObject("form").getJSONArray("controls");
		len = ctrlsArr.length();
		for(int i=0;i<len;i++){
			JSONArray arrTmp ;
			if(ctrlsArr.getJSONObject(i).has("controls")){
				arrTmp = ctrlsArr.getJSONObject(i).getJSONArray("controls");
			}else{	
				arrTmp = new JSONArray();
				arrTmp.put(ctrlsArr.getJSONObject(i));
			}
			for(int k=0;k<arrTmp.length();k++){
				JSONObject ctrl = arrTmp.getJSONObject(k);
				JSONObject dbObj = ctrl.getJSONObject("db");
				String f_name = dbObj.getString("f_name");
				String f_title = ctrl.getJSONObject("ui").getString("fieldLabel");
				String saveType = dbObj.getString("f_saveType");
				if(saveType.equals("1")){ //1==nosave
					dbObj.put("f_name", "");//将不用存储的f_name设为空
				}
				if(!f_name.trim().equals("") && !saveType.equals("1")){
					//String fieldLabel = ctrl.getJSONObject("ui").getString("fieldLabel");
					if(dbObj.getBoolean("l_allowSearch")){
						dbSearchableArr.put(f_name);
					}
					if(dbObj.getBoolean("l_allowSort")){
						dbSortableArr.put(f_name);
					}	
					int indexType = Integer.parseInt(dbObj.getString("indexType"));
					if(isFieldSearchable(indexType)){
						svrSearchableArr.put(f_name);
					}
					if(isFieldSortable(indexType)){
						svrSortableArr.put(f_name);
					}
					svrFieldsCfg.put(f_name,indexType);
				}
			}
		}

		//搜索配置JSON，存储在formConfig表里
		this.searchConfig = new JSONObject();
		this.searchConfig.put("searchable",svrSearchableArr);
		this.searchConfig.put("sortable",svrSortableArr);
		this.searchConfig.put("fieldsIndexConfig",svrFieldsCfg);
		
		//表字段配置JSON，存储在formConfig表里
		this.fieldsConfig  = new JSONObject();
		this.fieldsConfig.put("searchable", dbSearchableArr);
		this.fieldsConfig.put("sortable", dbSortableArr);
		this.fieldsConfig.put("fieldsConfig", this.getDbDefine());
		
		//列表页配置JSON,存储在listConfig表里
		this.listConfig = new JSONObject();
		this.listConfig.put("pagesize", pagesize);
		this.listConfig.put("myTemplate", cutomListTemplate);
		this.listConfig.put("template", listTemplate);
		//this.listConfig.put("fields", fieldsArr);
		this.listConfig.put("columns", colsArr);
		
		this.formConfig = this.getViewConfig();
			
		JSONObject retJson = new JSONObject();
		retJson.put("formConfig", this.formConfig);
		retJson.put("listConfig", this.listConfig);
		retJson.put("fieldsConfig", this.fieldsConfig);		
		retJson.put("searchConfig", this.searchConfig);	
		retJson.put("allFields",this.getAllFields());
				
		return retJson;
	}

	/*
	 * 解析得到表单配置(设计时和运行时共用)
	 */
	public JSONObject getViewConfig() throws JSONException
	{
		JSONObject rtn = new JSONObject(json.toString());
		JSONObject formjson = rtn.getJSONObject("form");
		formjson.remove("listPage");
		JSONArray arr = formjson.getJSONArray("controls");
		for(int i=0;i<arr.length();i++)
		{
			JSONArray subCtrls;
			JSONObject control = arr.getJSONObject(i);
			if(control.has("controls")){
				subCtrls = control.getJSONArray("controls");
			}else{
				subCtrls = new JSONArray();
				subCtrls.put(arr.get(i));
			}
			for(int k=0;k<subCtrls.length();k++){
				control = subCtrls.getJSONObject(k);
				control.put("f_name", control.getJSONObject("db").get("f_name"));
				control.remove("db");
			}
			
		}
		return rtn;
	}
	/*
	 * 解析获取数据字段配置
	 */
	public JSONArray getDbDefine() throws JSONException
	{
		JSONArray arr = new JSONArray();
		JSONArray ctrs = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<ctrs.length();i++)
		{
			JSONArray subCtrls;
			if( ctrs.getJSONObject(i).has("controls")){
				subCtrls =  ctrs.getJSONObject(i).getJSONArray("controls");
			}else{
				subCtrls = new JSONArray();
				subCtrls.put(ctrs.get(i));
			}
			for(int k=0;k<subCtrls.length();k++){
				JSONObject subCtrl = subCtrls.getJSONObject(k);
				JSONObject field = subCtrl.getJSONObject("db");
				if(!field.getString("f_saveType").equals("1")){
					field.put("f_title", subCtrl.getJSONObject("ui").getString("fieldLabel"))	; 
					arr.put(field);	
				}
			}

		}
		return arr;
	}	
	/*
	 * 解析获取所有字段（数组）
	 */
	public JSONArray getAllFields() throws JSONException
	{
		JSONArray arr = new JSONArray();
		JSONArray ctrs = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<ctrs.length();i++)
		{
			JSONArray subCtrls;
			if( ctrs.getJSONObject(i).has("controls")){
				subCtrls =  ctrs.getJSONObject(i).getJSONArray("controls");
			}else{
				subCtrls = new JSONArray();
				subCtrls.put(ctrs.get(i));
			}
			for(int k=0;k<subCtrls.length();k++){
				JSONObject field = subCtrls.getJSONObject(k).getJSONObject("db");
				String fldName = field.getString("f_name");
				String saveType = field.getString("f_saveType");
				if(!fldName.trim().equals("") && !saveType.equals("1")){
					String fldTitle = subCtrls.getJSONObject(k).getJSONObject("ui").getString("fieldLabel");
					JSONArray fld = new JSONArray("[" + fldName + "," + fldTitle + "]");
					arr.put(fld);
				}
			}
			

		}
		return arr;
	}
}
