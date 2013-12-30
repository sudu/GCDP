
package com.me.GCDP.action.search;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.me.GCDP.search.SearchHelper;
import com.me.GCDP.search.SearchPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.search.SearchService_V2;
import com.me.GCDP.search.util_V2.Page;
import com.opensymphony.xwork2.ActionSupport;

public class NewSearchAction extends ActionSupport
{
	
	/*****************************************************************************************
	 *									business methods
	 * **************************************************************************************/
	
	/**
	 * 说明： 用以创建、更新（特殊需求即TUIJIANWEI的）索引库 </br>
	 * 跳转：使用全局跳转，  msg 
	 */
	public String save()
	{
		try
		{
			/** create|update a new|old index lib **/
			
			Map resultMap = searchService.updateCore(coreName, schemaConfig);
			
			/** add data into index lib 'coreName' **/
			
			if (resultMap != null && resultMap.get("status").equals("0"))
			{
				log.info("docData: " + docData);
				List list = SearchHelper.processDocData(docData);
				searchService.add(coreName, list, schemaConfig);
				boolean addDocDataFlag = searchService.getAddStatus(0, list, null, coreName);
				if (addDocDataFlag)
				{
					hasError = false;
					msg = "数据保存成功！";
				}
				else
				{
					hasError = true;
					msg = "数据保存失败！";
				}
			}
			else
			{
				hasError = true;
				msg = "更新索引库失败！";
			}
		} 
		catch (Exception ex)
		{
			// log info
			
			log.error("save error: ");
			
			ex.printStackTrace();
			
			StringBuffer sb = new StringBuffer();
			sb.append("save error -- extra info：");
			sb.append("| coreName: ").append(coreName);
			sb.append("| schemaConfig: ").append(schemaConfig);
			sb.append("| data: ").append(docData);
			log.error(sb.toString());
			
			// display error info
			msg = "保存数据失败， 请检查数据格式！";
			hasError = true;
			return "msg";
		}
		
		return "msg";
	}
	
	public String search()
	{
		try
		{
			String sortStr = SearchHelper.processSort(sort); 
			
			/** assemble search order **/
			
			int order = 0;
			sortStr = sortStr.trim().toLowerCase();
			
			if (sortStr.endsWith(" asc"))
			{
				order = 1;
				sortStr =  sortStr.replace(" asc", ""); 
			}
				
			sortStr =  sortStr.replace(" desc", ""); 
			
			/** execute search action **/
			SearchPlugin searchPlugin = (SearchPlugin) pluginFactory.getP("search");
			Page page = searchPlugin.getData(coreName, condition, returnFiled, sortStr, start, limit, order); 
			List<Map<String,String>> searchResult = page.getResult();
			
			/** assemble search result **/
			
			num = searchResult.size();
			data = SearchHelper.processSearchResult(searchResult);
			
		}
		catch (Exception ex)
		{
			/** BACK END LOG **/
			log.error("search error：");
			
			ex.printStackTrace();
			
			String sortStr = SearchHelper.processSort(sort); 
			String searchInfo = SearchHelper.assembleSearchInfo(coreName, condition, returnFiled, sortStr, start, limit);
			log.error("search error --- extra info: " + searchInfo);
			
			/** FRONT END ERROR INFO **/
			/*msg = "搜索出错：" + ex.getMessage() + "， 请检查搜索条件！";*/
			msg = "搜索出错， 请检查搜索条件！";
			hasError = true;
			return "msg";
		}
		
		return "data";
	}
	
	/************************** getters, setters ****************************/

	public String getSchemaConfig()
	{
		return schemaConfig;
	}

	public void setSchemaConfig(String schemaConfig)
	{
		this.schemaConfig = schemaConfig;
	}

	public String getDocData()
	{
		return docData;
	}

	public void setDocData(String docData)
	{
		this.docData = docData;
	}
	
	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	public int getStart()
	{
		return start;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public String getReturnFiled()
	{
		return returnFiled;
	}

	public void setReturnFiled(String returnFiled)
	{
		this.returnFiled = returnFiled;
	}

	public String getSort()
	{
		return sort;
	}

	public void setSort(String sort)
	{
		this.sort = sort;
	}

	public String getCoreName()
	{
		return coreName;
	}

	public void setCoreName(String coreName)
	{
		this.coreName = coreName;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public boolean isHasError()
	{
		return hasError;
	}

	public void setHasError(boolean hasError)
	{
		this.hasError = hasError;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	public void setSearchService(SearchService_V2 searchService)
	{
		this.searchService = searchService;
	}
	
	public void setPluginFactory(ScriptPluginFactory pluginFactory)
	{
		this.pluginFactory = pluginFactory;
	}

	/****** search service ******/
	
	@Autowired
	@Qualifier("searchService") 
	private SearchService_V2 searchService;
	
	@Autowired
	@Qualifier("pluginFactory") 
	private ScriptPluginFactory pluginFactory;
	
	/****** save  : request parameters ******/ 
	
	private String schemaConfig; // schema.xml content
	private String docData;      // fieldName:fieldValue[,fieldName:fieldValue]
	
	/****** search: display result ******/
	
	private int    num;
	private String data;
	
	private boolean hasError = false;
	private String  msg;
	
	/****** search: request parameters ******/
	
	private String condition;
	private int    start = 0;    // --> start
	private int    limit = 10;  // --> limit
	private String returnFiled;
	private String sort; 
	private String coreName;
	
	private static Log log = LogFactory.getLog(NewSearchAction.class);
	
	private static final long	serialVersionUID	= 1L;
	
}
