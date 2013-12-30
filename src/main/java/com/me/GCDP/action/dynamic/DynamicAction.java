package com.me.GCDP.action.dynamic;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.mapper.DynPageStatusMapper;
import com.me.GCDP.mapper.DynamicConfigMapper;
import com.me.GCDP.model.DynPageStatus;
import com.me.GCDP.model.DynamicConfig;
import com.me.GCDP.model.PageCondition;
import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.Page;
import com.me.GCDP.util.chart.ChartService;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONArray;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class DynamicAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(DynamicAction.class);
	
	private Integer nodeId;
	private Integer type=1;
	private boolean error = false;
	
	private String total="0";
	private String msg="";
	private String ids;
	private Integer id;
	private ScriptService scriptService;
	private DynamicConfigMapper<DynamicConfig> dynamicConfigMapper = null;
	private DynPageStatusMapper<DynPageStatus> dynPageStatusMapper = null;
	private int start=0;
	private int limit=20;
	private List<Map<String, Object>> data;
    private String filterTxt;
	private String filterValue;
	private Page<DynPageStatus> pageListPage;
	private String requireCountChart;
	private String respTimeChart;
	private String startTime;
	private String endTime;
	private String dynPage = ""; // preview DYN PAGE

	private void saveRelation(String type) throws SQLException
	{
		if(ids.equals(""))
		{
			return;
		}
		String sql = "select id from cmpp_dynamic_r where dynId="+id+" and id in("+ids+") and type='"+type+"'";
		List<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
		List<Integer> lst = new ArrayList<Integer>();
		for(Map<String,Object> d:data)
		{
			Integer iid = (Integer) d.get("id");
			lst.add(iid);
		}
		//添加不存在的关系
		List<String> sqls= new ArrayList<String>();
		String[] arr = ids.split(",");
		for(String s:arr)
		{
			Integer iid =Integer.parseInt(s);
			if(!lst.contains(iid))
			{
				sqls.add("insert into cmpp_dynamic_r(dynId,id,type) value("+id+","+iid+",'"+type+"')");
			}
		}
		MySQLHelper.executeBatch(sqls);
	}
	private void deleteRalation(String type) throws SQLException
	{
		String sql = "delete from cmpp_dynamic_r where dynId="+id+" and type='"+type+"' and id in("+ids+")";
		MySQLHelper.ExecuteNoQuery(sql, null);
	}
	
	public List<Map<String, Object>> getData() {
		return data;
	}
	public String index()
	{
		return "index";
	}
	public String listData() throws SQLException
	{
		String sql = "select id,name,svrIp,status from cmpp_dynamicConfig where nodeId="+nodeId+" order by id limit "+start+","+limit;
		data = MySQLHelper.ExecuteSql(sql, null);
		return "listData";
	}
	public String getTotal() throws SQLException
	{
		String sql = "select count(*) num from cmpp_dynamicConfig where nodeId="+nodeId;
		List<Map<String, Object>> dd = MySQLHelper.ExecuteSql(sql, null);
		if(dd.size()>0)
		{
			total = dd.get(0).get("num").toString();
		}
		return total;
	}
	public String pushConfig()
	{
		try
		{
			String[] arr = ids.split(",");
			for(String sid:arr)
			{
				if(!sid.equals(""))
				{
					int configId = Integer.parseInt(sid);
					if(!pushConfig(configId))
					{
						error = true;
						break;
					}
				}
			}
		}
		catch(Exception ex)
		{
			error = true;
			msg = ex.getMessage();
			log.error("推送配置信息失败"+ex.toString());
		}
		return "pushConfig";
	}
	public String pushPage()
	{
		try {
			Map<String,Object> data = getConfig(id);
			String root = getConfigRoot(data);
			String sql = "select * from cmpp_dynamicPage where nodeId="+nodeId+" and id in("+ids+")";
			if(ids.equals(""))
			{
				sql ="select * from cmpp_dynamicPage where nodeId="+nodeId;
			}
			List<Map<String,Object>> pages = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String,Object> page:pages)
			{
				pushPage(page,root);
			}
			saveRelation("page");
		} catch (SQLException e) {
			error = true;
			msg = e.getMessage();
			log.error("推送页面失败"+e.toString());
			
		}
		return "pushPage";
	}
	private void pushPage(Map<String,Object> page,String root)
	{
		String em = "同步页面"+page.get("id")+"."+page.get("name")+"失败.";		
		try
		{
			String url =root+"dynPageLibAdd.do";
			Map<String,String> dataMap = new HashMap<String, String>();
			if(type==0)
			{
				em = "删除页面"+page.get("id")+"."+page.get("name")+"失败.";
				url =root+"dynPageLibDel.do";
				dataMap.put("nodeId", nodeId.toString());
				dataMap.put("dynPageID", page.get("id").toString());
			}
			else
			{
				dataMap.put("nodeId", nodeId.toString());
				dataMap.put("dynPageID", page.get("id").toString());
				dataMap.put("url", page.get("url").toString());
				String template = scriptService.open(nodeId,ScriptType.dynPage_template, page.get("id").toString());
				String script = scriptService.open(nodeId,ScriptType.dynPage, page.get("id").toString());
				script = scriptService.addCommonLib(script, nodeId);
				dataMap.put("script", script);
				dataMap.put("template", template);
				//初始化paramNames
				String params = page.get("params")==null?"":page.get("params").toString();
				String paramNames="";
				if(!params.equals(""))
				{
					JSONArray arr = new JSONArray(params);
					for(int i=0;i<arr.length();i++)
					{
						JSONObject json = arr.getJSONObject(i);
						String name = json.getString("name");
						paramNames = paramNames.equals("")?name:paramNames+","+name;
					}
				}
				dataMap.put("paramNames", paramNames);
			}
			Map<String,Object> rtn = HttpUtil.post(url, dataMap);
			String content = rtn.get("content").toString();
			JSONObject ret = new JSONObject(content);
			int success = ret.getInt("ret");
			if(success!=1)
			{
				error = true;
				String s = ret.has("msg")?ret.getString("msg"):"";
				msg = em+s;
			}
			log.info("dynPage : push finish , " + content );
		}
		catch (Exception e) {
			error = true;			
			msg = msg.equals("")?em+e.getMessage():msg+"\r\n"+em+e.getMessage();
			log.error(em+e.toString());
		}

	}
	public String pushTable()
	{
		try
		{
			Map<String,Object> data = getConfig(id);
			String root = getConfigRoot(data);
			String sql ="SELECT * FROM cmpp_formConfig WHERE nodeid="+nodeId+" and formId in("+ids+")";
			if(ids.equals(""))
			{
				sql ="SELECT * FROM cmpp_formConfig WHERE nodeid="+nodeId;
			}
			List<Map<String,Object>> tables = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String,Object> table:tables)
			{
				pushTable(table,root);
			}
			if(type==0)
			{
				deleteRalation("table");
			}
			else
			{
				saveRelation("table");
			}
		}
		catch (Exception e) {
			error = true;
			msg = e.getMessage();
			log.error("推送表结构失败"+e.toString());
		}
		
		return "pushTable";
	}
	private void pushTable(Map<String,Object> table,String root)
	{
		String em = "同步表单"+table.get("formId")+"."+table.get("name")+"失败.";		
		try
		{
			String url =root+"form.do";
			Map<String,String> dataMap = new HashMap<String, String>();
			dataMap.put("nodeId", nodeId.toString());
			dataMap.put("tName", table.get("tableName").toString());
			dataMap.put("fieldConfig", table.get("config").toString());
			
//			System.out.println(url);
//			System.out.println(dataMap);
			
			Map<String,Object> rtn = HttpUtil.post(url, dataMap);
						
			String content = rtn.get("content").toString();
//			System.out.println(content);
			JSONObject ret = new JSONObject(content);
			int success = ret.getInt("ret");
			if(success!=1)
			{
				error = true;
				String s = ret.has("msg")?ret.getString("msg"):"";
				msg = em+s;
			}
		}
		catch (Exception e) {
			error = true;			
			msg = msg.equals("")?em+e.getMessage():msg+"\r\n"+em+e.getMessage();
			log.error(em+e.toString());
		}
	}
	private Map<String,Object> getConfig(int id) throws SQLException
	{
		String sql = "select * from cmpp_dynamicConfig where id="+id;
		return MySQLHelper.ExecuteSql(sql, null).get(0);
	}
	private String getConfigRoot(Map<String,Object> config)
	{
		return "http://"+config.get("svrIp")+"/"+config.get("svrPath")+"/dynamic/";
	}
	public boolean pushConfig(int id) throws Exception
	{
		Map<String,Object> data = getConfig(id);
		JSONObject extJs = new JSONObject();
		String ext = data.get("ext")==null?"":data.get("ext").toString();
		if(!ext.equals(""))
		{
			JSONArray arr = new JSONArray(ext);
			for(int i=0;i<arr.length();i++)
			{
				JSONObject json = arr.getJSONObject(i);
				String name = json.getString("name");
				String value = json.getString("desc");
				if(!extJs.has(name))
				{
					extJs.put(name, value);
				}
			}
		}		
		JSONObject config = new JSONObject(data);
		config.put("ext", extJs);
		String url = getConfigRoot(data)+"config.do";
		Map<String,String> dataMap = new HashMap<String, String>();
		dataMap.put("config", config.toString());
//		System.out.println(url);
//		System.out.println(config.toString());
		Map<String,Object> rtn = HttpUtil.post(url, dataMap);
		String content = rtn.get("content").toString();
//		System.out.println(content);
		JSONObject ret = new JSONObject(content);
		int success = ret.getInt("ret");
		if(success==1)
		{
			return true;
		}
		else
		{
			msg = ret.getString("msg");
			return false;
		}
		
	}
	
	public String dynStatus(){
//		System.out.println(nodeId);
		return "dynStatus";
	}
	
	public String pagedDynStatus(){
		PageCondition pc = new PageCondition();
		int totalCount = 0;
		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		
		pc.setFilterTxt("dynID");
		pc.setFilterValue(id+"");
		List<DynPageStatus> result = dynPageStatusMapper.getLastPages(pc);
		totalCount  = dynPageStatusMapper.getLastPageCount(pc);
		
		this.setPageListPage(new Page<DynPageStatus>(new JSONArray(result),totalCount));
		return "pagedDynStatus";
	}
	
	public String showPageStatusDetail(){
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date endTime = new Date();
		Date beginTime = new Date(endTime.getTime()-24*60*60*1000);
		DynPageStatus dp = new DynPageStatus();
		dp.setDynID(id);
		dp.setPageUrl(filterValue);
		dp.setStartTime(tempDate.format(beginTime));
		dp.setEndTime(tempDate.format(endTime));
		List<DynPageStatus> dpl = dynPageStatusMapper.getPageStatusByTime(dp);
		try {
			this.chartGen(dpl,filterValue);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return "pageStatusDetail";
	}
	
	public String showChart(){
		DynPageStatus dp = new DynPageStatus();
		dp.setDynID(id);
		dp.setPageUrl(filterValue);
		dp.setStartTime(startTime);
		dp.setEndTime(endTime);
		List<DynPageStatus> dpl = dynPageStatusMapper.getPageStatusByTime(dp);
		try {
			this.chartGen(dpl,filterValue);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	
		return "chart";
	}
	
	public String pagedDynPageStatus(){
		int totalCount = 0;
		PageCondition pc = new PageCondition();
		
		
		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		pc.setNodeId(id);
		pc.setFilterTxt("pageUrl");
		pc.setFilterValue(filterValue);
		
		List<DynPageStatus> result = dynPageStatusMapper.getAllPageStatus(pc);
		totalCount  = dynPageStatusMapper.getAllPageStatusCount(pc);
		
		this.setPageListPage(new Page<DynPageStatus>(new JSONArray(result),totalCount));
		
		return "pagedDynStatus";
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void chartGen(List<DynPageStatus> result,String pageUrl) throws ParseException{
		HttpSession session = ServletActionContext.getRequest().getSession();
//		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Map> data1 = new ArrayList<Map>();
		List<Map> data2 = new ArrayList<Map>();
		Map<String, Comparable> d1;
		Map<String, Comparable> d2;
		String xKey1 = "date";
		String xKey2 = "date";
		String yKey1 = "requireCount";
		String yKey2 = "respTime";
		
		
		
		for(DynPageStatus tmp: result){
			d1 = new HashMap<String, Comparable>();
			d2 = new HashMap<String, Comparable>();
			
			d1.put(xKey1,tmp.getIssueDate());
			d2.put(xKey2,tmp.getIssueDate());
			
			d1.put(yKey1, Double.parseDouble(tmp.getRequireCount()+""));
			d2.put(yKey2, Double.parseDouble(tmp.getRespTime()==null?"0":(tmp.getRespTime()+"")));
			
			data1.add(d1);
			data2.add(d2);
		}
		
		requireCountChart = ChartService.createLineChart(session,pageUrl,data1,new String[]{"Access Count (times)"},"date",xKey1,"Access Count",new String[]{yKey1},true,"yyyy-MM-dd HH:mm:ss","##",500,240);
		respTimeChart = ChartService.createLineChart(session,pageUrl,data2,new String[]{"ResponesTime (ms)"},"date",xKey2,"ResponesTime",new String[]{yKey2},true,"yyyy-MM-dd HH:mm:ss","##",500,240);
		
//		System.out.println(requireCountChart + "****" + respTimeChart);

	}
	
	// PREVIEW DYN PAGE
	@SuppressWarnings("unchecked")
	public String previewDynPage()
	{
		
		// 运行、 渲染 动态页面
		
		Map<String, Object> dataPool = new HashMap<String, Object>();
		
		// for test script & template
		// dataPool.put("test_id", "95872");
		
		// 获取Request中用户自己输入的name-value
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		Map<String, String[]> params = request.getParameterMap();
		Iterator<String> it = params.keySet().iterator(); 
		Integer nodeId = null, id = null;
		String template = null;
		while (it.hasNext())
		{
			String name = it.next();
			String value = ((String[]) params.get(name))[0];
			if ("nodeId".equals(name))
			{
				nodeId = Integer.valueOf(value);
			}
			else if ("id".equals(name))
			{
				id = Integer.valueOf(value);
			} 
			else
			{
				dataPool.put(name, value);
			}	
		}
		
		// 编译动态页面
		try 
		{
			scriptService.run(nodeId, dataPool, ScriptType.dynPage, id.toString());
		} 
		catch (Exception ex) 
		{
			error = true;
			msg = "编译动态页面出错: " + ex.toString();
			log.error("编译动态页面出错: " + nodeId + "_" + id + ", " + ex.toString());
			
			return "previewDynPage";
		}
		
		try
		{
			template = scriptService.open(nodeId, ScriptType.dynPage_template, id.toString());
			log.info("get saved template --> " + (template != null ? true : false));
			
			dynPage = FreeMarkerHelper.process2(template, dataPool);
			log.debug("render dynpage --> " + (dynPage != null ? true : false));
		}
		catch (Exception ex)
		{
			error = true;
			msg = "渲染动态页面出错: " + ex.toString();
			log.error("渲染动态页面出错: " + nodeId + "_" + id + ", " + ex.toString());
			
			return "previewDynPage";
		}
		
		return "previewDynPage";
	}
	
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public DynamicConfigMapper<DynamicConfig> getDynamicConfigMapper() {
		return dynamicConfigMapper;
	}
	public void setDynamicConfigMapper(DynamicConfigMapper<DynamicConfig> dynamicConfigMapper) {
		this.dynamicConfigMapper = dynamicConfigMapper;
	}

	public DynPageStatusMapper<DynPageStatus> getDynPageStatusMapper() {
		return dynPageStatusMapper;
	}
	public void setDynPageStatusMapper(DynPageStatusMapper<DynPageStatus> dynPageStatusMapper) {
		this.dynPageStatusMapper = dynPageStatusMapper;
	}
	public ScriptService getScriptService() {
		return scriptService;
	}
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean getError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getFilterTxt() {
		return filterTxt;
	}
	public void setFilterTxt(String filterTxt) {
		this.filterTxt = filterTxt;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	public Page<DynPageStatus> getPageListPage() {
		return pageListPage;
	}
	public void setPageListPage(Page<DynPageStatus> pageListPage) {
		this.pageListPage = pageListPage;
	}
	public String getRequireCountChart() {
		return requireCountChart;
	}
	public void setRequireCountChart(String requireCountChart) {
		this.requireCountChart = requireCountChart;
	}
	public String getRespTimeChart() {
		return respTimeChart;
	}
	public void setRespTimeChart(String respTimeChart) {
		this.respTimeChart = respTimeChart;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDynPage() {
		return dynPage;
	}
	public void setDynPage(String dynPage) {
		this.dynPage = dynPage;
	}
	

}
