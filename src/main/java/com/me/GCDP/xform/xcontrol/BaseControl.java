package com.me.GCDP.xform.xcontrol;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import com.me.json.JSONArray;

import com.me.GCDP.script.plugin.HttpPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.xform.ControlConfigItem;
import com.me.GCDP.xform.XControlScriptType;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;
/**
 * <p>Title: </p>
 * <p>Description:用户控件的基础类，所有页面控件的基础，定义了控件的渲染规则，以及获取和设置控件值的方法 </p>
 * <p>Company: ifeng.com</p>
 * @author : yangjunjie
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-2-17              yangjunjie               create the class    </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class BaseControl {
	private String controlName;
	private Object value ="";
	protected ControlConfigItem conf;
	
	public String getControlId(){
		try {
			return conf.getJson().getJSONObject("ui").getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public ControlConfigItem getConf() {
		return conf;
	}
	public BaseControl() 
	{	
	}
	public void InitControl(ControlConfigItem conf) throws JSONException
	{
		this.conf = conf;
		controlName = conf.getControlType();
	}
	public String getControlName()
	{
		return controlName;
	}
	public String getField() throws JSONException
	{
		return conf.getControlField();
	}
	public Object getValue()
	{
		return value;
	}
	public void setValue(Object value)
	{
		this.value = value;
	}
	public void setValue(Map<String,Object> data) throws JSONException
	{
		if(data!=null)
		{
			this.value = data.get(getField());
		}
	}
	/*
	 * 渲染控件的配置
	 */
	public JSONObject render() throws JSONException
	{
		return conf.getJson();
	}
	/**
	 * 渲染控件数据源的配置
	 * @param requestUrl 当前请求的Uri地址,url数据源中相对地址需要拼接这个地址
	 * @return
	 * @throws Exception
	 */
	public String[][] renderDataSource(String requestUrl) throws Exception
	{
		String[][] jc=null;
		String type = conf.getDataSourceConfig("ext_dataSource_type");
		if(type.equals("sql"))
		{
			String sql =  conf.getDataSourceConfig("ext_dataSource_value");
			Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
			jc=formatData(data);
		}else if(type.equals("url")){
			String asyc = conf.getDataSourceConfig("ext_dataSource_asyc");
			if(asyc!="" && Boolean.parseBoolean(asyc)==true){
				//同步获取数据
				ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
				HttpPlugin http = (HttpPlugin) pf.getP("http");
				String url = conf.getDataSourceConfig("ext_dataSource_value");
				if(!StringUtils.startsWith(url, "http://")){
					url = requestUrl + url;
				}
				String response = http.sendGet(url, "UTF-8");//返回的接口数据格式：{"data":[{text:"",value:""}]}
				try{
					//JSONObject retJson = new JSONObject(response);
					//JSONArray arr = retJson.getJSONArray("data");
					JSONArray arr = new JSONArray(response);
					String[][] rtn = new String[arr.length()][2];
					for(int i=0;i<arr.length();i++){
						JSONArray item = arr.getJSONArray(i);
						rtn[i][0] = item.getString(0);
						rtn[i][1] = item.getString(1);
					}
					jc = rtn;
				}catch(Exception ex){
					throw new Exception("url数据源接口返回的数据异常;url:" + url);
				}
			}
		}
		return jc;
	}
	protected String[][] formatData(Vector<Map<String,Object>> data)
	{
		String[][] rtn = new String[data.size()][2];
		for(int index=0;index<data.size();index++)
		{
			Map<String,Object> item = data.get(index);
			Object o = item.get("value");
			rtn[index][0] = (o==null?"":o.toString());
			o = item.get("text");
			rtn[index][1] = (o==null?"":o.toString());
		}
		return rtn;
	}
	protected void runScript(XControlScriptType type)
	{
	}
}
