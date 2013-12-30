package com.me.GCDP.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.dynamic.DataUtil;
import com.me.GCDP.dynamic.ListUtil;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class DataAction extends ActionSupport {

	/**
	 * 通过配置自动实现数据存储和列表加载的
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(DataAction.class);
	private ScriptService scriptService = null;
	private String ids;
	public ScriptService getScriptService() {
		return scriptService;
	}
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}
	public Integer getFormId() {
		return formId;
	}
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	public Integer getListId() {
		return listId;
	}
	public void setListId(Integer listId) {
		this.listId = listId;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	private String script;
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	private String template;
	private Integer id;
	private Integer nodeId;
	private Integer formId;
	private Integer listId;
	private boolean error = false;
	private String msg;
	public int start=0;
	private List<Map<String,Object>> listData;
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
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int limit=10;
	public String where;
	public String sort;
	public int recordCount;
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public ListUtil lh;
	private Map<String, String[]> form;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;		
	}
	@SuppressWarnings("unchecked")
	public String save()
	{
		try
		{
			ActionContext context = ActionContext.getContext();
			HttpServletRequest request = (HttpServletRequest) context
					.get(ServletActionContext.HTTP_REQUEST);
			this.form = request.getParameterMap();
			id = DataUtil.saveData(form, formId, id);
		}
		catch (Exception e) {
			error = true;
			msg="保存出错:"+e.getMessage();
			log.error("保存表单出错"+formId+","+id+e.toString());
		}
		return "save";
	}
	@SuppressWarnings("unchecked")
	public String list() throws Exception
	{
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(ServletActionContext.HTTP_REQUEST);
		this.form = request.getParameterMap();
		lh = new ListUtil(listId,form);
		lh.initParms(where, sort, start, limit);
		recordCount = lh.getTotalNum();
		listData = lh.getData();
		return "list";
	}
	public String getListData() throws JSONException
	{
		
		try {			
			 if(listData!=null)
			 {
				 return new JSONArray(listData).toString();
			 }
			 else
			 {
				 return "[]";
			 }
		} catch (Exception e) {
			msg = e.getMessage();
			log.error(e);
			error = true;
			return "[]";
		}
	}
	public String getItemData()
	{
		try {
			Map<String,Object> data = DataUtil.getData(formId, id);
			if(!data.containsKey("id"))
			{
				data.put("id", id);
			}
			JSONObject js = new JSONObject(data);
			return js.toString();
		} catch (Exception e) {
			error = true;
			msg = "获取数据出错:fromId"+formId+","+id+e.getMessage();
			log.error(e);
			return "";
		}
	}
	public String getDynamicPageData()
	{
		try {
			Map<String,Object> data = DataUtil.getData(formId, id);
			if(!data.containsKey("id"))
			{
				data.put("id", id);				
			}
			template = scriptService.openLatest(nodeId,ScriptType.dynPage_template, id.toString());
			script = scriptService.openLatest(nodeId,ScriptType.dynPage, id.toString());
			
			data.put("script", script);
			data.put("template", template);
			JSONObject js = new JSONObject(data);
			return js.toString();
		} catch (Exception e) {
			error = true;
			msg = "获取数据出错:fromId"+formId+","+id+e.getMessage();
			log.error(e);
			return "";
		}
	}
	public String dycPageData()
	{		
		return "dycPageData";
	}
	public String saveDycPage()
	{
		save();		
		try {
            if(StringUtils.isNotBlank(template))
			    scriptService.saveDebug(nodeId, template, ScriptType.dynPage_template, id.toString());
            if(StringUtils.isNotBlank(script))
			    scriptService.saveDebug(nodeId, script, ScriptType.dynPage, id.toString());

		} catch (Exception e) {
			error = true;
			msg="保存脚本和模板出错:"+e.getMessage();
			log.error("保存出错脚本和模板出错"+formId+","+id+e.toString());
		}
		return "saveDycPage";
	}
	public String data()
	{
		return "data";
	}
	public String getMsg() {
		return msg;
	}
	public boolean getError() {
		return error;
	}
	public String delete()
	{
		try
		{
			lh = new ListUtil(listId,null);
			if(ids!=null&&(!ids.equals("")))
			{	
				String[] idArr = ids.split(",");
				for(String s:idArr)
				{	int iid = Integer.parseInt(s);
					lh.delete(iid);
				}
			}
			else
			{
				if(id!=0)
				{
					lh.delete(id);
				}
			}
		}
		catch(Exception e)
		{
			error = true;
			msg = "删除数据出错:"+id+","+e.getMessage();
			log.error(msg+e.toString());
		}
		return "delete";
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}

}
