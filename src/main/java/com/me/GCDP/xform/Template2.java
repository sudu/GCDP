package com.me.GCDP.xform;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.model.Template;
import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.oscache.OSCache;
import com.me.GCDP.script.plugin.HtmlPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONException;
import com.me.json.JSONObject;

import freemarker.template.TemplateException;

/**
 * @author yangbo
 * @version 2012-12-09
 * @doc 用来表示模板的类，定义了模板标签的解析规则，以及可视化编辑处理，预览等通用的处理方法
 */
public class Template2 {

	private static Log log = LogFactory.getLog(Template2.class);
	private int tplId;
	private FormConfig fc;

	private Map<String, Object> data = null;
	private String templateString;
	private String template; // compiled
	private Map<String, TagModel> tags = new HashMap<String, TagModel>();
	private Map<String, String> preTagContent = new HashMap<String, String>();
	private static Pattern tagP = Pattern.compile("\\{#renderdata([\\s\\S]*?)#\\}");

	private String id = "page";
	private String siteUrl = "http://www.ifeng.com/";
	private int level = 0;
	RenderType rendertype = RenderType.publish;
	FormService formService = (FormService) SpringContextUtil.getBean("formService");
	
	public Template2(FormConfig fc, String templateStr, RenderType renderType) {
		this.fc = fc;
		this.rendertype = renderType;
		setTemplateString(templateStr);
	}

	public Template2(FormConfig fc, int tplId, RenderType renderType) {
		this.fc = fc;
		this.tplId = tplId;
		this.rendertype = renderType;
		TemplateMapper<Template> templateMapper = (TemplateMapper<Template>) SpringContextUtil.getBean("templateMapper");
		String template = "";
		try {
			Template t = templateMapper.getById(tplId);
			template = t.getContent();
		} catch (Exception e) {
			log.error(e);
		}
//		String sql = "select id,name,content,enable,dataFormId,dataId from cmpp_template where id=" + tplId;
//		Vector<Map<String, Object>> data;
//		try {
//			data = MySQLHelper.ExecuteSql(sql, null);
//			if (data.size() > 0) {
//				template = (String) data.get(0).get("content");
//			}
//		} catch (SQLException e) {
//			log.error(e);
//		}
		setTemplateString(template);
	}

	public Template2(FormConfig fc, String id, Map<String, String> preTagContent, RenderType type) {
		this.fc = fc;
		this.id = id;
		if (preTagContent != null) {
			this.preTagContent = preTagContent;
		}
		this.rendertype = type;
	}

	public void init(RenderType renderType) throws IOException, TemplateException {
		this.template = FreeMarkerHelper.process2(this.templateString, this.data);
		long t1 = System.currentTimeMillis();
		// 解析标签
		Matcher tagM = tagP.matcher(template);
		int i = 0;
		while (tagM.find()) {
			String tag = tagM.group();
			if (!tags.containsKey(tag)) {
				String tagid = id + "_ctl" + i;
				Template2 ctag = new Template2(fc, tagid, preTagContent, this.rendertype);
				ctag.setLevel(this.level + 1);
				String funStr = tagM.group(1);
				TagModel tmd = RenderTag(funStr, ctag, renderType);
				tmd.setParms(funStr);
				tmd.setTagid(tagid);
				tags.put(tag, tmd);
			}
			i++;
		}
		long t2 = System.currentTimeMillis();
		log.info(id + "模板数据载入:" + (t2 - t1));
	}

