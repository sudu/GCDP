package com.me.GCDP.xform.xcontrol;

import com.me.json.JSONException;
import com.me.json.JSONObject;


public class Ext_form_ComboBox extends BaseControl {
	/*
	 * 渲染控件的配置
	 */
	public JSONObject render() throws JSONException
	{
		return conf.getJson();
	}
}
