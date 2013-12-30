package com.me.GCDP.action.xform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Permission;
import com.me.GCDP.model.Template;
import com.me.GCDP.model.Version;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.version.VersionHelper2;
import com.me.GCDP.xform.TemplateBuilder;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

public class TplFormAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(TplFormAction.class);

	public int start = 0;
	public int limit = 10;
	public int recordCount;

	@SuppressWarnings("rawtypes")
	private VersionMapper versionMapper = null;
	private SecurityManager securityManager = null;
	private TemplateMapper<Template> templateMapper = null;
	
	private int nodeId = 0;
	private int dataFormId = 0;
	private int dataId = 0;
	private int id = 0;
	private int enable;
	private String ids;
	private String where;
	private String initWhere;
	private String msg = "";
	private String name;
	private String content;
	private String powerPath;
	private boolean hasError = false;
	

	public String list() {
		return "list";
	}

	public String listData() throws Exception {
//		String sql = "select count(*) num from cmpp_template where dataFormId=" + getDataFormId() + " and dataId="
//				+ getDataId() + initWhere();
//		recordCount = Integer.parseInt(MySQLHelper.ExecuteSql(sql, null).get(0).get("num").toString());
		recordCount = templateMapper.countByCustom("where dataFormId=" + getDataFormId() + " and dataId=" + getDataId() + initWhere());
		return "listdata";
	}

	public String editor() {
		return "editor";
	}

	public String save() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("powerPath", powerPath);
		data.put("name", name);
		data.put("dataFormId", getDataFormId());
		data.put("dataId", getDataId());
		data.put("enable", enable);
		String path = getPath();
		if (getId() == 0) {//insert
			if (securityManager.checkPermission(path, Permission.ADD, data, 0, 0)) {
				Template t = new Template();
				t.setNodeid(nodeId);
				t.setName(name);
				t.setContent(content);
				t.setEnable(enable);
				t.setDataFormId(getDataFormId());
				t.setDataId(getDataId());
				t.setPowerPath(powerPath);
				templateMapper.insert(t);
				setId(t.getId());
			} else {
				hasError = true;
				msg = "你没有添加模板的权限.";
				log.info("userId=" + AuthorzationUtil.getUserId() + " 添加模板失败，没有权限。");
			}
		} else {//update
			Map<String, Object> oldData = getPowerData(getId());
			if (securityManager.checkPermission(path, Permission.MODIFY, oldData, 0, 0)
					&& securityManager.checkPermission(path, Permission.MODIFY, data, 0, 0)) {
				Template t = new Template();
				t.setNodeid(nodeId);
				t.setName(name);
				t.setContent(content);
				t.setEnable(enable);
				t.setPowerPath(powerPath);
				t.setId(getId());
				templateMapper.update(t);
			} else {
				hasError = true;
				msg = "你没有修改该模板的权限.";
				log.info("userId=" + AuthorzationUtil.getUserId() + " id=" + getId() + " 修改模板失败，没有权限。");
			}
		}
		if (!hasError) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("name", name);
			dataMap.put("content", content);
			dataMap.put("enable", enable);
			dataMap.put("powerPath", powerPath);
			try {
				setVersionData(AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]", getId(), dataMap);
			} catch (Exception ex) {
				log.info("userId=" + AuthorzationUtil.getUserId() + " id=" + getId() + " 保存历史记录出错。");
			}
		}

		TemplateBuilder.processRelation(getDataFormId(), getDataId(), getId(), content);
		return "save";
	}

	public String delete() {
		try {
			msg = "";
//			String sql = "delete from cmpp_template where id =?";
			String[] idArr = ids.split(",");
			String path = getPath();
			for (String sid : idArr) {
				Map<String, Object> oldData = getPowerData(Integer.parseInt(sid));
				if (securityManager.checkPermission(path, Permission.DELETE, oldData, dataFormId, dataId)) {
//					Object[] parms = new Object[1];
//					parms[0] = sid;
//					MySQLHelper.ExecuteNoQuery(sql, parms);
					
					templateMapper.deleteById(Integer.parseInt(sid));
					
					msg += "id=" + sid + "的模板删除成功<br/>";
				} else {
					msg += "id=" + sid + "的模板未授权，无法删除.<br/>";
				}
			}
		} catch (Exception e) {
			setHasError(true);
			setMsg(e.getMessage());
			log.error(e);
		}
		return "delete";
	}

	public String getData() throws Exception {
//		String sql = "select id,name,enable,modifyDate,powerPath from cmpp_template where dataFormId=" + dataFormId
//				+ " and dataId=" + dataId + initWhere() + " order by id desc limit " + start + "," + limit;
//		JSONArray rtn = new JSONArray(MySQLHelper.ExecuteSql(sql, null));
		Map<String, Object> custom = new HashMap<String, Object>();
		custom.put("customSelect", "id,name,`enable`,modifyDate,powerPath");
		custom.put("customWhere", "dataFormId=" + dataFormId + " and dataId=" + dataId + initWhere());
		custom.put("start", start);
		custom.put("limit", limit);
		List<Template> tList = templateMapper.getByCustom(custom);
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		for (Template t : tList) {
			mList.add(t.toMap());
		}
		JSONArray rtn = new JSONArray(mList);
		String str = rtn.toString();
		return str;
	}

	public String initWhere() throws JSONException {
		if (initWhere != null) {
			return initWhere;
		}
		if (where == null)
			return "";
		JSONArray params = new JSONArray(where);
		String value = params.getJSONObject(0).getString("value");
		if (value != null && value != "") {
			initWhere = " and name like'%" + value + "%' ";
		} else {
			initWhere = "";
		}
		return initWhere;
	}

	public String getRecordData() throws SQLException {
		if (id != 0) {
			Template t = templateMapper.getById(id);
			if (t != null) {
				return new JSONObject(t.toMap()).toString();
			}
//			String sql = "select * from cmpp_template where id=" + id;
//			Vector<Map<String, Object>> data = MySQLHelper.ExecuteSql(sql, null);
//			if (data.size() > 0) {
//				return new JSONObject(data.get(0)).toString();
//			}
		}
		return "{}";
	}

	public Map<String, Object> getPowerData(int id) throws SQLException {
//		return MySQLHelper.ExecuteSql(
//				"select id,name,`enable`,dataFormId,dataId,powerPath from cmpp_template where id=" + id, null).get(0);
		return templateMapper.getById(id).toMap();
	}

	public String getPath() throws Exception {
//		String sql = "select powerPath from cmpp_formConfig where tableName='cmpp_template'";
//		try {
//			return MySQLHelper.ExecuteSql(sql, null).get(0).get("powerPath").toString();
//		} catch (Exception e) {
//			log.error("获取权限表达式失败." + e);
//			return "";
//		}
		List<String> list = templateMapper.getPowerPathInFormConfig();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return "";
	}

	private void setVersionData(String userName, int rid, Map<String, Object> data) throws Exception {
		String key = nodeId + "_template_" + rid;
		JSONObject formDataJson = new JSONObject(data);
		VersionHelper2 fh = new VersionHelper2(key, userName, formDataJson.toString());
		fh.setVersionMapper(versionMapper);
		fh.save();
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setDataFormId(int dataFormId) {
		this.dataFormId = dataFormId;
	}

	public int getDataFormId() {
		return dataFormId;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

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

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public int getDataId() {
		return dataId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VersionMapper<Version> getVersionMapper() {
		return versionMapper;
	}

	public void setVersionMapper(VersionMapper<Version> versionMapper) {
		this.versionMapper = versionMapper;
	}
	
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	public TemplateMapper<Template> getTemplateMapper() {
		return templateMapper;
	}

	public void setTemplateMapper(TemplateMapper<Template> templateMapper) {
		this.templateMapper = templateMapper;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getPowerPath() {
		return powerPath;
	}

	public void setPowerPath(String powerPath) {
		this.powerPath = powerPath;
	}

}