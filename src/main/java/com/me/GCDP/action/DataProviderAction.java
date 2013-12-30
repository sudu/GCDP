package com.me.GCDP.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.StringUtil;

import com.me.GCDP.search.SearchPlugin;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.xform.Form2Plugin;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.ListConfig;
import com.me.GCDP.xform.ListHelper;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.search.util_V2.Page;
import com.me.json.JSONArray;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;


/**
 * 
 * <title>CMPP表单数据提供者类</title>
 * 
 * <p>负责对外部提供数据；表单数据描述；对外数据接口生成辅助</p>
 * <p>接口地址范例1:http://localhost:8888/Cmpp/runtime/dataProvider!getDataById.jhtml?nodeId=14&formId=120&dataId=5&callback=parse&ext=32,'dd'</p>
 * <p>接口地址范例2:http://localhost:8888/Cmpp/runtime/dataProvider.jhtml?from=db&nodeId=14&formId=120&where=id%3E5&fields=title,id&limit=5&start=0</p>
 * Copyright © 2013 Phoenix New Media Limited All Rights Reserved.
 * 
 * @author chengds 2013-11-07
 */
public class DataProviderAction  extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(XListAction.class);
	
	private Integer nodeId=0;
	private Integer formId=0;
	private Integer listId;
	private Integer dataId=0;
	private String from = "service";
	private String fields="*";
	private String where="";
	private String q;
	private String sorts;
	private Integer start = 0; 
	private Integer limit = 0;
	
	/*接口回调参数,应用于JSONP方式请求数据*/
	private String callback;
	private String ext;
	
	private int recordCount;
	private String recordJson;
	
	private boolean hasError = false;
	private String msg;
	
	public ListHelper listHelper = null;
	public ListConfig conf;
	public FormConfig fconf;
	private SearchPlugin searchPlugin;
	
	/**
	 * 表单数据通用查询接口方法.
	 * 必需参数:nodeId,formId,where|q
	 * 可选参数：fields,from,sorts,start,limit,callback,ext
	 * @return
	 */
	public String execute(){
		//数据校验
		if(this.nodeId==0 || this.formId==0){
			msg = "nodeId、formId不能为空不能为0";
			hasError = true;
			return "msg";	
		}
		if(StringUtil.isBlank(this.fields)){
			msg = "未指定需要返回的字段";
			log.error(msg);	
			hasError = true;
			return "msg";	
		}
		
		boolean success=false;
		if(from.equalsIgnoreCase("db")){
			success = searchFromDb();
		}else{
			//数据校验
			if(this.limit==0){
				msg = "limit不能为空不能为0";
				hasError = true;
				return "msg";	
			}
			success = searchFromService();
		}
		if(!success){
			return "msg";//不成功
		}
		return "datalist";
	}
	
	/**
	 * 根据数据ID查询所有数据.
	 * 必需参数:nodeId,formId,dataId
	 * 可选参数：callback,ext
	 * @return
	 */
	public String getDataById(){
		//数据校验
		if(this.nodeId==0 || this.formId==0 || this.dataId==0){
			msg = "nodeId、formId、dataId不能为空不能为0";
			hasError = true;
			return "msg";	
		}
		
		boolean success=searchById();
		
		if(!success){
			return "msg";//不成功
		}
		return "data";
	}
	
	private boolean searchById(){
		try {
			ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
			Form2Plugin form2 = (Form2Plugin) pf.getP("form2");
			Map<String,Object> recordMap = form2.getData(nodeId, formId, dataId);
			this.recordJson = new JSONObject(recordMap).toString();
		} catch (Exception e) {
			msg = e.toString();
			log.error(e.toString());	
			hasError = true;
			return false;
		}
		
		return true;
	}
	/**
	 * 从数据库搜索
	 * @return
	 * @throws Exception 
	 */
	private boolean searchFromDb(){
		if(sqlPreventInjection()){
			msg = "存在不安全的sql注入。where:" + where + ";fields:" + fields;
			log.error(msg);	
			hasError = true;
			return false;
		}
		
		try{
			Map<String,String> sqlMap = buildSql();
			this.recordCount = ((Long)MySQLHelper.ExecuteSql(sqlMap.get("sqlTotal"), null).get(0).get("totalCount")).intValue();
			
			Vector<Map<String,Object>> records=MySQLHelper.ExecuteSql(sqlMap.get("sql"), null);
			this.recordJson = new JSONArray(records).toString();
		} catch (Exception e) {
			msg = e.getMessage();
			log.error(e.toString());	
			hasError = true;
			return false;
		}
		
		return true;
	}
	/**
	 * 判断sql是否存在不安全注入
	 * @return
	 */
	private boolean sqlPreventInjection(){
		Pattern p=Pattern.compile("update|insert|delete",Pattern.CASE_INSENSITIVE);
		return  p.matcher(this.where).find() || p.matcher(this.fields).find();
	}
	/**
	 * 检查fields是否合法，是否存在不能被查询到的字段
	 * @param fromType
	 * @return
	 */
	private boolean checkReturnFields(String fromType){
		
		//TODO
		
		return true;
	}
	
	private Map<String,String> buildSql(){
		Map<String,String> sqlMap = new HashMap<String,String>();
		String where = StringUtil.isBlank(this.where)?"":"where " + this.where;
		String sorts = StringUtil.isBlank(this.sorts)?"":"order by " + this.sorts;
		String limit = this.limit==0?"":"limit " + this.start + "," + this.limit;
		
		String sqlTotal = String.format("select count(*) totalCount from frm_%d %s", this.formId,where);
		String sql = String.format("select %s from frm_%d %s %s %s", this.fields,this.formId,where,sorts,limit);
		
		sqlMap.put("sqlTotal", sqlTotal);
		sqlMap.put("sql", sql);
		
		return sqlMap;
	}
	
	/**
	 * 从数据引擎搜索
	 * @return
	 * @throws Exception 
	 */
	private boolean searchFromService(){
		try {
			ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
			this.searchPlugin = (SearchPlugin) pf.getP("search");
			Page dataPage = this.searchPlugin.getDataByQ(nodeId, formId, q, fields, sorts, start, limit);
			this.recordCount = dataPage.getTotalCount();
			List<Map<String, String>>  dataList = dataPage.getResult();
			this.recordJson  = new JSONArray(dataList).toString();
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.toString();
			log.error(e.toString());	
			hasError = true;
			return false;
		}
		return true;
	}

	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
		/*
		try {
			fconf = FormConfig.getInstance(nodeId, formId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getSorts() {
		return sorts;
	}

	public void setSorts(String sorts) {
		this.sorts = sorts;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getRecordJson() {
		return recordJson;
	}

	public void setRecordJson(String recordJson) {
		this.recordJson = recordJson;
	}

	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	
	
}
