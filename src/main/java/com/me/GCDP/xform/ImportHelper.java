package com.me.GCDP.xform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.search.SearchService_V2;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class ImportHelper {
	public static String Export(Integer nodeId,Integer formId) throws SQLException, JSONException, IOException
	{
		JSONObject js = new JSONObject();
		//获取formConfig
		List<Map<String,Object>> data = MySQLHelper.ExecuteSql("SELECT * FROM cmpp_formConfig WHERE formId="+formId,null);
		if(data.size()>0)
		{
			JSONObject formConfig = new JSONObject(data.get(0));
			js.put("formConfig", formConfig);
		}
		//获取viewConfig
		data = MySQLHelper.ExecuteSql("SELECT * FROM cmpp_viewConfig WHERE formId="+formId,null);
		if(data.size()>0)
		{
			JSONArray listConfig = new JSONArray(data);
			js.put("viewConfig", listConfig);
		}
		//获取listConfig
		data = MySQLHelper.ExecuteSql("SELECT * FROM cmpp_listConfig WHERE formId="+formId,null);
		if(data.size()>0)
		{
			JSONArray listConfig = new JSONArray(data);
			js.put("listConfig", listConfig);
		}
		ScriptService scriptService = (ScriptService) SpringContextUtil.getBean("scriptService");
		//获取保存前脚本
		String script = scriptService.open(nodeId,ScriptType.form, formId.toString(),"0");
		js.put("beforSave", script);
		//获取保存后脚本
		script = scriptService.open(nodeId,ScriptType.form, formId.toString(),"1");
		js.put("afterSave", script);
		//获取渲染脚本
		script = scriptService.open(nodeId,ScriptType.form, formId.toString(),"2");
		js.put("view", script);
		//获取发布脚本
		script = scriptService.open(nodeId,ScriptType.form, formId.toString(),"3");
		js.put("publish", script);
		//获取表单公用表单
		data = MySQLHelper.ExecuteSql("SELECT * FROM cmpp_template WHERE dataId =0 AND dataFormId ="+formId,null);
		if(data.size()>0)
		{
			JSONArray templates = new JSONArray(data);
			js.put("templates", templates);
		}
		return js.toString();
	}
	public static Integer Import(Integer nodeId,String strConfig) throws Exception
	{
		JSONObject config = new JSONObject(strConfig);
		//导入formConfig
		String formConfig = config.getJSONObject("formConfig").getString("config");
		String formName =  config.getJSONObject("formConfig").getString("name");
		Integer formId = FormConfig.createFormConfig(formName, formConfig, nodeId);
		//创建表结构
		TableSchemaHelper.createTable(FormConfig.getInstance(nodeId, formId));
		//导入viewConfig
		JSONArray viewConfigs = config.getJSONArray("viewConfig");
		for(int i=0;i<viewConfigs.length();i++)
		{
			String vc = viewConfigs.getJSONObject(i).getString("config");
			ViewConfig.createView(vc, formId);
		}
		//导入listConfig
		JSONArray listConfigs = config.getJSONArray("listConfig");
		for(int i=0;i<listConfigs.length();i++)
		{
			String lc = listConfigs.getJSONObject(i).getString("config");
			String name = listConfigs.getJSONObject(i).getString("name");
			ListConfig.createConfig(formId, name, lc);
		}
		//导入脚本
		ScriptService scriptService = (ScriptService) SpringContextUtil.getBean("scriptService");
		//获取保存前脚本
		String script ="";
		if(config.has("beforSave"))
		{
			script = config.getString("beforSave");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"0");
		}
		//获取保存后脚本
		if(config.has("afterSave"))
		{
			script = config.getString("afterSave");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"1");
		}
		//获取渲染脚本
		if(config.has("view"))
		{
			script = config.getString("view");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"2");
		}
		//获取发布脚本
		if(config.has("publish"))
		{
			script = config.getString("publish");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"3");
		}
		
		//5.更新搜索引擎 cds add at 2013/12/09
		SearchService_V2 searchSvr = (SearchService_V2) SpringContextUtil.getBean("searchService");
		SearchHelper shp = new SearchHelper(nodeId, formId, searchSvr);
		shp.createInedex();
		
		return formId;
	}

}
