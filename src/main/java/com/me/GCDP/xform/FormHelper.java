package com.me.GCDP.xform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.source.SubscribeService;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.UserLogUtil;
import com.me.GCDP.util.env.DbEnv;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.CmppDBPlugin;
import com.me.GCDP.search.SearchService_V2;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;

/*
 *@version 1.1 将cmppDBService替换成了cmppDBPlugin by chengds at 2013/8/28
 */
public class FormHelper {
	private Integer nodeId;
	public Map<String, Object[]> getForm() {
		return form;
	}

	public void setForm(Map<String, Object[]> form) {
		this.form = form;
	}
	private Integer formId;
	private Integer id;
	private FormConfig fc;
	private ScriptService scriptService;
	private SearchService_V2 searchSvr;
	private SubscribeService subSvr;
	private CmppDBPlugin dbsvr;
	private Map<String,Object[]> form;
	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	public FormConfig getFc() {
		return fc;
	}

	public void setFc(FormConfig fc) {
		this.fc = fc;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public SearchService_V2 getSearchSvr() {
		return searchSvr;
	}

	public void setSearchSvr(SearchService_V2 searchSvr) {
		this.searchSvr = searchSvr;
	}

	public SubscribeService getSubSvr() {
		return subSvr;
	}

	public void setSubSvr(SubscribeService subSvr) {
		this.subSvr = subSvr;
	}

	public CmppDBPlugin getDbsvr() {
		return dbsvr;
	}

	public void setDbsvr(CmppDBPlugin cmppDBPlugin) {
		this.dbsvr = cmppDBPlugin;
	}
	private static Log log = LogFactory.getLog(FormHelper.class);
	public static int createFormConfig(ConfigParser conf,Integer nodeid) throws SQLException, JSONException, IOException, TemplateException
	{
		if(nodeid==null)
		{
			nodeid=0;
		}
		JSONObject parseResult = conf.doParseConfig();
		String formName = conf.getFormName();
		JSONObject cf = new JSONObject();	
		JSONObject fieldsConfig = parseResult.getJSONObject("fieldsConfig");
		fieldsConfig.put("fields", parseResult.getJSONArray("allFields"));
		
		cf.put("searchConfig",parseResult.getJSONObject("searchConfig"));
		cf.put("fieldsConfig",fieldsConfig);
		
		String formConfig = parseResult.getJSONObject("formConfig").toString();
		

		int formId = FormConfig.createFormConfig(formName, cf.toString(), nodeid);		
		//保存view的config
		int viewId = ViewConfig.createView(formId,formName,formConfig);		
		//保存list
		JSONObject listCfgJson = parseResult.getJSONObject("listConfig");
		listCfgJson.put("viewId", viewId);
		String listConfigStr = listCfgJson.toString();
		ListConfig.createConfig(formId, formName, listConfigStr);		
		FormConfig cfg = FormConfig.getInstance(nodeid,formId);
		TableSchemaHelper.createTable(cfg);
		return formId;
	}
	
	public Map<String,Object> getDataFromNosql() throws JSONException, SQLException
	{
		//从nosql获取数据
		Map<String,Object> rtn = new HashMap<String, Object>();
		Vector<ControlDbItem> items = fc.getNosqlItem();
		for(ControlDbItem item:items)
		{
			String key = item.getFieldName();		
			rtn.put(key, dbsvr.getString(nodeId+"-"+formId+"-"+id+"-"+key));
		}
		return rtn;
	}
	public  Map<String,Object> getData()
	{
		Map<String,Object> rtn = new  HashMap<String, Object>();
		try
		{
			String sql = "select * from "+getTableName()+" where id="+id;			
			Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				rtn.putAll(data.get(0));
			}
			rtn.putAll(getDataFromNosql());
			return rtn;
		}
		catch(Exception ex)
		{
			log.error(ex.getMessage());
		}
		return null;
	}
	public static Map<String,Object> getData(Map<String, Object[]> form,Map<String,Object> data)
	{
		Map<String,Object> rtn = new HashMap<String, Object>();
		rtn.putAll(data);
		for(String key:data.keySet())
		{
			String fkey = "xform."+key;
			if(form.containsKey(fkey))
			{
				rtn.put(key, form.get(fkey)[0]);
			}
		}
		return rtn;
	}
	public  List<String> getFields(int viewId) throws SQLException, JSONException
	{
		List<String> rtn = new ArrayList<String>();
		Vector<ControlDbItem> items = fc.getSaveItem();
		for(ControlDbItem item:items)
		{
			String key=item.getFieldName();
			rtn.add(key);
		}
		return  rtn;
	}
	public  Map<String,Object> getPostData(int viewId) throws SQLException, JSONException
	{
		Map<String,Object> rtn = new HashMap<String, Object>();
		if(form==null)
		{
			return rtn;
		}
		List<String> fields = getFields(viewId);
		for(String f : fields)
		{
			String key= "xform."+f;
			if(!key.equals(""))
			{
				Object[] values = form.get(key);
				if(values!=null)
				{
					rtn.put(f, values[0]);				
				}
				else 
				{
					rtn.put(f,null);	
				}
			}
		}
		//rtn.putAll(getDataFromNosql(formId,id));
		return rtn;
	}
	public Map<String,Object> save(Map<String,Object> xform) throws Exception
	{
		return save(xform,0);
	}
	public Map<String,Object> save(Map<String,Object> xform,int viewId) throws Exception
	{
		Map<String, Object> dataPool = new HashMap<String, Object>();
		FormPlugin helper = new FormPlugin(formId, id, this);
		dataPool.put("nodeId", this.nodeId);
		dataPool.put("formId", formId);
		dataPool.put("id",id);
		dataPool.put("viewId", viewId);
		dataPool.put("form", form);
		dataPool.put("helper", helper);
		dataPool.put("verify", true);
        dataPool.put("xform", xform);
       //dataPool.put("cookie", CookieUtil.);
        
        String action = id==0?"add":"update";
        dataPool.put("action", action);
       String userId="";
       String userName="";
       try
       {
    	   userId = AuthorzationUtil.getUserId();
    	   userName =  AuthorzationUtil.getUserName();
       }
       catch (Exception e) {
		log.error("获取用户失败:"+e.getMessage());
	}
       dataPool.put("userId", userId);
       dataPool.put("userName", userName);

		scriptService.run(nodeId, dataPool, ScriptType.form,formId.toString(), "0");
		boolean verify = (Boolean) dataPool.get("verify");
		if (!verify) {
			return dataPool;
		}
		xform = (Map<String, Object>) dataPool.get("xform");		
		//viewid为0，则xform里的所有数据库的字段均保存
		int rid = saveData(xform,0);
				
		if (id == 0) {
			id = rid;
			dataPool.put("id", id);
			helper.setDataId(id);
		}
		SearchHelper shp = new SearchHelper(nodeId,formId, searchSvr);
		Map<String,Object> data = getData();
		shp.putData(id,data);
		scriptService.run(nodeId, dataPool,ScriptType.form,formId.toString(), "1");
		log.info("用户user='"+userId+":"+userName+"' 修改表单formId="+formId+",id="+id);
		//-----记录用户修改的数据内容----------
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("userName", userName);
		map.put("nodeId",nodeId);
		map.put("formId", formId);
		map.putAll(xform);
		UserLogUtil.getInstance(nodeId.toString()).log(map);		
		publish(0);
		return dataPool;
	}


