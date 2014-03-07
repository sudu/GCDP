/*
 * Created on 2011-5-13
 *
 * TODO 与搜索服务交互的类，根据配置建索引，或者根据配置搜索数据
 */
package com.me.GCDP.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.search.util_V2.Page;
import com.me.GCDP.util.EscapeUnescape;
import com.me.GCDP.xform.FormConfig;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;


public class SearchHelper {
	public FormConfig config;
	public SearchService_V2 svr;
	public SearchService_V2 getSvr() {
		return svr;
	}
	public void setSvr(SearchService_V2 svr) {
		this.svr = svr;
	}
	
	public SearchHelper(){}
	
	private Integer nodeId=0;
	private static Log log = LogFactory.getLog(SearchHelper.class);
	public SearchHelper(int nodeId,FormConfig config,SearchService_V2 svr){
		this.config = config;
		this.svr = svr;
		this.nodeId = nodeId;
		this.nodeId = (nodeId==0?config.getNodeId():nodeId);
	}
	public SearchHelper(int nodeId,int formId,SearchService_V2 svr) throws JSONException, SQLException{
		this.config = FormConfig.getInstance(nodeId,formId);
		this.svr = svr;
		this.nodeId = config.getNodeId();
	}
	public SearchHelper(int nodeId,int formId) throws JSONException, SQLException
	{
		this.config = FormConfig.getInstance(formId,nodeId);
		this.nodeId = (nodeId==0?config.getNodeId():nodeId);
	}
	public boolean needPostData() throws JSONException
	{
		Map<String,JSONObject> fields = config.getFields();
		for(String key:fields.keySet())
		{
			JSONObject field = fields.get(key);
			int type = field.getInt("indexType");
			if(type>0)
			{
				return true;
			}
		}
		return false;
	}
	@SuppressWarnings("rawtypes")
	public boolean createCore(){
		String coreName = getCoreName();
		Map result = null;
		try {
			result = svr.updateCore(coreName, new JSONObject(config.getConfig()));
		} catch (JSONException e) {
			log.error("create core from config is wrong "+e.getMessage());
			e.printStackTrace();
		}
		if(result!=null&&result.get("status").equals("0")){
			return true;
		}
		return false;
	}
	public void createInedex()
	{
		createCore();
	}
	
	@SuppressWarnings("rawtypes")
	public void putData(List<Map> data){
		try{
			if(!needPostData()){
				return;
			}
			String coreName = getCoreName();
			svr.addToDB(coreName, data, config.getConfig());
		}catch(JSONException e){
			log.error("get the config form FormConfig is wrong || "+e.getMessage());
		}
	}
	public void putData(int id,Map<String,Object> data)
	{
		
		try{
			if(!needPostData()){
				return;
			}
			String coreName = getCoreName();
			svr.add(id, coreName, data, new JSONObject(config.getConfig()));
		}catch(JSONException e){
			log.error("get the config form FormConfig is wrong || "+e.getMessage());
		}
		
	}
	public Page getData(String wheres,String sorts,String fields,int start, int limit) throws JSONException{
		
		Map<String,String> condition = null;
		List<String> lstFields = null;
		Page page = new Page();
		int pageSize = limit;
		page.setPageSize(pageSize);
		page.setStart(start);
		String coreName = getCoreName();
		if(!"".equals(wheres) && wheres != null){
			condition=conditionMap2(wheres);
		}
		if(!"".equals(fields) && fields != null){
			lstFields = new ArrayList<String>();
			String[] r = fields.split(",");
			for(String rs:r){
				lstFields.add(rs);
			}
		}
		
		String sortF = "id";
		int sortType = 0;
		if(sorts != null && !sorts.equals("")){
			if(sorts.contains(" ")){
				sortF = sorts.split(" ")[0];
				if(sorts.split(" ")[1].equalsIgnoreCase("asc")){
					sortType = 1;
				}else{
					sortType = 0;
				}
			}else{
				sortF = sorts;
			}
		}
		
		return svr.searchByPage(page,0,condition,lstFields,sortF,coreName,sortType);
	}

	private String getCoreName(){
		return "nodeId_"+nodeId+"_from_"+config.getFormId();
	}
	
	/*
	 * HAN XIANQI
	 */
	public static String processSort(String sort)
	{
		String sortStr = "";
		
		// sort = "field":"id", "order":"desc" --> id desc[, price asc]
		
		if (sort != null && sort.length() > 0)
		{
			try
			{
				JSONArray jsonArray = new JSONArray(sort);
				StringBuffer sb = new StringBuffer(); 
				for (int index = 0; index < jsonArray.length(); index++)
				{
					// process every field
					JSONObject jsonObj = jsonArray.getJSONObject(index);
					
					sb.append(jsonObj.get("field"));
					sb.append(" ");
					sb.append(jsonObj.get("order"));
					// deal with comma
					if (jsonArray.length() > 1 && index != jsonArray.length() - 1)
					{
						sb.append(",");
					}
				}
				sortStr = sb.toString();
			}
			catch (JSONException ex)
			{
				log.error("searching param 'sort': conversion from string to JSONArray|JSONObject failed.");
			}
		}
		return sortStr;
	}
	
