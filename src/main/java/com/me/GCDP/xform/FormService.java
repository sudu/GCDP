package com.me.GCDP.xform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.model.Template;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.CmppDBPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.search.SearchService_V2;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.source.SubscribeService;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.UserLogUtil;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;

/*
 *@version 1.1 将cmppDBService替换成了cmppDBPlugin by chengds at 2013/8/28
 *@version 1.2 往template注入全页预览时的碎片数据,以实现无需保存的全页预览  by chengds at 2014/3/5
 */
public class FormService {
	private static Log log = LogFactory.getLog(FormService.class);
	private ScriptService scriptService = null;
	private SearchService_V2 searchService = null;
	private SubscribeService subscribeService = null;
	private TemplateMapper<Template> templateMapper = null;

	private ScriptPluginFactory pluginFactory=null;
	private CmppDBPlugin cmppDBPlugin = null;
	
	/**
	 * 删除指定记录
	 * 
	 * @param fc
	 * @param dataId
	 * @throws SQLException
	 * @throws JSONException
	 */
	public void deleteData(FormConfig fc, int dataId) throws SQLException, JSONException {
		// 删除nosql对应字段值
		deleteDataFromNosql(fc, dataId);

		// 删除搜索引擎对应值
		removeFromSearch(fc, dataId);

		Map<String, Object> data = getData(fc, dataId);

		// 删除订阅需要data（type = 3 删除）
		Map<String, Object> dataPool = new HashMap<String, Object>();
		dataPool.put("data", data);
		dataPool.put("type", 3);
		dataPool.put("id", dataId);
		subscribeService.process(fc.getNodeId(), fc.getFormId(), dataPool);

		// 删除关系数据库对应值
		String tb = fc.getTableName();
		/*
		if (fc.hasPartition()) {
			Integer maxId = DbEnv.getInstance().getMaxBakId(tb + "_bak");
			if (maxId >= dataId) {
				tb = tb + "_bak";
			}
		}
		*/
		String sql = "delete from %s where id =?";
		Object[] parms = new Object[1];
		parms[0] = dataId;
		int rows = MySQLHelper.ExecuteNoQuery(String.format(sql, tb), parms);
		if(rows==0){
			//尝试从历史数据表里查询 cds add at 2013/12/12
			String bakTableName = tb +"_bak";
			String tsql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+ tb + "'";
			Vector<Map<String, Object>> bakTables = MySQLHelper.ExecuteSql(tsql, null);
			if(bakTables.size()>0){
				MySQLHelper.ExecuteNoQuery(String.format(sql, bakTableName), parms);
			}
		}
		
		
		String userId = "";
		String userName = "";
		try {
			userId = AuthorzationUtil.getUserId();
			userName = AuthorzationUtil.getUserName();
		} catch (Exception e) {
			log.error("获取用户失败:" + e.getMessage());
		}
		dataPool.put("userId", userId);
		dataPool.put("userName", userName);

		// -----记录用户修改的数据内容----------
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("userName", userName);
		map.put("nodeId", fc.getNodeId());
		map.put("formId", fc.getFormId());
		map.put("action", "delete");
		map.put("dataId", dataId);
		UserLogUtil.getInstance(Integer.toString(fc.getNodeId())).log(map);
	}

	public void deleteDataFromNosql(FormConfig fc, int dataId) throws JSONException {
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		Vector<ControlDbItem> items = fc.getNosqlItem();
		if(cmppDBPlugin==null){
			cmppDBPlugin = (CmppDBPlugin)pluginFactory.getP("cmppDB");
		}
		for (ControlDbItem item : items) {
			String key = item.getFieldName();
			cmppDBPlugin.remove(nodeId + "-" + formId + "-" + dataId + "-" + key);
		}
	}

