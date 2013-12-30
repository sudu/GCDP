package com.me.GCDP.xform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.oscache.OSCache;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;

public class FormConfig {
	private static Log log = LogFactory.getLog(JsonFormConfig.class);

	private int formId = 0;
	private int nodeId;
	private JSONObject json = null;
	private Map<String, JSONObject> fields;
	private String tableName;
	private String formConfig;
	private String powerPath;
	private String formName;

	public FormConfig(String jsonStr) throws JSONException {
		json = new JSONObject(jsonStr);
		init();
	}

	private FormConfig(int nodeId, int formId) throws SQLException, JSONException {
		this.formId = formId;
		this.nodeId = nodeId;
		String sql = "select * from cmpp_formConfig where formId=" + formId;
		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, null);
		if (datas.size() > 0) {
			Map<String, Object> item = datas.get(0);
			String jsonstr = item.get("config").toString();
			this.json = new JSONObject(jsonstr);

			Object tName = item.get("tableName");
			this.tableName = (tName == null ? "frm_" + formId : tName.toString());

			this.formName = item.get("name").toString();
			this.formConfig = item.get("config").toString();

			Object oPath = item.get("powerPath");
			this.powerPath = (oPath == null ? "" : oPath.toString());
			if (nodeId == 0) {
				this.nodeId = (Integer) item.get("nodeid");
			}
			init();
		} else {
			throw (new SQLException("未找到formId=" + formId + "的配置"));
		}
	}
