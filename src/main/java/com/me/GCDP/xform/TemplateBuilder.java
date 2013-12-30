package com.me.GCDP.xform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Template;
import com.me.GCDP.model.Version;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.version.VersionHelper2;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class TemplateBuilder {

	private static String reg = "\\{#renderdata([\\s\\S]*?)#\\}";
	private static Log log = LogFactory.getLog(TemplateBuilder.class);

	/*public static JSONArray build(String html, String commandData, FormPlugin fp, TemplateModel tpl, int nodeId)
			throws Exception {
		JSONArray rtn = new JSONArray();
		if (commandData == null)
			return rtn;
		Document d = Jsoup.parse(html);
		JSONArray commands = new JSONArray(commandData);
		Map<Element, JSONObject> tags = new HashMap<Element, JSONObject>();

		for (int i = 0; i < commands.length(); i++) {
			JSONObject rt = new JSONObject();

			JSONObject cmd = commands.getJSONObject(i);
			String xpath = cmd.getString("xpath");
			Elements es = d.select(xpath);
			if (es.size() == 0) {
				throw new Exception("未能根据xpath：" + xpath + "选取到dom节点。");
				// continue;
			}
			Element e = es.get(0);
			rt.put("xpath", xpath);
			String action = cmd.getString("action");
			if (action.equals("delete")) {
				e.removeAttr("cmpp_params");
				e.removeAttr("cmpp");
				rt.put("success", true);
				rtn.put(rt);
				continue;
			}
			int formId = cmd.getInt("formId");
			int id = cmd.getInt("id");
			int vtype = cmd.getInt("vtype");
			String title = cmd.getString("title");
			Map<String, Object> data = cmd.getJSONObject("params").toMap();
			data = getFormData(data);

			// System.out.println(xpath);
			// System.out.println(e.outerHtml());
			e.removeAttr("cmpp_params");
			e.removeAttr("cmpp");
			data.put("defaultHtml", e.outerHtml());
			JSONObject parms = new JSONObject();
			parms.put("formId", formId);
			parms.put("title", title);
			parms.put("vtype", vtype);
			try {
				FormHelper helper = fp.getFormHelper(formId, id);
				Map<String, Object> dataPool = helper.save(data);
				int rtnid = (Integer) dataPool.get("id");
				if (id == 0) {
					id = rtnid;
				}
				boolean verify = (Boolean) dataPool.get("verify");
				if (verify) {
					rt.put("success", true);
				} else {
					rt.put("success", false);
					rt.put("message", (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : ""));
				}
			} catch (Exception ex) {
				rt.put("success", false);
				rt.put("message", ex.getMessage());
			}
			rt.put("id", id);
			parms.put("id", id);
			// 替换标签
			tags.put(e, parms);
			rtn.put(rt);
		}
		
		getIdxElement(d, tags);
		saveTemplate(d, tags, tpl, fp, nodeId);

		return rtn;
	}

	public static JSONArray build(String html, String commandData, FormPlugin fp, int tplId, int nodeId)
			throws Exception {
		TemplateModel tpl = fp.getTemplate(tplId);
		return build(html, commandData, fp, tpl, nodeId);
	}*/

	private static void getIdxElement(Document d, Map<String, Map> data) throws JSONException {
		// System.out.print(d.html());
		Elements es = d.select("[cmpp_params]");
		for (Element e : es) {
			String cmppid = e.attr("cmppid");
			if (!data.containsKey(cmppid)) {
				String parms = e.attr("cmpp_params");
				e.removeAttr("cmpp_params");
				e.removeAttr("cmpp");
				JSONObject js = new JSONObject(parms);
				//data.put(e, js);
				Map<String,Object> m = new HashMap<String,Object>();
				m.put("e", e);
				m.put("parms", js);
				data.put(cmppid, m);
			}
		}
	}

	/*private static void saveTemplate(Document d, Map<Element, JSONObject> data, TemplateModel tpl, FormPlugin fp,
			int nodeId) throws Exception {

		List<Object[]> lst = new ArrayList<Object[]>();
		for (Element e : data.keySet()) {
			JSONObject tag = data.get(e);
			int formId = 0;
			int id = 0;
			int pid = 0;
			if (tag.has("formId")) {
				formId = tag.getInt("formId");
			}
			if (tag.has("id")) {
				id = tag.getInt("id");
			}
			if (tag.has("pid")) {
				pid = tag.getInt("pid");
			}
			if (pid != 0 && id == 0) {
				// 根据母碎片ID复制
				FormPlugin hp = fp.getHelper(formId, id);
				id = copyItem(formId, pid, hp);
				tag.put("id", id);
			}
			TextNode nd = new TextNode(getReplaceStr(formId, id), "");
			// e.replaceWith(nd);
			Object[] ts = new Object[2];
			ts[0] = e;
			ts[1] = nd;
			lst.add(ts);
		}
		for (Object[] ol : lst) {
			Element e1 = (Element) ol[0];
			TextNode n1 = (TextNode) ol[1];
			e1.replaceWith(n1);
		}
		
		String content = d.html();
		for (JSONObject js : data.values()) {
			int fid = js.getInt("formId");
			int id = js.getInt("id");
			content = content.replace(getReplaceStr(fid, id), "{#renderdata" + js.toString() + "#}");
		}
		// 处理模板的逻辑区块
		content = processCmppTag(tpl.getContent(), content);
		
		// 保存最终生成的模板内容
		String sql = "update cmpp_template set content=? where id =?";
		Object[] params = new Object[2];
		params[0] = content;
		params[1] = tpl.getId();
		MySQLHelper.ExecuteNoQuery(sql, params);
		// 保存历史版本
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("content", content);
		saveVersion(nodeId, tpl.getId(), dataMap);
		processRelation(tpl.getFormId(), tpl.getDataId(), tpl.getId(), content);
	}*/

	private static String getReplaceStr(int formId, int id) {
		return "{#tg:" + formId + "-" + id + "#}";
	}

	private static String processCmppTag(String template, String html) throws Exception {
		String rtn = html;
		Pattern p = Pattern.compile("<cmpp>[\\s\\S]*?</cmpp>|<!--cmpp:start-->[\\s\\S]*?<!--cmpp:end-->");
		Matcher m1 = p.matcher(template);
		Matcher m2 = p.matcher(html);
		if (m1.groupCount() == m2.groupCount()) {
			while (m1.find() && m2.find()) {
				rtn = rtn.replace(m2.group(), m1.group());
			}
		} else {
			throw new Exception("模板标注的逻辑区块与原始模板不匹配.");
		}
		return rtn;
	}

	public static Map<String, Object> getFormData(Map<String, Object> data) {
		Map<String, Object> rtn = new HashMap<String, Object>();
		for (String k : data.keySet()) {
			if (k.startsWith("xform.")) {
				rtn.put(k.replace("xform.", ""), data.get(k));
			}
		}
		return rtn;
	}

	public static void processRelation(TemplateModel md) {
		// md.
	}

	public static void processRelation(int formId, int dataId, int tplId, String template) {
		Pattern tagP = Pattern.compile(reg);
		String dataKey = formId + "|" + dataId;
		Matcher tagM = tagP.matcher(template);
		List<String> data1 = new ArrayList<String>();
		while (tagM.find()) {
			String parms = tagM.group(1);
			try {
				JSONObject arg = new JSONObject(parms);
				Integer dependFormId = Integer.parseInt(arg.get("formId").toString());
				int dependId = Integer.parseInt(arg.get("id").toString());
				String key = dependFormId + "|" + dependId;
				if (!data1.contains(key)) {
					data1.add(key);
				}
			} catch (JSONException e) {
				log.error(e.getMessage() + ",parms= " + parms);
			}

		}
		String sql = "select dependKey from cmpp_data_relation where templateId=? and dataKey=?";
		Object[] parm = new Object[2];
		parm[0] = tplId;
		parm[1] = dataKey;
		try {
			List<String> data2 = new ArrayList<String>();
			Vector<Map<String, Object>> data = MySQLHelper.ExecuteSql(sql, parm);
			for (Map<String, Object> d : data) {
				data2.add(d.get("dependKey").toString());
			}
			// 获取数据库里没有的关系
			List<String> addR = new ArrayList<String>();
			addR.addAll(data1);
			addR.removeAll(data2);
			insertRelation(addR, tplId, dataKey);
			// 获取数据库里冗余的关系
			List<String> deleteR = new ArrayList<String>();
			deleteR.addAll(data2);
			deleteR.removeAll(data1);
			deleteRelation(deleteR, tplId, dataKey);

		} catch (SQLException e) {
			log.error("处理模板关系出错:" + e.getMessage());
		}

	}

	private static void deleteRelation(List<String> data, int tplId, String dataKey) throws SQLException {
		if (data.size() > 0) {
			String ks = "";
			for (String s : data) {
				if (ks.equals("")) {
					ks = "'" + s + "'";
				} else {
					ks = ks + ",'" + s + "'";
				}
			}
			String sql = "delete from cmpp_data_relation where templateId=" + tplId + " and dependKey in(" + ks
					+ ") and dataKey='" + dataKey + "'";
			MySQLHelper.ExecuteNoQuery(sql, null);
		}
	}

	private static void insertRelation(List<String> data, int tplId, String dataKey) throws SQLException {
		if (data.size() > 0) {
			List<String> ls = new ArrayList<String>();
			String sql = "insert into cmpp_data_relation(templateId,dataKey,dependKey) values(%1$s,'%2$s','%3$s')";
			for (String s : data) {
				ls.add(String.format(sql, tplId, dataKey, s));
			}
			// System.out.println(ls);
			MySQLHelper.executeBatch(ls);
		}
	}

	public static int copy(TemplateModel tpl, int dataId, FormPlugin hp) throws Exception {
		String template = tpl.getContent();
		Pattern tagP = Pattern.compile(reg);
		Matcher tagM = tagP.matcher(template);
		while (tagM.find()) {
			String parms = tagM.group(1);
			JSONObject arg = new JSONObject(parms);
			Integer dependFormId = Integer.parseInt(arg.get("formId").toString());
			Integer dependId = Integer.parseInt(arg.get("id").toString());
			log.info("copy idx" + dependFormId + "|" + dependId);
			int newId = copyItem(dependFormId, dependId, hp);
			arg.put("id", newId);
			template = template.replace(parms, arg.toString());

		}
		Object[] params = new Object[5];
		params[0] = tpl.getName();
		params[1] = template;
		params[2] = tpl.getEnable();
		params[3] = tpl.getFormId();
		params[4] = dataId;
		String sql = "insert into cmpp_template(name,content,enable,dataFormId,dataId,createDate,modifyDate) value(?,?,?,?,?,now(),now())";
		int tplId = Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, params));
		sql = "update cmpp_template set name=? where id=?";
		params = new Object[2];
		params[0] = tpl.getName() + "_" + tplId;
		params[1] = tplId;
		MySQLHelper.ExecuteNoQuery(sql, params);

		tpl.setId(tplId);
		tpl.setDataId(dataId);
		processRelation(tpl);
		return tplId;
	}

	private static int copyItem(int formId, int id, FormPlugin hp) throws Exception {
		FormPlugin helper = hp.getHelper(formId, id);
		Map<String, Object> data = helper.getData();
		data.remove("id");
		return helper.saveData(0, data);
	}

	private static void saveVersion(int nodeId, int tid, Map<String, Object> data) {
		String userName = "";
		try {
			userName = AuthorzationUtil.getUserName() + "[" + AuthorzationUtil.getUserId() + "]";
			VersionMapper<Version> mp = (VersionMapper<Version>) SpringContextUtil.getBean("versionMapper");
			String key = nodeId + "_template_" + tid;
			JSONObject formDataJson = new JSONObject(data);
			VersionHelper2 fh = new VersionHelper2(key, userName, formDataJson.toString());
			fh.setVersionMapper(mp);
			fh.save();
		} catch (Exception e) {
			log.error(userName + ":保存模板历史记录出错" + e.toString());
		}
	}
	
	/**
	 * 遍历所有Dom节点添加CmppID属性（for模板可视化）
	 * @param html
	 * @return
	 */
	public static String addCmppID(String html){
		long t1 = System.currentTimeMillis();
		Document d = Jsoup.parse(html);
		List<Node> list = d.childNodes();
		if(list.size() > 0){
			processChildNodes(list, "item");
		}
		log.info("=======addCmppID() : " + (System.currentTimeMillis() - t1) + " ms");
		return d.html();
	}
	
	public static void removeCmppID(List<Node> list){
		if(list != null && list.size() > 0){
			for(int i = 0 ; i < list.size() ; i ++){
				Node n = list.get(i);
				n.removeAttr("cmppid");
				if(n.childNodes().size()>0){
					removeCmppID(n.childNodes());
				}
			}
		}
	}
	
	public static void processChildNodes(List<Node> list, String prefix){
		for(int i = 0 ; i < list.size() ; i ++){
			Node n = list.get(i);
			if(!n.nodeName().equalsIgnoreCase("html") 
					&& !n.nodeName().equalsIgnoreCase("head") 
					&& !n.nodeName().equalsIgnoreCase("body")
					&& !n.nodeName().equalsIgnoreCase("script")
					&& !n.nodeName().equalsIgnoreCase("noscript")
					&& !n.nodeName().equalsIgnoreCase("style")
					&& !n.nodeName().equalsIgnoreCase("meta")
					&& !n.nodeName().equalsIgnoreCase("title")
					&& !n.nodeName().equalsIgnoreCase("link")
					&& !n.nodeName().equalsIgnoreCase("cmpp")
					&& !n.nodeName().equalsIgnoreCase("cmpp_banner")){
				n.attr("cmppid", prefix+"-"+i);
			}
			if(n.childNodes().size()>0){
				processChildNodes(n.childNodes(),prefix+"-"+i);
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		String html = (String)HttpUtil.get("http://www.ifeng.com/", "").get("content");
		long t1 = System.currentTimeMillis();
		String ret = addCmppID(html);
		log.info(ret);
		log.info(System.currentTimeMillis() - t1);
	}

	/**
	 * Watch: new feature!!
	 * @param fc
	 * @param html
	 * @param commandData
	 * @param tpl
	 * @return
	 * @throws Exception
	 */
	public static JSONArray build(FormConfig fc, String html, String commandData, Template tpl) throws Exception {
		FormService formService = (FormService) SpringContextUtil.getBean("formService");
		JSONArray rtn = new JSONArray();
		if (commandData == null)
			return rtn;
		Document d = Jsoup.parse(html);
		JSONArray commands = new JSONArray(commandData);
		Map<String, Map> tags = new HashMap<String, Map>();
		/*
		 * 2013.3.21 by huwq
		 * xpath替换为cmppid
		 */
		for (int i = 0; i < commands.length(); i++) {
			JSONObject rt = new JSONObject();

			JSONObject cmd = commands.getJSONObject(i);
			String cmppid = cmd.getString("cmppid");
			Elements es = d.getElementsByAttributeValue("cmppid", cmppid);
			//String xpath = cmd.getString("xpath");
			//Elements es = d.select(xpath);
			if (es.size() == 0) {
				//throw new Exception("未能根据xpath：" + xpath + "选取到dom节点。");
				throw new Exception("未能根据cmppid：" + cmppid + "选取到dom节点。");
			}
			Element e = es.get(0);
			//rt.put("xpath", xpath);
			rt.put("cmppid", cmppid);
			String action = cmd.getString("action");
			e.removeAttr("cmppid");
			removeCmppID(e.childNodes());
			if (action.equals("delete")) {
				e.removeAttr("cmpp_params");
				e.removeAttr("cmpp");
				rt.put("success", true);
				rtn.put(rt);
				continue;
			}
			int formId = cmd.getInt("formId");
			int id = cmd.getInt("id");
			int vtype = cmd.getInt("vtype");
			String title = cmd.getString("title");
			Map<String, Object> data = cmd.getJSONObject("params").toMap();
			data = getFormData(data);

			// System.out.println(xpath);
			// System.out.println(e.outerHtml());
			e.removeAttr("cmpp_params");
			e.removeAttr("cmpp");
			data.put("defaultHtml", e.outerHtml());
			JSONObject parms = new JSONObject();
			parms.put("formId", formId);
			parms.put("title", title);
			parms.put("vtype", vtype);
			try {
				FormConfig fc2 = FormConfig.getInstance(fc.getNodeId(), formId);
				Map<String, Object> dataPool = formService.save(fc2, id,data);

				int rtnid = (Integer) dataPool.get("dataId");
				if (id == 0) {
					id = rtnid;
				}
				boolean verify = (Boolean) dataPool.get("verify");
				if (verify) {
					rt.put("success", true);
				} else {
					rt.put("success", false);
					rt.put("message", (dataPool.containsKey("msg") ? dataPool.get("msg").toString() : ""));
				}
			} catch (Exception ex) {
				rt.put("success", false);
				rt.put("message", ex.getMessage());
				ex.printStackTrace();
			}
			rt.put("id", id);
			parms.put("id", id);
			// 替换标签
			//tags.put(e, parms);
			Map m = new HashMap();
			m.put("e", e);
			m.put("parms", parms);
			tags.put(cmppid, m);
			rtn.put(rt);
		}
		
		getIdxElement(d, tags);
		
		//去掉CMPPid属性
		removeCmppID(d.childNodes());
		d.removeAttr("cmppid");
		
		saveTemplate(fc, d, tags, tpl);

		return rtn;
	}

	/**
	 * Watch: new feature!!
	 * @param fc
	 * @param d
	 * @param data
	 * @param tpl
	 * @throws Exception
	 */
	private static void saveTemplate(FormConfig fc, Document d, Map<String, Map> data, Template tpl)
			throws Exception {
		FormService formService = (FormService) SpringContextUtil.getBean("formService");
		List<Object[]> lst = new ArrayList<Object[]>();
		for (String cmppid : data.keySet()) {
			Map<String,Object> m = data.get(cmppid);
			Element e = (Element)m.get("e");
			JSONObject tag = (JSONObject)m.get("parms");
			int formId = 0;
			int id = 0;
			int pid = 0;
			if (tag.has("formId")) {
				formId = tag.getInt("formId");
			}
			if (tag.has("id")) {
				id = tag.getInt("id");
			}
			if (tag.has("pid")) {
				pid = tag.getInt("pid");
			}
			if (pid != 0 && id == 0) {
				// 根据母碎片ID复制
				// FormPlugin hp = fp.getHelper(formId,id);
				FormConfig fc2 = FormConfig.getInstance(fc.getNodeId(), formId);
				id = copyItem(fc2, pid, formService);
				tag.put("id", id);
			}
			TextNode nd = new TextNode(getReplaceStr(formId, id), "");
			// e.replaceWith(nd);
			Object[] ts = new Object[2];
			ts[0] = e;
			ts[1] = nd;
			lst.add(ts);
		}
		for (Object[] ol : lst) {
			Element e1 = (Element) ol[0];
			TextNode n1 = (TextNode) ol[1];
			e1.replaceWith(n1);
		}
		String content = d.html();
		for (Map m : data.values()) {
			//JSONObject js = new JSONObject(m.get("parms").toString());
			JSONObject js = (JSONObject)m.get("parms");
			int fid = js.getInt("formId");
			int id = js.getInt("id");
			content = content.replace(getReplaceStr(fid, id), "{#renderdata" + js.toString() + "#}");
		}
		// 处理模板的逻辑区块
		content = processCmppTag(tpl.getContent(), content);
		// 保存最终生成的模板内容
		Template t = new Template();
		t.setId(tpl.getId());
		t.setContent(content);
		TemplateMapper<Template> templateMapper = (TemplateMapper<Template>) SpringContextUtil.getBean("templateMapper");
		templateMapper.update(t);
		// 保存历史版本
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("content", content);
		saveVersion(fc.getNodeId(), tpl.getId(), dataMap);
		processRelation(tpl.getFormId(), tpl.getDataId(), tpl.getId(), content);
	}

	/**
	 * Watch: new feature!!
	 * @param fc
	 * @param id
	 * @param formService
	 * @return
	 * @throws Exception
	 */
	private static int copyItem(FormConfig fc, int id, FormService formService) throws Exception {
		Map<String, Object> data = formService.getData(fc, id);
		data.remove("id");
		return formService.saveData(fc, 0, data);
	}
	
	public static int copy(int tplId, int dataId) throws Exception {
		FormService formService = (FormService) SpringContextUtil.getBean("formService");
		TemplateMapper<Template> templateMapper = (TemplateMapper<Template>) SpringContextUtil.getBean("templateMapper");
		Template tpl = templateMapper.getById(tplId);
		if (tpl==null){
			throw new java.lang.Exception("tplId:"+tplId+" not exist.");
		}
		
		String template = tpl.getContent();
		Pattern tagP = Pattern.compile(reg);
		Matcher tagM = tagP.matcher(template);
		while (tagM.find()) {
			String parms = tagM.group(1);
			JSONObject arg = new JSONObject(parms);
			Integer dependFormId = Integer.parseInt(arg.get("formId").toString());
			Integer dependId = Integer.parseInt(arg.get("id").toString());
			log.info("copy idx" + dependFormId + "|" + dependId);
			
			FormConfig fc2 = FormConfig.getInstance(0, dependFormId);
			int newId = copyItem(fc2, dependId, formService);
			
			arg.put("id", newId);
			template = template.replace(parms, arg.toString());
		}
		
		Template t = new Template();
		t.setName(tpl.getName());
		t.setContent(template);
		t.setEnable(tpl.getEnable());
		t.setFormId(tpl.getFormId());
		t.setDataId(dataId);
		int affected = templateMapper.insert(t);
		if (affected > 0) {
			String newName = t.getName() + "_" + t.getId();
			tplId = t.getId();
			
			Template t2 = new Template();
			t2.setId(tplId);
			t2.setName(newName);
			templateMapper.update(t2);
			
			return tplId;
		} else {
			throw new java.lang.Exception("Encounter Error while copying template. tplId:"+tplId);
		}
	}
}