	/**
	 * 根据FormConfig和记录ID获取所有数据，自动合并关系数据库和NoSQL中的对应数据并返回。
	 * 
	 * @param fc
	 * @param dataId
	 * @return Map&lt;String, Object&gt;
	 */
	public Map<String, Object> getData(FormConfig fc, int dataId) {
		String tableName = fc.getTableName();

		Map<String, Object> rtn = new HashMap<String, Object>();
		try {
			String sql = "select * from " + tableName + " where id=" + dataId;
			Vector<Map<String, Object>> data = MySQLHelper.ExecuteSql(sql, null);
			if (data.size() > 0) {
				rtn.putAll(data.get(0));
			}else{
				//尝试从历史数据表里查询 cds add at 2013/12/12
				sql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+ tableName +"_bak'";
				Vector<Map<String, Object>> bakTables = MySQLHelper.ExecuteSql(sql, null);
				if(bakTables.size()>0){
					sql = "select * from " + tableName + "_bak where id=" + dataId;
					data = MySQLHelper.ExecuteSql(sql, null);
					if (data.size() > 0) {
						rtn.putAll(data.get(0));
					}
				}
			}
			rtn.putAll(getDataFromNosql(fc, dataId));
			return rtn;
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}

	/**
	 * 从nosql获取数据
	 * 
	 * @param FormConfig
	 *            fc
	 * @param int dataId
	 * @return Map<String, Object>
	 * @throws JSONException
	 * @throws SQLException
	 */
	public Map<String, Object> getDataFromNosql(FormConfig fc, int dataId) throws JSONException, SQLException {
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		Map<String, Object> rtn = new HashMap<String, Object>();
		Vector<ControlDbItem> items = fc.getNosqlItem();
		if(cmppDBPlugin==null){
			cmppDBPlugin = (CmppDBPlugin)pluginFactory.getP("cmppDB");
		}
		for (ControlDbItem item : items) {
			String key = item.getFieldName();
			//rtn.put(key, cmppDBService.getString(nodeId + "-" + formId + "-" + dataId + "-" + key));
			// FIXME 调试信息
			String nosqlKey = nodeId + "-" + formId + "-" + dataId + "-" + key;
			String _value = cmppDBPlugin.getString(nosqlKey);
			rtn.put(key, _value);
			if(_value==null){
				log.info("getDataFromNosql, Key="+nosqlKey+ ", value is null.");
			} else {
				log.info("getDataFromNosql, Key="+nosqlKey+ ", value.length="+String.valueOf(_value.length()));
			}
		}
		return rtn;
	}

	/**
	 * @param fc
	 * @return List<String>
	 * @throws SQLException
	 * @throws JSONException
	 */
	private static List<String> getFields(FormConfig fc) throws SQLException, JSONException {
		List<String> rtn = new ArrayList<String>();
		Vector<ControlDbItem> items = fc.getSaveItem();
		for (ControlDbItem item : items) {
			String key = item.getFieldName();
			rtn.add(key);
		}
		return rtn;
	}

	public static Map<String, Object> getPostData(FormConfig fc, Map<String, String[]> form) throws SQLException,
			JSONException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		if (form == null) {
			return rtn;
		}
		List<String> fields = getFields(fc);
		for (String f : fields) {
			String key = "xform." + f;
			if (!key.equals("")) {
				String[] values = form.get(key);
				if (values != null) {
					rtn.put(f, values[0]);
				} else {
					rtn.put(f, null);
				}
			}
		}
		return rtn;
	}
	
	/**
	 * 获取页面提交数据以及对应记录数据,先查询记录数据,再用提交数据覆盖
	 * @param fc
	 * @param form
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static Map<String, Object> getPostDataAndRecordData(FormConfig fc, Map<String, String[]> form) throws SQLException,
	JSONException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		if (form == null) {
			return rtn;
		}
		String[] ids = form.get("id");
		if(ids!=null){
			FormService formService = (FormService) SpringContextUtil.getBean("formService");
			Map<String, Object> data = formService.getData(fc, Integer.parseInt(ids[0]));
			rtn.putAll(data);
		}
		for(Object o : form.keySet()){ 
			String key = o.toString();
			rtn.put(key, form.get(o)[0]); 
		} 
		
		List<String> fields = getFields(fc);
		for (String f : fields) {
			String key = "xform." + f;
			if (!key.equals("")) {
				String[] values = form.get(key);
				if (values != null) {
					rtn.put(f, values[0]);
				} 
			}
		}
		return rtn;
	}

	public static Map<String, Object> getPostExtra(FormConfig fc, Map<String, Object[]> form) throws SQLException,
			JSONException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		if (form == null) {
			return rtn;
		}
		List<String> fields = getFields(fc);
		for (String f : fields) {
			String key = "xform." + f;
			if (!key.equals("")) {
				Object[] values = form.get(key);
				if (values != null) {
					rtn.put(f, values[0]);
				}
			}
		}
		return rtn;
	}

	public FormConfig getFormConfig(int nodeId, int formId) {
		try {
			return FormConfig.getInstance(nodeId, formId);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}

	public String render(FormConfig fc, Integer tplId, Map<String, Object> data, RenderType type) throws Exception {
		return render(fc, tplId, data, type, null,null);
	}
	//cds add
	public String render(FormConfig fc, Integer tplId, Map<String, Object> data, RenderType type,Map<String,Object> dataPool) throws Exception {
		return render(fc, tplId, data, type, null,dataPool);
	}
	
	public String render(FormConfig fc, String templateStr, Map<String, Object> data, RenderType type) throws Exception {
		return render(fc, templateStr, data, type, null);
	}
	
	public String render(FormConfig fc, Integer tplId, Map<String, Object> data, RenderType type, String siteUrl) throws Exception {
		return render(fc,tplId,data,type,siteUrl,null);
	}
	//cds add
	public String render(FormConfig fc, Integer tplId, Map<String, Object> data, RenderType type, String siteUrl,Map<String,Object> dataPool) throws Exception {
		if (tplId == null) {
			throw new Exception("tplId 不能为null");
		}
		if (data == null) {
			data = new java.util.HashMap<String, Object>();
		}
		Template2 template = new Template2(fc, tplId, type);
		if (siteUrl != null && siteUrl.length() > 0) {
			template.setSiteUrl(siteUrl);
		}

		if (dataPool != null && dataPool.containsKey("idxData") && dataPool.containsKey("idxId")) {
			Map<String, String> preTagContent = new HashMap<String, String>();
			preTagContent.put((String)dataPool.get("idxId"), (String)dataPool.get("idxData"));
			template.setPreTagContent(preTagContent);
		}
		

		return template.render(type, data);
	}

	public String render(FormConfig fc, String templateStr, Map<String, Object> data, RenderType type, String siteUrl) throws Exception {
		
		return render(fc,templateStr,data,type,siteUrl,null);
	}

	public String render(FormConfig fc, String templateStr, Map<String, Object> data, RenderType type, String siteUrl,Map<String,Object> dataPool) throws Exception {
		if (templateStr == null) {
			throw new Exception("templateStr 不能为null");
		}
		if (data == null) {
			data = new java.util.HashMap<String, Object>();
		}
		Template2 template = new Template2(fc, templateStr, type);
		if (siteUrl != null && siteUrl.length() > 0) {
			template.setSiteUrl(siteUrl);
		}
		
		/***cds add for 往template注入全页预览时的碎片数据,以实现无需保存的全页预览****/
		if (dataPool != null && dataPool.containsKey("idxData") && dataPool.containsKey("idxId")) {
			Map<String, String> preTagContent = new HashMap<String, String>();
			preTagContent.put((String)dataPool.get("idxId"), (String)dataPool.get("idxData"));
			template.setPreTagContent(preTagContent);
		}
		
