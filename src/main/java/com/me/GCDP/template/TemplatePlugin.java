package com.me.GCDP.template;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Template;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;
import com.me.GCDP.xform.FormConfig;
import com.me.GCDP.xform.FormService;
import com.me.GCDP.xform.RenderType;
import com.me.GCDP.xform.TemplateBuilder;

/*
 * @version 1.2 往template注入全页预览时的碎片数据,以实现无需保存的全页预览  by chengds at 2014/3/5
 */

@PluginClass(author = "yangbo", intro = "模板帮助插件",tag="模板")
@PluginExample(intro = "提供模板相关操作")
public class TemplatePlugin extends ScriptPlugin {
	private static Log log = LogFactory.getLog(TemplatePlugin.class);
	private FormService formService;
	private TemplateMapper<Template> templateMapper;
	private VersionMapper versionMapper;
	private Map<String,Object> dataPool;
	

	@Override
	public void init() {
	}

	// ///////////////////////////////////////////////////////////////////////
	// render
	// ///////////////////////////////////////////////////////////////////////

	@PluginIsPublic
	@PluginMethod(intro = "设置模板上下文dataPool", paramIntro = { "当前脚本上下文dataPool"})
	public void setDataPool(Map<String, Object> dataPool) throws Exception {
		this.dataPool = dataPool;
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID",
	"模板ID", "记录Map", "渲染类型" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, int tplId, Map<String, Object> data, String type) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.valueOf(type),dataPool);
	}

	public String render(int nodeId, int formId, int tplId, Map<String, Object> data, RenderType type) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, type,this.dataPool);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID",
	"模板ID", "记录Map", "渲染类型", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, int tplId, Map<String, Object> data, String type, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.valueOf(type), siteUrl,dataPool);
	}

	public String render(int nodeId, int formId, int tplId, Map<String, Object> data, RenderType type, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, type, siteUrl,dataPool);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录ID", paramIntro = { "节点Id", "数据表ID",
	"模板ID", "记录ID", "渲染类型" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, int tplId, Integer dataId, String type) throws Exception {
		return render(nodeId, formId, tplId, dataId, type, null);
	}

	public String render(int nodeId, int formId, int tplId, Integer dataId, RenderType type) throws Exception {
		return render(nodeId, formId, tplId, dataId, type, null);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录ID", paramIntro = { "节点Id", "数据表ID",
	"模板ID", "记录ID", "渲染类型", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, int tplId, Integer dataId, String type, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, RenderType.valueOf(type), siteUrl,this.dataPool);
	}

	public String render(int nodeId, int formId, int tplId, Integer dataId, RenderType type, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, type, siteUrl,dataPool);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版关联记录Map", paramIntro = { "节点Id",
	"数据表ID", "记录Map", "模板String", "渲染类型" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, Map<String, Object> data, String templateStr, String type)
			throws Exception {
		return render(nodeId, formId, data, templateStr, type, null);
	}

	public String render(int nodeId, int formId, Map<String, Object> data, String templateStr, RenderType type)
			throws Exception {
		return render(nodeId, formId, data, templateStr, type, null);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版关联记录Map", paramIntro = { "节点Id",
	"数据表ID", "记录Map", "模板String", "渲染类型", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String render(int nodeId, int formId, Map<String, Object> data, String templateStr, String type, String siteUrl)
			throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, RenderType.valueOf(type), siteUrl,dataPool);
	}

	public String render(int nodeId, int formId, Map<String, Object> data, String templateStr, RenderType type, String siteUrl)
			throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, type, siteUrl);
	}
	
	// ///////////////////////////////////////////////////////////////////////
	// renderPreview
	// ///////////////////////////////////////////////////////////////////////
	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录Map" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, int tplId, Map<String, Object> data) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.preview);
	}

	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, int tplId, Integer dataId) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, RenderType.preview);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版、关联记录Map", paramIntro = { "节点Id", "数据表ID", "记录Map", "模板String" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, Map<String, Object> data, String templateStr) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, RenderType.preview);
	}
	
	// 带siteUrl
	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录Map", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, int tplId, Map<String, Object> data, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.preview, siteUrl);
	}

	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, int tplId, Integer dataId, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, RenderType.preview, siteUrl);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版、关联记录Map", paramIntro = { "节点Id", "数据表ID", "记录Map", "模板String", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPreview(int nodeId, int formId, Map<String, Object> data, String templateStr, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, RenderType.preview, siteUrl);
	}

	// ///////////////////////////////////////////////////////////////////////
	// renderPublish
	// ///////////////////////////////////////////////////////////////////////
	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录Map" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, int tplId, Map<String, Object> data) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.publish);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录ID", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, int tplId, Integer dataId) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, RenderType.publish);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版关联记录Map", paramIntro = { "节点Id", "数据表ID", "记录Map", "模板String" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, Map<String, Object> data, String templateStr) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, RenderType.publish);
	}
	// 带siteUrl
	@PluginIsPublic
	@PluginMethod(intro = "预览状态下执行模板渲染：关联记录Map", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录Map", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, int tplId, Map<String, Object> data, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, tplId, data, RenderType.publish, siteUrl);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：关联记录ID", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, int tplId, Integer dataId, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> data;
		if (dataId != null) {
			data = getFormService().getData(fc, dataId);
		} else {
			data = null;
		}
		return formService.render(fc, tplId, data, RenderType.publish, siteUrl);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行模板渲染：自定义模版关联记录Map", paramIntro = { "节点Id", "数据表ID", "记录Map", "模板String", "SSI碎片路径" }, returnIntro = "返回渲染结果字符串")
	public String renderPublish(int nodeId, int formId, Map<String, Object> data, String templateStr, String siteUrl) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.render(fc, templateStr, data, RenderType.publish, siteUrl);
	}
	
	// ///////////////////////////////////////////////////////////////////////
	// other
	// ///////////////////////////////////////////////////////////////////////
	@PluginIsPublic
	@PluginMethod(intro = "获取模版信息", paramIntro = { "模板ID" }, returnIntro = "返回模板信息Map")
	public Map<String, Object> getTemplate(int tplId) {
//		String sql = "select id,name,content,enable,dataFormId,dataId from cmpp_template where id=" + tplId;
//		Map<String, Object> rtn = null;
//		try {
//			Vector<Map<String, Object>> data = MySQLHelper.ExecuteSql(sql, null);
//			if (data.size() > 0) {
//				rtn = data.get(0);
//			} else {
//				rtn = null;
//			}
//		} catch (SQLException e) {
//			log.error("获取模板出错,tplId=" + tplId + e.getMessage());
//		}
//		return rtn;
		Template tpl;
		try {
			tpl = templateMapper.getById(tplId);
			if (tpl != null) {
				return tpl.toMap();
			}
		}catch (Exception e) {
			log.error("获取模板出错,tplId=" + tplId + e.getMessage());
		}
		return null;
	}

	@PluginIsPublic
	@PluginMethod(intro = "将当前表单的指定模板复制出一个新的模板, 不改变除“绑定数据ID”以外的模版的其他属性", paramIntro = { "要复制的模板ID", "新模板绑定的dataId" }, returnIntro = "返回复制成功的新模板ID")
	public int copyTemplate(int tplId, int dataId) throws Exception {
		return TemplateBuilder.copy(tplId, dataId);
	}

	public boolean renameTemplate(int tplId, String name) throws TemplateException {
		Template t = new Template();
		int affected = 0;
		t.setId(tplId);
		t.setName(name);
		try {
			affected = templateMapper.update(t);
		} catch (Exception e) {
			log.error("获取模板出错,tplId=" + tplId + e.getMessage());
		}
		return affected > 0 ? true : false;
//		String sql = "update cmpp_template set name = '" + name + "' where id=" + tplId;
//		int affected = 0;
//		try {
//			affected = MySQLHelper.ExecuteNoQuery(sql, null);
//		} catch (SQLException e) {
//			log.error("获取模板出错,tplId=" + tplId + e.getMessage());
//		}
//		return affected > 0 ? true : false;
	}

	public int createTemplate(int nodeId, String templateName, String templateContent, int formId, int dataId) throws TemplateException, ParseException {
		return createTemplate(nodeId, templateName, templateContent, formId, dataId, null, null, null);
	}
	
	public int createTemplate(int nodeId, String templateName, String templateContent, int formId, int dataId, String powerPath)
			throws TemplateException, ParseException {
		return createTemplate(nodeId, templateName, templateContent, formId, dataId, powerPath, null, null);
	}
	
	public int createTemplate(int nodeId, String templateName, String templateContent, int formId, int dataId, String powerPath, String createDate) 
			throws TemplateException, ParseException {
		return createTemplate(nodeId, templateName, templateContent, formId, dataId, powerPath, createDate, null);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "创建模板", paramIntro = { "NodeId(必选)", "模版名称(必选)", "模板正文(必选)", "绑定表单Id(必选)", "绑定数据Id(必选),通用模版请填0", "权限Path(可选)", "创建时间(可选)", "修改时间(可选)" }, returnIntro = "返回创建成功的模板ID")
	public int createTemplate(int nodeId, String templateName, String templateContent, int formId, int dataId, String powerPath,
			String createDate, String modifyDate) throws TemplateException, ParseException {
		Template tpl = new Template();
		tpl.setNodeid(nodeId);
		tpl.setName(templateName);
		tpl.setContent(templateContent);
		tpl.setFormId(formId);
		tpl.setDataId(dataId);
		tpl.setEnable(Template.AVAILABLE);
		if (powerPath != null) {
			tpl.setPowerPath(powerPath);
		}
		if (createDate != null || modifyDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (createDate != null) {
				Date _createDate = sdf.parse(createDate);
				tpl.setCreateDate(_createDate);
			}
			if (modifyDate != null) {
				Date _modifyDate = sdf.parse(modifyDate);
				tpl.setModifyDate(_modifyDate);
			}
			templateMapper.insertFull(tpl);
		} else {
			templateMapper.insert(tpl);
		}
		return tpl.getId();
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "重命名模版", paramIntro = { "NodeId", "模版Id", "模版名称"}, returnIntro = "返回更新操作影响的行数")
	public int renameTemplate(int nodeId, int tplId, String templateName) throws Exception {
		if (templateName == null || templateName.length() == 0)
			throw new TemplateException("Encounter ivalid templateName when while renaming template.");
		Template tpl = new Template();
		tpl.setNodeid(nodeId);
		tpl.setId(tplId);
		tpl.setName(templateName);
		tpl.setModifyDate(new Date());
		return templateMapper.update(tpl);
	}
	
	public int modifyPowerPath(int nodeId, int tplId, String powerPath) throws Exception {
		if (powerPath == null || powerPath.length() == 0)
			throw new TemplateException("Encounter ivalid powerPath when while calling modifyPowerPath().");
		Template tpl = new Template();
		tpl.setNodeid(nodeId);
		tpl.setId(tplId);
		tpl.setPowerPath(powerPath);
		tpl.setModifyDate(new Date());
		return templateMapper.update(tpl);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "更新模板，NodeId和模版Id为必选", paramIntro = { "NodeId(必选)", "模版Id(必选)", "模版名称", "模板正文", "绑定表单Id", "绑定数据Id(必选),通用模版请填0", "权限Path", "创建时间", "修改时间" }, returnIntro = "返回更新操作影响的行数")
	public int updateTemplate(int nodeId, int tplId, String templateName, String templateContent, Integer enable) throws Exception {
		boolean doUpdate = false;
		if (templateName != null) {
			doUpdate = doUpdate || true;
			if (templateName.length() == 0) {
				throw new TemplateException("Encounter ivalid templateName when while updating template. Template Name cannot be empty.");
			}
		}
		if (templateContent != null) {
			doUpdate = doUpdate || true;
		}
		if (enable != null && (enable == 0 || enable == 1)) {
			doUpdate = doUpdate || true;
		} else {
			throw new TemplateException("Encounter ivalid Enable when while updating template. Template Enable can be 0(disable) or 1(enable).");
		}
		if (doUpdate == false) {
			log.warn("No update presented to database when updateTemplate() called.");
			return -1;
		}
		Template tpl = new Template();
		tpl.setNodeid(nodeId);
		tpl.setId(tplId);
		tpl.setName(templateName);
		tpl.setContent(templateContent);
		tpl.setEnable(enable);
		tpl.setModifyDate(new Date());
		return templateMapper.update(tpl);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "更新模板，NodeId和模版Id为必选", paramIntro = { "NodeId(必选)", "模版Id(必选)", "模版名称", "模板正文", "绑定表单Id", "绑定数据Id(必选),通用模版请填0", "权限Path", "创建时间", "修改时间" }, returnIntro = "返回更新操作影响的行数")
	public int updateTemplate(int nodeId, int tplId, String templateName, String templateContent, Integer enable, String powerPath) throws Exception {
		boolean doUpdate = false;
		if (templateName != null) {
			doUpdate = doUpdate || true;
			if (templateName.length() == 0) {
				throw new TemplateException("Encounter ivalid templateName when while updating template. Template Name cannot be empty.");
			}
		}
		if (templateContent != null) {
			doUpdate = doUpdate || true;
		}
		if (enable != null && (enable == 0 || enable == 1)) {
			doUpdate = doUpdate || true;
		} else {
			throw new TemplateException("Encounter ivalid Enable when while updating template. Template Enable can be 0(disable) or 1(enable).");
		}
		if (powerPath!= null) {
			doUpdate = doUpdate || true;
			if (powerPath.length() == 0) {
				throw new TemplateException("Encounter ivalid templateName when while updating template. Template powerPath cannot be empty.");
			}
		}
		if (doUpdate == false) {
			log.warn("No update presented to database when updateTemplate() called.");
			return -1;
		}
		Template tpl = new Template();
		tpl.setNodeid(nodeId);
		tpl.setId(tplId);
		tpl.setName(templateName);
		tpl.setContent(templateContent);
		tpl.setEnable(enable);
		tpl.setModifyDate(new Date());
		return templateMapper.update(tpl);
	}
	

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	public TemplateMapper<Template> getTemplateMapper() {
		return templateMapper;
	}

	public void setTemplateMapper(TemplateMapper<Template> templateMapper) {
		this.templateMapper = templateMapper;
	}
}