	public String render(RenderType type, Map<String, Object> data) throws IOException, TemplateException {
		setData(data);
		init(type);
		long t1 = System.currentTimeMillis();
		String content = template;
		for (String key : tags.keySet()) {
			TagModel tag = tags.get(key);
			String tagContent = tag.getContent();
			if (type == RenderType.idxEdit) {
				JSONObject _data = new JSONObject();
				try {
					_data.put("id", tag.getTagid());
					_data.put("editurl", tag.getEditurl());
					_data.put("type", tag.getType());
					_data.put("title", tag.getTitle());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				tagContent = addDataToHtmlTag(tagContent, _data.toString(), "editdata");
			}
			if (type == RenderType.tplEdit) {
				tagContent = addDataToHtmlTag(tagContent, tag.getParms(), "cmpp_params");
			}
			if (preTagContent.containsKey(tag.getTagid())) {
				tagContent = preTagContent.get(tag.getTagid());
			}
			content = content.replace(key, tagContent);
		}
		if (type != RenderType.tplEdit) {
			// 去掉cmpp标签
			content = content.replaceAll("</?cmpp>", "");
		}/*else{
			content = content.replaceAll("</?cmpp_banner>", "");
		}*/
		if (type == RenderType.blockEdit) {
			content = ProcessBlockTag(content);
		}
		if (type == RenderType.idxEdit) {
			content = content.replaceAll("</?cmpp_banner>", "");
		}
		if (type != RenderType.publish) {
			content = replaceSSI(siteUrl, content);
		} else {
			content = content.replaceAll("</?cmpp_banner>", "");
		}
		long t2 = System.currentTimeMillis();
		log.info(id + "模板渲染:" + (t2 - t1));
		return content;
	}

	// 解析模板上的标签内函数的方法，函数对应一段系统脚本，返回变量为脚本参数内的content参数
	private TagModel RenderTag(String parmsStr, Template2 ctr, RenderType renderType) {
		TagModel tmd = new TagModel();
		JSONObject arg = new JSONObject();
		try {
			arg = new JSONObject(parmsStr);
		} catch (JSONException e1) {
			log.error(e1);
			tmd.setContent("<!--" + e1.getMessage() + "-->");
		}
		Map<String, Object> args = arg.toMap();

		Object o = args.get("vtype");
		int type = 0;
		if (o != null) {
			type = Integer.parseInt(o.toString());
		}
		tmd.setType(type);
		try {
			if (ctr.getLevel() > 10) {
				throw new Exception(parmsStr + "标签解析出错，标签嵌套不能超过10级");
			}
			int nodeId = this.fc.getNodeId();
			Integer formId = Integer.parseInt(args.get("formId").toString());
			int id = Integer.parseInt(args.get("id").toString());

			Map<String, Object> argsMap = new HashMap<String, Object>();
			argsMap.put("args", args);
			// FIXME 20130130 yangbo
//			Map<String, Object> extraDataPool = new HashMap<String, Object>();
//			extraDataPool.put("args", argsMap);
			FormConfig fc2 = FormConfig.getInstance(fc.getNodeId(), formId);
			Map<String, Object> dataPool = formService.preview(fc2, 0, Integer.valueOf(id), renderType, argsMap);

			Object ot = args.get("title");
			String title = ot == null ? "" : ot.toString();
			tmd.setTitle(title);
			ot = dataPool.get("content");
			String content = ot == null ? "" : (String) ot;
			tmd.setContent(content);
			String editurl = "/Cmpp/runtime/template!preview.jhtml?formId=" + formId + "&id=" + id + "&nodeId="
					+ nodeId + "&viewId=0";
			ot = dataPool.get("editurl");
			editurl = ot == null ? editurl : (String) ot;
			tmd.setEditurl(editurl);
		} catch (Exception e) {
			log.error(e);
			tmd.setContent("<!--" + e.getMessage() + "-->");
		}
		return tmd;
	}

	private String ProcessBlockTag(String content)

	{
		// String rtn = "";
		StringWriter rtn = new StringWriter();
		Pattern pt = Pattern.compile("(?<=<cmpp_banner>)[\\s\\S]*?(?=</cmpp_banner>)");
		Matcher mc = pt.matcher(content);
		Integer i = 0;
		int p = 0;
		while (mc.find()) {

			String block = mc.group();
			String block2 = addDataToHtmlTag(block, i.toString(), "bannerId");
			int start = mc.start();
			int end = mc.end();
			if (start > p) {
				rtn.append(content.substring(p, start));
			}
			rtn.append(block2);
			p = end;
			// rtn = rtn.substring(0,start-1)+block2+rtn.substring(end-1);
			i++;
		}
		if (p < content.length()) {
			rtn.append(content.substring(p));
		}
		return rtn.toString();
	}

	public String getDomain(String url) {
		Pattern siteUrlP = Pattern.compile("http://[\\s\\S]*?[/\\\\]");
		Matcher m = siteUrlP.matcher(url);
		if (m.find()) {
			return m.group();
		}
		return url;
	}

	public void AddPreTagContent(String id, String content) {
		preTagContent.put(id, content);
	}

	private String addDataToHtmlTag(String content, String data, String tagName) {
		Pattern pt = Pattern.compile("(?<=^[^<]*?<)[^>]*(?=>)");
		Matcher mc = pt.matcher(content);
		if (mc.find()) {
			if (mc.group().startsWith("!--")) {
				return content;
			}
			String replace = mc.group() + " " + tagName + "='" + data + "'";
			return mc.replaceFirst(replace);
		}
		return content;
	}

	public Map<String, Object> initMap() {
		return new HashMap<String, Object>();
	}

	public String replaceSSI(String url, String content) {
		String rtn = content;

		String siteUrl = getDomain(url);
		Pattern tagP = Pattern.compile("<!--[ ]*#include[ ]*?virtual=\"([\\s\\S]*?)\"[ ]*-->");
		Matcher m = tagP.matcher(content);
		OSCache cache = (OSCache) SpringContextUtil.getBean("oscache_snappage");
		while (m.find()) {
			String tag = m.group();
			String incUrl = siteUrl + m.group(1);
			Object o = cache.get(incUrl);
			String incContent = "";
			if (o == null) {
				try {
					long t1 = System.currentTimeMillis();
					incContent = HttpUtil.get(incUrl, "").get("content").toString();
					// 验证抓取的html代码是否闭合
					HtmlPlugin htmlCheck = (HtmlPlugin) ((ScriptPluginFactory) SpringContextUtil
							.getBean("pluginFactory")).getP("html");
					String ck = htmlCheck.check(incContent);
					if (ck.equals("")) {
						cache.put(incUrl, incContent);
					} else {
						incContent = "<div style='background:red;height:40px;width:100%;'>抓取:" + incUrl + " 内容标签闭合检查失败:" + ck + "</div>";
					}
					log.info("抓取:"+incUrl + " | " + (System.currentTimeMillis() - t1) + " ms");
				} catch (Exception ex) {
					incContent = "<div style='background:red;height:40px;width:100%;'>抓取:" + incUrl + " 失败:" + ex.getMessage() + "</div>";
				}
			} else {
				incContent = o.toString();
			}

			rtn = rtn.replace(tag, incContent);
		}
		return rtn;
	}

	/*
	 * 兼容Template.java
	 */
	public void setTemplate(String templateStr, Map<String, Object> data) throws IOException, TemplateException {
		setTemplateString(templateStr);
		data = data == null ? new HashMap<String, Object>() : data;
		setData(data);
	}

	/*
	 * 兼容Template.java
	 */
	public String render() throws IOException, TemplateException {
		return render(this.rendertype);
	}
	
	/*
	 * 兼容Template.java
	 */
	public String render(RenderType type) throws IOException, TemplateException {
		return render(type, getData());
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public Map<String, String> getPreTagContent() {
		return preTagContent;
	}

	public RenderType getRendertype() {
		return rendertype;
	}

	public void setRendertype(RenderType rendertype) {
		this.rendertype = rendertype;
	}

	public FormConfig getFc() {
		return fc;
	}

	public void setFc(FormConfig fc) {
		this.fc = fc;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getTemplateString() {
		return templateString;
	}

	public void setTemplateString(String templateString) {
		this.templateString = templateString;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public FormService getFormService() {
		return formService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

}
