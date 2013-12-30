package com.me.GCDP.action.xform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.search.SearchService_V2;
import com.me.GCDP.search.util_V2.Page;
import com.me.json.JSONArray;
import com.opensymphony.xwork2.ActionSupport;

public class SearchAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SearchAction.class);
	private String msg = "";
	private boolean hasError = false;
	private int formId;
	private int nodeId;
	private int limit;
	private int start=0;
	private String condition;
	private Page page;
	private String fields;
	SearchHelper sh;
	SearchService_V2 searchService;
	public void setSearchService(SearchService_V2 searchService)
	{
		this.searchService = searchService;
	}
	public String getMsg() {
		return msg;
	}
	public boolean getHasError() {
		return hasError;
	}
	public void setFormId(int formId) {
		this.formId = formId;
	}
	public int getFormId() {
		return formId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getNodeId() {
		return nodeId;
	}
	public String search()
	{
		this.page = getPage();
		return "search";
	}
	public Page getPage()
	{
		try{
		sh = new SearchHelper(nodeId, formId,searchService);
		return sh.getData(condition, "",fields, start, limit);
		}
		catch (Exception e) {
			{
				hasError = true;
				log.error(e);
				msg = "获取数据失败:"+e.getMessage();
				return null;
			}
		}
	}
	public String getData()
	{
		return (new JSONArray(page.getResult()).toString());
	}
	public int getCount()
	{
		return this.page.getTotalCount();
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getCondition() {
		return condition;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getLimit() {
		return limit;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStart() {
		return start;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getFields() {
		return fields;
	}
	
}
