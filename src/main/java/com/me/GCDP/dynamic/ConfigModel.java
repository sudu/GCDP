package com.me.GCDP.dynamic;

import com.me.json.JSONObject;

public class ConfigModel {
	private int id;
	private String tableName;
	private String insertField ;
	private String updateField;
	private String timeField;
	private String userField;
	private String formName;
	private String returnField;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public ConfigModel(JSONObject js)
	{
		String sid = getJsonData(js, "formId");
		id = sid.equals("")?0:Integer.parseInt(sid);
		setInsertField(getJsonData(js, "insertField"));
		//setListField(getJsonData(js, "list"));
		setTableName(getJsonData(js, "tableName"));
		setFormName(getJsonData(js, "formName"));
		setReturnField(getJsonData(js, "returnField"));
		
		setUpdateField(getJsonData(js, "updateField"));
		setTimeField(getJsonData(js, "setTime"));
		setUserField(getJsonData(js, "setUser"));
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
	public String getInsertField() {
		return insertField;
	}
	public void setInsertField(String saveField) {
		this.insertField = saveField;
	}
	public boolean isContainField(String field,String list)
	{
		String[] arr = list.split(",");
		for(String f:arr)
		{
			if(f.equals(field))
			{
				return true;
			}
		}
		return false;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public String getReturnField() {
		if(returnField.equals(""))
		{
			return insertField;
		}
		return returnField;
	}
	public void setReturnField(String returnField) {
		this.returnField = returnField;
	}
	public String getUpdateField() {
		return updateField.equals("")?insertField:updateField;
	}
	public void setUpdateField(String updateField) {
		this.updateField = updateField;
	}
	public String getTimeField() {
		return timeField;
	}
	public void setTimeField(String timeField) {
		this.timeField = timeField;
	}
	public String getUserField() {
		return userField;
	}
	public void setUserField(String userField) {
		this.userField = userField;
	}

}
