package com.me.GCDP.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.xform.ConfigParser;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormPlugin;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.ImportHelper;
import com.me.GCDP.xform.ViewConfig;
import com.me.GCDP.xform.xcontrol.BaseControl;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.search.SearchService_V2;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

import freemarker.template.TemplateException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author : ?
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-3-16                 ?                   create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class XFormAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(XFormAction.class);
	// 节点ID
	private Integer nodeId = 0;

	private int id = 0;
	private Integer formId;
	private int viewId;

	private String viewData;

	private String config;
	private String msg = "";
	private String formName;
	private String powerPath;
	private String data = "";

	private boolean hasError = false;

	private SearchService_V2 searchSvr;
	private FormPlugin formSvr;
	private String confData;
	private String btImport;
	private File conData;
	private String conDataFileName;

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String saveConfig() throws Exception, TemplateException {
		try {
			ConfigParser jsonConfig = new ConfigParser(config);
			if (formId == null || formId == 0) {
				formId = FormService.createFormConfig(jsonConfig, nodeId);
			} else {
				FormConfig.updateFormConfig(nodeId, this.formId, this.config, this.formName, this.powerPath);
			}
			// SearchHelper shp = new SearchHelper(nodeId,formId,searchSvr);
			// shp.createIndex();
			// searchSvr.updateCore(nodeId+"_frm_"+formId,
			// FormConfig.getInstance(nodeId, formId).getJsonConfig(), type);
			SearchHelper shp = new SearchHelper(nodeId, formId, searchSvr);
			shp.createInedex();
			// System.out.println("创建索引成功");
		} catch (JSONException e) {
			Error(e, "不正确的配置格式");
		} catch (SQLException e) {
			Error(e, "保存配置出错");
		} catch (Exception ex) {
			Error(ex, ex.getMessage());
		}
		return "saveConfig";
	}

	/**
	 * 
	 * @return
	 */
	public String render() {
		return "render";
	}

	/**
	 * 
	 * @return
	 */
	public String getFormData() {
		if (id == 0) {
			return "''";
		}
		return new JSONObject(getFormSvr().getFormHelper().getData()).toString();
	}

	/**
	 * 
	 * @return
	 */
	public String parseViewData() {
		try {
			ViewConfig vc = ViewConfig.getInstance(viewId);
			Vector<BaseControl> ctrs = vc.getControl();
			JSONObject config = new JSONObject();
			for (int i = 0; i < ctrs.size(); i++) {
				String[][] render = ctrs.get(i).renderDataSource();
				if (render != null) {
					config.put(ctrs.get(i).getControlId(), render);
				}

			}
			return config.toString();
		} catch (Exception e) {
			Error(e, "获取控件数据失败");
			return "";
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getFormListConfig() {
		try {
			return FormService.getFormListConfig(nodeId, formId);
		} catch (Exception e) {
			Error(e, "获取表单数据失败");
			return "";
		}
	}

	/**
	 * 
	 * @return
	 */
	public String FormListConfig() {
		return "formListConfig";
	}

	/**
	 * 
	 * @return
	 */
	public String saveFormConfig() {
		try {
			FormConfig.SaveFormConfig(config, formId);
		} catch (Exception e) {
			Error(e, "保存formConfig配置失败");
		}
		return "msg";
	}

	/**
	 * 
	 * @return
	 */
	public String saveViewConfig() {
		try {
			if (viewId == 0) {
				viewId = ViewConfig.createView(config, formId);
			} else {
				ViewConfig.updateView(viewId, config);
			}
		} catch (Exception e) {
			Error(e, "保存view配置失败");
		}
		return "saveviewconfig";
	}

	/**
	 * 
	 * @return
	 */
	public String formManage() {
		formSvr.init(nodeId, formId, id, null);
		return "formManage";
	}


	/**
	 * 表单描述接口
	 * 必需参数:nodeId,formId
	 * @return
	 */
	public String formDescription(){
		formSvr.init(nodeId, formId, id, null);
		return "formDescription";
	}
	
	/**
	 * 表单描述首页
	 * 必需参数:nodeId
	 * @return
	 */
	public String formDescriptionIndex(){
		return "formDescriptionIndex";
	}

	/**
	 * 
	 * @return
	 */
	public String viewManage() {
		return "viewManage";
	}
	/**
	 * 
	 * @return
	 */
	public String viewMgr() {
		return "viewMgr";
	}
	/**
	 * 
	 * @return
	 */
	public String designer() {
		return "designer";
	}

	/**
	 * 异常处理私有方法
	 * 
	 * @param e
	 * @param msg
	 */
	private void Error(Exception e, String msg) {
		e.printStackTrace();
		this.msg = msg + e.getMessage();
		log.error(msg + "formId=" + formId + ",id=" + id + e);
		this.hasError = true;
	}

	public String copyViewConfig() {
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean isSuccess = false;
		String sql = "insert into cmpp_viewConfig(name,config,formId)"
				+ "select CONCAT(name,'_copy') name,config,formId from cmpp_viewConfig where viewId=?";
		Object[] parms = new Object[1];
		parms[0] = viewId;
		try {
			String r = MySQLHelper.InsertAndReturnId(sql, parms);
			if (r != null) {
				isSuccess = true;
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("success", isSuccess);
			jsonObj.put("viewId", r);
			response.getWriter().write(jsonObj.toString());
			response.getWriter().flush();
		} catch (SQLException e) {
			log.error(e);
		} catch (JSONException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}
	
	public String importForm() throws Exception {
		// confData = ImportHelper.Export(14, 87);
		// ImportHelper.Import(3, confData);
		if (conData == null) {
			return "import";
		}
		if (!btImport.isEmpty()) {
			String str = null;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(conData));
				String s = null;
				while ((s = br.readLine()) != null) {
					if (str == null) {
						str = s;
					} else {
						str += "\r\n" + s;
					}
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						log.error(e);
						// e.printStackTrace();
					}
				}
			}
			Integer fid = ImportHelper.Import(nodeId, str);
			HttpServletResponse response = ServletActionContext.getResponse();
			response.sendRedirect("formMgr.html?nodeId=" + nodeId + "&formId=" + fid);
		}
		return "import";
	}

	public void export() throws SQLException, JSONException, IOException {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		confData = ImportHelper.Export(nodeId, formId);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/octet-stream;charset=UTF-8");
		response.addHeader("Content-Disposition", "inline;filename=\"" + URLEncoder.encode(fc.getFormName(), "UTF-8")
				+ ".json\"");

		byte[] buffer = new byte[4096]; // 缓冲区
		BufferedOutputStream output = null;
		BufferedInputStream input = null;
		try {
			output = new BufferedOutputStream(response.getOutputStream());
			//input = new BufferedInputStream(new StringInputStream(confData));
			input = new BufferedInputStream(new ByteArrayInputStream(confData.getBytes()));
			// response.setContentLength(confData.length());
			int n = (-1);
			while ((n = input.read(buffer, 0, 4096)) > -1) {
				output.write(buffer, 0, n);
			}
			response.flushBuffer();
		} catch (Exception e) {
		} // 用户可能取消了下载
		finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}
		// return "";
	}

	/*
	 * getter and setter
	 */

	public String getFields() {
		try {
			FormConfig fc = FormConfig.getInstance(0, formId);
			return fc.getFieldNames();
		} catch (Exception e) {
			Error(e, "获取Fields失败");
			return "[]";
		}
	}

	public String getViewConfig() {
		try {
			String viewCfg = ViewConfig.getView(viewId);
			if (viewCfg.equals("")) {
				viewCfg = "{}";
			}
			return viewCfg;
		} catch (Exception e) {
			Error(e, "获取view配置失败");
			return "{}";
		}
	}

	public String getFormConfig() throws SQLException {
		return this.formSvr.getFormHelper().getFc().getFormConfig();
	}

	public String FormConfig() {
		return "formConfig";
	}
	
	public String getFormConfig2() throws SQLException {
		formSvr.init(nodeId, formId, id, null);
		return this.formSvr.getFormHelper().getFc().getFormConfig();
	}
	
	public String getPowerPath() throws SQLException {
		return this.formSvr.getFormHelper().getFc().getPowerPath();
	}

	public String getConfig() {
		try {
			JSONObject config = new JSONObject(ViewConfig.getView(viewId));
			return config.toString();
		} catch (Exception e) {
			Error(e, "获取控件数据失败");
			return "";
		}

	}

	public String getViewData() {
		this.viewData = parseViewData();
		return viewData;
	}

	public void setViewData(String viewData) {
		this.viewData = viewData;
	}

	public void setFormName(String value) {
		this.formName = value;
	}

	public void setPowerPath(String value) {
		this.powerPath = value;
	}

	public String getFormName() throws SQLException {
		return this.formSvr.getFormHelper().getFc().getFormName();
	}

	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public int getFormId() {
		return formId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() throws UnsupportedEncodingException {
		return URLEncoder.encode(msg, "utf-8");
	}

	public String getData() {
		return data;
	}

	public boolean getHasError() {
		return hasError;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setFormId(int formId) {
		this.formId = formId;
		ScriptPluginFactory pf = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		this.formSvr = (FormPlugin) pf.getP("form");
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

	public FormPlugin getFormSvr() {
		return formSvr;
	}

	public String getConfData() {
		return confData;
	}

	public void setConfData(String confData) {
		this.confData = confData;
	}

	public String getBtImport() {
		return btImport;
	}

	public void setBtImport(String btImport) {
		this.btImport = btImport;
	}

	public File getConData() {
		return conData;
	}

	public void setConData(File conData) {
		this.conData = conData;
	}

	public String getConDataFileName() {
		return conDataFileName;
	}

	public void setConDataFileName(String conDataFileName) {
		this.conDataFileName = conDataFileName;
	}

}