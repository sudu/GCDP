package com.me.GCDP.action.xform;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.model.Permission;
import com.me.GCDP.model.Template;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.env.NodeEnv;
import com.me.GCDP.xform.BuildBlock;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormHelper;
import com.me.GCDP.xform.FormPlugin;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.RenderType;
import com.me.GCDP.xform.TemplateBuilder;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class TemplateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(TemplateAction.class);
	
	private SecurityManager securityManager = null;
	private TemplateMapper<Template> templateMapper = null;
	
	private RenderType type = RenderType.preview;
	private int formId = 0;
	private int nodeId = 0;
	private int id; // tplId
	private Map<String, Object[]> form;
	private Integer dataId = 0;
	private String format = "html";
	private String actionData;
	private String args;
	private String cmd;
	private String msg = "";
	private boolean hasError = false;
	
	//全页预览时提交的碎片正文和碎片渲染ID
	private String idxData;
	private String idxId;
	
//	private Map<String, Object> xform = new HashMap<String, Object>();

	private FormService formService;

	private FormPlugin formSvr;
	private FormHelper fmHelper;

	public String preview() throws JSONException, SQLException {
		setDefaultDataId();
		if (format.equals("json")) {
			return "preview-json";
		}
		return "preview";
	}

	public String blockEditor() throws JSONException, SQLException {
		setDefaultDataId();
		type = RenderType.blockEdit;
		return "blockEditor";
	}
	
	public String idxEditor() throws JSONException, SQLException {
		setDefaultDataId();
		type = RenderType.idxEdit;
		return "idxEditor";
	}

	public String design() throws JSONException, SQLException {
		setDefaultDataId();
		type = RenderType.tplEdit;
		return "design";
	}

	public String getContent() throws Exception {
		if (type == RenderType.tplEdit && id == 0) {
			return "模板设计状态必须指定模板ID";
		}
		return render();
	}
	
	private void setDefaultDataId() throws JSONException, SQLException {
		if (getDataId() == 0) {
			FormConfig fc = FormConfig.getInstance(0, getFormId());
			Integer lastId = getFormService().getFormLastId(fc);
			if (lastId > 0) {
				setDataId(lastId);
			}
		}
	}

	public String buildBlock() throws Exception {
		Template tpl = getFormService().getTemplate(getId());
		JSONObject rtn = BuildBlock.build(cmd, tpl);
		if (rtn.getBoolean("success")) {
			type = RenderType.tplEdit;
			String html = render();
			try {
				FormConfig fc = FormConfig.getInstance(getNodeId(), getDataFormId());
				TemplateBuilder.build(fc, html, "[]", tpl);
			} catch (Exception e) {
				e.printStackTrace();
				rtn.put("success", false);
				rtn.put("message", "TemplateBuilder.build 预处理模板出错:" + e.getMessage());
			}
		}
		msg = rtn.toString();
		return "buildBlock";
	}

	public String build() throws JSONException, SQLException {
		setDefaultDataId();
		type = RenderType.tplEdit;
		try {
			String html = render();
			String path = getPath();
			Map<String, Object> data = getPowerData(id);
			if (securityManager.checkPermission(path, Permission.MODIFY, data, 0, 0)) {
				FormConfig fc = FormConfig.getInstance(nodeId, formId);
				actionData = TemplateBuilder.build(fc, html, cmd, getFormService().getTemplate(id)).toString();
			} else {
				actionData = "[]";
				msg = "你没有修改该模板的权限。";
				hasError = true;
			}
		} catch (Exception e) {
			actionData = "[]";
			log.error(e);
			e.printStackTrace(); //yb debug
			msg = e.getMessage();
			hasError = true;
		}
		return "build";
	}

	private String render() throws Exception {
		long t1 = System.currentTimeMillis();
		String content = "";
		try {
//			Map<String, Object> argsMap = new HashMap<String, Object>();
//			argsMap.put("args", args);
			// FIXME 20130130 yangbo
			Map<String, Object> formMap = new HashMap<String, Object>();
//			formMap.put("args", argsMap);
			formMap.put("args", args);
			if (idxId != null) {
				formMap.put("idxId", idxId);
			}
			if (idxData != null) {
				formMap.put("idxData", idxData);
			}
			formMap.put("form", form); // FIXME for 老form插件兼容
			FormConfig fc = FormConfig.getInstance(getNodeId(), getDataFormId());
			Map<String, Object> dataPool = getFormService().preview(fc, getId(), getDataId(), type, getFormService().getPostExtra(fc, form), formMap);
			Object o = dataPool.get("content");
			content = o == null ? " " : o.toString();
			
			if(!content.equals(" ") && type.equals(RenderType.tplEdit)){
				//模板编辑时对所有DOM节点加入cmppid属性
				content = TemplateBuilder.addCmppID(content);
			}
		} catch (Exception ex) {
			content = ex.getMessage();
			throw ex;
		}
		log.info("=======render() : " + (System.currentTimeMillis() - t1) + " ms");
		return content;
	}

	/*
	 * 从节点配置里读取通栏配置 bannerSetting
	 */
	public String getBannerSetting() {
		NodeEnv nodeEnv = NodeEnv.getNodeEnvInstance();
		Map<String, Object> envMap = nodeEnv.getEnvByKey(getNodeId(), "bannerSetting");
		String bannerSetting = "{}";
		if (envMap.containsKey("value")) {
			bannerSetting = envMap.get("value").toString();
		}
		return bannerSetting;
	}

	public Map<String, Object> getPowerData(int id) throws Exception {
		Template t = templateMapper.getById(id);
		if (t != null) {
			return t.toMap();
		}
		return null;
	}

	public String getPath() throws Exception {
//		String sql = "select powerPath from cmpp_formConfig where tableName='cmpp_template'";
//		try {
//			return MySQLHelper.ExecuteSql(sql, null).get(0).get("powerPath").toString();
//		} catch (SQLException e) {
//			log.error("获取权限表达式失败." + e);
//			return "";
//		}
		List<String> list = templateMapper.getPowerPathInFormConfig();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return "";
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// Setters & Getters
	// /////////////////////////////////////////////////////////////////////////////////////////////

	public FormPlugin getFormSvr() {
		formSvr.init(nodeId, formId, dataId, form);
		return formSvr;
	}

	public FormHelper getFmHelper() {
		if (fmHelper == null) {
			fmHelper = getFormSvr().getFormHelper();
		}
		return fmHelper;
	}

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String value) {
		this.args = value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public Integer getDataFormId() {
		return formId;
	}

	@SuppressWarnings("unchecked")
	public void setDataFormId(int formId) {
		this.formId = formId;
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		this.form = new HashMap<String, Object[]>();
		form.putAll(request.getParameterMap());
		ScriptPluginFactory pf = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
		this.formSvr = (FormPlugin) pf.getP("form");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public Map<String, Object[]> getForm() {
		return form;
	}

	public String getActionData() {
		return actionData;
	}

	public String getAction() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean getHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public int getDataId() {
		return dataId;
	}

	public RenderType getType() {
		return type;
	}

	public void setType(RenderType type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = RenderType.valueOf("type");
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void setXform(Map<String, Object> xform) {
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public TemplateMapper<Template> getTemplateMapper() {
		return templateMapper;
	}

	public void setTemplateMapper(TemplateMapper<Template> templateMapper) {
		this.templateMapper = templateMapper;
	}

	public String getIdxData() {
		return idxData;
	}

	public void setIdxData(String idxData) {
		this.idxData = idxData;
	}

	public String getIdxId() {
		return idxId;
	}

	public void setIdxId(String idxId) {
		this.idxId = idxId;
	}

}
