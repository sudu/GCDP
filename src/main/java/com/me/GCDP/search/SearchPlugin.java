package com.me.GCDP.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.script.plugin.CoderPlugin;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;
import com.me.GCDP.search.util_V2.Page;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.xform.FormPlugin;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;


@PluginClass(author = "jiangy", intro = "搜索插件2：提供创建索引，修改索引，删除索引数据，简单查询，复杂查询，当前索引是否存在等方法",tag="数据")
@PluginExample(intro = "var search=pluginFactory.getP(\"search\");<br />"+
"search.putData(14,130,1)把14节点130表单id为1的数据推送到搜索引擎;<br />"+
"var page = search.getData(14,130,\"值:字段名1,值:字段名2\",\"字段名1,字段名2\",\"id desc\",0,10) <br>" +
"查询节点14的表单号130的数据，返回条数为10条，id升序排序,(只能支持一种排序),支持简单的查询<br/>"+
"var page = search.getDataByQ(14,130,\"字段名1:值 AND 字段名2:值\",\"字段名1,字段名2\",\"id desc,time desc\",0,10) <br>" +
"查询节点14的数据，返回条数为10条，id升序排序(支持多种排序),支持多种查询，详情请看方法说明<br/>"+
"var count = page.getTotalCount(); //数据总数<br/>"+
"var data = page.getResult(); 数据<br/>"+
"for(var i =0;i&ltdata.size();i++)<br/>"+
"{<br/>"+
"  var r = data.get(i);<br/>"+
"  var value = r.get('title');<br/> title为字段名，value为字段的值"+
"}"
)
public class SearchPlugin extends ScriptPlugin {
	
	private static Log log = LogFactory.getLog(SearchPlugin.class);
	
	private SearchService_V2 searchService=null;
	
	private String contain = null;
	
	private String configMD5 = null;
	
	private FormPlugin formSvr = null;
	
	public void setSearchService(SearchService_V2 searchService) {
		this.searchService = searchService;
	}
	
	@Override
	public void init() {
		
	}
	

	@PluginIsPublic
	@PluginMethod(intro = "将数据发送到搜索服务", 
			paramIntro = { "当前表单的nodeId","当前表单的formId","记录ID","当前表单的数据"},
			returnIntro = "无返回内容"
			)
	public void putData(int nodeId,int formId,int id,Map<String,Object> data) throws Exception
	{	if(data!=null){
				SearchHelper helper = new SearchHelper(nodeId, formId, searchService);
				if(data.containsKey("id")){
					data.remove("id");
				}
				helper.putData(id, data);	
		}else{
			throw new Exception("the Map object is null");
		}
		
	}
	//批量上传数据
	@SuppressWarnings("rawtypes")
	public void putData(int nodeId,int formId,List<Map> data) throws Exception{
		if(data!=null){
			SearchHelper helper = new SearchHelper(nodeId, formId, searchService);
			helper.putData(data);	
		}else{
			throw new Exception("the List object is null");
		}
		
	}
	@PluginIsPublic
	@PluginMethod(intro = "将数据从数据库发送到搜索服务", 
			paramIntro = { "节点ID","当前表单的formId","数据ID"},
			returnIntro = "无返回内容"
			)
	public void putData(int nodeId,int formId,int id)
	{
		try
		{	ScriptPluginFactory pf = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
			formSvr = (FormPlugin) pf.getP("form");
			formSvr.init(nodeId, formId, id);
			Map<String,Object> data = formSvr.getHelper().getData();
			putData(nodeId,formId,id,data);
		}
		catch (Exception e) {
			log.error("同步数据到搜索服务出错 id = :"+id+"||"+e.getMessage());
			e.printStackTrace();
		}
	}
	@PluginIsPublic
	@PluginMethod(intro = "删除相关数据", 
			paramIntro = { "当前表单的nodeId","当前表单的formId","要删除的数据id(格式为1,2,3)"},
			returnIntro = "无返回内容"
			)
	public void deleteData(int nodeid,int fromid,String stringIds) throws Exception{
		if(stringIds!=null&&!"".equals(stringIds)){
			String[] ids = stringIds.split(",");
			List<Object> list = new ArrayList<Object>();
			for(String cont:ids){
				list.add(cont);
			}
			String coreName = "nodeId_"+nodeid+"_from_"+fromid;
			Map<String,Object> map = searchService.deleteDate(list, coreName);
			if(map!=null&&map.get("status").equals("0"))
			{	log.info("success doing deleteData "+coreName+" ids = "+stringIds);
			}else{
				log.info("fail to deleteData"+coreName+" ids = "+stringIds);
			}
		}else{
			throw new Exception("the stringIds is null");
		}
		
	}
	//多条件查询

