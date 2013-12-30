package com.me.GCDP.xform;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.xform.xcontrol.BaseControl;
import com.me.GCDP.xform.xcontrol.XControlFactory;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;


public class JsonFormConfig {
	private JSONObject json=null;
	private JSONObject runtimeJson;
	private static Log log = LogFactory.getLog(JsonFormConfig.class);
	public JsonFormConfig(String jsonStr) throws JSONException
	{
		json = new JSONObject(jsonStr);
		runtimeJson = getListRuntimeConfig();
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
	public String getBeforeJs()
	{
		return getJs("s_beforeScript");
	}
	public String getSavedJs()
	{
		return getJs("s_afterScript");
	}
	private String getJs(String name)
	{
		try {
			return json.getJSONObject("form").getJSONObject("cfg").getString(name);
		} catch (JSONException e) {
			log.error(e);
		}
		return "";
	}
	public Vector<ControlDbItem> getDbItem() throws JSONException
	{
		Vector<ControlDbItem> rtn = new Vector<ControlDbItem>();
		JSONArray ctrConfigs = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<ctrConfigs.length();i++)
		{
			JSONObject ctr = (JSONObject) ctrConfigs.get(i);
			ControlDbItem item = new ControlDbItem();
			String type;
			try
			{
				type = ctr.getJSONObject("db").getString("f_saveType");
			}
			catch (Exception e) {
				type="nosave";
			}
			if(type.equals("db"))
			{
				item.setFieldName(ctr.getJSONObject("db").getString("f_name"));
				item.setFieldType(ctr.getJSONObject("db").getString("f_type"));
				item.setFieldLength(ctr.getJSONObject("db").getInt("f_length"));
				rtn.add(item);
			}
		}
		return rtn;
	}
	public Vector<BaseControl> getControl() throws JSONException
	{
		Vector<BaseControl> rtn = new Vector<BaseControl>();
		JSONArray confArr = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<confArr.length();i++)
		{
			JSONObject conf = confArr.getJSONObject(i);
			ControlConfigItem confItem = new ControlConfigItem(conf);
			BaseControl ctr = XControlFactory.getInstance().create(confItem.getControlType());
			ctr.InitControl(confItem);
			rtn.add(ctr);
		}
		return rtn;
	}
	public Vector<BaseControl> getDBControl() throws JSONException
	{
		Vector<BaseControl> rtn = new Vector<BaseControl>();
		JSONArray confArr = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<confArr.length();i++)
		{
			JSONObject conf = confArr.getJSONObject(i);
			String type;
			try
			{
				type = conf.getJSONObject("db").getString("f_saveType");
			}
			catch (Exception e) {
				type="nosave";
			}
			if(type.equals("db"))
			{
				ControlConfigItem confItem = new ControlConfigItem(conf);
				BaseControl ctr = new BaseControl();
				ctr.InitControl(confItem);
				rtn.add(ctr);
			}
		}
		return rtn;
	}
	public int getListPagesize()
	{
		try
		{
			return json.getJSONObject("form").getJSONObject("listPage").getInt("pagesize");
		}
		catch (Exception e) {
			return 10;
		}
	}
	private Vector<String> getListFields(String key)
	{
		Vector<String> rtn = new Vector<String>();
		try
		{
		
			JSONArray arr = runtimeJson.getJSONObject("db").getJSONArray(key);
			for(int i =0;i<arr.length();i++)
			{
				rtn.add(arr.getString(i));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}
	public Vector<String> getListFields()
	{
		Vector<String> rtn = new Vector<String>();
		try
		{
			JSONArray arr = runtimeJson.getJSONArray("fields");
			for(int i =0;i<arr.length();i++)
			{
				rtn.add(arr.getString(i));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}
	public Vector<String> getListSearchFields()
	{
		return getListFields("searchable");
	}
	private Boolean getFieldIsSearchable(String indexType){
		Boolean ret = false;
		//[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储']],
		if(indexType.equals("索引") || indexType.equals("索引+分词") || indexType.equals("索引+排序") || indexType.equals("全文索引")|| indexType.equals("特殊符号分词") ){
			ret = true;
		}	
		return ret;
	}
	private Boolean getFieldIsSortable(String indexType){
		Boolean ret = false;
		if(indexType.contains("排序") ){
			ret = true;
		}	
		return ret;
	}
	public JSONObject getListRuntimeConfig() throws JSONException
	{

		JSONObject retJson=new JSONObject();
		//需要返回的字段
		JSONArray fieldsArr = new JSONArray();
		fieldsArr.put("id");
		
		//列描述
		JSONArray colsArr = new JSONArray();
		//默认把ID列添加到列集合中
		JSONObject idColJson= new JSONObject();
		idColJson.put("title", "ID").put("tpl", "{id}").put("field", "id").put("width", 85).put("isView", true);
		colsArr.put(idColJson);
		
		JSONArray dbSearchableArr = new JSONArray();
		JSONArray dbSortableArr = new JSONArray();
		JSONArray svrSearchableArr = new JSONArray();
		JSONArray svrSortableArr = new JSONArray();
		
		JSONObject listPageJson = json.getJSONObject("form").getJSONObject("listPage");
		retJson.put("pagesize", getListPagesize());
		JSONArray columsArr= listPageJson.getJSONArray("colums");
		JSONObject tempJson;
		int len = columsArr.length();
		for(int i=0;i<len;i++){
			JSONObject col = columsArr.getJSONObject(i);
			JSONObject commonCfg = col.getJSONObject("通用");
			JSONObject dbCfg = col.getJSONObject("从数据库查询");
			JSONObject serviceCfg = col.getJSONObject("从接口查询");
			
			String field = commonCfg.getString("f_name");
			fieldsArr.put(field);

			tempJson= new JSONObject();
			tempJson.put("title", commonCfg.getString("l_columnName"))
					.put("tpl", commonCfg.getString("l_tpl"))
					.put("field", field)
					.put("isView", commonCfg.getBoolean("l_isView"));
			
			colsArr.put(tempJson);
			if(dbCfg.getBoolean("l_allowSearch")){
				dbSearchableArr.put(field);
			}
			if(dbCfg.getBoolean("l_allowSort")){
				dbSortableArr.put(field);
			}
			String indexType = serviceCfg.getString("indexType");
			if(getFieldIsSearchable(indexType)){
				svrSearchableArr.put(field);
			}
			if(getFieldIsSortable(indexType)){
				svrSortableArr.put(field);
			}
		}

		//db搜索配置
		JSONObject dbJson = new JSONObject();
		dbJson.put("searchable", dbSearchableArr);
		dbJson.put("sortable", dbSortableArr);
		
		//service搜索配置
		JSONObject serviceJson = new JSONObject();
		serviceJson.put("searchable", svrSearchableArr);
		serviceJson.put("sortable", svrSortableArr);
		
		retJson.put("fields", fieldsArr);
		retJson.put("columns", colsArr);
		retJson.put("db", dbJson);
		retJson.put("service", serviceJson);	
		return retJson;
	}
	public String getListConfig() throws JSONException
	{
		return getListRuntimeConfig().toString();
	}
	public JSONObject getFormDesignConfig() throws JSONException
	{
		JSONObject rtn = new JSONObject(json.toString());
		JSONObject formjson = rtn.getJSONObject("form");
		formjson.remove("listPage");
		JSONArray arr = formjson.getJSONArray("controls");
		for(int i=0;i<arr.length();i++)
		{
			JSONObject control = arr.getJSONObject(i);
			control.remove("f_type");
			control.remove("f_allowNull");
			control.remove("f_length");
			control.remove("f_saveType");
		}
		return rtn;
	}
	public JSONObject getListDesignConfig() throws JSONException
	{
		JSONObject rtn = new JSONObject(json.toString());
		JSONObject formjson = rtn.getJSONObject("form");
		formjson.remove("controls");
		formjson.remove("scirpt");
		return rtn;
	}
	public JSONObject getDbDefine() throws JSONException
	{
		JSONArray arr = new JSONArray();
		JSONArray ctrs = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<ctrs.length();i++)
		{
			JSONObject field = ctrs.getJSONObject(i).getJSONObject("db");
			arr.put(field);
		}
		JSONObject rtn = new JSONObject();
		rtn.put("fields", arr);
		return rtn;
	}
}
