package com.me.GCDP.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.me.json.JSONObject;

public class ListModel {
private String sql;
private List<String> listFields;
private List<String> searchFields;
private String tableName;
private String listName;
private String filter;
private Integer listId;
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<String> getListFields() {
		return listFields;
	}
	public void setListFields(String listFields) {
		this.listFields = new ArrayList<String>();
		String[] arr = listFields.split(",");
		for(String f:arr)
		{
			this.listFields.add(f);
		}
	}
	public List<String> getSearchFields() {
		return searchFields;
	}
	public void setSearchFields(String searchFields) {
		this.searchFields = new ArrayList<String>();
		String[] arr = searchFields.split(",");
		for(String f:arr)
		{
			this.searchFields.add(f);
		}
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public Integer getListId() {
		return listId;
	}
	public void setListId(Integer listId) {
		this.listId = listId;
	}
	public ListModel(JSONObject js)
	{
		this.setSql(getJsonData(js,"sql"));
		this.setListFields(getJsonData(js,"returnField"));
		this.setSearchFields(getJsonData(js,"searchField"));
		this.setTableName(getJsonData(js,"tableName"));
		this.setListName(getJsonData(js,"listName"));
		this.setFilter(getJsonData(js,"filter"));
		String sid = getJsonData(js, "id");
		this.listId= sid.equals("")?0:Integer.parseInt(sid);
	}
	private String getJsonData(JSONObject js,String key)
	{
		try
		{
			if(js.has(key))
			{
				return js.getString(key);
			}
		}
		catch (Exception e) {
		}
		return "";
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
