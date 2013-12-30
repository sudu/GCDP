package com.me.GCDP.action.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.NodeMapper;
import com.me.GCDP.model.Node;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.env.NodeEnv;
import com.me.GCDP.workflow.exception.InvalidParamException;
import com.me.GCDP.xform.FormPlugin;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.SysInitializationService;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * <p>Title: </p>
 * <p>Description: 系统表单（文章，栏目）字段映射配置</p>
 * <p>Company: ifeng.com</p>
 * @author :dengzc
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason                    </p>
 * <p>2013-12-5        dengzc               create the class             </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class XFormAdapterAction extends ActionSupport {

	private static final long serialVersionUID = 4183619142972688796L;
	private static final Log log = LogFactory.getLog(XFormAdapterAction.class);

	private Integer nodeId = 0;
	private String type;// 字段适配配置类型（doc表示文章；column表示栏目）
	private String fieldAdapterCfg;// 字段适配配置(json字符串)
	private String msg;// 返回json值

	private Map<String, Object> docMap = null;
	private Map<String, Object> columnMap = null;

	private SysInitializationService sysInitializationService;

	public String fieldAdapterCfg() {
		return "fieldAdapterCfg";
	}

	public String sysFormFieldAdapterCfg() {
		return "sysFormFieldAdapterCfg";
	}

	/**
	 * 初始化或重置系统表单
	 * @return
	 */
	public String initOrResetSysForm() {
		try {
			sysInitializationService.init(nodeId);
			msg = "{success:true}";
		} catch (Exception e) {
			log.error("XFormAdapteAction initOrResetSysForm() error:" + e.getMessage(), e);
			msg = "{success:false,msg:\"" + e.getMessage() + "\"}";
		}
		return "initOrResetSysForm";
	}
	
	/**
	 * 存储配置数据到节点环境配置数据中
	 * @return
	 */
	public String saveFieldAdapterCfg() {
		try {
			String key = "";
			String desc = "";
			if ("doc".equals(type)) {
				key = SysInitializationService.KEY_docFieldAdapterCfg;
				desc = SysInitializationService.DESC_docFieldAdapterCfg;
			} else if ("column".equals(type)) {
				key = SysInitializationService.KEY_columnFieldAdapterCfg;
				desc = SysInitializationService.DESC_columnFieldAdapterCfg;
			} else {
				throw new InvalidParamException("无效的字段适配配置Key:" + type != null ? type : "null");
			}
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
				if (key.equals(map.get("key").toString())) {
					map.put("value", fieldAdapterCfg);
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("key", key);
				m.put("value", fieldAdapterCfg);
				m.put("desc", desc);
				envList.add(m);
			}
			node.setEnvMap(envList);
			nodeMapper.update(node);
			NodeEnv.getNodeEnvInstance().refresh();
			msg = "{success:true}";
		} catch (Exception e) {
			log.error("XFormAdapteAction saveFieldAdapterCfg() error:" + e.getMessage(), e);
			msg = "{success:false,msg:\"" + e.getMessage() + "\"}";
		}

		return "saveFieldAdapterCfg";
	}

	// getter and setter
	/**
	 * 获取所有表单列表
	 * @return
	 * @throws Exception
	 */
	public String getFormListConfig() throws Exception {
		return FormService.getFormListConfig(nodeId, null);
	}

	/**
	 * 获取文章表单配置信息
	 * @return
	 * @throws Exception
	 */
	public String getDocFormConfig() throws Exception {
		if (docMap == null) {
			return "";
		}
		int formId = Integer.valueOf(new JSONObject(docMap.get("value").toString()).get("sysFormId").toString());
		ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		FormPlugin formSvr = (FormPlugin) pf.getP("form");
		formSvr.init(nodeId, formId, 0, null);
		return formSvr.getFormHelper().getFc().getFormConfig();
	}

	/**
	 * 获取栏目表单配置信息
	 * @return
	 * @throws Exception
	 */
	public String getColumnFormConfig() throws Exception {
		if (columnMap == null) {
			return "";
		}
		int formId = Integer.valueOf(new JSONObject(columnMap.get("value").toString()).get("sysFormId").toString());
		ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		FormPlugin formSvr = (FormPlugin) pf.getP("form");
		formSvr.init(nodeId, formId, 0, null);
		return formSvr.getFormHelper().getFc().getFormConfig();
	}

	/**
	 * 从节点环境配置数据中取出系统表单（文章、栏目）字段适配配置
	 * @return
	 * @throws JSONException
	 */
	public String getSysFormFieldAdapterCfg() throws JSONException {
		docMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_docFieldAdapterCfg);
		columnMap = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, SysInitializationService.KEY_columnFieldAdapterCfg);
		JSONObject jsObj = new JSONObject();
		jsObj.put(SysInitializationService.KEY_docFieldAdapterCfg, docMap != null ? docMap.get("value").toString() : "");
		jsObj.put(SysInitializationService.KEY_columnFieldAdapterCfg, columnMap != null ? columnMap.get("value").toString() : "");
		return jsObj.toString();
	}

	public String getMsg() {
		return msg;
	}

	public void setFieldAdapterCfg(String fieldAdapterCfg) {
		this.fieldAdapterCfg = fieldAdapterCfg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public void setSysInitializationService(SysInitializationService sysInitializationService) {
		this.sysInitializationService = sysInitializationService;
	}
	
}