/**
<pre>
	private FormConfig(String jsonStr, int formId) throws JSONException {
		this.formId = formId;
		json = new JSONObject(jsonStr);
		init();
	}
</pre>
*/
//
	/**
	 * 是否有分表
	 * @return
	 */
	public boolean hasPartition() {
		try {
			return json.getJSONObject("tableBak").getBoolean("enableBak");
		} catch (JSONException e) {
			return false;
		}
	}

	private void init() throws JSONException {
		fields = new HashMap<String, JSONObject>();
		JSONArray arr = json.getJSONObject("fieldsConfig").getJSONArray("fieldsConfig");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject field = arr.getJSONObject(i);
			int type = field.getInt("f_saveType");
			if (type == 2)// db
			{
				fields.put(field.getString("f_name"), field);
			}
		}
	}

	public JSONObject getField(String field) {
		return fields.get(field);
	}

	public Vector<ControlDbItem> getDbItem() throws JSONException {
		return getItem(2);
	}

	public Vector<ControlDbItem> getSaveItem() throws JSONException {
		Vector<ControlDbItem> rtn = getDbItem();
		rtn.addAll(getNosqlItem());
		return rtn;
	}

	public Vector<ControlDbItem> getNosqlItem() throws JSONException {
		return getItem(3);
	}

	public Vector<ControlDbItem> getItem(int type) throws JSONException {
		Vector<ControlDbItem> rtn = new Vector<ControlDbItem>();
		JSONArray ctrConfigs = json.getJSONObject("fieldsConfig").getJSONArray("fieldsConfig");
		for (int i = 0; i < ctrConfigs.length(); i++) {
			JSONObject ctr = (JSONObject) ctrConfigs.get(i);
			ControlDbItem item = new ControlDbItem();
			int tp = 1;
			try {
				tp = ctr.getInt("f_saveType");
			} catch (Exception e) {
				type = 1;
			}
			if (tp == type) {
				item.setFieldName(ctr.getString("f_name"));
				item.setFieldType(ctr.getString("f_type"));
				item.setFieldLength(ctr.getInt("f_length"));
				rtn.add(item);
			}
		}
		return rtn;
	}

	public String getSeachAble() throws JSONException {
		return json.getJSONObject("fieldsConfig").getJSONArray("searchable").toString();
	}

	public String getSearchConfig() throws JSONException {
		JSONObject rtn = new JSONObject(json.toString());
		rtn.getJSONObject("fieldsConfig").remove("fieldsConfig");
		return rtn.toString();
	}

	public String getSortAble() throws JSONException {
		return json.getJSONObject("fieldsConfig").getJSONArray("sortable").toString();
	}

	public static String getConfig(int formId) throws SQLException {
		String sql = "select config from cmpp_formConfig where formId=" + formId;
		return MySQLHelper.ExecuteSql(sql, null).get(0).get("config").toString();
	}

	public static String getFormName(int formId) throws SQLException {
		String sql = "select name from cmpp_formConfig where formId=" + formId;
		return MySQLHelper.ExecuteSql(sql, null).get(0).get("name").toString();
	}

	public String getFieldNames() throws JSONException {
		return json.getJSONObject("fieldsConfig").getJSONArray("fields").toString();
	}

	public Vector<String> getListFields() {
		Vector<String> rtn = new Vector<String>();
		try {
			JSONArray arr = json.getJSONObject("fieldsConfig").getJSONArray("fields");
			for (int i = 0; i < arr.length(); i++) {
				String key = arr.getJSONArray(i).getString(0);
				if (fields.containsKey(key)) {
					rtn.add(arr.getJSONArray(i).getString(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	public Vector<String> getListSearchFields() {
		Vector<String> rtn = new Vector<String>();
		try {
			JSONArray arr = json.getJSONObject("fieldsConfig").getJSONArray("searchable");
			for (int i = 0; i < arr.length(); i++) {

				rtn.add(arr.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	public static void SaveFormConfig(String strJson, int formId) throws SQLException, JSONException {
		String sql = "update cmpp_formConfig set config=? where formId=?";
		Object[] parms = new Object[2];
		parms[0] = strJson;
		parms[1] = formId;
		MySQLHelper.ExecuteNoQuery(sql, parms);
		updateOSCache(formId);
	}

	public static void updateFormConfig(int nodeId, int formId, String config, String formName, String powerPath)
			throws SQLException, IOException, JSONException, TemplateException {
		String sql = "update cmpp_formConfig set `name`=?,config=?,powerPath=? where formId=?";
		Object[] parms = new Object[4];
		parms[0] = formName;
		parms[1] = config;
		parms[2] = powerPath;
		parms[3] = formId;

		MySQLHelper.ExecuteNoQuery(sql, parms);
		FormConfig cfg = new FormConfig(nodeId, formId);
		updateOSCache(cfg);
		TableSchemaHelper.updateTable(cfg);
	}

	public static FormConfig getInstance(int nodeId, int formId) throws JSONException, SQLException {
		OSCache oscache_formconfig = (OSCache) SpringContextUtil.getBean("oscache_formconfig");
		String key = formId + "";
		FormConfig fc = (FormConfig) oscache_formconfig.get(formId + "");
		if (fc == null) {
			fc = new FormConfig(nodeId, formId);
			oscache_formconfig.put(key, fc);
		}
		return fc;
	}

	public static int createFormConfig(String formName, String config, int nodeId) throws NumberFormatException,
			SQLException {
		String sql = "insert into cmpp_formConfig(name,config,nodeid) value(?,?,?)";
		Object[] parms = new Object[3];
		parms[0] = formName;
		parms[1] = config;
		parms[2] = nodeId;
		int _formId = Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));
		updateOSCache(_formId);
		return _formId;
	}
	
	public static int createFormConfig(String formName, String config, int nodeId, int isSysForm) throws NumberFormatException,
		SQLException {
		String sql = "insert into cmpp_formConfig(name,config,nodeid,isSysForm) value(?,?,?,?)";
		int _formId = Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, new Object[]{formName, config, nodeId, isSysForm}));
		updateOSCache(_formId);
		return _formId;
	}

	/**
	 * 更新OSCache，失败则清除OSCache对应值
	 * 
	 * @param formId
	 */
	private static void updateOSCache(int formId) {
		FormConfig cfg;
		String key = formId + "";
		OSCache oscache_formconfig = (OSCache) SpringContextUtil.getBean("oscache_formconfig");
		try {
			cfg = new FormConfig(0, formId);
			oscache_formconfig.put(key, cfg);
		} catch (Exception e) {
			log.warn(e);
			oscache_formconfig.remove(key);
		}
	}

	private static void updateOSCache(FormConfig fc) {
		int _formId = fc.getFormId();
		OSCache oscache_formconfig = (OSCache) SpringContextUtil.getBean("oscache_formconfig");
		String key = _formId + "";
		oscache_formconfig.put(key, fc);
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public String getFormName() {
		return this.formName;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getPowerPath() {
		return powerPath;
	}

	public void setPowerPath(String powerPath) {
		this.powerPath = powerPath;
	}

	public String getFormConfig() {
		return formConfig;
	}

	public Map<String, JSONObject> getFields() {
		return fields;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getConfig() {
		return json.toString();
	}

	public JSONObject getJsonConfig() {
		return json;
	}
}
