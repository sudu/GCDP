package com.me.GCDP.xform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.me.GCDP.mapper.NodeMapper;
import com.me.GCDP.model.Node;
import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.env.NodeEnv;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.search.SearchService_V2;
import com.me.json.JSONArray;
import com.me.json.JSONObject;


/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :dengzc
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason                    </p>
 * <p>2013-12-5        dengzc               create the class             </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class SysInitializationService {
	// private static final Log log = LogFactory.getLog(SysFormService.class);
	// 节点环境变量
	public static final String KEY_docFieldAdapterCfg = "docFieldAdapterCfg";// 文章字段适配配置Key
	public static final String KEY_columnFieldAdapterCfg = "columnFieldAdapterCfg";// 栏目字段适配配置Key
	public static final String DESC_docFieldAdapterCfg = "文章字段适配配置";// 文章字段适配配置描述
	public static final String DESC_columnFieldAdapterCfg = "栏目字段适配配置";// 栏目字段适配配置描述

	private static final String docSysFormCfgPath = "/config/sysDocFormCfg.json";// 文章系统表单配置文件路径
	private static final String columnSysFormCfgPath = "/config/sysColumnFormCfg.json";// 栏目系统表单配置文件路径
	
	public static final String TEMPLATEENVKEY = "templateAdapterCfg";
	private static final String TEMPLATEENVKEYPATH = "/config/sysTemplateFormCfg.json";//配置文件路径
	public static final String TEMPLATEENVKEYDESC = "自定义碎片模板表环境配置";// 环境变量描述
	
	public static final String LOGICENVKEY = "logicAdapterCfg";
	private static final String LOGICENVKEYPATH = "/config/sysLogicFormCfg.json";//配置文件路径
	public static final String LOGICENVKEYDESC = "自定义碎片逻辑表环境配置";// 环境变量描述
	
	public static final String CUSTOMIDXENVKEY = "customIdxAdapterCfg";
	private static final String CUSTOMIDXENVKEYPATH = "/config/sysCustomIdxFormCfg.json";//配置文件路径
	public static final String CUSTOMIDXENVKEYDESC = "自定义碎片表环境配置";// 环境变量描述
	
	private SearchService_V2 searchService;
	
	/**
	 * 创建节点后，节点初始化操作；多次调用时为重置操作
	 * @param nodeId
	 * @throws Exception
	 */
	public void init(Integer nodeId) throws Exception {
		//TODO 事务回滚
		
		// 创建文章系统表单
		createDocSysForm(nodeId);
		// 创建栏目系统表单
		createColumnSysForm(nodeId);
		//创建自定义系统表单
		initCustomIdxSysForm(nodeId);
	}

	/**
	 * 创建文章系统表单
	 */
	private void createDocSysForm(Integer nodeId) throws Exception {

		// 1.读取配置
		String pre = SysInitializationService.class.getResource("/").getPath().toString();
		String strConfig = getFileContent(pre + docSysFormCfgPath);

		// 2.创建
		Integer formId = 0;
		Integer viewId = 0;
		Integer listId = 0;
		JSONObject config = new JSONObject(strConfig);
		// 导入formConfig
		String formConfig = config.getJSONObject("formConfig").getString("config");
		String formName = config.getJSONObject("formConfig").getString("name");
		int isSysForm = config.getJSONObject("formConfig").getInt("isSysForm");
		formId = getSysFormId(nodeId, formName, isSysForm);
		if (formId > 0) {
			// 已经存在文章系统表单时
			viewId = getSysFormViewId(formId);
			listId = getSysFormListId(formId);
		} else {
			// 不存在文章系统表单时，创建
			formId = FormConfig.createFormConfig(formName, formConfig, nodeId, isSysForm);
			// 创建表结构
			TableSchemaHelper.createTable(FormConfig.getInstance(nodeId, formId));
			// 导入viewConfig
			JSONArray viewConfigs = config.getJSONArray("viewConfig");
			for (int i = 0; i < viewConfigs.length(); i++) {
				String vc = viewConfigs.getJSONObject(i).getString("config");
				int id = ViewConfig.createView(vc, formId);
				// 取第一个视图ID
				if (i == 0) {
					viewId = id;
				}
			}
			// 导入listConfig
			JSONArray listConfigs = config.getJSONArray("listConfig");
			for (int i = 0; i < listConfigs.length(); i++) {
				String lc = listConfigs.getJSONObject(i).getString("config");
				String name = listConfigs.getJSONObject(i).getString("name");
				int id = ListConfig.createConfig(formId, name, lc);
				// 取第一个列表视图ID
				if (i == 0) {
					listId = id;
				}
			}
		}

		// 3.写入节点环境变量
		@SuppressWarnings("unchecked")
		NodeMapper<Node> nodeMapper = (NodeMapper<Node>) SpringContextUtil.getBean("nodeMapper");
		Node node = new Node();
		node.setId(nodeId);
		List<Node> list = nodeMapper.get(node);
		if (list == null || list.size() < 1) {
			throw new RuntimeException("节点【" + nodeId + "】没有节点配置数据！");
		}
		List<Map<String, Object>> envList = list.get(0).getEnvMap();
		boolean isExist = false;
		for (Map<String, Object> map : envList) {
			if (KEY_docFieldAdapterCfg.equals(map.get("key").toString())) {
				map.put("value", "{\"sysFormId\":" + formId + ",\"viewId\":" + viewId + ",\"listId\":" + listId + ",\"localFormId\":0,\"map\":{}}");
				map.put("desc", DESC_docFieldAdapterCfg);
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("key", KEY_docFieldAdapterCfg);
			m.put("value", "{\"sysFormId\":" + formId + ",\"viewId\":" + viewId + ",\"listId\":" + listId + ",\"localFormId\":0,\"map\":{}}");
			m.put("desc", DESC_docFieldAdapterCfg);
			envList.add(m);
		}

		node.setEnvMap(envList);
		nodeMapper.update(node);
		NodeEnv.getNodeEnvInstance().refresh();
		
		//4.更新搜索引擎
		SearchHelper shp = new SearchHelper(nodeId, formId, searchService);
		shp.createInedex();
	}

	/**
	 * 创建栏目系统表单
	 */
	private void createColumnSysForm(Integer nodeId) throws Exception {
		// 1.读取配置
		String pre = SysInitializationService.class.getResource("/").getPath().toString();
		String strConfig = getFileContent(pre + columnSysFormCfgPath);

		// 2.创建
		Integer formId = 0;
		Integer viewId = 0;
		Integer listId = 0;
		JSONObject config = new JSONObject(strConfig);
		// 导入formConfig
		String formConfig = config.getJSONObject("formConfig").getString("config");
		String formName = config.getJSONObject("formConfig").getString("name");
		int isSysForm = config.getJSONObject("formConfig").getInt("isSysForm");
		formId = getSysFormId(nodeId, formName, isSysForm);
		if (formId > 0) {
			// 已经存在栏目系统表单
			viewId = getSysFormViewId(formId);
			listId = getSysFormListId(formId);
		} else {
			// 不存在栏目系统表单，创建
			formId = FormConfig.createFormConfig(formName, formConfig, nodeId, isSysForm);
			// 创建表结构
			TableSchemaHelper.createTable(FormConfig.getInstance(nodeId, formId));
			// 导入viewConfig
			JSONArray viewConfigs = config.getJSONArray("viewConfig");
			for (int i = 0; i < viewConfigs.length(); i++) {
				String vc = viewConfigs.getJSONObject(i).getString("config");
				int id = ViewConfig.createView(vc, formId);
				// 取第一个视图ID
				if (i == 0) {
					viewId = id;
				}
			}
			// 导入listConfig
			JSONArray listConfigs = config.getJSONArray("listConfig");
			for (int i = 0; i < listConfigs.length(); i++) {
				String lc = listConfigs.getJSONObject(i).getString("config");
				String name = listConfigs.getJSONObject(i).getString("name");
				int id = ListConfig.createConfig(formId, name, lc);
				// 取第一个列表视图ID
				if (i == 0) {
					listId = id;
				}
			}
		}

		// 3.写入节点环境变量
		@SuppressWarnings("unchecked")
		NodeMapper<Node> nodeMapper = (NodeMapper<Node>) SpringContextUtil.getBean("nodeMapper");
		Node node = new Node();
		node.setId(nodeId);
		List<Node> list = nodeMapper.get(node);
		if (list == null || list.size() < 1) {
			throw new RuntimeException("节点【" + nodeId + "】没有节点配置数据！");
		}
		List<Map<String, Object>> envList = list.get(0).getEnvMap();
		boolean isExist = false;
		for (Map<String, Object> map : envList) {
			if (KEY_columnFieldAdapterCfg.equals(map.get("key").toString())) {
				map.put("value", "{\"sysFormId\":" + formId + ",\"viewId\":" + viewId + ",\"listId\":" + listId + ",\"localFormId\":0,\"map\":{}}");
				map.put("desc", DESC_columnFieldAdapterCfg);
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("key", KEY_columnFieldAdapterCfg);
			m.put("value", "{\"sysFormId\":" + formId + ",\"viewId\":" + viewId + ",\"listId\":" + listId + ",\"localFormId\":0,\"map\":{}}");
			m.put("desc", DESC_columnFieldAdapterCfg);
			envList.add(m);
		}

		node.setEnvMap(envList);
		nodeMapper.update(node);
		NodeEnv.getNodeEnvInstance().refresh();
		
		//4.更新搜索引擎
		SearchHelper shp = new SearchHelper(nodeId, formId, searchService);
		shp.createInedex();
	}

	/**
	 * 创建自定义系统表单
	 */
	private void initCustomIdxSysForm(Integer nodeId) throws Exception {
		// 1.创建系统模板表
		createCustomIdxSysForm(nodeId,TEMPLATEENVKEYPATH,TEMPLATEENVKEY,TEMPLATEENVKEYDESC);
		//2.创建系统逻辑表
		createCustomIdxSysForm(nodeId,LOGICENVKEYPATH,LOGICENVKEY,LOGICENVKEYDESC);
		//3.创建自定义碎片表
		createCustomIdxSysForm(nodeId,CUSTOMIDXENVKEYPATH,CUSTOMIDXENVKEY,CUSTOMIDXENVKEYDESC);
		
	}
	
	private void createCustomIdxSysForm(Integer nodeId,String configFilePath,String envKey,String envDesc) throws Exception{
		// 1.读取配置
		String pre = SysInitializationService.class.getResource("/").getPath().toString();
		String strConfig = getFileContent(pre + configFilePath);

		// 2.创建
		Integer formId = 0;
		List<Integer> viewIdList = new ArrayList<Integer>();
		Integer listId = 0;
		JSONObject config = new JSONObject(strConfig);
		// 导入formConfig
		String formConfig = config.getJSONObject("formConfig").getString("config");
		String formName = config.getJSONObject("formConfig").getString("name");
		int isSysForm = config.getJSONObject("formConfig").getInt("isSysForm");
		formId = getSysFormId(nodeId, formName, isSysForm);
		if (formId > 0) {
			// 已经存在系统表单时
			viewIdList = getSysIdxFormViewId(formId);
			listId = getSysFormListId(formId);
		} else {
			// 不存在系统表单时，创建
			formId = FormConfig.createFormConfig(formName, formConfig, nodeId, isSysForm);
			// 创建表结构
			TableSchemaHelper.createTable(FormConfig.getInstance(nodeId, formId));
			// 导入viewConfig
			JSONArray viewConfigs = config.getJSONArray("viewConfig");
			for (int i = 0; i < viewConfigs.length(); i++) {
				String vc = viewConfigs.getJSONObject(i).getString("config");
				int id = ViewConfig.createView(vc, formId);
				viewIdList.add(id);
			}
			// 导入listConfig
			JSONArray listConfigs = config.getJSONArray("listConfig");
			for (int i = 0; i < listConfigs.length(); i++) {
				String lc = listConfigs.getJSONObject(i).getString("config");
				String name = listConfigs.getJSONObject(i).getString("name");
				int id = ListConfig.createConfig(formId, name, lc);
				// 取第一个列表视图ID
				if (i == 0) {
					listId = id;
				}
			}
		}

		//3.
		//导入脚本
		ScriptService scriptService = (ScriptService) SpringContextUtil.getBean("scriptService");
		//获取保存前脚本
		String script ="";
		if(config.has("beforSave"))
		{
			script = config.getString("beforSave");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"0");
		}
		//获取保存后脚本
		if(config.has("afterSave"))
		{
			script = config.getString("afterSave");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"1");
		}
		//获取渲染脚本
		if(config.has("view"))
		{
			script = config.getString("view");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"2");
		}
		//获取发布脚本
		if(config.has("publish"))
		{
			script = config.getString("publish");
			scriptService.save(nodeId,script,ScriptType.form, formId.toString(),"3");
		}
				
		// 4.写入节点环境变量
		@SuppressWarnings("unchecked")
		NodeMapper<Node> nodeMapper = (NodeMapper<Node>) SpringContextUtil.getBean("nodeMapper");
		Node node = new Node();
		node.setId(nodeId);
		
		List<Node> list = nodeMapper.get(node);
		if (list == null || list.size() < 1) {
			throw new RuntimeException("节点【" + nodeId + "】没有节点配置数据！");
		}
		List<Map<String, Object>> envList = list.get(0).getEnvMap();
		boolean isExist = false;
		
		for (Map<String, Object> map : envList) {
			if (envKey.equals(map.get("key").toString())) {
				String viewIds = new JSONArray(viewIdList).toString();
				
				map.put("value", "{\"sysFormId\":" + formId + ",\"viewIds\":" + viewIds + ",\"listId\":" + listId + "}");
				map.put("desc", envDesc);
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			String viewIds = new JSONArray(viewIdList).toString();	
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("key", envKey);
			m.put("value", "{\"sysFormId\":" + formId + ",\"viewIds\":" + viewIds + ",\"listId\":" + listId + "}");
			m.put("desc", envDesc);
			envList.add(m);
		}

		node.setEnvMap(envList);
		nodeMapper.update(node);
		NodeEnv.getNodeEnvInstance().refresh();
		
		//5.更新搜索引擎
		SearchHelper shp = new SearchHelper(nodeId, formId, searchService);
		shp.createInedex();
	}
	
	private String getFileContent(String path) {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			throw new RuntimeException("文件[path=" + path + "]不存在，或者是目录");
		}
		String str = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {
				if (str == null) {
					str = s;
				} else {
					str += "\r\n" + s;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("获取文件[path=" + path + "]内容异常:" + e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return str;
	}

	/**
	 * 获取系统表单ID，返回-1，表示不存在系统表单（根据nodeId，表单名称，isSysForm来判断唯一性）
	 */
	private int getSysFormId(Integer nodeId, String formName, Integer isSysForm) throws SQLException {
		String sql = "select formId from cmpp_formConfig where nodeid=? and name=? and isSysForm=? order by formId asc";
		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, new Object[] { nodeId, formName, isSysForm });
		if (datas == null || datas.size() < 1) {
			return -1;
		}
		return Integer.valueOf(datas.get(0).get("formId").toString());
	}

	/**
	 * 获取系统表单的视图ID，返回-1，表示不存在
	 */
	private int getSysFormViewId(Integer formId) throws SQLException {
		String sql = "select viewId from cmpp_viewConfig where formId=? order by viewId asc";
		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, new Object[] { formId });
		if (datas == null || datas.size() < 1) {
			return -1;
		}
		return Integer.valueOf(datas.get(0).get("viewId").toString());
	}
	/**
	 * 获取系统碎片表单的视图ID list
	 */
	private List<Integer> getSysIdxFormViewId(Integer formId) throws SQLException {
		String sql = "select viewId from cmpp_viewConfig where formId=? order by viewId asc";
		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, new Object[] { formId });
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<datas.size();i++){
			list.add(Integer.valueOf(datas.get(i).get("viewId").toString()));
		}
		return list;
	}
	/**
	 * 获取系统表单的列表视图ID，返回-1，表示不存在
	 */
	private int getSysFormListId(Integer formId) throws SQLException {
		String sql = "select listId from cmpp_listConfig where formId=? order by listId asc";
		Vector<Map<String, Object>> datas = MySQLHelper.ExecuteSql(sql, new Object[] { formId });
		if (datas == null || datas.size() < 1) {
			return -1;
		}
		return Integer.valueOf(datas.get(0).get("listId").toString());
	}
	
	public void setSearchService(SearchService_V2 searchService) {
		this.searchService = searchService;
	}
}