	@PluginIsPublic
	@PluginMethod(intro = "简易搜索", 
			paramIntro = { "nodeid","formid","条件(收索的条件格式为'值:字段名1,值:字段名2')<br>" +
					"1.中文分词的强制搜索：如姓名搜索--name:\"李四\"<br>"+
					"2.或查询  例子：name:name1#name2"+
					"3.and查询 例子:name:name1 name2"+
					"4.非查询 name不等于张山 例子:-name:张三",
			"返回字段(格式必须是name1,name2...返回所有字段格式为(*))","排序字段(格式：id desc或id asc)",
			"开始条数(0为第一条)","返回数据条数"},
			returnIntro = "返回的内容"
			)
	public Page getData(int nodeId,int formId,String wheres,String fields,String sorts,int start,int limit) throws Exception
	{
		SearchHelper helper = new SearchHelper(nodeId, formId, searchService);
		return helper.getData(wheres, sorts, fields, start, limit);
		
	}
	
	@SuppressWarnings("rawtypes")
	@PluginIsPublic
	@PluginMethod(intro = "批量导入数据", 
			paramIntro = { "索引库名","数据",
			"当前索引库配置信息" +
			"||索引库配置信息格式:" +
			"[{\"f_name\":\"name\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3}," +
			"{\"f_name\":\"name2\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3}"+"]"
			},
			returnIntro = "boolean"
			)
	public boolean putData(String coreName,List<Map> list,String jsonconfig) throws Exception{
		// 每次修改索引库时判断
		if(coreName!=null&&!"".equals(coreName)&&list!=null&&list.size()>0&&jsonconfig!=null && !"".equals(jsonconfig)){
			try {
				ScriptPluginFactory pf = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
				CoderPlugin coder = (CoderPlugin) pf.getP("coder");
				String configCo = coder.md5Encode(jsonconfig);
				if(configMD5==null||!configMD5.equals(configCo)){
					if(createCore(coreName,jsonconfig)){
						searchService.add(coreName, list, jsonconfig);
						
						configMD5 = configCo;
					}
				}else{
					searchService.add(coreName, list, jsonconfig);
				}
				
			} catch (Exception e) {
				log.error("false to putData to search || "+e.getMessage());
			}
		}else{
			throw new Exception("coreName || list || sonconfig is null");
		}
		return true;
		
		
	}
	/**
	 * 
	 * @Description:导入一条数据
	 * @param coreName
	 * @param map
	 * [{\"f_name\":\"name\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3},{\"f_name\":\"name2\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3}"+"]";
	 * @param jsonconfig
	 * @return boolean
	 * @throws Exception 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PluginIsPublic
	@PluginMethod(intro = "导入一条数据", 
			paramIntro = { "索引库名","数据",
			"当前索引库配置信息" +
			"||索引库配置信息格式:" +
			"[{\"f_name\":\"name\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3}," +
			"{\"f_name\":\"name2\",\"f_type\":\"VARCHAR\",\"f_allowNull\":true,\"l_allowSearch\":true,\"l_allowSort\":true,\"indexType\":3}"+"]"
			},
			returnIntro = "boolean")
	public boolean putData(String coreName,Map<String,Object> map,String jsonconfig) throws Exception{
		List list = new ArrayList();
		list.add(map);
		return putData(coreName,list,jsonconfig);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "搜索", 
			paramIntro = { "索引库名","条件(收索的条件格式为json格式')",
			"返回字段(格式必须json格式)","排序字段",
			"开始条数","返回数据条数"},
			returnIntro = "返回的内容"
			)
	public Page getData(String coreName,String wheres,String fields,String sorts,int start,int limit,int sortType)throws Exception{
		if(coreName!=null && !"".equals(coreName)){
			Map<String,String> condition = null;
			List<String> lstFields = null;
			Page page = new Page();
			int pageSize = limit;
			page.setPageSize(pageSize);
			page.setStart(start);
			if(!"".equals(wheres)&&wheres!=null){
				try {
					condition=conditionMap(wheres);
				} catch (JSONException e) {
					log.error("the wheres can't match the JSONObject "+e.getMessage());
				}
			}
			if(!"".equals(fields)&&fields!=null){
				lstFields=new ArrayList<String>();
				String[] r=fields.split(",");
				for(String rs:r){
					lstFields.add(rs);
				}
				//返回所有的数据，自动分页
			}
			return searchService.searchByPage(page,0,condition,lstFields,sorts,coreName,sortType);
		}else{
			throw new Exception("coreName is null");
		}
		
	}
	/**
	 *
	 * @Description:and 和or 的查询配置
	 * @param condition
	 * @return Map<String,String>
	 * @throws JSONException 
	 */
	public Map<String,String> conditionMap(String condition) throws JSONException{
		JSONArray list = new JSONArray(condition);
		Map<String,String> m=new HashMap<String,String>();
		for(int i=0;i<list.length();i++){
			JSONObject item = list.getJSONObject(i);
			String key=item.getString("field");
			String value=item.getString("value").trim();
			if(value.contains(" ") || value.contains("#")){
				value = "(" + value + ")";
				value = value.replaceAll("\\s{1,}", " AND "+key+":").replaceAll("#", " OR "+key+":");
			}
			
			m.put(key, value);
		}

		return m;
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "判断这个索引库是否存在", 
			paramIntro = { "索引库名"},
			returnIntro = ""
			)
	public boolean contain(String coreName) throws Exception{
		if(coreName != null && !"".equals(coreName)){
			if(contain == null){
				
				int readipLength = searchService.getReadip().length;
				for(int i = 0;i < readipLength;i ++){
					if(searchService.containCoreName(coreName,i)){
						contain = "yes";
						return true;
					}
				}
				contain = "no";
				return false;
				
			}else{
				if(contain.equals("yes")){
					return true;
				}else{
					contain = "no";
					return false;
				}
			}
		
		}else{
			throw new Exception("the coreName is null");
		}
	}
	@PluginIsPublic
	@PluginMethod(intro = "判断这个索引库是否存在", 
			paramIntro = {"节点id","表单id"},
			returnIntro = ""
			)
	public boolean contain(int nodeId,int formId ) throws Exception{
		String coreName = "nodeId_"+nodeId+"_from_"+formId;
		return contain(coreName);
	}	
	