	public int saveData(Map<String, Object> xform) throws SQLException, JSONException
	{
		return saveData(xform,0);
	}
	public int saveData(Map<String, Object> xform,int viewId) throws SQLException, JSONException
	{ 
		int rtn = 0;
		Map<String,Object> filds = new HashMap<String, Object>();
		String table = getTableName();

		List<ControlDbItem> fArr = fc.getDbItem();
		for(ControlDbItem f : fArr)
		{
			String key = f.getFieldName();
			if(!key.equals(""))
			{
				Object value = xform.get(key);
				if(value!=null)
				{
					//System.out.println(key+":"+(values[0]));
					if(value.equals(""))
					{
						value = null;
					}
					filds.put(key,value);					
				}
			}
		}
		int nid = id;
		int type =2;
		if(id!=0)
		{
			update(filds, table);
			rtn = id;
		}
		else
		{
			rtn =  insert(filds, table);
			nid = rtn;
			type=1;
		}
		//处理保存到nosql的操作		
		Vector<ControlDbItem> nosqlItems = fc.getNosqlItem();
		for(ControlDbItem item:nosqlItems)
		{
			String key=item.getFieldName();
			if(!key.equals(""))
			{
				Object value = xform.get(key);
				if(value!=null)
				{
					//将数据存到nosql
					String sk = nodeId+"-"+formId+"-"+id+"-"+key;
					if(id==0)
					{
						sk = nodeId+"-"+formId+"-"+rtn+"-"+key;
					}
					dbsvr.put(sk, value);
				}
			}
		}
		Map<String,Object> pool = new HashMap<String, Object>();
		pool.put("data", xform);
		pool.put("type", type);
		pool.put("id", nid);
		try
		{
			subSvr.process(nodeId,formId,pool);
		}
		catch(Exception e)
		{
			log.error(e);
		}
		return rtn;
	}
	private void update(Map<String, Object> fields,String table) throws SQLException
	{
		Object[] parms;
		if(fields.isEmpty())
		{
			return;
		}
		else
		{
			parms = new Object[fields.size()];
		}
		String sql ="update %s set %s where id="+id;
		int k=0;
		String fs="";
		for(String key:fields.keySet())
		{
			if(fs.equals(""))
			{
				fs = "`"+key+"`=?";
			}
			else
			{
				fs = fs+",`"+key+"`=?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		String tb = table;
		if(this.fc.hasPartition())
		{
			Integer maxId = DbEnv.getInstance().getMaxBakId(table+"_bak");
			if(id<=maxId)
			{
				tb= table+"_bak";
			}
		}
		sql = String.format(sql,tb,fs);
		
		MySQLHelper.ExecuteNoQuery(sql, parms);
	}
	private static int insert(Map<String, Object> fields,String table) throws SQLException
	{
		Object[] parms;
		if(fields.isEmpty())
		{
			return 0;
		}
		else
		{
			parms = new Object[fields.size()];
		}
		String sql="";

		sql ="insert into %s(%s) values(%s)";
		String fs="";
		String vs="";
		int k=0;
		for(String key:fields.keySet())
		{
			if(fs.equals(""))
			{
				fs = "`"+key+"`";
				vs = "?";
			}
			else
			{
				fs = fs+",`"+key+"`";
				vs = vs+","+"?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		sql = String.format(sql, table,fs,vs);
		return Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));
	}
	public  String getTableName() throws SQLException, JSONException
	{
		return fc.getTableName();
	}
	public  void deleteData(int id) throws SQLException, JSONException
	{
		deleteData(id,getHelper(id).getData());
	}
	public void deleteData(int id,Map<String, Object> data) throws SQLException, JSONException
	{   
		String userId = "";
		String userName = "";
		try {
			userId = AuthorzationUtil.getUserId();
			userName = AuthorzationUtil.getUserName();
		} catch (Exception e) {
			log.error("获取用户失败:" + e.getMessage());
		}
		
		// -----记录用户修改的数据内容----------
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("userName", userName);
		map.put("nodeId", nodeId);
		map.put("formId", formId);
		map.put("data", data);
		map.put("deleteId", id);
		UserLogUtil.getInstance(Integer.toString(nodeId)).log(map);
		
		// 删除nosql对应字段值
		deleteDataFromNosql(id);
		
		// 删除搜索引擎对应值
		removeFromSearch(id);
		
		// 删除推送 （type=3 means 删除）
		Map<String, Object> dataPool = new HashMap<String, Object>();
		dataPool.put("data", data);
		dataPool.put("type", 3);
		dataPool.put("id", id);
		subSvr.process(nodeId, formId, dataPool);
		
		String tb = getTableName();
		if(fc.hasPartition())
		{
			Integer maxId = DbEnv.getInstance().getMaxBakId(tb+"_bak");
			if(maxId>=id)
			{
				tb = tb+"_bak";
			}
		}
		String sql = "delete from "+tb+" where id =?";
		Object[] parms = new Object[1];
		parms[0] = id;
		MySQLHelper.ExecuteNoQuery(sql, parms);

	}
	public void deleteDataFromNosql(int dataId) throws JSONException {
		Vector<ControlDbItem> items = fc.getNosqlItem();
		for (ControlDbItem item : items) {
			String key = item.getFieldName();
			this.dbsvr.remove(nodeId + "-" + formId + "-" + dataId + "-" + key);
		}
	}
	public void removeFromSearch(int dataId) throws JSONException, SQLException {
		String coreName = "nodeId_" + nodeId + "_from_" + formId;
		List<Object> list = new ArrayList<Object>();
		Integer _id = new Integer(dataId);
		list.add(_id.toString());
		SearchService_V2 searchService = (SearchService_V2) SpringContextUtil.getBean("searchService");
		Map<String, Object> map = searchService.deleteDate(list, coreName);
		if (map != null && map.get("status").equals("0")) {
			log.info("success doing deleteData " + coreName + " id = " + dataId);
		} else {
			log.info("fail to deleteData" + coreName + " ids = " + dataId);
		}
	}
	public static String getFormListConfig(Integer nodeid,Integer formid) throws SQLException, JSONException
	{
		if(nodeid==null)
		{
			nodeid=0;
		}
		if(formid==null)
		{
			formid=0;
		}
		String fWhere="";
		String sql;
		if(formid!=0){
			fWhere = " and formId=" + formid;
			sql ="select formId,name,tableName from cmpp_formConfig where nodeid in (0,"+nodeid+")" + fWhere + " order by formId desc";
		}else{
			sql ="select formId,name,tableName from cmpp_formConfig where nodeid ="+nodeid+ " order by formId desc";
		}
		JSONArray rtn = new JSONArray();
		
		Vector<Map<String,Object>> datas = MySQLHelper.ExecuteSql(sql, null);
		for(int i=0;i<datas.size();i++)
		{
			Map<String,Object> data = datas.get(i);
			JSONObject item = new JSONObject();
			int formId = (Integer) data.get("formId");
			item.put("id",formId);
			item.put("title",data.get("name"));
			item.put("tableName",data.get("tableName"));
			JSONArray viewArr = new JSONArray();
			sql ="select viewId,name from cmpp_viewConfig where formId="+formId + " order by viewId desc";
			Vector<Map<String,Object>> vdatas = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String,Object> vd:vdatas)
			{
				JSONObject view = new JSONObject();
				view.put("id",vd.get("viewId"));
				view.put("title",vd.get("name"));
				viewArr.put(view);
			}
			item.put("forms", viewArr);
			JSONArray listArr = new JSONArray();
			sql ="select listId,name from cmpp_listConfig where formId="+formId + " order by listId desc";
			Vector<Map<String,Object>> ldatas = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String,Object> ld:ldatas)
			{
				JSONObject list = new JSONObject();
				list.put("id",ld.get("listId"));
				list.put("title",ld.get("name"));
				listArr.put(list);
			}
			item.put("list", listArr);
			rtn.put(item);
		}
		return rtn.toString();
	}
	
