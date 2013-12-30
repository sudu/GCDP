package com.me.GCDP.action.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.adapter.AdapterService;
import com.me.GCDP.util.env.NodeEnv;
import com.me.GCDP.xform.SysInitializationService;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * <p>Title: </p>
 * <p>Description: 文章/栏目对外的视图，数据统一接口（仅作代理）</p>
 * <p>Company: ifeng.com</p>
 * @author :dengzc
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason                    </p>
 * <p>2013-12-5        dengzc               create the class             </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class XAdapterAction extends ActionSupport {

	private static final long serialVersionUID = 4528118218852500781L;
	private static final Log log = LogFactory.getLog(XAdapterAction.class);

	private int nodeId;
	//private String fd;
	private String q;
	private String where;
	private String sort;
	private int start = 0;
	private int limit = 0;

	private String data;
	private String result;
	private AdapterService adapterService;
	private ScriptService scriptService;
	
	/**
	 * 渲染文章列表视图接口
	 * @throws Exception
	 */
	public String renderDocList() {
		try {
			// 从节点配置中获取formId与listId
			Map<String, Object> docMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_docFieldAdapterCfg);
			JSONObject jsonObj = new JSONObject(docMap.get("value").toString());
			int formId = jsonObj.getInt("sysFormId");
			int listId = jsonObj.getInt("listId");

			HttpServletRequest request = ServletActionContext.getRequest();
			String queryString = request.getQueryString();
			HttpServletResponse response = ServletActionContext.getResponse();
			response.sendRedirect("xlist!render.jhtml?formId=" + formId + "&listId=" + listId + "&" + queryString);
			return null;
		} catch (Exception e) {
			result = "渲染文章列表视图接口异常：" + e.getMessage();
			log.error(result, e);
			return "result";
		}
	}

	/**
	 * 文章列表数据接口
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String docListData() {
		try {
			// 获取字段映射配置数据
			Map<String, Object> docMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_docFieldAdapterCfg);
			JSONObject jsonObj = new JSONObject(docMap.get("value").toString());
			Map<String, String> fieldMap = (Map<String, String>) jsonObj.getJSONObject("map").toMap();

			int formId = jsonObj.getInt("localFormId");
			int listId = jsonObj.getInt("listId");
			// String fd = adapterService.getInAdapterFd(this.fd, fieldMap);
			// String q = adapterService.getInAdapterQ(this.q, fieldMap);
			String sort = adapterService.getInAdapterSort(this.sort, fieldMap);

			Map<String, Object> dataPool = new HashMap<String, Object>();
			dataPool.put("formId", formId);
			dataPool.put("listId", listId);
			dataPool.put("from", "service");
			dataPool.put("fd", "");
			dataPool.put("q", q);
			dataPool.put("sort", sort);
			dataPool.put("start", start < 1 ? 0 : start);
			dataPool.put("limit", limit < 1 ? 10 : limit);

			HttpServletRequest request = ServletActionContext.getRequest();
			StringBuffer sb = request.getRequestURL();
			dataPool.put("url", sb.substring(0, sb.lastIndexOf("/") + 1) + "xlist!data.jhtml");
			// 执行字段转换脚本
			scriptService.run(nodeId, dataPool, ScriptType.common, "common.docsysform");

			// 出现错误情况
			if (dataPool.containsKey("result")) {
				result = dataPool.get("result").toString();
				return "result";
			}

			data = dataPool.get("data").toString();
			return "data";
		} catch (Exception e) {
			result = "{\"success\":false,\"message\":\"" + "文章列表数据接口异常：" + e.getMessage() + "\"}";
			log.error(result, e);
			return "result";
		}
	}

	/**
	 * 渲染栏目列表视图接口
	 * @throws Exception
	 */
	public String renderColumnList() {
		try {
			// 从节点配置中获取formId与listId
			Map<String, Object> docMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_columnFieldAdapterCfg);
			JSONObject jsonObj = new JSONObject(docMap.get("value").toString());
			int formId = jsonObj.getInt("sysFormId");
			int listId = jsonObj.getInt("listId");

			HttpServletRequest request = ServletActionContext.getRequest();
			String queryString = request.getQueryString();
			HttpServletResponse response = ServletActionContext.getResponse();
			response.sendRedirect("xlist!render.jhtml?formId=" + formId + "&listId=" + listId + "&" + queryString);
			return null;
		} catch (Exception e) {
			result = "渲染栏目列表视图接口异常：" + e.getMessage();
			log.error(result, e);
			return "result";
		}
	}

	/**
	 * 栏目列表数据接口
	 * @return
	 * @throws Exception
	 */
	public String columnListData() {
		try {
			// 获取字段映射配置数据
			Map<String, Object> docMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_columnFieldAdapterCfg);
			JSONObject jsonObj = new JSONObject(docMap.get("value").toString());
			// @SuppressWarnings("unchecked")
			// Map<String, String> fieldMap = (Map<String, String>) jsonObj.getJSONObject("map").toMap();

			int formId = jsonObj.getInt("localFormId");
			int listId = jsonObj.getInt("listId");
			// String fd = adapterService.getInAdapterFd(this.fd, fieldMap);
			// String q = adapterService.getInAdapterQ(this.q, fieldMap);
			// String sort = adapterService.getInAdapterSort(this.sort, fieldMap);

			Map<String, Object> dataPool = new HashMap<String, Object>();
			dataPool.put("formId", formId);
			dataPool.put("listId", listId);
			dataPool.put("from", "db");
			dataPool.put("where", where);
			dataPool.put("sort", sort);
			dataPool.put("start", start < 1 ? 0 : start);
			dataPool.put("limit", limit < 1 ? Integer.MAX_VALUE : limit);

			dataPool.put("nodeId", nodeId);

			HttpServletRequest request = ServletActionContext.getRequest();
			StringBuffer sb = request.getRequestURL();
			int port = request.getLocalPort();
			dataPool.put("sourceHost", port == 80 ? request.getServerName() : request.getServerName() + ":" + port);
			dataPool.put("url", sb.substring(0, sb.lastIndexOf("/") + 1) + "../runtime/xlist!data.jhtml");
			// 执行字段转换脚本
			scriptService.run(nodeId, dataPool, ScriptType.common, "common.columnsysform");

			// 出现错误情况
			if (dataPool.containsKey("result")) {
				result = dataPool.get("result").toString();
				return "result";
			}

			data = dataPool.get("data").toString();
			return "data";
		} catch (Exception e) {
			result = "{\"success\":false,\"message\":\"" + "栏目列表数据接口异常：" + e.getMessage() + "\"}";
			log.error(result, e);
			return "result";
		}
	}
	
	// getter and setter
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setAdapterService(AdapterService adapterService) {
		this.adapterService = adapterService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

//	public void setFd(String value) {
//		this.fd = value;
//	}

	public void setQ(String value) {
		this.q = value;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getData() {
		return data;
	}

	public String getResult() {
		return result;
	}
}
