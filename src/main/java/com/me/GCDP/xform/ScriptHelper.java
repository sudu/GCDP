/*
package com.me.GCDP.xform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.me.GCDP.nosql.client.CmppDBClientService;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.search.SearchService;
import com.me.GCDP.source.SubscribeService;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.version.VersionHelper2;

public class ScriptHelper extends ScriptPlugin{
	private static Log log = LogFactory.getLog(ScriptHelper.class);
	private int formId;
	private int id;
	ScriptService scriptService;
	public ScriptService getScriptService() {
		return scriptService;
	}
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}
	public SearchService getSearchSvr() {
		return searchSvr;
	}
	public void setSearchSvr(SearchService searchSvr) {
		this.searchSvr = searchSvr;
	}
	public SubscribeService getSubSvr() {
		return subSvr;
	}
	public void setSubSvr(SubscribeService subSvr) {
		this.subSvr = subSvr;
	}
	SearchService searchSvr;
	SubscribeService subSvr;
	CmppDBClientService dbSvr;
	private int nodeId;
	FormHelper helper;
	private Map<String,Object[]> form=null;
	public ScriptHelper(int formId,int id,FormHelper helper)
	{
		this.formId = formId;
		this.id = id;
		this.helper = helper;
	}
	public void setDataId(int dataId)
	{
		this.id = dataId;
	}
	public TemplateModel getDefaultTemplate()
	{
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where dataFormId="+formId+" and dataId in(0,"+id+") and enable=1 order by dataId desc,id desc limit 1";
		//System.out.println(sql);
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				return formatTemplate(data.get(0));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Error(e);
		}
		
		return null;
	}
	public TemplateModel getTemplate()
	{
		return getDefaultTemplate();
	}
	public TemplateModel getTemplate(int tplid)
	{
		TemplateModel rtn =null;
		if(tplid==0)
		{
			rtn = getDefaultTemplate();
		}
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where id="+tplid;
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				rtn = formatTemplate(data.get(0));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Error(e);
		}
		
		return rtn;
	}
	public List<TemplateModel> getTemplateList()
	{
		List<TemplateModel> rtn = new ArrayList<TemplateModel>();
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where dataFormId="+formId+" and dataId in(0,"+id+") and enable=1 order by dataId desc,id desc";
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String, Object> d:data)
			{
				rtn.add(formatTemplate(d));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Error(e);
		}
		return rtn;
	}
	public int copyTemplate(TemplateModel tpl) throws Exception
	{
		return TemplateBuilder.copy(tpl, id,this);
	}
	public int copyTemplate(int tplId) throws Exception
	{
		TemplateModel tpl = getTemplate(tplId);
		return TemplateBuilder.copy(tpl, id,this);
	}
	private TemplateModel formatTemplate(Map<String,Object> data)
	{
		TemplateModel rtn = new TemplateModel();
		rtn.setId((Integer) data.get("id"));
		rtn.setName((String) data.get("name"));
		rtn.setContent((String) data.get("content"));
		rtn.setEnable((Integer) data.get("enable"));
		rtn.setDataId((Integer) data.get("dataId"));
		rtn.setFormId((Integer) data.get("dataFormId"));
		return rtn;
	}
	private void Error(Exception e)
	{
		log.error("获取模板出错formId="+formId+",id="+id+e.getMessage());
	}
	public Map<String,Object> getData(int id) throws JSONException, SQLException
	{
		Map<String,Object> data= helper.getHelper(id).getData();
		if(form!=null)
		{
			return FormHelper.getData(form, data);
		}
		return data;
	}
	public Map<String,Object> getData() throws JSONException, SQLException
	{
		return getData(id);
	}
	public int getMaxId()
	{
		return helper.getFormLastId();
	}
	public Map<String,Object> getLastData() throws JSONException, SQLException
	{
		return getData(getMaxId());
	}
	public HashMap<String,Object> initMap()
	{
		return new HashMap<String, Object>();
	}
	public List<Object> initList()
	{
		return new ArrayList<Object>();
	}
	public Map<String,Object> render(int id,int tplId) throws Exception
	{
		return render(id, tplId, RenderType.publish);	
	}
	public Map<String,Object> render() throws Exception
	{
		return render(id,0,RenderType.publish);
	}
	public Map<String,Object> render(int id) throws Exception
	{
		return render(id,0,RenderType.publish);
	}
	public Map<String,Object> render(int id,int tplId,RenderType type) throws Exception
	{
		FormHelper hp = helper.getHelper(id);
		return hp.preview(tplId,type);	
	}
	public Map<String,Object> publish(int id,int tplId) throws Exception
	{
		return helper.getHelper(id).publish(tplId);
	}
	public Map<String,Object> publish(int id) throws Exception
	{
		return helper.getHelper(id).publish(0);
	}
	public Map<String,Object> publish() throws Exception
	{
		return helper.publish(0);
	}
	public int saveData(int id,Map<String,Object> data) throws SQLException, JSONException
	{
		return helper.getHelper(id).saveData(data);
	}
	public int saveData(Map<String,Object> data) throws SQLException, JSONException
	{
		return helper.saveData(data);
	}
	public Map<String,Object> save(int id,int viewId,Map<String,Object> data) throws Exception
	{
		return helper.getHelper(id).save(data,viewId);		
	}
	public Map<String,Object> runScript(String key,String ids) throws Exception
	{
		return helper.runScript(key, ids);
	}
	public Map<String,Object> save(int id,Map<String,Object> data) throws Exception
	{
		data.remove("id");
		return helper.getHelper(id).save(data,0);		
	}
	public void setForm(Map<String,Object[]> form) {
		this.form = form;
	}
	public Map<String,Object[]> getForm() {
		return form;
	}
	public ScriptHelper getHelper(int formId,int id) throws JSONException, SQLException
	{
		FormHelper hp = this.helper.getHelper(formId,id);
		return new ScriptHelper(formId, id, hp);
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		init(nodeId,formId,id,null);
	}
	public void init(int nodeId,int formId,int id)
	{
		init(nodeId,formId,id,null);
	}
	public void init(int nodeId,int formId,int id,Map<String,Object[]> form)
	{
		this.nodeId = nodeId;
		this.formId = formId;
		this.id = id;
		this.form = form;
		initFormHelper(nodeId,formId,id,form);
	}
	private void initFormHelper(int nodeId,int formId,int id,Map<String,Object[]> form)
	{
		try
		{
			this.helper = new FormHelper(nodeId,formId,id,form);
			helper.setDbsvr(dbSvr);
			helper.setSearchSvr(searchSvr);
			helper.setScriptService(scriptService);
			helper.setSubSvr(subSvr);
		}
		catch(Exception e)
		{
			log.error("初始化formhelper失败formId="+formId+e.getMessage());
		}	
	}
	public VersionHelper2 getVersionHelper2(String key,String userName,String xformDataStr) {
		VersionHelper2 fh = new VersionHelper2(key,userName,xformDataStr);
		return fh;
	}

}
*/