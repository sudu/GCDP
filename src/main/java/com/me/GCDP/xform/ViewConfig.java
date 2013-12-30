package com.me.GCDP.xform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.xform.xcontrol.BaseControl;
import com.me.GCDP.xform.xcontrol.XControlFactory;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;

public class ViewConfig {
	private JSONObject json=null;
	private int formId;
	private int viewId;	
	private ViewConfig(int viewId) throws SQLException, JSONException
	{
		String sql = "select config,formId from cmpp_viewConfig v where viewId="+viewId;
		//System.out.println(viewId);
		Map<String,Object> data =  MySQLHelper.ExecuteSql(sql, null).get(0);
		String vconfig = data.get("config").toString();
		formId = (Integer) data.get("formId");
		json = new JSONObject(vconfig);
		viewId = this.viewId;
		
	}
	public String getViewName() throws JSONException
	{
		return getViewName(json);
	}
	public JSONObject render() throws JSONException, TemplateException, IOException, SQLException
	{
		JSONObject result = new JSONObject(json.toString());
		JSONObject form = result.getJSONObject("form");
		
		JSONArray controls = getControlsConfig();
		form.put("controls", controls);
		/*
		form.remove("controls");
		Vector<BaseControl> ctrls = this.getControl();
		JSONArray controls = new JSONArray();
		for(int i=0,len =ctrls.size();i<len;i++){
			BaseControl control = ctrls.get(i);	
			JSONObject controlItem = control.render();
			//移除ext_dataSource_value 的 sql语句
			if(controlItem.has("data")){
				if(controlItem.getJSONObject("data").getString("ext_dataSource_type")=="sql"){
					controlItem.getJSONObject("data").remove("ext_dataSource_value");
				}	
			}
			//将控件配置添加到控件数组
			controls.put(controlItem);
		}
		form.put("controls", controls);
		*/
		return result;
	}
	public static String getViewName(JSONObject json) throws JSONException
	{
		return json.getJSONObject("form").getJSONObject("ui").getString("title");
	}
	/*
	 * 获取所有控件配置，保持原结构
	 */
	public JSONArray getControlsConfig() throws JSONException
	{
		JSONArray controls = new JSONArray();
		JSONArray confArr = json.getJSONObject("form").getJSONArray("controls");
		for(int i=0;i<confArr.length();i++)
		{
			JSONObject conf = confArr.getJSONObject(i);		
			
			ControlConfigItem confItem = new ControlConfigItem(conf);
			BaseControl ctr = XControlFactory.getInstance().create(confItem.getControlType());
			ctr.InitControl(confItem);
			JSONObject controlItem = ctr.render();
			//移除ext_dataSource_value 的 sql语句
			if(controlItem.has("data")){
				if(controlItem.getJSONObject("data").getString("ext_dataSource_type")=="sql"){
					controlItem.getJSONObject("data").remove("ext_dataSource_value");
				}	
			}
			
			//在内部查找一次controls
			if(conf.has("controls")){
				JSONArray subCtrls = conf.getJSONArray("controls");
				JSONArray subCtrlsNew=new JSONArray();
				for(int j=0;j<subCtrls.length();j++){
					JSONObject conf_inter = subCtrls.getJSONObject(j);
					confItem = new ControlConfigItem(conf_inter);
					ctr = XControlFactory.getInstance().create(confItem.getControlType());
					ctr.InitControl(confItem);
					
					JSONObject subCtr = ctr.render();
					//移除ext_dataSource_value 的 sql语句
					if(subCtr.has("data")){
						if(subCtr.getJSONObject("data").getString("ext_dataSource_type")=="sql"){
							subCtr.getJSONObject("data").remove("ext_dataSource_value");
						}	
					}
					subCtrlsNew.put(subCtr);
				}
				controlItem.put("controls",subCtrlsNew);
				
			}

			controls.put(controlItem);
		}
		return controls;
	}
	
	/*
	 *获取所有控件对象 
	 */
	public Vector<BaseControl> getControl() throws JSONException
	{
		Vector<BaseControl> rtn = new Vector<BaseControl>();
		JSONArray confArr = json.getJSONObject("form").getJSONArray("controls");
		JSONArray confArr_inter=new JSONArray();
		for(int i=0;i<confArr.length();i++)
		{
			JSONObject conf = confArr.getJSONObject(i);			
			//在内部查找一次controls
			if(conf.has("controls")){
				confArr_inter = conf.getJSONArray("controls");
				for(int j=0;j<confArr_inter.length();j++){
					JSONObject conf_inter = confArr_inter.getJSONObject(j);
					ControlConfigItem confItem = new ControlConfigItem(conf_inter);
					BaseControl ctr = XControlFactory.getInstance().create(confItem.getControlType());
					ctr.InitControl(confItem);
					rtn.add(ctr);
				}
				continue;
			}
		
			ControlConfigItem confItem = new ControlConfigItem(conf);
			BaseControl ctr = XControlFactory.getInstance().create(confItem.getControlType());
			ctr.InitControl(confItem);
			rtn.add(ctr);
		}
		return rtn;
	}
	
	public static void updateView(int viewId,String config) throws JSONException, SQLException
	{
		JSONObject js = new JSONObject(config);
		String viewName = getViewName(js);
		String sql ="update cmpp_viewConfig set name=?,config=? where viewId="+viewId;
		Object[] parms = new Object[2];
		parms[0] = viewName;
		parms[1] = config;
		MySQLHelper.ExecuteNoQuery(sql, parms);
	}
	public static int createView(String config,int formId) throws JSONException, SQLException
	{
		JSONObject js = new JSONObject(config);
		String viewName = getViewName(js);
		return createView(formId,viewName,config);
	}
	public static int createView(int formId,String viewName,String config) throws SQLException
	{		
		String sql ="insert cmpp_viewConfig(name,config,formId) value(?,?,?)";
		Object[] parms = new Object[3];
		parms[0] = viewName;
		parms[1] = config;
		parms[2] = formId;
		return Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));	
	}
	public static String getView(int viewId) throws SQLException
	{
		String sql = "select config from cmpp_viewConfig where viewId="+viewId;
		Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
		if(data.size()>0)
		 return MySQLHelper.ExecuteSql(sql, null).get(0).get("config").toString();
		else
		  return "";
	}
	public static int getMinViewId(int formId) throws SQLException
	{
		String sql ="select viewId from cmpp_viewConfig where formId="+formId+" order by viewId LIMIT 1";
		Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
		if(data.size()>0)
		{
			return (Integer) data.get(0).get("viewId");
		}
		return 0;
	}
	public static ViewConfig getInstance(int viewId) throws SQLException, JSONException
	{
		return new ViewConfig(viewId);		
	}
}
