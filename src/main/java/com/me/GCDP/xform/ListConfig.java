package com.me.GCDP.xform;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.MySQLHelper;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class ListConfig {
	private JSONObject json=null;
	private static Log log = LogFactory.getLog(JsonFormConfig.class);
	public ListConfig(String jsonStr) throws JSONException
	{
		json = new JSONObject(jsonStr);
	}
	public String getConfig() throws JSONException, SQLException
	{
		JSONObject myJson = new JSONObject(json.toString());
		JSONArray extButtons = myJson.getJSONObject("buttons").getJSONArray("ext");
		if(extButtons!=null){
			for(int i =0;i<extButtons.length();i++){
				extButtons.getJSONObject(i).remove("script");
			}
		}
		if(myJson.has("sql") && !myJson.getString("sql").equals("")){
			myJson.put("isCustomSql",true);
		}else{
			myJson.put("isCustomSql",false);
		}
		myJson.remove("myTemplate");
		myJson.remove("template");
		if(myJson.has("headInject")) myJson.remove("headInject");
		if(myJson.has("bodyInject")) myJson.remove("bodyInject");
		if(myJson.has("extOnReadyJs")) myJson.remove("extOnReadyJs");
		myJson.remove("sql");
		myJson.remove("filter");
		if(myJson.has("customSearchHTML")) myJson.remove("customSearchHTML");
		if(myJson.has("searchableFields")) {
			JSONObject searchableFields = proccessSearchableFields(myJson.getJSONObject("searchableFields"));
			myJson.put("searchableFields",searchableFields);
		}
		//处理searchSvr search 的sql数据源
		if(myJson.has("search")){
			myJson.put("search", proccessSqlDatasource(myJson.getJSONArray("search")))	;
		}
		if(myJson.has("searchSvr")){
			myJson.put("searchSvr", proccessSqlDatasource(myJson.getJSONArray("searchSvr")))	;
		}
		return myJson.toString();
	}
	/*
	 * 处理控件数据源
	 */
	private JSONArray proccessSqlDatasource(JSONArray searchSvr) throws JSONException, SQLException{
		JSONArray dataArr = searchSvr;
		for(int i=0;i<dataArr.length();i++){
			JSONObject o = dataArr.getJSONObject(i);
			if(o.has("dataSourceType") && o.has("dataSource")){
				String dataSourceType = o.getString("dataSourceType");
	        	String dataSource = o.getString("dataSource");
	        	
	        	if(dataSourceType.equals("sql") && !dataSource.equals("") ){
	        		Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(dataSource, null);
	        		JSONArray jc=formatData(data);
	        		o.put("dataSource",jc.toString());
	        	}
			}
		}
		return dataArr;
	}
	
	
	/*
	 * 处理控件数据源
	 */
	private JSONObject proccessSearchableFields(JSONObject jsonData) throws JSONException, SQLException{
		JSONObject json=new JSONObject(jsonData.toString());
		Iterator<?> it = json.keys();  
        while(it.hasNext()){//遍历JSONObject  
        	String key = (String)it.next();
        	JSONObject o = json.getJSONObject(key);
        	String dataSourceType = o.getString("dataSourceType");
        	String dataSource = o.getString("dataSource");
        	if(dataSourceType.equals("sql") && !dataSource.equals("") ){
        		Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(dataSource, null);
        		JSONArray jc=formatData(data);
        		o.put("dataSource",jc.toString());
        	}
              
        }  
		return json;
	}
	private JSONArray formatData(Vector<Map<String,Object>> data)
	{
		JSONArray dataArr = new JSONArray();
		for(int index=0;index<data.size();index++)
		{
			JSONArray arr = new JSONArray();
			Map<String,Object> item = data.get(index);
			Object o = item.get("value");
			arr.put((o==null?"":o.toString()));
			o = item.get("text");
			arr.put((o==null?"":o.toString()));
			dataArr.put(arr);
		}
		return dataArr;
	}
	public JSONObject getJsonConfig()
	{
		return json;
	}
	public int getListPagesize()
	{
		try
		{
			return json.getInt("pagesize");
		}
		catch (Exception e) {
			return 10;
		}
	}
	public String getSql()
	{
		try
		{
			String sourceType="";
			if(json.has("sourceType")){
				sourceType = json.getString("sourceType");
			}
			if(sourceType.equals("sql"))
				return json.getString("sql");
			else
				return "";
		}
		catch (Exception e) {
			return "";
		}
	}	
	public String getFilter()
	{
		try
		{
			return json.getString("filter");
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public JSONArray getMustReturnFields()
	{
		try
		{
			return json.getJSONArray("mustReturnFields");
		}
		catch (Exception e) {
			return null;
		}
	}		
	public String getListName()
	{
		try {
			return json.getJSONObject("form").getJSONObject("ui").getString("title");
		} catch (JSONException e) {
			log.error(e);
		}
		return "";
	}
	public static String getListConfig(int listId) throws SQLException{
		String sql = "select config from cmpp_listConfig where listId="+listId;
		return MySQLHelper.ExecuteSql(sql, null).get(0).get("config").toString();
	}
	public static int createConfig(int formId,String formName,String config) throws NumberFormatException, SQLException
	{
		String sql ="insert into cmpp_listConfig(formId,name,config) value(?,?,?)";
		Object[] parms = new Object[3];
		parms[0] = formId;
		parms[1] = formName;
		parms[2] = config;
		return Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));	
	}
	/*
	 * 获取页头注入的内容
	 */
	public String getHeadInject() throws JSONException {
		return json.getString("headInject");
	}
	/*
	 * 获取页尾注入的内容
	 */
	public String getBodyInject() throws JSONException {
		return json.getString("bodyInject");
	}
	/*
	 * 获取Ext.onReady注入的JS
	 */
	public String getExtOnReadyJs() throws JSONException {
		return json.getString("extOnReadyJs");
	}
	/*
	 * 获取自定义搜索HTML
	 */
	public String getCustomSearchHTML() throws JSONException {
		return json.getString("customSearchHTML");
	}
	/*
	 * 获取列表项配置
	 */
	public JSONArray getColumns() throws JSONException {
		return json.getJSONArray("columns");
	}

}
