package com.me.GCDP.xform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.source.SubscribeService;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.version.VersionHelper2;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.plugin.CmppDBPlugin;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;
import com.me.GCDP.search.SearchService_V2;
import com.me.json.JSONException;


@PluginClass(author = "yangjunjie", intro = "表单插件，提供表单操作的帮助插件",tag="表单")
@PluginExample(intro ="//表单脚本【保存前，保存后，渲染，发布】等脚本的dataPool中已经内置了form插件的实例可以直接获得，不需要通过插件系统重新获得并实例化该对象,通过dataPool中获取到该对象后不需要重新初始化;<br />"+
"var helper = dataPool.get(\"helper\");<br />"+
"//在非表单脚本环境，例如在接口脚本，任务计划脚本中需要通过插件系统获得，目前获得支持从插件工厂方法获得，和通过脚本头部显式声明获得;<br />"+
"//通过插件工厂获得，获得后需要初始化<br />"+
"var form=pluginFactory.getP(\"form\");<br />"+
"form.init(nodeId,formId,id);<br />"+
"//通过脚本头部显示声明获得,获得后需要初始化<br/>"+
"//头部加入以下注释"+
"//#plugin=form#<br />"+
"form.init(nodeId,formId,id);<br />"
)
public class FormPlugin extends ScriptPlugin{
	private static Log log = LogFactory.getLog(FormPlugin.class);
	private int formId;
	private int id;
	ScriptService scriptService;
	public ScriptService getScriptService() {
		return scriptService;
	}
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}
	public SearchService_V2 getSearchSvr() {
		return searchSvr;
	}
	public void setSearchSvr(SearchService_V2 searchSvr) {
		this.searchSvr = searchSvr;
	}
	public SubscribeService getSubSvr() {
		return subSvr;
	}
	public void setSubSvr(SubscribeService subSvr) {
		this.subSvr = subSvr;
	}
	SearchService_V2 searchSvr;
	SubscribeService subSvr;
	private CmppDBPlugin cmppDBPlugin;
	
	private int nodeId;
	FormHelper helper;
	private Map<String,Object[]> form=null;
	public FormPlugin(int formId,int id,FormHelper helper)
	{
		this.formId = formId;
		this.id = id;
		this.helper = helper;
	}
	public FormPlugin() {
	}
	public void setDataId(int dataId)
	{
		this.id = dataId;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单启用的默认模板", 
	paramIntro = {},
	returnIntro = "返回模板实例对象,类型为TemplateModel,包含[getName,getContent,getPubUrl,getEnable,getFormId,getDataId]方法,puburl为权限目标，非发布路径，需注意"
	)
	public TemplateModel getDefaultTemplate()
	{
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where dataFormId="+formId+" and dataId in(0,"+id+") and enable=1 order by dataId desc,id desc limit 1";
		//System.out.println(sql);
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				return formatTemplate(data.get(0));
			}
		} catch (SQLException e) {
			Error(e);
		}
		
		return null;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单启用的默认模板", 
	paramIntro = {},
	returnIntro = "同getDefaultTemplate()方法"
	)
	public TemplateModel getTemplate()
	{
		return getDefaultTemplate();
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取指定的表单模板", 
	paramIntro = {"模板ID"},
	returnIntro = "返回模板实例对象,类型为TemplateModel"
	)
	public TemplateModel getTemplate(int tplid)
	{
		TemplateModel rtn =null;
		if(tplid==0)
		{
			rtn = getDefaultTemplate();
		}
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where id="+tplid;
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				rtn = formatTemplate(data.get(0));
			}
		} catch (SQLException e) {
			Error(e);
		}
		
		return rtn;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单状态为有效的所有模板列表,按照模板id有大到小的顺序排列", 
	paramIntro = {},
	returnIntro = "返回模板实例对象列表"
	)
	public List<TemplateModel> getTemplateList()
	{
		List<TemplateModel> rtn = new ArrayList<TemplateModel>();
		String sql ="select id,name,content,enable,dataFormId,dataId from cmpp_template where dataFormId="+formId+" and dataId in(0,"+id+") and enable=1 order by dataId desc,id desc";
		Vector<Map<String, Object>> data;
		try {
			data = MySQLHelper.ExecuteSql(sql, null);
			for(Map<String, Object> d:data)
			{
				rtn.add(formatTemplate(d));
			}
		} catch (SQLException e) {
			Error(e);
		}
		return rtn;
	}
	@PluginIsPublic
	@PluginMethod(intro = "将当前表单的指定模板复制出一个新的模板", 
	paramIntro = {"要复制的模板实例对象"},
	returnIntro = "返回复制成功的新模板ID"
	)
	public int copyTemplate(TemplateModel tpl) throws Exception
	{
		return TemplateBuilder.copy(tpl, id,this);
	}
	@PluginIsPublic
	@PluginMethod(intro = "将当前表单的指定模板复制出一个新的模板", 
	paramIntro = {"要复制的模板ID"},
	returnIntro = "返回复制成功的新模板ID"
	)
	public int copyTemplate(int tplId) throws Exception
	{
		TemplateModel tpl = getTemplate(tplId);
		return TemplateBuilder.copy(tpl, id,this);
	}
	private TemplateModel formatTemplate(Map<String,Object> data)
	{
		TemplateModel rtn = new TemplateModel();
		rtn.setId((Integer) data.get("id"));
		rtn.setName((String) data.get("name"));
		rtn.setContent((String) data.get("content"));
		rtn.setEnable((Integer) data.get("enable"));
		rtn.setDataId((Integer) data.get("dataId"));
		rtn.setFormId((Integer) data.get("dataFormId"));
		return rtn;
	}
	private void Error(Exception e)
	{
		log.error("获取模板出错formId="+formId+",id="+id+e.getMessage());
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单的指定ID的数据", 
	paramIntro = {"数据记录的ID"},
	returnIntro = "该记录的Map数据"
	)
	public Map<String,Object> getData(int id) throws JSONException, SQLException
	{
		Map<String,Object> data= helper.getHelper(id).getData();
		if(form!=null)
		{
			return FormHelper.getData(form, data);
		}
		return data;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单当前记录的数据", 
	paramIntro = {},
	returnIntro = ""
	)
	public Map<String,Object> getData() throws JSONException, SQLException
	{
		return getData(id);
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单记录的最大ID", 
	paramIntro = {},
	returnIntro = ""
	)
	public int getMaxId()
	{
		return helper.getFormLastId();
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前表单最新的数据", 
	paramIntro = {},
	returnIntro = ""
	)
	public Map<String,Object> getLastData() throws JSONException, SQLException
	{
		return getData(getMaxId());
	}
	@PluginIsPublic
	@PluginMethod(intro = "初始化一个空的Map", 
	paramIntro = {},
	returnIntro = ""
	)
	public HashMap<String,Object> initMap()
	{
		return new HashMap<String, Object>();
	}
	@PluginIsPublic
	@PluginMethod(intro = "初始化一个空的List", 
	paramIntro = {},
	returnIntro = ""
	)
	public List<Object> initList()
	{
		return new ArrayList<Object>();
	}
	@PluginIsPublic
	@PluginMethod(intro = "指定记录ID，和模板ID渲染表单，并返回渲染脚本dataPool", 
	paramIntro = {"记录ID","模板ID"},
	returnIntro = ""
	)
	public Map<String,Object> render(int id,int tplId) throws Exception
	{
		return render(id, tplId, RenderType.publish);	
	}
	@PluginIsPublic
	@PluginMethod(intro = "渲染当前表单，并返回渲染脚本dataPool", 
	paramIntro = {},
	returnIntro = ""
	)
	public Map<String,Object> render() throws Exception
	{
		return render(id,0,RenderType.publish);
	}
	@PluginIsPublic
	@PluginMethod(intro = "指定记录ID渲染表单，并返回渲染脚本dataPool", 
	paramIntro = {"记录ID"},
	returnIntro = ""
	)
	public Map<String,Object> render(int id) throws Exception
	{
		return render(id,0,RenderType.publish);
	}
	@PluginIsPublic
	@PluginMethod(intro = "指定记录ID，和模板ID，以及渲染类型渲染表单，并返回渲染脚本dataPool", 
	paramIntro = {"记录ID","模板ID","渲染类型"},
	returnIntro = ""
	)
	public Map<String,Object> render(int id,int tplId,RenderType type) throws Exception
	{
		FormHelper hp = helper.getHelper(id);
		return hp.preview(tplId,type);	
	}
	@PluginIsPublic
	@PluginMethod(intro = "指定记录ID，和模板ID，发布表单，并返回发布脚本dataPool", 
	paramIntro = {"记录ID","模板ID"},
	returnIntro = ""
	)
	public Map<String,Object> publish(int id,int tplId) throws Exception
	{
		return helper.getHelper(id).publish(tplId);
	}
	@PluginIsPublic
	@PluginMethod(intro = "指定记录ID，发布表单，并返回发布脚本dataPool", 
	paramIntro = {"记录ID"},
	returnIntro = ""
	)
	public Map<String,Object> publish(int id) throws Exception
	{
		return helper.getHelper(id).publish(0);
	}
	@PluginIsPublic
	@PluginMethod(intro = "发布表单，并返回发布脚本dataPool", 
	paramIntro = {},
	returnIntro = ""
	)
	public Map<String,Object> publish() throws Exception
	{
		return helper.publish(0);
	}
	@PluginIsPublic
	@PluginMethod(intro = "保存表单数据，不执行表单脚本", 
	paramIntro = {"id","表单数据的map"},
	returnIntro = "当id为0时为新增返回保存后该记录的ID，当id不为0时候，返回值为1表示成功0表示失败"
	)
	public int saveData(int id,Map<String,Object> data) throws SQLException, JSONException
	{
		return helper.getHelper(id).saveData(data);
	}
	public int saveData(Map<String,Object> data) throws SQLException, JSONException
	{
		return helper.saveData(data);
	}
	@PluginIsPublic
	@PluginMethod(intro = "删除表单数据，不执行表单脚本", 
	paramIntro = {"id"},
	returnIntro = ""
	)
	public void deleteData(int id) throws SQLException, JSONException
	{
		helper.getHelper(id).deleteData(id);
	}
	public Map<String,Object> save(int id,int viewId,Map<String,Object> data) throws Exception
	{
		return helper.getHelper(id).save(data,viewId);		
	}
	public Map<String,Object> runScript(String key,String ids) throws Exception
	{
		return helper.runScript(key, ids);
	}
	@PluginIsPublic
	@PluginMethod(intro = "保存表单数据，并执行表单脚本，返回保存后表单的dataPool", 
	paramIntro = {"id","表单数据的map"},
	returnIntro = ""
	)
	public Map<String,Object> save(int id,Map<String,Object> data) throws Exception
	{
		data.remove("id");
		return helper.getHelper(id).save(data,0);		
	}
	public void setForm(Map<String,Object[]> form) {
		this.form = form;
	}
	public Map<String,Object[]> getForm() {
		return form;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取指定表单ID，及记录ID的表单插件对象", 
	paramIntro = {"表单ID","数据ID"},
	returnIntro = ""
	)
	public FormPlugin getHelper(int formId,int id) throws JSONException, SQLException
	{
		FormHelper hp = this.helper.getHelper(formId,id);
		return new FormPlugin(formId, id, hp);
	}
	public FormPlugin getHelper()
	{
		return this;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获取当前nodeId", 
	paramIntro = {"id","表单数据的map"},
	returnIntro = ""
	)
	public int getNodeId() {
		return nodeId;
	}
	@PluginIsPublic
	@PluginMethod(intro = "设置当前nodeId", 
	paramIntro = {"nodeId"},
	returnIntro = ""
	)
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	@Override
	public void init() {
		init(nodeId,formId,id,null);
	}
	@PluginIsPublic
	@PluginMethod(intro = "初始化插件，设置插件当前的节点id，表单id，以及数据id", 
	paramIntro = {"nodeId","formId","id"},
	returnIntro = ""
	)
	public void init(int nodeId,int formId,int id)
	{
		init(nodeId,formId,id,null);
	}
	public void init(int nodeId,int formId,int id,Map<String,Object[]> form)
	{
		this.nodeId = nodeId;
		this.formId = formId;
		this.id = id;
		this.form = form;
		ScriptPluginFactory scriptPluginFactory = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
		this.cmppDBPlugin = (CmppDBPlugin)scriptPluginFactory.getP("cmppDB");
		initFormHelper(nodeId,formId,id,form);
	}
	private void initFormHelper(int nodeId,int formId,int id,Map<String,Object[]> form)
	{
		try
		{
			this.helper = new FormHelper(nodeId,formId,id,form);
			helper.setDbsvr(cmppDBPlugin);
			helper.setSearchSvr(searchSvr);
			helper.setScriptService(scriptService);
			helper.setSubSvr(subSvr);
		}
		catch(Exception e)
		{
			log.error("初始化formhelper失败formId="+formId+e.getMessage());
		}	
	}
	public VersionHelper2 getVersionHelper2(String key,String userName,String xformDataStr) {
		VersionHelper2 fh = new VersionHelper2(key,userName,xformDataStr);
		return fh;
	}
	public FormHelper getFormHelper(int formId, int id) throws JSONException, SQLException {
		return helper.getHelper(formId,id);
	}
	public FormHelper getFormHelper(){
		return helper;
	}
}