	/**
	 * HAN XIANQI
	 */
	public static String processSearchResult(List<Map<String,String>> searchResult)
	{
		String result = "";
		
		JSONArray jsonArray = new JSONArray(searchResult);
		result = jsonArray.toString();
		
		return result;
	}
	
	/**
	 * HAN XIANQI
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> processDocData(String docData)
	{
		List<Map<String,Object>> jsonObjs = new ArrayList<Map<String,Object>>();
		
		JSONArray jsonArray = null;
		
		try
		{
			jsonArray = new JSONArray(docData); 
			
			for (int index = 0; index < jsonArray.length(); index ++)
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj = jsonArray.getJSONObject(index);
				jsonObjs.add(jsonObj.toMap());
			}
			
		}
		catch (JSONException ex)
		{
			log.error("save param 'docData': conversion from string to JSONArray failed： " + ex.getMessage());
		}
		
		return jsonObjs;
	}
	
	public static String assembleSearchInfo(String coreName, String condition, String returnFiled, String sortStr, int start, int limit)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("coreName: ").append(coreName);
		sb.append("| condition: ").append(condition);
		sb.append("| returnFiled: ").append(returnFiled);
		sb.append("| sortStr: ").append(sortStr);
		sb.append("| start: ").append(start);
		sb.append("| limit: ").append(limit);
		return sb.toString();
	}
	
	public Page getDataByQ(String conditionQ,String sorts,String fields,int start, int limit) throws JSONException{
		
		List<String> lstFields = null;
		Page page = new Page();
		int pageSize = limit;
		page.setPageSize(pageSize);
		page.setStart(start);
		String coreName = getCoreName();
		if(!"".equals(fields) && fields != null){
			lstFields = new ArrayList<String>();
			String[] r = fields.split(",");
			for(String rs:r){
				lstFields.add(rs);
			}
		}
		
		String sortF = "id desc";
		if(sorts != null && !sorts.equals("")){
			sortF = sorts;
		}
		return svr.searchByPageByQ(page,0,conditionQ,lstFields,sortF,coreName);
	}

	public Page getDataByParams(Map<String, String> params, String sorts,String fields,int start, int limit) throws JSONException{

		List<String> lstFields = null;
		Page page = new Page();
		int pageSize = limit;
		page.setPageSize(pageSize);
		page.setStart(start);
		String coreName = getCoreName();
		if(!"".equals(fields) && fields != null){
			lstFields = new ArrayList<String>();
			String[] r = fields.split(",");
			for(String rs:r){
				lstFields.add(rs);
			}
		}

		String sortF = "id desc";
		if(sorts != null && !sorts.equals("")){
			sortF = sorts;
		}
		return svr.searchByPageAndParams(page,0,params,lstFields,sortF,coreName);
	}

	//查询条件特殊字符处理
	public Map<String,String> conditionMap2(String condition){
		String[] c=condition.split(",");
		Map<String,String> m=new HashMap<String,String>();
		for(String con:c){
			con = con.trim();
			String value = EscapeUnescape.unescape(con.split(":")[0].trim());
			String field = con.split(":")[1].trim();
			String con1;
			if(value.endsWith(">") && value.startsWith("<")){//如果值被<>包起来则当作是一个完整的值，不做任何and 和 or 处理
				con1 = value.substring(1, value.length()-1);
			}else{
				con1 = value.replaceAll("\\s{1,}", " AND "+field+":");//对值里面含有空格的字符
			}
			log.info("**the search condition value is "+value);
			String con2 = con1.replaceAll("#", " OR "+field+":");
			//特殊字符处理   * ? :\
			m.put(con.split(":")[1], con2);
		}
		return m;
	};
//	//特殊符号 + - && || ! ( ) { } [ ] ^ ” ~ * ? :\ (\符号的替换一定要放到第一)
//	public static String stringTransferred(String str){
//		String tranList[] = {"\\+","\\-","\\&&","\\|\\|","\\(","\\)",
//				"\\{","\\}","\\[","\\]","\\\"","\\^","\\~","\\*","\\?","\\:"};
//		String tranedStr = str.replaceAll("\\\\", "\\\\\\\\");
//		for(int i = 0;i<tranList.length;i++){
//			String tra = tranList[i];
//			tranedStr = tranedStr.replaceAll(tra, "\\"+tra);
//		}
//			return tranedStr;
//	}
//
//	public static void main(String[] args){
//		String str = "大家都说\\我很好:\"人们才是最好的\"。&&谁说的+你-我||都可以(什么都行),{你说呢}，[心情好]，~我~你^他*我们?";
//		System.out.println(stringTransferred(str)) ;
//		
//		
//	}

}