	/*
	 * //根据表单试图模板配置
	 */	
	public static JSONObject getTemplateJson(int viewId) throws Exception{
		String sql;
		sql ="select config from cmpp_viewConfig where viewId="+viewId;
		Map<String,Object> data =  MySQLHelper.ExecuteSql(sql, null).get(0);
		String config = data.get("config").toString();
		JSONObject configJson = new JSONObject(config);
		JSONObject tplJson = configJson.getJSONObject("form").getJSONObject("template");
		return tplJson;
	}
	public int getFormLastId()
	{
		String sql="";
		try
		{
			String tbName = getTableName();
			sql = "select id from `"+tbName+"` order by id desc limit 1";
			Vector<Map<String,Object>> rtn = MySQLHelper.ExecuteSql(sql, null);
			if(rtn.size()>0)
			{
				return (Integer) rtn.get(0).get("id");
			}
		}
		catch(Exception ex)
		{
			log.error(sql+"获取表单最大ID出现错误"+ex.getMessage());
		}
		return 0;
	}
	public List<Integer> getFormIdList(int startId,int num)
	{
		List<Integer> rtn = new ArrayList<Integer>();
		String sql="";
		try
		{
			String tbName = getTableName();
			sql = "select id from `"+tbName+"` where id>="+startId+" order by id limit "+num;
			Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				for(Map<String,Object> d:data)
				{
				  rtn.add((Integer)d.get("id"));
				}			
			}
		}
		catch(Exception ex)
		{
			log.error(sql+"获取表单id列表出现错误"+ex.getMessage());
		}
		return rtn;
	}
	public Map<String,Object> preview(Map<String,Object> args,int tplId,RenderType type,Template2 template) throws Exception
	{		
		//ScriptHelper helper = new ScriptHelper(formId, id,this);
		if(template==null)
		{
			Map<String,String> preTagContent = null;
			if(form!=null)
			{
				if(form.get("idxId")!=null)
				{  
					preTagContent = new HashMap<String, String>();
					preTagContent.put(form.get("idxId")[0].toString(),form.get("idxData")[0].toString());
				}
			}
			//template = new Template(this,preTagContent,type);
			template = new Template2(fc, Integer.toString(tplId), preTagContent, type);
		}
		Map<String, Object> dataPool = new HashMap<String, Object>();
		
		dataPool.put("args", args);
		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		dataPool.put("id", id);
		dataPool.put("tplId", tplId);		
		dataPool.put("form", form);
		dataPool.put("renderType", type);
		dataPool.put("type", type);
		FormPlugin helper  = new FormPlugin(formId, id, this);
		helper.setForm(form);
		dataPool.put("helper",helper);
		dataPool.put("template", template);

		scriptService.run(nodeId, dataPool, ScriptType.form,formId.toString(),"2");
		return dataPool;
	}
	public Map<String,Object> preview(int tplId,RenderType type) throws Exception
	{
		return preview(null,tplId,type,null);
	}
	public Map<String,Object> runScript(String key,String ids) throws Exception
	{
		Map<String, Object> dataPool = new HashMap<String, Object>();
		dataPool.put("ids", ids);
		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		FormPlugin helper  = new FormPlugin(formId, id, this);
		dataPool.put("helper",helper);
		scriptService.run(nodeId, dataPool, ScriptType.form,formId.toString(),key);
		return dataPool;
	}
	public Map<String,Object> publish(int tplId,boolean isPublish,Map<String,Object> pool) throws Exception
	{
		Map<String, Object> dataPool = new HashMap<String, Object>();
		FormPlugin helper = new FormPlugin(formId, id, this);
		dataPool.put("isPublish", isPublish);
		dataPool.put("helper", helper);		
		if(pool==null)
		{
			dataPool.put("formId", formId);
			dataPool.put("id", id);
			dataPool.put("tplId", tplId);
			dataPool.put("nodeId", nodeId);	
		}
		else
		{
			dataPool.putAll(pool);
		}						
		scriptService.run(nodeId, dataPool, ScriptType.form,formId.toString(),"3");
		return dataPool;		
	}
	public Map<String,Object> publish(int tplId) throws Exception
	{
		return publish(tplId,true,null);
	}
	public FormHelper(int nodeId,int formId,int id,Map<String,Object[]> form) throws JSONException, SQLException {
		this.formId = formId;	
		this.id = id;
		this.form = form;
		fc = FormConfig.getInstance(nodeId, formId);
		this.nodeId = fc.getNodeId();
	}
	public FormHelper(int formId,int id) throws JSONException, SQLException {
		this.id = id;
		this.formId = formId;
		this.fc = FormConfig.getInstance(0, formId);
		this.nodeId = fc.getNodeId();
	}
	private FormHelper(int id,FormConfig fc) throws JSONException, SQLException {
		this.formId = fc.getFormId();
		this.nodeId = fc.getNodeId();
		this.id=id;
		this.fc = fc;
	}
	public FormHelper getHelper(int id) throws JSONException, SQLException {
		if(this.id==id)
		{
			return this;
		}
		else
		{
			FormHelper rtn = new FormHelper(id, this.getFc());
			rtn.setDbsvr(this.dbsvr);
			rtn.setScriptService(this.scriptService);
			rtn.setSubSvr(this.subSvr);
			rtn.setSearchSvr(this.searchSvr);
			return rtn;
		}
	}
	public FormHelper getHelper(int formId,int id) throws JSONException, SQLException {
		FormHelper rtn = new FormHelper(this.nodeId,formId,id,null);
		rtn.setDbsvr(this.dbsvr);
		rtn.setScriptService(this.scriptService);
		rtn.setSubSvr(this.subSvr);
		rtn.setSearchSvr(this.searchSvr);
		return rtn;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
}
