package com.me.GCDP.xform;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;
import com.me.json.JSONException;

@PluginClass(author = "yangbo", intro = "表单插件，提供表单操作的帮助插件",tag="表单")
@PluginExample(intro = "可选渲染类型包括：<br/>publish,idxEdit,tplEdit,blockEdit,preview<br/>")
public class Form2Plugin extends ScriptPlugin {
	private static Log log = LogFactory.getLog(Form2Plugin.class);
	FormService formService;

	public Form2Plugin() {
	}

	@Override
	public void init() {
	}

	// ///////////////////////////////////////////////////////////////////////
	// preview
	// ///////////////////////////////////////////////////////////////////////

	@PluginIsPublic
	@PluginMethod(intro = "执行预览脚本：指定节点ID, 数据表ID, 和模板ID, 记录ID, 以及渲染类型", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID",
			"渲染类型" }, returnIntro = "返回预览脚本dataPool")
	public Map<String, Object> preview(int nodeId, int formId, int tplId, Integer dataId, String type) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.preview(fc, tplId, dataId, RenderType.valueOf(type), null);
	}

	public Map<String, Object> preview(int nodeId, int formId, int tplId, Integer dataId, RenderType type)
			throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.preview(fc, tplId, dataId, type, null);
	}

	@PluginIsPublic
	@PluginMethod(intro = "执行预览脚本：指定节点ID, 数据表ID, 和模板ID, 记录Map, 以及渲染类型", paramIntro = { "节点Id", "数据表ID", "模板ID",
			"记录Map", "渲染类型" }, returnIntro = "返回预览脚本dataPool")
	public Map<String, Object> preview(int nodeId, int formId, int tplId, Map<String, Object> data, String type)
			throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.preview(fc, tplId, data, RenderType.valueOf(type), null);
	}

	public Map<String, Object> preview(int nodeId, int formId, int tplId, Map<String, Object> data, RenderType type)
			throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.preview(fc, tplId, data, type, null);
	}

	// ///////////////////////////////////////////////////////////////////////
	// publish
	// ///////////////////////////////////////////////////////////////////////

	@PluginIsPublic
	@PluginMethod(intro = "执行发布脚本：指定节点ID, 数据表ID, 和模板ID, 记录ID", paramIntro = { "节点Id", "数据表ID", "模板ID", "记录ID" }, returnIntro = "返回发布脚本dataPool")
	public Map<String, Object> publish(int nodeId, int formId, int tplId, Integer dataId) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.publish(fc, tplId, dataId, null);
	}

	// ///////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////

	@PluginIsPublic
	@PluginMethod(intro = "获取指定记录：指定节点ID, 数据表ID, 记录ID", paramIntro = { "节点Id", "数据表ID", "记录ID" }, returnIntro = "返回指定记录Map")
	public Map<String, Object> getData(int nodeId, int formId, int dataId) throws JSONException, SQLException {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.getData(fc, dataId);
	}

	@PluginIsPublic
	@PluginMethod(intro = "删除指定记录：指定节点ID, 数据表ID, 记录ID<br/>删除成功无返回，失败抛出异常。<br/>如若涉及下线操作，请自行下线再删除数据。<b>谨慎删除！</b><br/>", paramIntro = {
			"节点Id", "数据表ID", "记录ID" }, returnIntro = "无返回，失败抛出异常")
	public void deleteData(int nodeId, int formId, int dataId) throws JSONException, SQLException {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		formService.deleteData(fc, dataId);
	}

	@PluginIsPublic
	@PluginMethod(intro = "保存指定记录，并发布：指定节点ID, 数据表ID, 记录ID, 数据Map", paramIntro = { "节点Id", "数据表ID", "记录ID(新插入数据时填0)", "数据Map" }, returnIntro = "成功返回记录ID，失败抛出异常")
	public int save(int nodeId, int formId, int dataId, Map<String, Object> data) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> dataPool = formService.save(fc, dataId, data);
		return Integer.parseInt(dataPool.get("dataId").toString());
	}

	@PluginIsPublic
	@PluginMethod(intro = "只保存数据到关系数据库或NOSQL数据库，不推送到搜索引擎：指定节点ID, 数据表ID, 记录ID(新插入数据时填0), 数据Map", paramIntro = {}, returnIntro = "成功返回记录ID，失败抛出异常")
	public int saveData(int nodeId, int formId, int dataId, Map<String, Object> data) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		return formService.saveData(fc, dataId, data);
	}

	@PluginIsPublic
	@PluginMethod(intro = "保存指定记录并推送到搜索引擎，不触发发布。参数：指定节点ID, 数据表ID, 记录ID(新插入数据时填0), 数据Map", paramIntro = {}, returnIntro = "成功返回记录ID，失败抛出异常")
	public int saveAllData(int nodeId, int formId, int dataId, Map<String, Object> data) throws Exception {
		FormConfig fc = FormConfig.getInstance(nodeId, formId);
		Map<String, Object> dataPool = formService.saveAllData(fc, dataId, data);
		return (Integer) dataPool.get("dataId");
	}

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

}