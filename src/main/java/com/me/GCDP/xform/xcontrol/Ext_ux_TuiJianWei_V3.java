package com.me.GCDP.xform.xcontrol;

import java.sql.SQLException;

import com.me.GCDP.xform.ListConfig;
import com.me.json.JSONException;
import com.me.json.JSONObject;

/*
 * 推荐位V3控件
 * author:chengds
 */
public class Ext_ux_TuiJianWei_V3 extends BaseControl {
	/*
	 * 渲染控件的配置
	 */
	public JSONObject render() throws JSONException
	{
		JSONObject json = conf.getJson();
		JSONObject ui = json.getJSONObject("ui");
		String listId = ui.getString("dataSourceListId");
		ui.remove("dataSourceListId");
		if(listId!=""){
			try {
				String listcfgStr = ListConfig.getListConfig(Integer.parseInt(listId));
				ListConfig listConfig = new ListConfig(listcfgStr);
				
				String columnsCfg = listConfig.getColumns().toString();
				String fields = listConfig.getMustReturnFields().toString();
				ui.put("columnsCfg", columnsCfg);
				ui.put("fields", fields);
				ui.put("isWidthFreeRow", listConfig.getJsonConfig().getInt("isWidthFreeRow"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
}
