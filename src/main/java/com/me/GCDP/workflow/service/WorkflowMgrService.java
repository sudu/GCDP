package com.me.GCDP.workflow.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.hibernate.SessionFactory;

import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.workflow.dm.PersistManagerFactoryHibernateForCmppImpl;
import com.me.GCDP.workflow.exception.InvalidConfigurationException;
import com.me.GCDP.workflow.model.ActivityDef;
import com.me.GCDP.workflow.model.AndSuitePluginDef;
import com.me.GCDP.workflow.model.AsyncSuitePluginDef;
import com.me.GCDP.workflow.model.CmppActivityDef;
import com.me.GCDP.workflow.model.CmppPluginDef;
import com.me.GCDP.workflow.model.CmppProcessDef;
import com.me.GCDP.workflow.model.ConditionSuitePluginDef;
import com.me.GCDP.workflow.model.ForEachSuitePluginDef;
import com.me.GCDP.workflow.model.OrSuitePluginDef;
import com.me.GCDP.workflow.model.PluginDef;
import com.me.GCDP.workflow.model.ProcessDef;
import com.me.GCDP.workflow.model.ScriptPluginDef;
import com.me.GCDP.workflow.model.SwitchSuitePluginDef;
import com.ifeng.common.dm.DataManagerException;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.dm.QueryResult;
import com.ifeng.common.dm.persist.HQuery;

@SuppressWarnings({"rawtypes","unchecked","unused"})
/**
 * @author xiongfeng
 *
 */
