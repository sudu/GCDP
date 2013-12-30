package com.me.GCDP.action.xform;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.Hanyu2Pinyin;
import com.me.GCDP.util.MySQLHelper;
import com.me.json.JSONArray;
import com.opensymphony.xwork2.ActionSupport;

public class TemplateCtrAction extends ActionSupport {

	private static final long serialVersionUID = -4854489616198358682L;
	
	private static Log log = LogFactory.getLog(TemplateCtrAction.class);
	
	private int dataFormId;
	private boolean hasError = false;
	private String msg = "";
	private Integer dataId;
	private Integer nodeId = 0;

	public String list() {
		return "list";
	}

	public String idxTypeList() throws SQLException {
		return "idxTypeList";
	}

	// TODO 似乎没有使用
	public List<Map<String, Object>> getListData() {
		String sql2 = dataId == 0 ? "" : " union select id,name,dataId,0 order1 from cmpp_template where dataFormId="
				+ dataFormId + " and dataId =0";
		String sql = "select id,name,dataId from("
				+ "select id,name,dataId,1 order1 from cmpp_template where dataFormId=" + dataFormId + " and dataId ="
				+ dataId + sql2 + ")a order by order1,id desc limit 100";
		try {
			List<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, null);
			for (Map<String, Object> data : datas) {
				String name = data.get("name").toString();
				Set<String> quanping = Hanyu2Pinyin.getPinyin(name);
				Set<String> jianping = Hanyu2Pinyin.getJianpin(name);
				data.put("quanpin", new JSONArray(quanping).toString());
				data.put("jianpin", new JSONArray(jianping).toString());
				data.put("dataId", data.get("dataId").toString());
			}
			// JSONArray js = new JSONArray(datas);
			// return js;
			return datas;
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			log.error(e);
		}
		return null;
	}

	public List<Map<String, Object>> getIdxTypeList() throws SQLException {
		String sql = "select * from cmpp_idxType where nodeId=" + nodeId;
		List<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, null);
		return datas;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public String getMsg() {
		return msg;
	}

	public boolean getHasError() {
		return hasError;
	}

	public int getDataFormId() {
		return dataFormId;
	}

	public void setDataFormId(int dataFormId) {
		this.dataFormId = dataFormId;
	}

	public int getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId == null ? 0 : dataId;
	}

}