		return template.render(type, data);
	}
	/**
	 * 预览，指定模版ID、记录ID和注入参数，提供注入点
	 * 
	 * @param fc
	 * @param tplId
	 * @param dataId
	 * @param type
	 * @param extra
	 *            注入到渲染数据中的数据Map，会合并Map到data，冲突时覆盖默认值
	 * @param extraDataPool
	 *            注入到dataPool中的数据Map，会合并Map到dataPool，冲突时覆盖默认值。会覆盖dataPool中所有冲突值，谨慎使用。
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> preview(FormConfig fc, Integer tplId, Integer dataId, RenderType type,
			Map<String, Object> extra, Map<String, Object> extraDataPool) throws Exception {
		Map<String, Object> data = null;
		if (dataId == null) {
			dataId = (Integer) extra.get("dataId");
			data = extra;
		} else {
			data = getData(fc, dataId);
			if (extra != null && extra.size() > 0) {
				data.putAll(extra);
			}
		}
		
		return preview(fc, tplId, data, type, extraDataPool);
	}
	
	/**
	 * 预览，指定模版ID、记录ID和注入参数，提供注入点
	 * 
	 * @param fc
	 * @param tplId
	 * @param dataId
	 * @param type
	 * @param extraDataPool
	 *            注入到dataPool中的数据Map，会合并Map到dataPool，冲突时覆盖默认值。会覆盖dataPool中所有冲突值，谨慎使用。
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> preview(FormConfig fc, Integer tplId, Integer dataId, RenderType type, Map<String, Object> extraDataPool) throws Exception {
		Map<String, Object> data = getData(fc, dataId);
		return preview(fc, tplId, data, type, extraDataPool);
	}
	
	/**
	 * 预览，指定模版ID、记录ID和注入参数，提供注入点
	 * 
	 * @param fc
	 * @param tplId
	 * @param data
	 *            注入到dataPool中的xdata中
	 * @param type
	 * @param extraDataPool
	 *            注入到dataPool中的数据Map，会合并Map到dataPool，冲突时覆盖默认值。会覆盖dataPool中所有冲突值，谨慎使用。
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> preview(FormConfig fc, Integer tplId, Map<String, Object> data, RenderType type,
		 Map<String, Object> extraDataPool) throws Exception {
		Map<String, String> preTagContent = null;
		Map<String, Object> dataPool = new HashMap<String, Object>();
		
		Integer dataId = (Integer)data.get("id");
		dataId = dataId==null?0:dataId;
		dataPool.put("dataId", dataId);
		dataPool.put("tplId", tplId);

		/*兼容Template.java */
		Map<String, Object[]> form = null;
		if (extraDataPool != null && extraDataPool.containsKey("form")) {
			form = (Map<String, Object[]>)extraDataPool.get("form");
//			if (form != null) {
//				if (form.get("idxId")!=null) {
//					preTagContent = new HashMap<String, String>();
//					preTagContent.put(form.get("idxId")[0].toString(), form.get("idxData")[0].toString());
//				}
//			}
		}
		
		if (extraDataPool != null && extraDataPool.containsKey("idxData") && extraDataPool.containsKey("idxId")) {
			preTagContent = new HashMap<String, String>();
			preTagContent.put((String)extraDataPool.get("idxId"), (String)extraDataPool.get("idxData"));
		}
		
		Template2 template = new Template2(fc, tplId.toString(), preTagContent, type);
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		FormPlugin helper  = (FormPlugin)((ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory")).getP("form");
		helper.init(nodeId, formId, dataId, form);
		dataPool.put("helper",helper);
		dataPool.put("template", template);
		dataPool.put("id", dataId);
		dataPool.put("type", type);
		/*兼容*/
		
		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		dataPool.put("renderType", type);
		dataPool.put("xdata", data);
		
		if (extraDataPool != null && extraDataPool.size() > 0) {
			dataPool.putAll(extraDataPool);
		}

		return preview(nodeId, formId, dataPool);
	}
	
	public Map<String, Object> preview(int nodeId, int formId, Map<String, Object> dataPool) throws Exception {
		long t1 = System.currentTimeMillis();
		scriptService.run(nodeId, dataPool, ScriptType.form, String.valueOf(formId), "2");
		log.info("=======FormService.preview() : " + (System.currentTimeMillis() - t1) + " ms");
		return dataPool;
	}

	public Map<String, Object> publish(FormConfig fc, Integer tplId, Integer dataId, Map<String, Object> extraDataPool)
			throws Exception {
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		Map<String, Object> dataPool = new HashMap<String, Object>();
		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		dataPool.put("tplId", tplId);
		dataPool.put("dataId", dataId);
		
		//兼容Template.java
		dataPool.put("id", dataId);
		FormPlugin helper  = (FormPlugin)((ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory")).getP("form");
		helper.init(nodeId, formId, dataId);
		dataPool.put("helper", helper);	
		//兼容end
		
		if (extraDataPool != null) {
			dataPool.putAll(extraDataPool);
		}
		scriptService.run(nodeId, dataPool, ScriptType.form, Integer.toString(formId), "3");
		return dataPool;
	}

	/**
	 * 返回新建Form的ID
	 * 
	 * @see called by com.ifeng.cmpp.action.XFormAction::saveConfig()
	 * @param conf
	 * @param nodeId
	 * @return int
	 * @throws SQLException
	 * @throws JSONException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static int createFormConfig(ConfigParser conf, Integer nodeId) throws SQLException, JSONException,
			IOException, TemplateException {
		if (nodeId == null) {
			nodeId = 0;
		}
		JSONObject parseResult = conf.doParseConfig();
		String formName = conf.getFormName();
		JSONObject cf = new JSONObject();
		JSONObject fieldsConfig = parseResult.getJSONObject("fieldsConfig");
		fieldsConfig.put("fields", parseResult.getJSONArray("allFields"));

		cf.put("searchConfig", parseResult.getJSONObject("searchConfig"));
		cf.put("fieldsConfig", fieldsConfig);

		String formConfig = parseResult.getJSONObject("formConfig").toString();

		int formId = FormConfig.createFormConfig(formName, cf.toString(), nodeId);
		// 保存view的config
		int viewId = ViewConfig.createView(formId, formName, formConfig);
		// 保存list
		JSONObject listCfgJson = parseResult.getJSONObject("listConfig");
		listCfgJson.put("viewId", viewId);
		String listConfigStr = listCfgJson.toString();
		ListConfig.createConfig(formId, formName, listConfigStr);
		FormConfig cfg = FormConfig.getInstance(nodeId, formId);
		TableSchemaHelper.createTable(cfg);
		return formId;
	}

	public Map<String, Object> runScript(FormConfig fc, String key, String ids) throws Exception {
		Map<String, Object> dataPool = new HashMap<String, Object>();
		Integer nodeId = fc.getNodeId();
		Integer formId = fc.getFormId();
		dataPool.put("ids", ids);
		dataPool.put("nodeId", fc.getNodeId());
		dataPool.put("formId", fc.getFormId());
		scriptService.run(nodeId, dataPool, ScriptType.form, formId.toString(), key);
		return dataPool;
	}

	/**
	 * 推送到搜索引擎
	 * 
	 * @param FormConfig
	 *            fc
	 * @param int dataId
	 * @param Map
	 *            <String, Object> xform
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void pushIntoSearch(FormConfig fc, int dataId) throws JSONException, SQLException {
		// 推送到搜索引擎
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		SearchHelper shp = new SearchHelper(nodeId, formId, searchService);
		Map<String, Object> data = getData(fc, dataId);
		shp.putData(dataId, data);
	}

	/**
	 * 删除搜索引擎对应数据
	 * 
	 * @param fc
	 * @param dataId
	 * @param xform
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void removeFromSearch(FormConfig fc, int dataId) throws JSONException, SQLException {
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();
		String coreName = "nodeId_" + nodeId + "_from_" + formId;
		List<Object> list = new ArrayList<Object>();
		Integer _id = new Integer(dataId);
		list.add(_id.toString());
		Map<String, Object> map = searchService.deleteDate(list, coreName);
		if (map != null && map.get("status").equals("0")) {
			log.info("success doing deleteData " + coreName + " id = " + dataId);
		} else {
			log.info("fail to deleteData" + coreName + " ids = " + dataId);
		}
	}
	
	/**
	 * 调用saveAllData方法保存数据，会依次序执行<br/>
	 * {保存前脚本}-保存-{保存后脚本}-推送到搜索引擎<br/>
	 * PS1:保存前和保存后通过dataPool中的verify决定是否继续执行
	 * 
	 * @param fc
	 * @param dataId
	 * @param xform
	 * @return Map&lt;String, Object&gt; 最终返回保存后脚本处理后的数据Map
	 * @throws Exception
	 */
	public Map<String, Object> saveAllData(FormConfig fc, int dataId, Map<String, Object> xform) throws Exception {
		return saveAllData(fc, dataId, xform, null);
	}

	/**
	 * 调用saveAllData方法保存数据，会依次序执行<br/>
	 * {保存前脚本}-保存-{保存后脚本}-推送到搜索引擎<br/>
	 * PS1:保存前和保存后通过dataPool中的verify决定是否继续执行<br/>
	 * 
	 * @param fc
	 * @param dataId
	 * @param xform
	 * @param injection 在保存前脚本执行前注入injection到dataPool,但是无法通过注入修改dataPool中的userId和userName
	 * @return Map&lt;String, Object&gt; 最终返回保存后脚本处理后的数据Map
	 * @throws Exception（message格式："出错步骤;出错信息"）
	 */
	public Map<String, Object> saveAllData(FormConfig fc, int dataId, Map<String, Object> xform, Map<String, Object>injection) throws FormSaveException {
		Map<String, Object> dataPool = new HashMap<String, Object>();
		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();

		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		dataPool.put("dataId", dataId);
		dataPool.put("verify", true);
		dataPool.put("doPublish", true);
		dataPool.put("xform", xform);

		//兼容变量
		String action = (dataId == 0 ? "add" : "update");
		dataPool.put("action", action);
		dataPool.put("id", dataId);
		
		if(injection!=null) {
			dataPool.putAll(injection);
		}

		String userId = "";
		String userName = "";
		try {
			userId = AuthorzationUtil.getUserId();
			userName = AuthorzationUtil.getUserName();
		} catch (Exception e) {
			log.error("获取用户失败:" + e.getMessage());
		}
		dataPool.put("userId", userId);
		dataPool.put("userName", userName);

		// 运行保存前脚本
		try {
			scriptService.run(nodeId, dataPool, ScriptType.form, Integer.toString(formId), "0");
		} catch (Exception e) {
			log.error("运行保存前脚本异常，userId:" + userId + "|userName:" + userName + "|formId:" + formId + "|dataId:" + dataId + "|msg:" + e.getMessage());
			throw new FormSaveException(e.getMessage(), e, "保存前脚本", nodeId, formId, dataId);
		}
		boolean verify = (Boolean) dataPool.get("verify");
		if (!verify) {
			// 如果verify为false，则不继续执行保存后。
			return dataPool;
		}

		// 运行保存
		xform = (Map<String, Object>) dataPool.get("xform");
		int rid;
		try {
			rid = saveData(fc, dataId, xform);
		} catch (Exception e) {
			log.error("运行保存异常，userId:" + userId + "|userName:" + userName + "|formId:" + formId + "|dataId:" + dataId + "|msg:" + e.getMessage());
			throw new FormSaveException(e.getMessage(), e, "保存", nodeId, formId, dataId);
		}
		if (dataId == 0) {
			dataId = rid;
			dataPool.put("dataId", dataId);
			
			/*
			 * TODO 
			 * 兼容老Form体系
			 */
			FormPlugin helper = (FormPlugin)dataPool.get("helper");
			if(helper != null){
				helper.setDataId(dataId);
				helper.helper.setId(dataId);
			}
		}

		// 运行保存后脚本
		try {
			scriptService.run(nodeId, dataPool, ScriptType.form, Integer.toString(formId), "1");
		} catch (Exception e) {
			log.error("运行保存后脚本异常，userId:" + userId + "|userName:" + userName + "|formId:" + formId + "|dataId:" + dataId + "|msg:" + e.getMessage());
			throw new FormSaveException(e.getMessage(), e, "保存后脚本", nodeId, formId, dataId);
		}
		log.info("用户user='" + userId + ":" + userName + "' 修改表单formId=" + formId + ",id=" + dataId);
		
		try {
			// 推送到搜索引擎
			pushIntoSearch(fc, dataId);
		} catch (Exception e) {
			log.info("保存数据到数据库成功，推送到搜索引擎出现异常。" + "用户user='" + userId + ":" + userName + "' 修改表单formId=" + formId
					+ ",id=" + dataId+ ",msg=" + e.getMessage());
			throw new FormSaveException(e.getMessage(), e, "推送到搜索引擎", nodeId, formId, dataId);
		}

		return dataPool;
	}

	/**
	 * 调用save方法保存数据，会依次序执行<br/>
	 * {保存前脚本}-保存-{保存后脚本}-推送到搜索引擎-{(预览脚本)发布脚本}<br/>
	 * PS1:保存前和保存后通过dataPool中的verify决定是否继续执行 PS2:发布脚本会自动调用预览脚本<br/>
	 * 
	 * @param fc
	 * @param dataId 为0时插入一条新数据
	 * @param xform 
	 * @return Map&lt;String, Object&gt;
	 * @throws Exception
	 */
	public Map<String, Object> save(FormConfig fc, int dataId, Map<String, Object> xform) throws Exception {
		return save(fc, dataId, xform, null);
	}
	
	/**
	 * 调用save方法保存数据，会依次序执行<br/>
	 * {保存前脚本}-保存-{保存后脚本}-推送到搜索引擎-{(预览脚本)发布脚本}<br/>
	 * PS1:保存前和保存后通过dataPool中的verify和doPublish同时决定是否继续执行 <br/>
	 * PS2:发布脚本会自动调用预览脚本<br/>
	 * 
	 * @param fc
	 * @param dataId
	 * @param xform
	 * @param injection
	 * @return Map&lt;String, Object&gt;
	 * @throws Exception （message格式："出错步骤;出错信息"）
	 */
	public Map<String, Object> save(FormConfig fc, int dataId, Map<String, Object> xform, Map<String, Object> injection) throws FormSaveException {
		Map<String, Object> dataPool = saveAllData(fc, dataId, xform, injection);

		// 运行发布脚本
		try {
			Boolean verify = (Boolean) dataPool.get("verify");
			Boolean doPublish = (Boolean) dataPool.get("doPublish");
			if (!verify || !doPublish) {
				return dataPool;
			}
			dataId = (Integer)dataPool.get("dataId");
			publish(fc, null, dataId, null);
		} catch (Exception e) {
			log.error("FormService::save, saveAllData成功，publish失败:" + e.getMessage());
			throw new FormSaveException(e.getMessage(), e, "发布脚本", fc.getNodeId(), fc.getFormId(), dataId);
		}
		return dataPool;
	}

	/**
	 * 只保存数据到关系数据库或NOSQL数据库
	 * 
	 * @param fc
	 * @param dataId
	 *            主键ID，为null时未插入
	 * @param xform
	 *            更新或插入用的map
	 * @return int 返回主键ID
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int saveData(FormConfig fc, int dataId, Map<String, Object> xform) throws SQLException, JSONException {
		String userId = "";
		String userName = "";
		try {
			userId = AuthorzationUtil.getUserId();
			userName = AuthorzationUtil.getUserName();
		} catch (Exception e) {
			log.error("获取用户失败:" + e.getMessage());
		}

		int nodeId = fc.getNodeId();
		int formId = fc.getFormId();

		// 筛选出关系数据库字段
		Map<String, Object> fields = new HashMap<String, Object>();
		List<ControlDbItem> fArr = fc.getDbItem();
		for (ControlDbItem f : fArr) {
			String key = f.getFieldName();
			if (!key.equals("")) {
				Object value = xform.get(key);
				if (value != null) {
					if (value.equals("")) {
						value = null;
					}
					fields.put(key, value);
				}
			}
		}

		// 更新关系数据库
		int type;
		int nid = dataId;
		if (dataId != 0) {
			update(fc, dataId, fields);
			type = 2;
		} else {
			dataId = insert(fc, fields);
			nid = dataId;
			type = 1;
		}

		// -----记录用户修改的数据内容----------
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("userName", userName);
		map.put("nodeId", nodeId);
		map.put("formId", formId);
		map.put("action", type == 1 ? "insert" : "update");
		map.put("dataId", dataId);
		map.put("xform", xform);
		UserLogUtil.getInstance(Integer.toString(nodeId)).log(map);

		// 处理保存到nosql的操作
		if(cmppDBPlugin==null){
			cmppDBPlugin = (CmppDBPlugin)pluginFactory.getP("cmppDB");
		}
		Vector<ControlDbItem> nosqlItems = fc.getNosqlItem();
		for (ControlDbItem item : nosqlItems) {
			String key = item.getFieldName();
			if (!key.equals("")) {
				Object value = xform.get(key);
				if (value != null) {
					// 将数据存到nosql
					String sk = nodeId + "-" + formId + "-" + dataId + "-" + key;
					cmppDBPlugin.put(sk, value);
				}
			}
		}

		// 推送订阅
		Map<String, Object> pool = new HashMap<String, Object>();
		pool.put("data", xform);
		pool.put("type", type);
		pool.put("id", nid);
		try {
			subscribeService.process(nodeId, formId, pool);
		} catch (Exception e) {
			log.error(e);
		}

		return dataId;
	}

	/**
	 * 更新关系数据库
	 * 
	 * @param fc
	 * @param dataId
	 * @param fields
	 * @return int 返回影响的行数
	 * @throws SQLException
	 */
	private static int update(FormConfig fc, Integer dataId, Map<String, Object> fields) throws SQLException {
		Object[] parms;
		if (fields.isEmpty() || dataId == null) {
			return 0;
		} else {
			parms = new Object[fields.size()];
		}
		String sql = "update %s set %s where id=" + dataId.toString();
		int k = 0;
		String fs = "";
		for (String key : fields.keySet()) {
			if (fs.equals("")) {
				fs = "`" + key + "`=?";
			} else {
				fs = fs + ",`" + key + "`=?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		String tb = fc.getTableName();
		
		//先更新主表，若数据存在于主表，则返回影响的行数为1，否则返回0，
		//返回0时去历史表更新数据，因为这条数据有可能已经转移到历史表中
		int rows = MySQLHelper.ExecuteNoQuery(String.format(sql, tb, fs), parms);
		if(rows==0){
			//尝试从历史数据表里查询 cds add at 2013/12/12
			String bakTableName = tb +"_bak";
			String tsql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME='"+ tb + "'";
			Vector<Map<String, Object>> bakTables = MySQLHelper.ExecuteSql(tsql, null);
			if(bakTables.size()>0){
				rows = MySQLHelper.ExecuteNoQuery(String.format(sql, bakTableName, fs), parms);
			}
		}
		
		/*
		if (fc.hasPartition()) {
			Integer maxId = DbEnv.getInstance().getMaxBakId(tb + "_bak");
			if (dataId <= maxId) {
				tb += "_bak";
			}
		}
		sql = String.format(sql, tb, fs);
		*/
		return rows;
	}

	/**
	 * 插入关系数据库
	 * 
	 * @param fc
	 *            FormConfig
	 * @param fields
	 *            Map&lt;String, Object&gt;
	 * @return int 返回数据ID
	 * @throws SQLException
	 */
	private static int insert(FormConfig fc, Map<String, Object> fields) throws SQLException {
		Object[] parms;
		if (fields.isEmpty()) {
			return 0;
		} else {
			parms = new Object[fields.size()];
		}
		String sql = "";

		sql = "insert into %s(%s) values(%s)";
		String fs = "";
		String vs = "";
		int k = 0;
		for (String key : fields.keySet()) {
			if (fs.equals("")) {
				fs = "`" + key + "`";
				vs = "?";
			} else {
				fs = fs + ",`" + key + "`";
				vs = vs + "," + "?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		sql = String.format(sql, fc.getTableName(), fs, vs);
		return Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));
	}

	/**
	 * 获取对应节点ID和数据表ID的{表单页}列表和{列表页}列表的JSON字符串
	 * formId为null或0时，返回对应nodeId的全列表
	 * nodeId为0，返回系统数据表对应的列表
	 * 
	 * @demo 
	 *       [{"id":3018,"title":"節目單列表","tableName":"frm_3018","list":[{"id":266
	 *       ,"title":"我的表单"}],"forms":[{"id":283,"title":"我的表单"}]}]
	 * @param nodeId
	 *            Integer
	 * @param formId
	 *            Integer
	 * @return String
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static String getFormListConfig(Integer nodeId, Integer formId) throws SQLException, JSONException {
		if (nodeId == null) {
			nodeId = 0;
		}
		if (formId == null) {
			formId = 0;
		}
		String fWhere = "";
		String sql;
		if (formId != 0) {
			fWhere = " and formId=" + formId;
			sql = "select formId,name,tableName,isSysForm from cmpp_formConfig where nodeid in (0," + nodeId + ")" + fWhere
					+ " order by formId desc";
		} else {
			sql = "select formId,name,tableName,isSysForm from cmpp_formConfig where nodeid =" + nodeId + " order by formId desc";
		}
		JSONArray rtn = new JSONArray();

		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, null);
		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> data = datas.get(i);
			JSONObject item = new JSONObject();
			int _formId = (Integer) data.get("formId");
			item.put("id", _formId);
			item.put("title", data.get("name"));
			item.put("tableName", data.get("tableName"));
			item.put("isSysForm", (Integer) data.get("isSysForm"));
			JSONArray viewArr = new JSONArray();
			sql = "select viewId,name from cmpp_viewConfig where formId=" + _formId + " order by viewId desc";
			Vector<Map<String, Object>> vdatas = MySQLHelper.ExecuteSql(sql, null);
			for (Map<String, Object> vd : vdatas) {
				JSONObject view = new JSONObject();
				view.put("id", vd.get("viewId"));
				view.put("title", vd.get("name"));
				viewArr.put(view);
			}
			item.put("forms", viewArr);
			JSONArray listArr = new JSONArray();
			sql = "select listId,name from cmpp_listConfig where formId=" + _formId + " order by listId desc";
			Vector<Map<String, Object>> ldatas = MySQLHelper.ExecuteSql(sql, null);
			for (Map<String, Object> ld : ldatas) {
				JSONObject list = new JSONObject();
				list.put("id", ld.get("listId"));
				list.put("title", ld.get("name"));
				listArr.put(list);
			}
			item.put("list", listArr);
			rtn.put(item);
		}
		return rtn.toString();
	}

	public Template getTemplate(int tplid) {
		if (tplid == 0) {
			return null;
		}
		Template t = null;
		try {
			t = templateMapper.getById(tplid);
		} catch (Exception e) {
			log.error(e);
		}
		return t;
//		TemplateModel rtn = null;
//		String sql = "select id,name,content,enable,dataFormId,dataId from cmpp_template where id=" + tplid;
//		Vector<Map<String, Object>> data;
//		try {
//			data = MySQLHelper.ExecuteSql(sql, null);
//			if (data.size() > 0) {
//				rtn = formatTemplate(data.get(0));
//			}
//		} catch (SQLException e) {
//			log.error(e);
//		}

//		return rtn;
	}

//	private TemplateModel formatTemplate(Map<String, Object> data) {
//		TemplateModel rtn = new TemplateModel();
//		rtn.setId((Integer) data.get("id"));
//		rtn.setName((String) data.get("name"));
//		rtn.setContent((String) data.get("content"));
//		rtn.setEnable((Integer) data.get("enable"));
//		rtn.setDataId((Integer) data.get("dataId"));
//		rtn.setFormId((Integer) data.get("dataFormId"));
//		return rtn;
//	}

	/**
	 * 根据表单试图模板配置
	 * 
	 * @TODO 挪到TEMPLATE类中
	 */
	public static JSONObject getTemplateJson(int viewId) throws Exception {
		String sql;
		sql = "select config from cmpp_viewConfig where viewId=" + viewId;
		Map<String, Object> data = MySQLHelper.ExecuteSql(sql, null).get(0);
		String config = data.get("config").toString();
		JSONObject configJson = new JSONObject(config);
		JSONObject tplJson = configJson.getJSONObject("form").getJSONObject("template");
		return tplJson;
	}

	public List<Integer> getFormIdList(FormConfig fc, int startId, int limit) {
		List<Integer> rtn = new ArrayList<Integer>();
		String sql = "";
		try {
			String tbName = fc.getTableName();
			sql = "select id from `" + tbName + "` where id>=" + startId + " order by id limit " + limit;
			Vector<Map<String, Object>> data = MySQLHelper.ExecuteSql(sql, null);
			if (data.size() > 0) {
				for (Map<String, Object> d : data) {
					rtn.add((Integer) d.get("id"));
				}
			}
		} catch (Exception ex) {
			log.error(sql + "获取表单id列表出现错误" + ex.getMessage());
		}
		return rtn;
	}

	public Integer getFormLastId(FormConfig fc) {
		String sql = "";
		try {
			String tbName = fc.getTableName();
			sql = "select id from `" + tbName + "` order by id desc limit 1";
			Vector<Map<String, Object>> rtn = MySQLHelper.ExecuteSql(sql, null);
			if (rtn.size() > 0) {
				return (Integer) rtn.get(0).get("id");
			}
		} catch (Exception ex) {
			log.error(sql + "获取表单最大ID出现错误" + ex.getMessage());
		}
		return 0;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public SearchService_V2 getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService_V2 searchService) {
		this.searchService = searchService;
	}

	public SubscribeService getSubscribeService() {
		return subscribeService;
	}

	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
	}

	public TemplateMapper<Template> getTemplateMapper() {
		return templateMapper;
	}

	public void setTemplateMapper(TemplateMapper<Template> templateMapper) {
		this.templateMapper = templateMapper;
	}
	
	public ScriptPluginFactory getPluginFactory() {
		return pluginFactory;
	}

	public void setPluginFactory(ScriptPluginFactory pluginFactory) {
		this.pluginFactory = pluginFactory;
	}

}
