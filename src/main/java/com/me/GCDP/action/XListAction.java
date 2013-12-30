package com.me.GCDP.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.StringUtil;

import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.model.Permission;
import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormPlugin;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.ListConfig;
import com.me.GCDP.xform.ListHelper;
import com.me.GCDP.xform.ViewConfig;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.search.SearchService_V2;
import com.me.GCDP.search.util_V2.Page;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

public class XListAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(XListAction.class);

	private SecurityManager securityManager = null;
	private int nodeId;

	private SearchService_V2 searchSvr;
	private Page searchData = null;
	private FormPlugin formSvr;
	private String customedTpl = "";
	private String templateName = "";
	private Map<String, Object> dataPool = new HashMap<String, Object>();
	private String scriptKey;

	/**
	 * from:'service',
	 * type:channel__,//索引来源（频道号）
	 * fd:listConfig__.fields.join(','),//需要输出的字段
	 * q:q.join(','),
	 * sort:sort.join(','),
	 * formId:formId__
	 */
	public ListConfig conf;
	public FormConfig fconf;
	public int start = 0;
	public int limit = 10;
	public int formId;
	public int listId;
	public int recordCount;
	public String type;
	public String from = "db";
	public String fd;
	public String q;
	public String sortFlds;

	public ListHelper lh = null;
	public SearchHelper sh = null;
	public String wheres = null;
	private String ids;
	private boolean hasError = false;
	private String msg;
	public String sorts = null;
	
	/*预览post过来的列表页配置*/
	public String listCfgJSON;


	public String list() throws Exception {
		return "list";
	}
	
	public String preview(){
		//todo 根据listCfgJSON构建conf
		String ftl = render();
		return ftl;
	}
	
	public String render() {
		String path = fconf.getPowerPath();
		boolean check = securityManager.checkPermission(path, Permission.CHECK);
		if (!check) {
			this.customedTpl = "你没有查看列表的权限";
			return "renderTpl";
		}
		try {
			JSONObject listConfigJson = conf.getJsonConfig();
			if (listConfigJson == null) {
				return "renderError";
			} else {
				String cusTpl = listConfigJson.getString("myTemplate");
				if (cusTpl.equals("")) {
					this.templateName = listConfigJson.getString("template");
					return "render";
				} else {
					Map<String, Object> renderMap = new HashMap<String, Object>();
					renderMap.put("formId", this.formId);
					renderMap.put("listId", this.listId);
					renderMap.put("nodeId", this.nodeId);
					renderMap.put("listConfig", getListConfig());
					renderMap.put("searchConfig", getSearchConfig());

					this.customedTpl = FreeMarkerHelper.process2(cusTpl, renderMap);
					return "renderTpl";
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return "renderError";
	}
	
	/**
	 * 用于跨节点访问列表页（无权限验证，但要求登录验证）
	 * @return
	 */
	public String render_noauth() {
		try {
			JSONObject listConfigJson = conf.getJsonConfig();
			if (listConfigJson == null) {
				return "renderError";
			} else {
				String cusTpl = listConfigJson.getString("myTemplate");
				if (cusTpl.equals("")) {
					this.templateName = listConfigJson.getString("template");
					return "render";
				} else {
					Map<String, Object> renderMap = new HashMap<String, Object>();
					renderMap.put("formId", this.formId);
					renderMap.put("listId", this.listId);
					renderMap.put("nodeId", this.nodeId);
					renderMap.put("listConfig", getListConfig());
					renderMap.put("searchConfig", getSearchConfig());

					this.customedTpl = FreeMarkerHelper.process2(cusTpl, renderMap);
					return "renderTpl";
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return "renderError";
	}

	public String getData() throws SQLException, JSONException {
		try {
			if (from.equals("service")) {
				return new JSONArray(searchData.getResult()).toString();
			} else if (from.equals("bak")) {
				if (!fconf.hasPartition()) {
					msg = "表单未设置分表存储.";
					hasError = true;
					JSONObject ret = new JSONObject();
					ret.put("success", false);
					ret.put("message", msg);
					return ret.toString();
				}
				return new JSONArray(lh.getData(true)).toString();
			} else {
				return new JSONArray(lh.getData()).toString();
			}
		} catch (Exception e) {
			msg = e.getMessage();
			log.error(e);
			hasError = true;
			JSONObject ret = new JSONObject();
			ret.put("success", false);
			ret.put("message", msg);
			return ret.toString();
		}
	}

	public String runScript() {
		try {
			msg = "";
			dataPool = getFormSvr().getHelper().runScript(scriptKey, ids);
		} catch (Exception e) {
			msg = e.getMessage();
			log.error(e);
			hasError = true;
		}
		if (dataPool.get("refresh") == null) {
			dataPool.put("refresh", false);
		}
		return "runScript";
	}

	public String data() throws Exception {
		try {
			if (from.equals("service")) {
				sh = new SearchHelper(nodeId, formId, searchSvr);
				String st = "";
				if (!sorts.equals("")) {
					JSONArray arr = new JSONArray(sorts);
					if (arr.length() > 0) {
						st = arr.getJSONObject(0).getString("field") + " " + arr.getJSONObject(0).getString("order");
					}
				}
				searchData = sh.getData(q, st, fd, start, limit);
				recordCount = searchData.getTotalCount();
			} else {
				lh = new ListHelper(conf, formId, nodeId);
				lh.initParms(wheres, sorts, start, limit);
				recordCount = lh.getTotalNum();
			}
			return "data";
		} catch (Exception e) {
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
	}

	public String delete() {
		try {
			msg = "";
			String userId = AuthorzationUtil.getUserId();
			String[] idArr = ids.split(",");

			FormService formService = (FormService) SpringContextUtil.getBean("formService");
			FormConfig fc = FormConfig.getInstance(nodeId, formId);
			for (String strId : idArr) {
				int deleteId = Integer.parseInt(strId);
				Map<String, Object> data = formService.getData(fc, deleteId);
				if (securityManager.checkPermission(fc.getPowerPath(), Permission.DELETE, data, formId, deleteId)) {
					formService.deleteData(fc, deleteId);
				} else {
					hasError = true;
					msg += "id:" + deleteId + "的数据删除失败,你没有删除该数据的权限<br/>";
					log.info("userId:" + userId + "删除数据 formId=" + formId + ",id=" + deleteId + "失败,权限验证未通过");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			hasError = true;
		}
		msg = msg.equals("") ? "" : msg;
		return "delete";
	}

	public int getNum() {
		return recordCount;
	}

	public int getViewId() throws SQLException {
		return ViewConfig.getMinViewId(formId);
	}

	public void setSearchService(SearchService_V2 svr) {
		this.searchSvr = svr;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public String getCustomSearchHTML() throws JSONException {
		return conf.getCustomSearchHTML();
	}

	public String getCustomedTpl() {
		return customedTpl;
	}

	public String getTemplateName() {
		return templateName;
	}

	public FormPlugin getFormSvr() {
		formSvr.init(nodeId, formId, 0, null);
		return formSvr;
	}

	public Map<String, Object> getDataPool() {
		return dataPool;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) throws JSONException, SQLException {
		this.listId = listId;
		if(StringUtil.isBlank(listCfgJSON)){
			conf = new ListConfig(ListConfig.getListConfig(listId));
		}else{
			conf = new ListConfig(listCfgJSON);
		}
	}

	public String getScriptKey() {
		return scriptKey;
	}

	public void setScriptKey(String scriptKey) {
		this.scriptKey = scriptKey;
	}

	public String getSortable() throws JSONException {
		return fconf.getSortAble();
	}

	public String getSearchable() throws JSONException {
		return fconf.getSeachAble();
	}

	public String getSearchConfig() throws JSONException {
		return fconf.getSearchConfig();
	}

	public String getListConfig() throws JSONException, SQLException {
		return conf.getConfig().toString();
	}

	public String getHeadInject() throws JSONException {
		return conf.getHeadInject();
	}

	public String getBodyInject() throws JSONException {
		return conf.getBodyInject();
	}

	public String getExtOnReadyJs() throws JSONException {
		return conf.getExtOnReadyJs();
	}

	public boolean isHasError() {
		return hasError;
	}

	public String getMsg() {
		return msg;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setWhere(String wheres) {
		this.wheres = wheres;
	}

	public void setSort(String sorts) {
		this.sorts = sorts;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setFrom(String value) {
		this.from = value;
	}

	public void setQ(String value) {
		this.q = value;
	}

	public void setType(String value) {
		this.type = value;
	}

	public void setFd(String value) {
		this.fd = value;
	}

	public void setSortFlds(String value) {
		this.sortFlds = value;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) throws JSONException, SQLException {
		this.formId = formId;
		fconf = FormConfig.getInstance(nodeId, formId);
		ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		this.formSvr = (FormPlugin) pf.getP("form");
	}

	public String getListCfgJSON() {
		return listCfgJSON;
	}

	public void setListCfgJSON(String listCfgJSON) {
		this.listCfgJSON = listCfgJSON;
	}
}
