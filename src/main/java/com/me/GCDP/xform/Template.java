//package com.me.GCDP.xform;
//
//import java.io.IOException;
//import java.io.StringWriter;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.me.GCDP.freemarker.FreeMarkerHelper;
//import com.me.GCDP.script.plugin.HtmlPlugin;
//import com.me.GCDP.script.plugin.ScriptPluginFactory;
//import com.me.GCDP.util.HttpUtil;
//import com.me.GCDP.util.SpringContextUtil;
//import com.me.GCDP.util.oscache.OSCache;
//
//import freemarker.template.TemplateException;
//
//
///*
// * author:yangjunjie 
// * 用来表示模板的类，定义了模板标签的解析规则，以及可视化编辑处理，预览等通用的处理方法
// * modify at 2011-8-30
// * 调整标签解析方式，只有一种类型的标签。
// */
//public class Template {
//	
//	private static Log log = LogFactory.getLog(Template.class);
//	
//	private String template;
//	private String fTemplate;
//	private  Map<String,Object> data;
//	//private TagParserMapper<TagParserModel> tmp = null;
//	private Map<String,TagModel> tags = new HashMap<String, TagModel>();
//	private Map<String,String> preTagContent = new HashMap<String, String>();
//	private  static Pattern tagP = Pattern.compile("\\{#renderdata([\\s\\S]*?)#\\}");
//	private String id="page";
//	private String siteUrl="http://www.ifeng.com/";
//	private FormHelper formSvr;
//	public FormHelper getFormSvr() {
//		return formSvr;
//	}
//	public void setFormSvr(FormHelper formSvr) {
//		this.formSvr = formSvr;
//	}
//	public String getSiteUrl() {
//		return siteUrl;
//	}
//	public void setSiteUrl(String siteUrl) {
//		this.siteUrl = siteUrl;
//	}
//	public Map<String, String> getPreTagContent() {
//		return preTagContent;
//	}
//	RenderType rendertype = RenderType.publish;
//	public RenderType getRendertype() {
//		return rendertype;
//	}
//	public void setRendertype(RenderType rendertype) {
//		this.rendertype = rendertype;
//	}
//	private int level=0;
//	public int getLevel() {
//		return level;
//	}
//	public void setLevel(int level) {
//		this.level = level;
//	}
//	public Template(String id,Map<String,String> preTagContent,RenderType type)
//	{
//		this.id = id;
//		if(preTagContent!=null)
//		{
//		 this.preTagContent = preTagContent;
//		}
//		this.rendertype = type;
//	}
//	public Template(FormHelper formSvr,Map<String,String> preTagContent,RenderType type)
//	{
//		this.formSvr = formSvr;
//		if(preTagContent!=null)
//		{
//		 this.preTagContent = preTagContent;
//		}
//		this.rendertype = type;
//	}
//	public String getDomain(String url)
//	{
//		Pattern siteUrlP =  Pattern.compile("http://[\\s\\S]*?[/\\\\]");
//		Matcher m = siteUrlP.matcher(url);
//		if(m.find())
//		{
//			return m.group();
//		}
//		return url;
//	}
//	public void AddPreTagContent(String id,String content)
//	{
//		preTagContent.put(id, content);
//	}
//	@SuppressWarnings("unchecked")
//	public void init()
//	{
//		//tmp = (TagParserMapper<TagParserModel>) ctx.getBean("tagParserMapper");
//		//解析标签
//		Matcher tagM = tagP.matcher(template);
//		int i=0;
//		while(tagM.find())
//		{
//			String tag = tagM.group();
//			if(!tags.containsKey(tag))
//			{
//				String tagid = id+"_ctl"+i;
//				Template ctag = new Template(tagid,preTagContent,this.rendertype);
//				ctag.setLevel(this.level+1);
//				String funStr = tagM.group(1);
//				TagModel tmd = RenderTag(funStr,ctag);
//				tmd.setParms(funStr);
//				tmd.setTagid(tagid);
//				tags.put(tag,tmd);
//			}
//			i++;
//		}	
//	}
//	
//	public void setTemplate(String template, Map<String,Object> data) throws IOException, TemplateException
//	{	
//		this.fTemplate = template;
//		this.data = data==null?new HashMap<String, Object>():data;
//		this.template = FreeMarkerHelper.process2(this.fTemplate, this.data);
//		long t1 = System.currentTimeMillis();
//		init();
//		long t2 = System.currentTimeMillis();
//		log.info(id+"模板数据载入:"+(t2-t1));
//	}
//	public void setTemplate(String template)
//	{
//		this.template = template;
//		init();
//	}
//	public String render(RenderType type)
//	{
//		long t1 = System.currentTimeMillis();
//		//
//		String content=template;
//		for(String key:tags.keySet())
//		{
//			TagModel tag = tags.get(key);
//			String tagContent = tag.getContent();
//			if(type==RenderType.idxEdit)
//			{
//				JSONObject data = new JSONObject();
//				try {
//					data.put("id", tag.getTagid());
//					data.put("editurl", tag.getEditurl());
//					data.put("type", tag.getType());
//					data.put("title", tag.getTitle());
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				tagContent = addDataToHtmlTag(tagContent,data.toString(),"editdata");
//			}
//			if(type== RenderType.tplEdit)
//			{
//				tagContent = addDataToHtmlTag(tagContent,tag.getParms(),"cmpp_params");
//			}
//			if(preTagContent.containsKey(tag.getTagid()))
//			{
//				tagContent = preTagContent.get(tag.getTagid());
//			}
//			content = content.replace(key,tagContent);
//		}
//		if(type!=rendertype.tplEdit)
//		{
//			//去掉cmpp标签
//			content = content.replaceAll("</?cmpp>", "");
//		}
//		if(type==RenderType.blockEdit)
//		{
//			content = ProcessBlockTag(content);
//		}
//		if(type!=RenderType.publish)
//		{
//			content = replaceSSI(siteUrl, content);
//		}
//		else
//		{
//			content = content.replaceAll("</?cmpp_banner>","");
//		}
//		long t2 = System.currentTimeMillis();
//		log.info(id+"模板渲染:"+(t2-t1));
//		//
//		return content;
//	}
//	//模板解析方法
//	public String render()
//	{
//		return render(this.rendertype);
//	}
//	//解析模板上的标签内函数的方法，函数对应一段系统脚本，返回变量为脚本参数内的content参数
//	public TagModel RenderTag(String parmsStr,Template ctr)
//	{
//		TagModel tmd = new TagModel();		
//			JSONObject arg= new JSONObject();
//			try {
//				arg = new JSONObject(parmsStr);
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				log.error(e1);
//				tmd.setContent( "<!--"+e1.getMessage()+"-->");
//			}
//			Map<String,Object> args = arg.toMap();
//			
//			Object o = args.get("vtype");
//			int type=0;
//			if(o!=null)
//			{
//				type = Integer.parseInt(o.toString());
//			}
//			tmd.setType(type);
//			try {
//				 if(ctr.getLevel()>10)
//				 {
//				  throw new Exception(parmsStr+"标签解析出错，标签嵌套不能超过10级");
//				 }
//				 int nodeId = formSvr.getNodeId();
//				 Integer formId = Integer.parseInt(args.get("formId").toString());
//				 int id =  Integer.parseInt(args.get("id").toString());
//				 
//				 FormHelper helper = formSvr.getHelper(formId,id);
//				 ctr.setFormSvr(helper);
//				 Map<String, Object> dataPool = helper.preview(args, 0, rendertype,ctr);
//
//				 Object ot = args.get("title");
//				 String title = ot==null?"":ot.toString();
//				 tmd.setTitle(title);
//				 ot = dataPool.get("content");
//				 String content =  ot==null?"":(String) ot;
//				 tmd.setContent(content);
//				 String editurl ="/Cmpp/runtime/template!preview.jhtml?formId="+formId+"&id="+id+"&nodeId="+nodeId+"&viewId=0";
//				 ot =  dataPool.get("editurl");
//				 editurl = ot==null?editurl:(String) ot;
//				 tmd.setEditurl(editurl);
//			} catch (Exception e) {
//				log.error(e);
//				tmd.setContent( "<!--"+e.getMessage()+"-->");
//			}
//		return tmd;
//	}
//	private String ProcessBlockTag(String content)
//	
//	{
//		//String rtn = "";
//		StringWriter rtn = new StringWriter();
//		Pattern pt = Pattern.compile("(?<=<cmpp_banner>)[\\s\\S]*?(?=</cmpp_banner>)");
//		Matcher mc = pt.matcher(content);
//		Integer i = 0;
//		int p = 0;
//		while(mc.find())
//		{
//			
//			String block = mc.group();
//			String block2 = addDataToHtmlTag(block,i.toString(),"bannerId");
//			int start = mc.start();
//			int end = mc.end();
//			if(start>p)
//			{
//				rtn.append(content.substring(p, start));
//			}
//			rtn.append(block2);
//			p = end;
//			//rtn = rtn.substring(0,start-1)+block2+rtn.substring(end-1);
//			i++;
//		}
//		if(p<content.length())
//		{
//			rtn.append(content.substring(p));
//		}
//		return rtn.toString();
//	}
//	private String addDataToHtmlTag(String content,String data,String tagName)
//	{
//		Pattern pt = Pattern.compile("(?<=^[^<]*?<)[^>]*(?=>)");
//		Matcher mc = pt.matcher(content);
//		if(mc.find())
//		{
//			if(mc.group().startsWith("!--"))
//			{
//				return content;
//			}
//			String replace = mc.group()+" "+tagName+"='"+data+"'";
//			return mc.replaceFirst(replace);
//		}
//		return content;
//	}
//	
//	public Map<String,Object > initMap()
//	{
//		return new HashMap<String, Object>();
//	}
//	public String replaceSSI(String url,String content)
//	{
//		String rtn = content;
//		
//		String siteUrl = getDomain(url);
//		Pattern tagP = Pattern.compile("<!--[ ]*#include[ ]*?virtual=\"([\\s\\S]*?)\"[ ]*-->");
//		Matcher m =tagP.matcher(content);		
//		OSCache cache = (OSCache) SpringContextUtil.getBean("oscache_snappage");	
//		while(m.find())
//		{
//			String tag = m.group();
//			String incUrl = siteUrl + m.group(1);
//			Object o  =  cache.get(incUrl);
//			String incContent ="";
//			if(o==null)
//			{
//				try
//				{
//					//System.out.println("抓取:"+incUrl);
//					incContent = HttpUtil.get(incUrl,"").get("content").toString();
//					//验证抓取的html代码是否闭合
//					HtmlPlugin htmlCheck = (HtmlPlugin) ((ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory")).getP("html");
//					String ck = htmlCheck.check(incContent);
//					if(ck.equals(""))
//					{
//						cache.put(incUrl, incContent);
//					}
//					else
//					{
//						incContent="<!--抓取:"+incUrl+" 内容标签闭合检查失败:"+ck+" -->";
//					}
//					//System.out.println(incContent);
//				}
//				catch(Exception ex)
//				{
//					incContent="<!--抓取:"+incUrl+"失败,"+ex.getMessage()+"-->";
//				}
//			}
//			else
//			{
//				incContent = o.toString();
//			}
//			
//			rtn = rtn.replace(tag, incContent);
//		}
//		return rtn;
//	}
//}