	@PluginIsPublic
	@PluginMethod(intro = "创建索引库", 
			paramIntro = { "索引库名","索引库配置信息"},
			returnIntro = ""
			)
	public boolean createCore(String coreName,String config) throws Exception{
		if(coreName != null && !"".equals(coreName) && config != null && !"".equals(config)){
			Map<String,Object> map = null;
			try {
				map = searchService.updateCore(coreName, config);
			} catch (Exception e) {
				log.error("false to create core "+coreName+"||"+e.getMessage());
			}if(map!=null&&map.get("status").equals("0")){
				return true;
			}
		}else{
			throw new Exception("coreName || config is null");
		}
	
		return false;
	}
	

	@PluginIsPublic
	@PluginMethod(intro = "复杂搜索", 
			paramIntro = { "nodeid","formid","条件(依照solr查询条件依据：key:value AND key:value AND time:[2011-11-04T18:36:15Z TO *]"+
			"1.日期格式的几种操作：timestamp:[* TO NOW]<br>"+
			" createdate:[1976-03-06T23:59:59Z TO *]<br>"+
			" createdate:[1995-12-31T23:59:59Z TO 2007-03-06T00:00:00Z]<br>"+
			" pubdate:[NOW-1YEAR/DAY TO NOW/DAY+1DAY]<br>"+
			" createdate:[1976-03-06T23:59:59Z TO 1976-03-06T23:59:59Z+1YEAR]<br>"+
			" createdate:[1976-03-06T23:59:59Z/YEAR TO 1976-03-06T23:59:59Z](返回所有字段：*:*))<br>"+
			" 2.'['表示包含，'{'表示不包含<br>"+
			"3.有和没有查询：source:凤凰 AND -source:凤凰卫视<br>"+
			"4.或查询用OR:(key:value OR key:value) AND key:value,用()来强调优先查询,若value里包含:，需要对:加转义，比如http\\://ent.ifeng.com/ddd.html<BR>"+
			"5.范围查询1：id:[1 TO 100] 从1 到100<br>"+
			"6.范围查找2，相当于sql里面的in --source:(凤凰 OR 卫视 OR 娱乐)<br>"+
			"7.source:((凤凰 OR 卫视 OR 娱乐)+媒体)<br>"+
			"8.+等同于 AND ;-等同于 NOT ;<br>"+
			"9.查询空值  NOT fieldName:['' TO *] 或则-fieldName:['' TO *]<br>"+
			"10.查询非空值fieldName:['' TO *]<br>"+
			"11.非查询 name不等于张山 例子:-name:张三"+
			"12.中文分词的强制搜索：如姓名搜索--name:\"李四\"",
			"返回字段(格式必须是name1,name2...返回所有字段格式为(*))","排序字段(格式：id desc,time asc)",
			"开始条数(0为第一条)","返回数据条数"},
			returnIntro = "返回的内容"
			)
	public Page getDataByQ(int nodeId,int formId,String condition,String fields,String sorts,int start,int limit) throws Exception
	{
		Page ret = null;
		try{
			SearchHelper helper = new SearchHelper(nodeId, formId, searchService);
			ret = helper.getDataByQ(condition, sorts, fields, start, limit);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
		
	}

	@PluginIsPublic
	@PluginMethod(intro = "复杂搜索",
			paramIntro = { "nodeid","formid","高级条件搜索",
					"返回字段(格式必须是name1,name2...返回所有字段格式为(*))","排序字段(格式：id desc,time asc)",
					"开始条数(0为第一条)","返回数据条数"},
			returnIntro = "返回的内容"
	)
	public Page getDataByParams(int nodeId,int formId,Map<String,String> params,String fields,String sorts,int start,int limit) throws Exception
	{
		Page ret = null;
		try{
			SearchHelper helper = new SearchHelper(nodeId, formId, searchService);
			ret = helper.getDataByParams(params, sorts, fields, start, limit);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;

	}

}


