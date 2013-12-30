package com.me.GCDP.xform;

import com.me.json.JSONException;
import com.me.json.JSONObject;

public class ControlConfigItem {
	private JSONObject json;
	
	public void setJson(JSONObject json) {
		this.json = json;
	}
	public JSONObject getJson() {
		return json;
	}
	public ControlConfigItem(JSONObject json)
	{
		this.json = json;
	}
	public String getControlType() throws JSONException
	{
		return json.getString("name").replace('.', '_');
	}
	public String getControlId() throws JSONException
	{
		return json.getString("id");
	}
	public String getControlField() throws JSONException
	{
		try{
			if(!json.isNull("f_name")){
				return json.getString("f_name");
			}else{
				return "";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public String getDataSourceConfig(String key)
	{
		try {
			if(!json.isNull("data")){
				return json.getJSONObject("data").getString(key);
			}else{
				return "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	public String getExtConfig(String key)
	{
		try {
			if(!json.isNull("ext")){
				return json.getJSONObject("ext").getString(key);
			}else{
				return "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}