public class WorkflowMgrService {
	public WorkflowMgrService() {
		
	}
	public WorkflowMgrService(SessionFactory sessionFactory) {
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory);
		processDm = new PersistDataManager(CmppProcessDef.class, dmFactory);
		activityDm = new PersistDataManager(CmppActivityDef.class, dmFactory);
		pluginDm = new PersistDataManager(CmppPluginDef.class, dmFactory);
	}
	
	private static Log log = LogFactory.getLog(WorkflowMgrService.class);
	private static PersistDataManager processDm;
	private static PersistDataManager activityDm;
	private static PersistDataManager pluginDm;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
	
	/**
	 * 获取数据库中存储的所有流程的id
	 * @return
	 */
	private int[] getProcessIds() {
		HQuery query = new HQuery();
		int[] ids = null;
		query.setQueryString("select p.processId from CmppProcessDef p");
		try {
			QueryResult processResult = processDm.query(query);
			ids = new int[processResult.getRowCount()];
			List processList = processResult.getData(0, processResult.getRowCount());
			int i = 0;
			for (Object object : processList) {
				ids[i++] = (Integer)object;
			}
		} catch (DataManagerException e) {
			log.error("Exception occured while get process ids", e);
		}
		return ids;
	}
	
	/**
	 * 将数据库中存储的流程发布为XML文件
	 * @param filePath 文件的路径，例如：E:\\a.xml
	 * @throws Exception 
	 */
	public void releaseWorkflowToXML(String filePath) 
			throws Exception {
		log.info("Release processes to:" + filePath);
		try {
			ObjectToXMLService instance = new ObjectToXMLService();
			
			Document doc = null;
			try {
				doc = instance.buildDocumentFromObject(buildProcessDefsFromDB());
			} catch (DataManagerException e) {
				log.error(e);
				throw new Exception("拼装ProcessDef对象失败", e);
			}
			instance.writeXMLToFile(doc, filePath);
		} catch (InvalidConfigurationException e) {
			log.error("Exception occured while release workflow to xml file", e);
		}
	}
	
	/**
	 * 将数据库张存储的流程发布到配置文件中配置的默认文件中
	 * @throws Exception 
	 */
	public void releaseWorkflowToDefaultXML() throws Exception {
		String filePath = CmppConfig.getKey("workflow.workflowfile.dir") 
				+ CmppConfig.getKey("workflow.workflowfile.filename");
		releaseWorkflowToXML(filePath);
	}
	
	private void buildPluginForActivity(CmppPluginDef plugin, 
			ScriptPluginDef scriptPluginDef) {
		scriptPluginDef.setExpids(plugin.getExceptionIds());
		scriptPluginDef.setIds(plugin.getIds());
		if (plugin.getMailToEnable() == 'Y') {
			scriptPluginDef.setMailTo(plugin.getMailTo());
		}
		scriptPluginDef.setnActivity(plugin.getnActivity());
		scriptPluginDef.setNodeId(plugin.getNodeId() + "");
		scriptPluginDef.setRtxTo(plugin.getRtxTo());
		scriptPluginDef.setScriptType(plugin.getScriptType());
		if (plugin.getSmsToEnable() == 'Y') {
			scriptPluginDef.setSmsTo(plugin.getSmsTo());
		}
		scriptPluginDef.setSuspendsOnException(plugin.getStopOnException() 
				== 'Y' ? "true" : "false");
		scriptPluginDef.setDefId(plugin.getPluginId());
		scriptPluginDef.setExeCase(plugin.getExecuteCase());
		if (plugin.getPluginType() == 1) {
			scriptPluginDef.setType(PluginEnum.CONDSCRIPT.toString());
		}else if (plugin.getPluginType() == 2) {
			scriptPluginDef.setType(PluginEnum.SWITCHCONDSCRIPT.toString());
		}else {
			scriptPluginDef.setType(PluginEnum.SCRIPT.toString());
		}
		if (plugin.getSubRoutine() != null && !"".equals(plugin.getSubRoutine())) {
			scriptPluginDef.setSubRoutine(plugin.getSubRoutine());
		}
	}
	
	/**
	 * 根据指定Id将流程状态改为上线，并且发布流程
	 * @param processId
	 * @throws Exception 
	 */
	public void enableAndReleaseflow(int processId) throws Exception {
		log.info("Enable process:" + processId);
		if (isProcessExist(processId)) {
			CmppProcessDef processDef = queryProcessDefinition(processId);
			processDef.setStatus((byte)1);
			try {
				processDm.modify(processDef, new String[]{"status"}, null);
			} catch (DataManagerException e) {
				log.error("Exception occured while enable status of " + processId, e);
				throw new Exception("上线失败");
			}
			releaseWorkflowToDefaultXML();
		}else {
			log.info("Process:" + processId + " does not exist");
		}
	}
	
	/**
	 * 将指定Id的流程状态改为下线，并且发布流程
	 * @param processId
	 * @throws Exception 
	 */
	public void disableAndReleaseflow(int processId) throws Exception {
		log.info("Disable process:" + processId);
		if (isProcessExist(processId)) {
			CmppProcessDef processDef = queryProcessDefinition(processId);
			processDef.setStatus((byte)-1);
			try {
				processDm.modify(processDef, new String[]{"status"}, null);
			} catch (DataManagerException e) {
				log.error("Exception occured while enable status of " + processId, e);
				throw new Exception("上线失败");
			}
			releaseWorkflowToDefaultXML();
		}else {
			log.info("Process:" + processId + " does not exist");
		}
	}
	
	/**
	 * 从数据库中将流程定义信息构造成List<ProcessDef>对象
	 * @throws DataManagerException 
	 */
	public List<ProcessDef> buildProcessDefsFromDB() 
			throws DataManagerException {
		log.info("Build processes from database");
		int[] ids = getProcessIds();
		List<ProcessDef> processList = new ArrayList<ProcessDef>();
		try {
			for (int i = 0; i < ids.length; i++) {
				CmppProcessDef process = (CmppProcessDef) processDm.queryById(ids[i]);
				if (process.getStatus() != 1) {
					continue;
				}
				ProcessDef processDef = new ProcessDef();
				processDef.setName(ids[i] + "");//用Id替代name
				processDef.setTitle(process.getProcessTitle());
				processDef.setDescription(process.getProcessDesc());
				List<ActivityDef> activitieDefs = new ArrayList<ActivityDef>();
				//查询该流程下属的活动
				HQuery activityQuery = new HQuery();
				activityQuery.setQueryString("from CmppActivityDef p where p.process.processId = ? order by activityId asc");
				List paramList = new ArrayList();
				paramList.add(new Integer(ids[i]));
				activityQuery.setParalist(paramList);
				QueryResult activities = processDm.query(activityQuery);
				List activitiesList = activities.getData(0, activities.getRowCount());
				
				for (Object activityObj : activitiesList) {
					CmppActivityDef activity = (CmppActivityDef)activityObj;
					ActivityDef activityDef = new ActivityDef();
					activityDef.setName(activity.getActivityId() + "");//用Id替代name
					activityDef.setTitle(activity.getActivityTitle());
					activityDef.setDescription(activity.getActivityDesc());
					
					//查询活动下属的插件的信息
					HQuery pluginQuery = new HQuery();
					pluginQuery.setQueryString("from CmppPluginDef p where p.activity.activityId = ? order by pluginId asc");
					List pluginParamList = new ArrayList();
					pluginParamList.add(activity.getActivityId());
					pluginQuery.setParalist(pluginParamList);
					QueryResult plugins = processDm.query(pluginQuery);
					List pluginList = plugins.getData(0, plugins.getRowCount());		
					
					switch (activity.getActivityType()) {
					case 0:		//AndSuite
						AndSuitePluginDef pluginDef = new AndSuitePluginDef();
						//设置type属性即可，name和nextActivity属性可以不设置
						pluginDef.setType(PluginEnum.AND.toString());
						List<PluginDef> andPlugins = new ArrayList<PluginDef>();
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							buildPluginForActivity(plugin, scriptPluginDef);
							andPlugins.add(scriptPluginDef);
						}
						pluginDef.setAndPlugins(andPlugins);
						activityDef.setPlugin(pluginDef);
						break;
					case 1:		//OrSuite
						OrSuitePluginDef pluginDef2 = new OrSuitePluginDef();
						pluginDef2.setType(PluginEnum.OR.toString());
						List<PluginDef> orPlugins = new ArrayList<PluginDef>();
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							buildPluginForActivity(plugin, scriptPluginDef);
							orPlugins.add(scriptPluginDef);
						}
						pluginDef2.setOrPlugins(orPlugins);
						activityDef.setPlugin(pluginDef2);
						break;
					case 2:		//ForeachSuite
						ForEachSuitePluginDef pluginDef3 = new ForEachSuitePluginDef();
						pluginDef3.setType(PluginEnum.FOREACH.toString());
						List<PluginDef> foreachPlugins = new ArrayList<PluginDef>();
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							buildPluginForActivity(plugin, scriptPluginDef);
							foreachPlugins.add(scriptPluginDef);
						}
						pluginDef3.setForEachPlugins(foreachPlugins);
						activityDef.setPlugin(pluginDef3);
						break;
					case 3:		//ConditionSuite
						ConditionSuitePluginDef pluginDef4 = new ConditionSuitePluginDef();
						pluginDef4.setType(PluginEnum.COND.toString());
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							if(plugin.getPluginType() == 3) {
								buildPluginForActivity(plugin, scriptPluginDef);
								pluginDef4.setTrueCondition(scriptPluginDef);
							}else if(plugin.getPluginType() == 4) {
								buildPluginForActivity(plugin, scriptPluginDef);
								pluginDef4.setFalseCondition(scriptPluginDef);
							}else if(plugin.getPluginType() == 1) {
								buildPluginForActivity(plugin, scriptPluginDef);
								pluginDef4.setCondition(scriptPluginDef);
							}
						}
						activityDef.setPlugin(pluginDef4);
						break;
					case 4:		//SwitchSuite
						SwitchSuitePluginDef pluginDef5 = new SwitchSuitePluginDef();
						pluginDef5.setType(PluginEnum.SWITCH.toString());
						Map<String, PluginDef> stepModulesMap = new HashMap<String, PluginDef>();
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							if (plugin.getPluginType() == 2) {
								buildPluginForActivity(plugin, scriptPluginDef);
								pluginDef5.setSwitchCondition(scriptPluginDef);
							}else if(plugin.getPluginType() == 0) {
								buildPluginForActivity(plugin, scriptPluginDef);
								stepModulesMap.put(plugin.getPluginId() + "", scriptPluginDef);
								
							}
						}
						pluginDef5.setStepModulesMap(stepModulesMap);
						activityDef.setPlugin(pluginDef5);
						break;
					case 5:		//AsyncSuite
						AsyncSuitePluginDef pluginDef6 = new AsyncSuitePluginDef();
						pluginDef6.setType(PluginEnum.ASYNC.toString());
						List<PluginDef> asyncPlugins = new ArrayList<PluginDef>();
						for (Object pluginObj : pluginList) {
							CmppPluginDef plugin = (CmppPluginDef)pluginObj;
							ScriptPluginDef scriptPluginDef = new ScriptPluginDef();
							buildPluginForActivity(plugin, scriptPluginDef);
							asyncPlugins.add(scriptPluginDef);
						}
						pluginDef6.setAsyncPlugins(asyncPlugins);
						activityDef.setPlugin(pluginDef6);
						break;
					default:
						break;
					}
					activitieDefs.add(activityDef);
				}
				processDef.setActivitiesList(activitieDefs);
				processList.add(processDef);
			}
			
		} catch (DataManagerException e) {
			log.error("Exception occured while release workflow", e);
			throw e;
			
		}
		return processList;
	}
	
	
	/**
	 * 根据Id查询出
	 * @param processId
	 * @return
	 */
	public String queryCompleteProcessInfo(int processId) {
		CmppProcessDef processDef = queryProcessDefinition(processId);
		if (processDef != null) {
			return processDef.getProcessDefInfo();
		}else {
			return "";
		}
	}
	

	/**
	 * 添加一个流程定义
	 * 
	 * @param process
	 */
	public int addProcessDefinition(CmppProcessDef process) {
		int processId = 0;
		try {
			//1.先添加流程信息
			processId = (Integer) processDm.add(process, null);
		} catch (DataManagerException e) {
			log.error("Exception occured while persist a new process", e);
		}
		return processId;
	}
	
	/**
	 * 根据流程Id查询一个流程本身的信息
	 * @param processId
	 * @return
	 */
	public CmppProcessDef queryProcessDefinition(int processId) {
		try {
			return (CmppProcessDef) processDm.queryById(processId);
		} catch (DataManagerException e) {
			log.error("Exception occured while query the process " + processId, e);
			return null;
		}
	}
	
	/**
	 * 根据流程Id查询一个流程的定义字符串
	 * @param processId
	 * @return
	 * @throws Exception 
	 */
	public String queryProcessDefinitionStr(int processId) throws Exception {
		log.debug("Starts to query process definition:" + processId);
		CmppProcessDef processDef = queryProcessDefinition(processId);
		if(processDef != null) {
			log.info("Query process definition.processName:" + processId);
			return processDef.getProcessDefInfo();
		}else {
			log.info("The process " + processId + " does not exist");
			throw new Exception("The process of " + processId + " does not exist");
		}
	}


	
	/**
	 * 给指定流程添加一个活动
	 * @param processId
	 * @param activity
	 * @return
	 * @throws Exception 
	 */
	private int addActivityDefinition(int processId, CmppActivityDef activity) throws Exception {
		log.debug("Starts to add activity to process:" + processId);
		int activityId = 0;
		try {
			if (isProcessExist(processId)) {
				CmppProcessDef process = (CmppProcessDef) processDm.queryById(processId);
				activity.setProcess(process);
				activityId = (Integer)activityDm.add(activity, null);
			}else {
				log.info("process:" + processId + " does not exist");
				throw new Exception("Process:" + + processId + " does not exist");
			}
			
		} catch (DataManagerException e) {
			log.error("Exception occured while persist a new acticity to process " 
																	+ processId, e);
		}
		log.debug("Starts to add activity:" + activityId + " to process:" 
				+ processId + " successfully");
		return activityId;
	}
	
	
	
	
	/**
	 * 给指定的活动添加一个插件
	 * @param activityId
	 * @param plugin
	 * @return
	 */
	private int addPluginDefinition(int activityId, CmppPluginDef plugin) {
		log.debug("Starts to add plugin to activity:" + activityId);
		int pluginId = 0;
		try {
			CmppActivityDef activity = (CmppActivityDef)activityDm.queryById(activityId);
			plugin.setActivity(activity);
			pluginId = (Integer)pluginDm.add(plugin, null);
		} catch (DataManagerException e) {
			log.error("Exception occured while persist a new plugin to activity " + activityId, e);
		}
		log.debug("Ends to add plugin:" + pluginId + " to activity:" + activityId);
		return pluginId;
	}
	
	
	
	/**
	 * <p>根据前端传递过来的流程定义信息的字符串(JSON)，解析成Java端的CmppProcessDef对象。</p>
	 * <p>其中，CmppProcessDef对象的activities字段存储该流程下属的所有活动(CmppActivityDef);</p>
	 * <p>同理，每个CmppActivityDef对象的plugins字段存储该活动下属的所有插件(CmppPluginDef)。</p>
	 * <p>反过来，对于每一个活动(CmppActivityDef)，都有一个字段process指向其所属的流程(CmppProcessDef),</p>
	 * <p>同理，每一个插件(CmppPluginDef)都有一个字段activity指向其所属的活动(CmppActivityDef)</p>
	 * @param cfgJsonStr
	 * @return
	 * @throws Exception 
	 */
	public CmppProcessDef constructProcessFromConfig(int nodeId, String cfgJsonStr) throws Exception {
		log.info("Construct process from json");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject object = (JSONObject) JSONSerializer.toJSON(cfgJsonStr);
		/**
		 * 从流程定义信息中，读取流程本身的一些信息，例如流程名称、title等
		 */
		log.debug("Construct process info");
		String headNodeKey = "";//记录流程真正第一个节点的key
		CmppProcessDef process = new CmppProcessDef();
		JSONObject processObj = object.getJSONObject("graph");
		process.setProcessName(processObj.getString("title"));//name用title替代
		process.setProcessTitle(processObj.getString("title"));
		process.setProcessDesc(processObj.getString("description"));
		process.setCreator(processObj.getString("author"));
		process.setNodeId(nodeId);
		Date now = new Date();
		try {
			process.setCreateTime(sdf.parse(processObj.getString("createTime")));
			process.setRecentModifyTime(now);
		} catch (ParseException e) {
			log.error("Exception occured while parse date for process create time", e);
		}
		process.setActivities(new ArrayList<CmppActivityDef>());
		
		JSONObject cellsObj = object.getJSONObject("cells");
		Map<String, JSONObject> cellMap = loadAllCellsIntoMap(cellsObj);
		Set<String> keys = cellsObj.keySet();
		JSONObject nodeObj = null;
		for(String key : keys) {
			nodeObj = cellMap.get(key);
			if ("begin".equals(nodeObj.get("type"))) {
				//begin类型，记录下其对应child的key的值
				headNodeKey = nodeObj.getJSONArray("children").getString(0);
			}else if("switch".equals(nodeObj.get("type"))) {
				//switch类型的活动
				//1.创建活动，并从相应的流程定义信息中读取一些属性
				log.debug("Construct switch activity info");
				CmppActivityDef activityDef = new CmppActivityDef();
				activityDef.setActivityType((byte)4);
				/*
				 * 前端没有实际的Title、Description传递过来，但是为了遵循icommon组建的要求，
				 * 将这两个属性设置为空字符串
				 */
				activityDef.setActivityTitle("title");
				activityDef.setActivityDesc("description");
				activityDef.setCreateTime(new Date());
				activityDef.setPlugins(new ArrayList<CmppPluginDef>());
				activityDef.setProcess(process);
				process.getActivities().add(activityDef);
				
				//2.switch类型活动的SwithCondition插件
				log.debug("Construct switch condition plugin info");
				CmppPluginDef pluginDef = new CmppPluginDef();
				pluginDef.setPluginType((byte)2);
				pluginDef.setActivity(activityDef);
				pluginDef.setCfgCode(key);
				pluginDef.setPluginName(nodeObj.getJSONObject("property").getString("label"));
				pluginDef.setNodeId(nodeId);
				pluginDef.setMailTo(nodeObj.getJSONObject("property").getString("mailTo"));
				pluginDef.setSmsTo(nodeObj.getJSONObject("property").getString("smsTo"));
				pluginDef.setMailToEnable(nodeObj.getJSONObject("property").getInt("mailToEnable")==0?'N':'Y');
				pluginDef.setSmsToEnable(nodeObj.getJSONObject("property").getInt("smsToEnable")==0?'N':'Y');
				pluginDef.setStopOnException('Y');
				activityDef.getPlugins().add(pluginDef);
				
				//3.switch类型活动各个分支的插件
				Iterator it = nodeObj.getJSONArray("children").iterator();
				while(it.hasNext()) {
					String childKey = (String) it.next();
					JSONObject nodeObj_ = cellMap.get(childKey);
					if ("end".equals(nodeObj_.getString("type"))) {
						throw new Exception("分支不允许直接连接结束:" + key);
					}else if("switch".equals(nodeObj_.getString("type"))) {
						throw new Exception("分支不允许直接连接分支:" + key);
					}
					for(Object o : nodeObj_.getJSONArray("children")) {
						if (nodeObj.getJSONArray("children").contains(o)) {
							throw new Exception("分支活动中的分支之间不允许直接连接：" + childKey);
						}
					}
					log.debug("Construct switch branch plugin info");
					CmppPluginDef pluginDef_ = new CmppPluginDef();
					pluginDef_.setPluginType((byte)0);
					pluginDef_.setActivity(activityDef);
					pluginDef_.setCfgCode(childKey);
					pluginDef_.setExecuteCase(nodeObj_.getJSONObject("property").getString("case"));
					pluginDef_.setPluginName(nodeObj_.getJSONObject("property").getString("label"));
					pluginDef_.setNodeId(nodeId);
					pluginDef_.setMailTo(nodeObj_.getJSONObject("property").getString("mailTo"));
					pluginDef_.setSmsTo(nodeObj_.getJSONObject("property").getString("smsTo"));
					pluginDef_.setMailToEnable(nodeObj_.getJSONObject("property").getInt("mailToEnable")==0?'N':'Y');
					pluginDef_.setSmsToEnable(nodeObj_.getJSONObject("property").getInt("smsToEnable")==0?'N':'Y');
					pluginDef_.setStopOnException('Y');
					activityDef.getPlugins().add(pluginDef_);
					
				}
			}else if ("and".equals(nodeObj.get("type"))) {
				//And类型的活动
				JSONArray parentArray = nodeObj.getJSONArray("parent");
				if (parentArray.size() > 0) {
					if ("and".equals(cellMap.get(parentArray.get(0)).get("type")) || 
							"begin".equals(cellMap.get(parentArray.get(0)).get("type")) ||
							"end".equals(cellMap.get(parentArray.get(0)).get("type"))) {
						//1.创建And类型的活动
						log.debug("Construct and activity info");
						CmppActivityDef activityDef = new CmppActivityDef();
						activityDef.setActivityType((byte)0);
						activityDef.setActivityTitle("title");
						activityDef.setActivityDesc("description");
						activityDef.setPlugins(new ArrayList<CmppPluginDef>());
						activityDef.setProcess(process);
						process.getActivities().add(activityDef);
						//2.And类型的插件，只会有一个插件
						log.debug("Construct and plugin info");
						CmppPluginDef pluginDef_ = new CmppPluginDef();
						pluginDef_.setPluginType((byte)0);
						pluginDef_.setActivity(activityDef);
						pluginDef_.setCfgCode(key);
						pluginDef_.setPluginName(nodeObj.getJSONObject("property").getString("label"));
						pluginDef_.setNodeId(nodeId);
						pluginDef_.setMailTo(nodeObj.getJSONObject("property").getString("mailTo"));
						pluginDef_.setSmsTo(nodeObj.getJSONObject("property").getString("smsTo"));
						pluginDef_.setMailToEnable(nodeObj.getJSONObject("property").getInt("mailToEnable")==0?'N':'Y');
						pluginDef_.setSmsToEnable(nodeObj.getJSONObject("property").getInt("smsToEnable")==0?'N':'Y');
						pluginDef_.setStopOnException('Y');
						activityDef.getPlugins().add(pluginDef_);
					}
				}
				
			}else if ("async".equals(nodeObj.get("type"))) {
				//Async类型的活动
				
				//0.对于Async类型来说，先为这个Async创建一个And活动
				log.debug("Construct and activity before async activity");
				CmppActivityDef andActivityDef = new CmppActivityDef();
				andActivityDef.setActivityType((byte)0);
				andActivityDef.setActivityTitle("title");
				andActivityDef.setActivityDesc("description");
				andActivityDef.setPlugins(new ArrayList<CmppPluginDef>());
				andActivityDef.setProcess(process);
				process.getActivities().add(andActivityDef);
				
				log.debug("Construct and plugin before async activity");
				CmppPluginDef andChildPluginDef = new CmppPluginDef();
				andChildPluginDef.setPluginType((byte)0);
				andChildPluginDef.setActivity(andActivityDef);
				andChildPluginDef.setCfgCode(key);
				andChildPluginDef.setPluginName(nodeObj.getJSONObject("property").getString("label"));
				andChildPluginDef.setNodeId(nodeId);
				andChildPluginDef.setMailTo(nodeObj.getJSONObject("property").getString("mailTo"));
				andChildPluginDef.setSmsTo(nodeObj.getJSONObject("property").getString("smsTo"));
				andChildPluginDef.setMailToEnable(nodeObj.getJSONObject("property").getInt("mailToEnable")==0?'N':'Y');
				andChildPluginDef.setSmsToEnable(nodeObj.getJSONObject("property").getInt("smsToEnable")==0?'N':'Y');
				andChildPluginDef.setStopOnException('Y');
				andActivityDef.getPlugins().add(andChildPluginDef);
				
				//1.创建活动
				log.debug("Construct async activity info");
				CmppActivityDef activityDef = new CmppActivityDef();
				activityDef.setActivityType((byte)5);
				activityDef.setActivityTitle("title");
				activityDef.setActivityDesc("description");
				activityDef.setPlugins(new ArrayList<CmppPluginDef>());
				activityDef.setProcess(process);
				process.getActivities().add(activityDef);
				
				//2.创建Asynch活动下面的插件(并行的多个分支)
				log.debug("Construct async branch plugin");
				Iterator it = nodeObj.getJSONArray("children").iterator();
				while (it.hasNext()) {
					String childKey = (String) it.next();
					JSONObject nodeObj_ = cellMap.get(childKey);
					if ("switch".equals(nodeObj_.getString("type")) ||
							"async".equals(nodeObj_.getString("type"))) {
						throw new Exception("异步活动不支持分支或者异步支路：" + childKey);
					}
					for(Object o : nodeObj_.getJSONArray("children")) {
						if (nodeObj.getJSONArray("children").contains(o)) {
							throw new Exception("异步活动的各条支路之间不允许直接连接：" + childKey);
						}
					}
					CmppPluginDef pluginDef_ = new CmppPluginDef();
					pluginDef_.setPluginType((byte)0);
					pluginDef_.setActivity(activityDef);
					pluginDef_.setCfgCode(childKey);
					pluginDef_.setPluginName(nodeObj_.getJSONObject("property").getString("label"));
					pluginDef_.setNodeId(nodeId);
					pluginDef_.setMailTo(nodeObj_.getJSONObject("property").getString("mailTo"));
					pluginDef_.setSmsTo(nodeObj_.getJSONObject("property").getString("smsTo"));
					pluginDef_.setMailToEnable(nodeObj_.getJSONObject("property").getInt("mailToEnable")==0?'N':'Y');
					pluginDef_.setSmsToEnable(nodeObj_.getJSONObject("property").getInt("smsToEnable")==0?'N':'Y');
					pluginDef_.setStopOnException('Y');
					activityDef.getPlugins().add(pluginDef_);
				}
			}else {
				//end类型，什么都不做
			}
		}
		process.setProcessDefInfo(cfgJsonStr);
		
		//将实际上的第一个节点对应的活动移到第一位置
		CmppActivityDef headActivity = findActivityByCfgCode0(headNodeKey, process.getActivities());
		if (headActivity != null) {
			List<CmppActivityDef> activities0 = process.getActivities();
			int headActivityIndex = Integer.MAX_VALUE;
			for(int index = 0; index < activities0.size(); index++) {
				if (activities0.get(index) == headActivity) {
					headActivityIndex = index;
					break;
				}
			}
			for (int index0 = headActivityIndex - 1; index0 >= 0; index0--) {
				activities0.set(index0 + 1, activities0.get(index0));
			}
			activities0.set(0, headActivity);
		}
		
		
		return process;
	}
	
	/**
	 * 修改一个流程的定义
	 * @param process
	 * @throws Exception 
	 */
	private void updateCompleteProcess(CmppProcessDef process) throws Exception {
		log.info("Update complete process.processId:" + process.getProcessId());
		for(CmppActivityDef activity : process.getActivities()) {
			for (CmppPluginDef plugin : activity.getPlugins()) {
				try {
					pluginDm.modify(plugin, new String[] {
							"nActivity","subRoutine","ids","exceptionIds"}, null);
				} catch (DataManagerException e) {
					log.error("Exception occured while update nActivity,subRoutine,ids for process:"
							+ process.getProcessId() + " successfully");
					throw new Exception("更新流程定义出错", e);
				}
			}
		}
	}

	/**
	 * 持久化一个完整的流程定义
	 * @param flowObject
	 * @throws Exception 
	 */
	private int saveCompleteProcess(CmppProcessDef process) 
			throws Exception {
		int processId = addProcessDefinition(process);
		log.info("Add process-activity-plugin.processId:" + processId);
		for(CmppActivityDef activity : process.getActivities()) {
			try {
				activityDm.add(activity, null);
				for (CmppPluginDef plugin : activity.getPlugins()) {
					pluginDm.add(plugin, null);
				}
			} catch (DataManagerException e) {
				log.error("Exception occured while add process-activity-plugin", e);
				throw new Exception("添加流程-活动-插件信息出错", e);
			}
		}
		return processId;
	}
	
	/**
	 * 持久化一个流程定义中的所有活动和插件，并且修改流程信息(该方法主要给更新流程使用)
	 * @param process
	 * @throws Exception 
	 */
	private void saveCompleteProcess(int processId, CmppProcessDef process) 
			throws Exception {
		log.info("Update activity-plugin for process.processId:" + processId);
		try {
			CmppProcessDef oldProcess = (CmppProcessDef) processDm.queryById(processId);
			oldProcess.setProcessName(process.getProcessName());
			oldProcess.setProcessTitle(process.getProcessTitle());
			oldProcess.setProcessDesc(process.getProcessDesc());
			oldProcess.setCreator(process.getCreator());
			oldProcess.setCreateTime(process.getCreateTime());
			oldProcess.setAuthPath(process.getAuthPath());
			oldProcess.setProcessDefInfo(process.getProcessDefInfo());
			oldProcess.setRecentModifyTime(process.getRecentModifyTime());
			//只要进行了编辑操作，即使已经上线，那么也会改为编辑状态
			oldProcess.setStatus((byte)0);
			processDm.modify(oldProcess, new String[] {"processName","processTitle",
					"processDesc","creator","createTime","authPath","processDefInfo",
					"recentModifyTime","status"}, null);
			for(CmppActivityDef activity : process.getActivities()) {
				int activityId = addActivityDefinition(processId, activity);
				for (CmppPluginDef plugin : activity.getPlugins()) {
					addPluginDefinition(activityId, plugin);
				}
			}
		} catch (DataManagerException e) {
			log.error("Exception occured while Update activity-plugin " +
					"for process.processId:" + processId, e);
			throw new Exception("更新活动-插件信息出错", e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 根据流程id，删除流程下面的活动和插件<br/>
	 * @param processId 
	 * @throws Exception 
	 */
	public void deleteCompleteProcess(int processId) 
			throws Exception {
		log.info("Delete activity-plugin for process.processId:" + processId);
		CmppProcessDef process = null;
		 try {
			process = queryProcessDefinition(processId);
			
			HQuery query = new HQuery();
			query.setQueryString("from CmppActivityDef p where p.process.processId = ?");
			List paramList = new ArrayList();
			paramList.add(new Integer(processId));
			query.setParalist(paramList);
			QueryResult activities = processDm.query(query);
			List activitiesList = activities.getData(0, activities.getRowCount());
			
			for (Object object : activitiesList) {
				CmppActivityDef activity = (CmppActivityDef)object;
				
				HQuery pluginQuery = new HQuery();
				pluginQuery.setQueryString("from CmppPluginDef p where " +
						"p.activity.activityId = ?");
				List pluginParamList = new ArrayList();
				pluginParamList.add(activity.getActivityId());
				pluginQuery.setParalist(pluginParamList);
				QueryResult plugins = processDm.query(pluginQuery);
				List pluginList = plugins.getData(0, plugins.getRowCount());
				for (Object pluginObj : pluginList) {
					CmppPluginDef plugin = (CmppPluginDef)pluginObj;
					//删除插件
					pluginDm.delete(pluginObj, null);
				}
				//删除活动
				activityDm.delete(activity, null);
			}
		} catch (DataManagerException e) {
			log.error("Exception occured while Delete activity-plugin " +
					"for process.processId" + processId, e);
			throw new Exception("删除活动-插件信息出错", e);
			
		}
	}
	
	/**
	 * 根据流程id，删除整个流程:<br/>
	 * @param processId 
	 * @throws Exception 
	 */
	public void deleteCompleteProcess0(int processId) 
			throws Exception {
		log.info("Delete process-activity-plugin.processId:" + processId);
		CmppProcessDef process = null;
		 try {
			process = queryProcessDefinition(processId);
			
			HQuery query = new HQuery();
			query.setQueryString("from CmppActivityDef p where p.process.processId = ?");
			List paramList = new ArrayList();
			paramList.add(new Integer(processId));
			query.setParalist(paramList);
			QueryResult activities = processDm.query(query);
			List activitiesList = activities.getData(0, activities.getRowCount());
			
			for (Object object : activitiesList) {
				CmppActivityDef activity = (CmppActivityDef)object;
				
				HQuery pluginQuery = new HQuery();
				pluginQuery.setQueryString("from CmppPluginDef p where " +
						"p.activity.activityId = ?");
				List pluginParamList = new ArrayList();
				pluginParamList.add(activity.getActivityId());
				pluginQuery.setParalist(pluginParamList);
				QueryResult plugins = processDm.query(pluginQuery);
				List pluginList = plugins.getData(0, plugins.getRowCount());
				for (Object pluginObj : pluginList) {
					CmppPluginDef plugin = (CmppPluginDef)pluginObj;
					//删除插件
					pluginDm.delete(pluginObj, null);
				}
				//删除活动
				activityDm.delete(activity, null);
			}
			
			processDm.delete(process, null);
		} catch (DataManagerException e) {
			log.error("Exception occured while delete " +
					"process-activity-plugin.processId:" + processId, e);
			throw new Exception("删除流程-活动-插件信息出错", e);
		}
	}

	/**
	 * 获取到JSON字符串形式的流程定义信息后<br/>
	 * 1.对字符串进行解析，生成CmppProcessDef对象；<br/>
	 * 2.将完整的流程、活动、插件信息存储进数据库中；<br/>
	 * 3.设置流程中各个活动的后继关系；<br/>
	 * 4.将修改过后继关系的流程更新进数据库中
	 * @param flowDefInfo 前端提供的关于整个流程的描述(JSON字符串)
	 * @return 添加一个流程定义后该流程定义在数据库中的id
	 * @throws Exception 
	 */
	public int interpretAndSaveProcess(int nodeId, String flowDefInfo) throws Exception {
		log.info("Starts to interpret and save process");
		CmppProcessDef process = constructProcessFromConfig(nodeId, flowDefInfo);
		JSONObject object = (JSONObject) JSONSerializer.toJSON(flowDefInfo);
		JSONObject cellsObj = object.getJSONObject("cells");
		Map<String, JSONObject> cellMap = loadAllCellsIntoMap(cellsObj);
		
		//先保存
		int processId = saveCompleteProcess(process);
		//设置Activity之间的next关系和subRoutine关系
		
		setTransition0(process,cellMap);
		//再更新
		updateCompleteProcess(process);
		log.info("Ends to interpret and save process:" + processId + " successfully");
		return processId;
	}
	
	/**
	 * 更新流程Id为processId的流程：<br/>
	 * 1. 删除Id为processId的整个流程；<br/>
	 * 2. 重新添加
	 * @param processId
	 * @param flowDefInfo
	 * @throws Exception 
	 */
	public int interpretAndUpdateProcess(int processId, String flowDefInfo) 
			throws Exception {
		log.info("Starts to interpret and update process:" + processId);
		if (!isProcessExist(processId)) {
			throw new Exception("The process of " + processId + " does not exist");
		}else {
			int nodeId = getNodeIdOfExistProcess(processId);
			CmppProcessDef process = constructProcessFromConfig(nodeId, flowDefInfo);
			deleteCompleteProcess(processId);//删除活动-插件
			JSONObject object = (JSONObject) JSONSerializer.toJSON(flowDefInfo);
			JSONObject cellsObj = object.getJSONObject("cells");
			Map<String, JSONObject> cellMap = loadAllCellsIntoMap(cellsObj);
			process.setProcessId(processId);
			saveCompleteProcess(processId, process);
			setTransition0(process,cellMap);
			updateCompleteProcess(process);
			log.info("Ends to interpret and update process:" + processId + " successfully");
			return processId;
		}
	}
	
	
	private int getNodeIdOfExistProcess(int processId) {
		int nodeId = -1;
		try {
			CmppProcessDef oldProcess = (CmppProcessDef) processDm.queryById(processId);
			nodeId = oldProcess.getNodeId();
		} catch (DataManagerException e) {
			log.error("Exception occured while query nodeId of Exist Process");
		}
		return nodeId;
	}
	
	/**
	 * 根据id判断流程是否存在
	 * @param processId
	 * @return
	 */
	private boolean isProcessExist(int processId) {
		try {
			CmppProcessDef oldProcess = (CmppProcessDef) processDm.queryById(processId);
			if (oldProcess == null) {
				return false;
			}
		} catch (DataManagerException e) {
			log.error("Exception occured while verify if a process is exist", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 一次性从流程定义信息中将所有插件(cell)读取进一个Map并且返回
	 * @param object
	 * @return
	 */
	private Map<String, JSONObject> loadAllCellsIntoMap(JSONObject cellsObj) {
		Set<String> keys = cellsObj.keySet();
		Map<String, JSONObject> cellMap = new HashMap<String, JSONObject>();
		for(String key : keys) {
			cellMap.put(key, cellsObj.getJSONObject(key));
		}
		return cellMap;
	}
	
	/**
	 * 设置各个Activity之间的next关系,以及Async类型活动中每个分支的子流程关系
	 * @param process
	 * @param cellMap
	 * @throws Exception 
	 */
	private void setTransition0(CmppProcessDef process, Map<String, JSONObject> cellMap) 
			throws Exception {
		log.info("Set transition relation for process:" + process.getProcessId());
		for (int i = 0; i < process.getActivities().size(); i++) {
			CmppActivityDef activityDef = process.getActivities().get(i);
			if (activityDef.isBelongsToSubRoutine() == false) {
				if (activityDef.getActivityType() == 0) {
					log.debug("Set transition relation for and activity.activityId:" 
							+ activityDef.getActivityId());
					String childCfgCode = activityDef.getPlugins().get(0).getCfgCode();
					JSONObject thisObject = cellMap.get(childCfgCode);
					JSONArray thisChildren = thisObject.getJSONArray("children");
					activityDef.getPlugins().get(0).setnActivity(
							findActivityByCfgCode0((String)thisChildren.get(0), process.getActivities())
							== null ? null : findActivityByCfgCode0((String)thisChildren.get(0)
									, process.getActivities()).getActivityId() + "");
					
					/*
					 * 设置And活动中插件的ids和exceptionids属性
					 */
					log.debug("Set ids and exceptionids for and activity.activityId:" 
							+ activityDef.getActivityId());
					String ids = "" + process.getProcessId() + "," 
							+ retrieveNumFromCfgCode(childCfgCode); 
					activityDef.getPlugins().get(0).setIds(ids);
					activityDef.getPlugins().get(0).setExceptionIds(ids);
				}else if (activityDef.getActivityType() == 4) {
					//Switch类型
					List<CmppPluginDef> branches = getSwitchBranches(activityDef);
					for(CmppPluginDef plugin : branches) {
						log.debug("Set transition relation for switch branch plugin.pluginId:" 
								+ plugin.getPluginId());
						JSONObject branchObj = cellMap.get(plugin.getCfgCode());
						JSONArray childArray = branchObj.getJSONArray("children");
						if (childArray.size() > 0) {
							//当前switch分支所指向的下一个cell的key
							String childCfgCode = (String) childArray.get(0);
							int activityIdRet = findActivityByCfgCode(childCfgCode, process.getActivities());
							if (activityIdRet == -1) {
								plugin.setnActivity(null);
							}else {
								plugin.setnActivity("" + activityIdRet);
							}
						}
						/*
						 * 设置Switch类型的各个分支的ids和exceptionids属性
						 */
						log.debug("Set ids and exceptionids for switch branch plugin.pluginId:" 
								+ plugin.getPluginId());
						String cfgCode = plugin.getCfgCode();
						String ids = "" + process.getProcessId() 
								+ "," + retrieveNumFromCfgCode(cfgCode);
						plugin.setIds(ids);
						plugin.setExceptionIds(ids);
					}
					
					//设置Switch活动中，SwitchCondition中的ids和exceptionids属性
					CmppPluginDef switchCondition = getSwitchConfition(activityDef);
					log.debug("Set ids and exceptionids for switch condition plugin.pluginId:" 
							+ switchCondition.getPluginId());
					String cfgCode = switchCondition.getCfgCode();
					String ids = "" + process.getProcessId() 
							+ "," + retrieveNumFromCfgCode(cfgCode);
					switchCondition.setIds(ids);
					switchCondition.setExceptionIds(ids);
				}else if (activityDef.getActivityType() == 5) {
					//Async类型
					for(int k = 0; k < activityDef.getPlugins().size(); k++) {
						String asyncBranch = activityDef.getPlugins().get(k).getCfgCode();
						JSONObject asyncBranchObj = cellMap.get(asyncBranch);
						/*
						 * 为Async活动的分支插件设置ids和exceptionids属性
						 */
						log.debug("Set ids and exceptionids for async branch plugin.pluginId:" 
								+ activityDef.getPlugins().get(k).getPluginId());
						String ids = "" + process.getProcessId() 
								+ "," + retrieveNumFromCfgCode(asyncBranch);
						activityDef.getPlugins().get(k).setIds(ids);
						activityDef.getPlugins().get(k).setExceptionIds(ids);
						
						JSONArray asyncBranchChildren = asyncBranchObj.getJSONArray("children");
						String asyncBranchChild = (String)asyncBranchChildren.get(0);//只可能有一个child，所以get(0)
						JSONObject asyncBranchChildObj = cellMap.get(asyncBranchChild);
						
						CmppActivityDef current = activityDef;
						while(asyncBranchChildObj.getJSONArray("parent").size() == 1) {
							//存在子流程
							CmppActivityDef asyncBranchChildActivity = findActivityByCfgCode0
									(asyncBranchChild, process.getActivities());
//								if (asyncBranchChildActivity.getActivityType() != 0) {
//									throw new Exception("异步活动的子流程中只允许顺序活动：" + asyncBranchChild);
//								}
							/*
							 * 为子流程中的活动设置ids和exceptionids属性
							 */
							log.debug("Set ids and exceptionids for async subroutine plugin.pluginId:" 
									+ asyncBranchChildActivity.getPlugins().get(0).getPluginId());
							String subRoutineCfgCode = asyncBranchChildActivity.getPlugins().get(0).getCfgCode();
							String ids0 = "" + process.getProcessId() 
									+ "," + retrieveNumFromCfgCode(subRoutineCfgCode);
							asyncBranchChildActivity.getPlugins().get(0).setIds(ids0);
							asyncBranchChildActivity.getPlugins().get(0).setExceptionIds(ids0);
							
							current.getPlugins().get(k).setSubRoutine(asyncBranchChildActivity
									.getActivityId() + "");
							asyncBranchChildActivity.setBelongsToSubRoutine(true);
							
							current = findActivityByCfgCode0(asyncBranchChild, process.getActivities());
							asyncBranchChild = (String) asyncBranchChildObj.getJSONArray("children").get(0);
							asyncBranchChildObj = cellMap.get(asyncBranchChild);
							
						}
						//子流程已经结束了
						log.debug("Set transition relation for async branch plugin.pluginId:" 
								+ activityDef.getPlugins().get(k).getPluginId());
						activityDef.getPlugins().get(k).setnActivity(findActivityByCfgCode0
								(asyncBranchChild, process.getActivities()) == null ? null 
								: findActivityByCfgCode0(asyncBranchChild, process.getActivities()).getActivityId() + "");
					}
					
				}
			}
		}
	}
	
	/**
	 * 根据插件配置编码找出所属的活动
	 * @param cfgCode
	 * @param activities
	 * @return
	 */
	private CmppActivityDef findActivityByCfgCode0(String cfgCode, List<CmppActivityDef> activities) {
		CmppActivityDef activityDef = null;
		for(CmppActivityDef activity : activities) {
			for(CmppPluginDef plugin : activity.getPlugins()) {
				if (cfgCode.equals(plugin.getCfgCode())) {
					return activity;
				}
			}
		}
		return activityDef;
	}
	
	
	/**
	 * 从一组活动中查找出插件配置编码为指定值的那个活动的id
	 * @param cfgCode
	 * @param activities
	 * @return
	 */
	private  int findActivityByCfgCode(String cfgCode, List<CmppActivityDef> activities) {
		int nActivity = -1;
		for(CmppActivityDef activity : activities) {
			for(CmppPluginDef plugin : activity.getPlugins()) {
				if (cfgCode.equals(plugin.getCfgCode())) {
					nActivity = activity.getActivityId();
					return nActivity;
				}
			}
		}
		
		return nActivity;
	}
	
	/**
	 * 从一个Switch类型的活动中选出所有分支插件插件
	 * @param activityDef
	 * @return
	 */
	private  List<CmppPluginDef> getSwitchBranches(CmppActivityDef activityDef) {
		List<CmppPluginDef> branches = new ArrayList<CmppPluginDef>();
		for(CmppPluginDef def : activityDef.getPlugins()) {
			if (def.getPluginType() == 0) {
				branches.add(def);
			}
		}
		return branches;
	}
	
	/**
	 * 从一个Switch类型的活动中选出SwitchCondition插件
	 * @param activityDef
	 * @return
	 */
	private  CmppPluginDef getSwitchConfition(CmppActivityDef activityDef) {
		for(CmppPluginDef def : activityDef.getPlugins()) {
			if (def.getPluginType() == 2) {
				return def;
			}
		}
		return null;
	}
	
	/**
	 * 根据cfgCode提取出后面的数字，用来提供ids和exceptionids
	 * @param cfgCode
	 * @return
	 */
	private String retrieveNumFromCfgCode(String cfgCode) {
		Pattern p = Pattern.compile("[1-9]\\d*");
		Matcher m = p.matcher(cfgCode);
		String num = "888";
		if (m.find()) {
			num = m.group();
		}
		return num;
	}
}