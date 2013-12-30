package com.me.GCDP.workflow.service;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultText;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

import com.me.GCDP.workflow.exception.InvalidConfigurationException;
import com.me.GCDP.workflow.exception.InvalidParamException;
import com.me.GCDP.workflow.model.ActivityDef;
import com.me.GCDP.workflow.model.AndSuitePluginDef;
import com.me.GCDP.workflow.model.AsyncSuitePluginDef;
import com.me.GCDP.workflow.model.ConditionSuitePluginDef;
import com.me.GCDP.workflow.model.ForEachSuitePluginDef;
import com.me.GCDP.workflow.model.OrSuitePluginDef;
import com.me.GCDP.workflow.model.PluginDef;
import com.me.GCDP.workflow.model.ProcessDef;
import com.me.GCDP.workflow.model.ScriptPluginDef;
import com.me.GCDP.workflow.model.SwitchSuitePluginDef;
import com.me.GCDP.workflow.model.WhileSuitePluginDef;


/**
 * ObjectsToXMLUtil类是一个工具类，能够完成从包含流程定义信息的对象到流程定义XML的转换以及
 * 流程定义文件的维护工作。该类的具体功能如下：
 * 
 * <p>1.根据一个List<ProcessDef>，生成一棵包含流程定义信息的DOM树，并返回该DOM树的文档节点(Document)
 * {@link ObjectToXMLService#buildDocumentFromObject(List)}</p>
 * <p>2.创建新的流程,参见{@link ObjectToXMLService#addNewProcess(ProcessDef, Document)} </p>
 * <p>3.修改现有流程,参见{@link ObjectToXMLService#modifyDocument(ProcessDef, Document)}</p>
 * <p>4.删除某个流程,参见{@link ObjectToXMLService#deleteProcess(String, Document)}</p>
 * <p>5.对流程定义文件进行备份,参见{@link ObjectToXMLService#XMLBackup(String)}</p>
 * @author xiongfeng
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public final class ObjectToXMLService {
	
	private static Log log = LogFactory.getLog(ObjectToXMLService.class);
	
//	private static DBToXMLService instance;
	
	private static final String PROCESS_DEFINITION = 
			"com.ifeng.common.workflow.ProcessDefinition";
	private static final String ACTIVITY_DEFINITION = 
			"com.ifeng.common.workflow.ActivityDefinition";
//	private DBToXMLService() {}
	
	private static final int DEF_BAK_NUM = 50;
	
	private int bakNum = 0;
	
//	public static synchronized DBToXMLService getInstance() {
//		if(instance == null) {
//			instance = new DBToXMLService();
//		}
//		return instance;
//	}
	
	public ObjectToXMLService() {}
	
	/**
	 * 为AndSuite对应的Element节点添加子节点
	 * @param pluginElem <plugin type="com.ifeng.common.plugin.process.AndSuite">
	 *         对应的Element对象
	 * @param pluginDef 
	 */
	private void buildAndChildTags(Element pluginElem, PluginDef pluginDef) {
		AndSuitePluginDef andSuiteDef = (AndSuitePluginDef)pluginDef;
		for(PluginDef andPluginDef : andSuiteDef.getAndPlugins()) {
			Element andElem = pluginElem.addElement("and-plugin")
					.addAttribute("name", andPluginDef.getName())
					.addAttribute("type", andPluginDef.getType())
					.addAttribute("next-activity", andPluginDef.getNextActivity());
			
			if (((ScriptPluginDef)andPluginDef).getSubRoutine() != null) {
				andElem.addAttribute("sub-routine",
						((ScriptPluginDef)andPluginDef).getSubRoutine());
				
			}
			buildScriptChildTags(andElem, andPluginDef);
		}
	}
	
	/**
	 * 为OrSuite对应的Element节点添加子节点
	 * @param pluginElem pluginElem <plugin type="com.ifeng.common.plugin.process.OrSuite">
	 *         对应的Element对象
	 * @param pluginDef
	 */
	private void buildOrChildTags(Element pluginElem, PluginDef pluginDef) {
		OrSuitePluginDef orSuiteDef = (OrSuitePluginDef)pluginDef;
		for(PluginDef orPluginDef : orSuiteDef.getOrPlugins()) {
			Element orElem = pluginElem.addElement("or-plugin")
					.addAttribute("name", orPluginDef.getName())
					.addAttribute("type", orPluginDef.getType())
					.addAttribute("next-activity", orPluginDef.getNextActivity());
			buildScriptChildTags(orElem, orPluginDef);
		}
	}
	
	/**
	 * 为ConditionSuite对应的Element节点添加子节点
	 * @param pluginElem
	 * @param pluginDef
	 */
	private void buildCondChildTags(Element pluginElem, PluginDef pluginDef) {
		ConditionSuitePluginDef condSuiteDef = (ConditionSuitePluginDef)pluginDef;
		//对于Condition，不设置next-activity属性
		Element condElem = pluginElem.addElement("condition")
				.addAttribute("name", condSuiteDef.getCondition().getName())
				.addAttribute("type", condSuiteDef.getCondition().getType());
		buildScriptChildTags(condElem, condSuiteDef.getCondition());
		
		Element trueElem = pluginElem.addElement("on-true")
				.addAttribute("name", condSuiteDef.getTrueCondition().getName())
				.addAttribute("type", condSuiteDef.getTrueCondition().getType())
				.addAttribute("next-activity", condSuiteDef.getTrueCondition().getNextActivity());
		buildScriptChildTags(trueElem, condSuiteDef.getTrueCondition());
		
		Element falseElem = pluginElem.addElement("on-false")
				.addAttribute("name", condSuiteDef.getFalseCondition().getName())
				.addAttribute("type", condSuiteDef.getFalseCondition().getType())
				.addAttribute("next-activity", condSuiteDef.getFalseCondition().getNextActivity());
		buildScriptChildTags(falseElem, condSuiteDef.getFalseCondition());
	}
	
	/**
	 * 为ForEachSuite对应的Element节点添加子节点
	 * @param pluginElem
	 * @param pluginDef
	 */
	private void buildForEachChildTags(Element pluginElem, PluginDef pluginDef) {
		ForEachSuitePluginDef forEachSuiteDef = (ForEachSuitePluginDef)pluginDef;
		for(PluginDef forEachDef : forEachSuiteDef.getForEachPlugins()) {
			Element foreachElem = pluginElem.addElement("foreach-plugin")
					.addAttribute("name", forEachDef.getName())
					.addAttribute("type", forEachDef.getType())
					.addAttribute("next-activity", forEachDef.getNextActivity());
			buildScriptChildTags(foreachElem, forEachDef);
		}
	}
	
	/**
	 * 为SwitchSuite对应的Element节点添加子节点
	 * @param pluginElem
	 * @param pluginDef
	 */
	private void buildSwitchChildTags(Element pluginElem, PluginDef pluginDef) {
		SwitchSuitePluginDef switchSuiteDef = (SwitchSuitePluginDef)pluginDef;
		//对于SwitchCondition，不设置其next-activity属性
		Element switchCondElem = pluginElem.addElement("switchCondition")
				.addAttribute("name", switchSuiteDef.getSwitchCondition().getName())
				.addAttribute("type", switchSuiteDef.getSwitchCondition().getType());
		buildScriptChildTags(switchCondElem, switchSuiteDef.getSwitchCondition());
		Element stepModulesElem = pluginElem.addElement("stepModulesMap")
				.addAttribute("type", ".MapConfig");
		Set<String> keySet = switchSuiteDef.getStepModulesMap().keySet();
		for(String str : keySet) {
			Element entry = stepModulesElem.addElement("entry");
			//entry.addElement("key").addText(str);
			String caseValue = ((ScriptPluginDef)switchSuiteDef.getStepModulesMap().get(str)).getExeCase();
			entry.addElement("key").addAttribute("value", caseValue);
			Element valElem = entry.addElement("value")
				.addAttribute("name", switchSuiteDef.getStepModulesMap().get(str).getName())
				.addAttribute("type", switchSuiteDef.getStepModulesMap().get(str).getType())
				.addAttribute("next-activity", switchSuiteDef.getStepModulesMap().get(str)
											.getNextActivity());
			buildScriptChildTags(valElem, switchSuiteDef.getStepModulesMap().get(str));
		}
	}
	
	/**
	 * 
	 * @param pluginElem
	 * @param pluginDef
	 */
	private void buildWhileChildTags(Element pluginElem, PluginDef pluginDef) {
		WhileSuitePluginDef whileSuiteDef = (WhileSuitePluginDef)pluginDef;
		Element whileCondElem = pluginElem.addElement("condition")
			.addAttribute("name", whileSuiteDef.getWhileConfition().getName())
			.addAttribute("type", whileSuiteDef.getWhileConfition().getType());
		buildScriptChildTags(whileCondElem, whileSuiteDef.getWhileConfition());
		
		Element trueElem = pluginElem.addElement("on-true")
			.addAttribute("name", whileSuiteDef.getOnTruePlugin().getName())
			.addAttribute("type", whileSuiteDef.getOnTruePlugin().getType())
			.addAttribute("next-activity", whileSuiteDef.getOnTruePlugin().getNextActivity());
		buildScriptChildTags(trueElem, whileSuiteDef.getOnTruePlugin());
		
	}
	
	/**
	 * 为AsyncSuite对应的Element节点添加子节点
	 * @param pluginElem <plugin type="...AsyncSuite">对应的Element对象
	 * @param pluginDef 
	 */
	private void buildAsyncChildTags(Element pluginElem, PluginDef pluginDef) {
		AsyncSuitePluginDef asyncSuiteDef = (AsyncSuitePluginDef)pluginDef;
		for(PluginDef asyncPluginDef : asyncSuiteDef.getAsyncPlugins()) {
			Element asyncElem = pluginElem.addElement("async-plugin")
					.addAttribute("name", asyncPluginDef.getName())
					.addAttribute("type", asyncPluginDef.getType())
					.addAttribute("next-activity", asyncPluginDef.getNextActivity());
			if (((ScriptPluginDef)asyncPluginDef).getSubRoutine() != null) {
				asyncElem.addAttribute("sub-routine",
						((ScriptPluginDef)asyncPluginDef).getSubRoutine());
				
			}
			buildScriptChildTags(asyncElem, asyncPluginDef);
		}
	}
	
	/**
	 * 为ScriptPlugin对应的元素添加一系列<set-property>标签
	 * @param pluginElem
	 * @param pluginDef
	 */
	private void buildScriptChildTags(Element pluginElem, PluginDef pluginDef) {
		if (pluginDef instanceof ScriptPluginDef) {
			if (((ScriptPluginDef) pluginDef).getNodeId() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "nodeId")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getNodeId());
			}
			if (((ScriptPluginDef) pluginDef).getIds() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "ids")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getIds());
			}
			if (((ScriptPluginDef) pluginDef).getMailTo() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "mailTo")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getMailTo());
			}
			if (((ScriptPluginDef) pluginDef).getRtxTo() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "rtxTo")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getRtxTo());
			}
			
			if (((ScriptPluginDef) pluginDef).getSmsTo() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "smsTo")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getSmsTo());
			}
			
			if (((ScriptPluginDef) pluginDef).getScriptType() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "scriptType")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getScriptType());
			}
			if (((ScriptPluginDef) pluginDef).getSuspendsOnException() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "suspendsOnException")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getSuspendsOnException());
			}
			if (((ScriptPluginDef) pluginDef).getExpids() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "exceptionids")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getExpids());
			}
			if (((ScriptPluginDef) pluginDef).getnActivity() != null) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "nextActivity")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getnActivity());
			}
			if (((ScriptPluginDef) pluginDef).getDefId() > 0) {
				pluginElem.addElement("set-property")
				.addAttribute("name", "pluginDefId")
				.addAttribute("value",((ScriptPluginDef) pluginDef).getDefId() + "");
			}
		}
	}
	
	
	/**
	 * 根据PluginDef的类型，为该PluginDef对象所对应的Element节点添加子节点
	 * (类似于根据插件类型不同为<plugin>标签添加不同的子标签)
	 * @param pluginElem
	 * @param pluginDef
	 * @return
	 * @throws InvalidConfigurationException
	 * @throws PluginTypeNotSupportException
	 */
	private Element buildChildTagsByType(Element pluginElem, PluginDef pluginDef) 
			throws InvalidConfigurationException {
		String type = pluginDef.getType();
		
		if (null == type || type.trim().equals("")) {
			throw new InvalidConfigurationException("type attribute of plugin was not set");
		}
		if (type.equals(PluginEnum.AND.toString())) {
			buildAndChildTags(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.OR.toString())) {
			buildOrChildTags(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.COND.toString())) {
			buildCondChildTags(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.FOREACH.toString())) {
			buildForEachChildTags(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.SWITCH.toString())) {
			buildSwitchChildTags(pluginElem, pluginDef);
				
		}else if (type.equals(PluginEnum.WHILE.toString())) {
			buildWhileChildTags(pluginElem, pluginDef);
			
		}else if(type.equals(PluginEnum.ASYNC.toString())) {
			buildAsyncChildTags(pluginElem, pluginDef);
		}else {
			throw new InvalidConfigurationException("There is no support for plugin type of " 
														+ type);
		}
		
		return pluginElem;
	}
	
	/**
	 * 通过一个List<ProcessDef>集合，构造一颗DOM树，并且返回该DOM树的Document节点
	 * @param processList
	 * @return
	 * @throws InvalidConfigurationException
	 * @throws PluginTypeNotSupportException
	 */
	public Document buildDocumentFromObject(List<ProcessDef> processList) 
			throws InvalidConfigurationException {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("config");
		for(ProcessDef processDef : processList) {
			Element processElement = root.addElement("config")
					.addAttribute("name", processDef.getName())
					.addAttribute("type", PROCESS_DEFINITION);
			processElement.addElement("title").addText(processDef.getTitle());
			processElement.addElement("description").addText(processDef.getDescription());
			//Map<String, ActivityDef> activitiesMap = processDef.getActivitiesMap();
			List<ActivityDef> activitiesList = processDef.getActivitiesList();
			for (ActivityDef activityDef : activitiesList) {
				Element activityElem = processElement.addElement("activity")
						.addAttribute("name", activityDef.getName())
						.addAttribute("type", ACTIVITY_DEFINITION);
				activityElem.addElement("title").addText(activityDef.getTitle() == null ? "" : activityDef.getTitle());
				activityElem.addElement("description").addText(activityDef.getDescription() == null ? "" : activityDef.getDescription());
				PluginDef pluginDef = activityDef.getPlugin();
				Element pluginElem = activityElem.addElement("plugin")
						.addAttribute("name", pluginDef.getName())
						.addAttribute("type", pluginDef.getType())
						.addAttribute("next-activity", pluginDef.getNextActivity());
				//构建<plugin>标签的子标签
				pluginElem = buildChildTagsByType(pluginElem,pluginDef);
			}
		}
		return doc;
	}
	
	/**
	 * 根据已经修改过的流程，重新构建一个Document对象
	 * @param process 需要修改的流程
	 * @param doc 为修改之前的流程文件对应的Document对象
	 * @return
	 * @throws PluginTypeNotSupportException 
	 * @throws InvalidConfigurationException 
	 */
	public Document modifyDocument(ProcessDef process, Document doc) 
			throws InvalidConfigurationException {
		Element root = doc.getRootElement();
		//XPath解析出<config>标签中name属性是与需要修改流程的name属性相同的节点
		Element nodeToModify = (Element) root.selectSingleNode("config[@name='" + process.getName() + "']");
		//将该节点从DOM树中移除
		root.remove(nodeToModify);
		//创建一个新的元素，它代表修改过的新的流程信息
		Element newProcessElem = root.addElement("config")
				.addAttribute("name", process.getName())
				.addAttribute("type", PROCESS_DEFINITION);
		newProcessElem.addElement("title").addText(process.getTitle());
		newProcessElem.addElement("description").addText(process.getDescription());
		//Map<String, ActivityDef> activitiesMap = processDef.getActivitiesMap();
		List<ActivityDef> activitiesList = process.getActivitiesList();
		for (ActivityDef activityDef : activitiesList) {
			Element activityElem = newProcessElem.addElement("activity")
					.addAttribute("name", activityDef.getName())
					.addAttribute("type", ACTIVITY_DEFINITION);
			activityElem.addElement("title").addText(activityDef.getTitle());
			activityElem.addElement("description").addText(activityDef.getDescription());
			PluginDef pluginDef = activityDef.getPlugin();
			Element pluginElem = activityElem.addElement("plugin")
					.addAttribute("name", pluginDef.getName())
					.addAttribute("type", pluginDef.getType())
					.addAttribute("next-activity", pluginDef.getNextActivity());
			//构建<plugin>标签的子标签
			pluginElem = buildChildTagsByType(pluginElem,pluginDef);
		}
		return doc;
	}
	
	/**
	 * 添加一个新的流程
	 * @param process
	 * @param doc
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	public Document addNewProcess(ProcessDef process, Document doc) 
			throws InvalidConfigurationException {
		if(!isProcessNameValid(process.getName(), doc)) {
			throw new InvalidConfigurationException("Dulplicate Process Name");
		}
		Element root = doc.getRootElement();
		Element newProcessElem = root.addElement("config")
				.addAttribute("name", process.getName())
				.addAttribute("type", PROCESS_DEFINITION);
		newProcessElem.addElement("title").addText(process.getTitle());
		newProcessElem.addElement("description").addText(process.getDescription());
		//Map<String, ActivityDef> activitiesMap = processDef.getActivitiesMap();
		List<ActivityDef> activitiesList = process.getActivitiesList();
		if(! isActivityNamesValid(activitiesList,process.getName(),doc)) {
			throw new InvalidConfigurationException("Dulplicate Activity Name");
		}
		for (ActivityDef activityDef : activitiesList) {
			Element activityElem = newProcessElem.addElement("activity")
					.addAttribute("name", activityDef.getName())
					.addAttribute("type", ACTIVITY_DEFINITION);
			activityElem.addElement("title").addText(activityDef.getTitle());
			activityElem.addElement("description").addText(activityDef.getDescription());
			PluginDef pluginDef = activityDef.getPlugin();
			Element pluginElem = activityElem.addElement("plugin")
					.addAttribute("name", pluginDef.getName())
					.addAttribute("type", pluginDef.getType())
					.addAttribute("next-activity", pluginDef.getNextActivity());
			//构建<plugin>标签的子标签
			pluginElem = buildChildTagsByType(pluginElem,pluginDef);
		}
		
		return doc;
	}
	
	/**
	 * 删除一个指定的流程
	 * @param processName
	 * @param doc
	 */
	public void deleteProcess(String processName, Document doc) {
		Element root = doc.getRootElement();
		Node node = root.selectSingleNode("config[@name='" + processName + "']");
		root.remove(node);
	}
	
	/**
	 * 判断一个流程名称是否合法：检测现有的流程定义文件中是否有重名情况。
	 * 没有重名则合法，反之不合法
	 * @param processName
	 * @param doc
	 * @return true：当流程名称不合法(有重复)
	 * 		   false：当流程名称合法(无重复)
	 */
	public boolean isProcessNameValid(String processName, Document doc) {
		Element root = doc.getRootElement();
		//使用XPath选择name属性为processName的<config>节点
		Node node = root.selectSingleNode("config[@name='" + processName + "']");
		//如果这个节点为null，证明没有重复名称，流程合法
		return node == null;
	}
	
	/**
	 * 判断一组Activity的名称中是否都合法(否没有重复的名称)
	 * @param activities
	 * @param processName
	 * @param doc
	 * @return
	 */
	public boolean isActivityNamesValid(List<ActivityDef> activities, 
			String processName, Document doc) {
		//使用XPath选择name属性为processName的<config>节点
		for(ActivityDef def : activities) {
			if(!isActivityNameValid(def.getName(),processName,doc)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断某个流程中的某个Activity的名称是否被占用(是否合法),也即判断有没有重名
	 * @param activityName
	 * @param processName
	 * @param doc
	 * @return true:代表该活动名称是合法的	false:代表该活动名称是非法的
	 */
	public boolean isActivityNameValid(String activityName, 
			String processName, Document doc) {
		Element root = doc.getRootElement();
		//使用XPath选择name属性为processName的<config>节点
		Node processNode = root.selectSingleNode("config[@name='" + processName + "']");
		Node activityNode = processNode.selectSingleNode("activity[@name='" 
													+ activityName + "']");
		return activityNode == null;
	}
	
	/**
	 * 将Document对象所对应的一颗DOM树的信息写入XML文件中
	 * @param doc DOM树对应的Document节点
	 * @param path 要写入的文件路径
	 * @throws InvalidConfigurationException 
	 */
	public void writeXMLToFile(Document doc, String path) 
			throws InvalidConfigurationException {
		XMLWriter xmlWriter = null;
		try {
			//如果Schema验证失败，那么抛出异常
//			if (!validateWithSchema(doc, "/data/cmpp/workflow/WorkFlow.xsd")) {
//				throw new InvalidConfigurationException("The configuration file does not " +
//						"conforms with the schema!");
//				
//			}
			//真正写入之前先进行备份
			XMLBackup(path);
			File file = new File(path);
			OutputFormat format = OutputFormat.createPrettyPrint();
			xmlWriter = new XMLWriter(new FileWriter(file), format);
			xmlWriter.write(doc);
//			CmppDBClientService nosql = (CmppDBClientService) SpringContextUtil.getBean("cmppDBService");
//			nosql.put("lastmodify:" + path, file.lastModified());
			
			//写完之后清除多余备份,这里使用的是默认的备份数目
			//clearRedundantBackups(path.substring(0, path.lastIndexOf(File.separator) + 1));
			clearRedundantBackups(path.substring(0, path.lastIndexOf("/") + 1));
		} catch (IOException e) {
			log.error(e);
		} catch (InvalidParamException e) {
			log.error(e);
		} finally {
			if (xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		
	}
	
	/**
	 * 重载的writeXMLToFile方法
	 * @param doc
	 * @param writer
	 */
	/*
	public void writeXMLToFile(Document doc, FileWriter writer) {
		XMLWriter xmlWriter = new XMLWriter(writer);
		try {
			xmlWriter.write(doc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	
	/**
	 * 对已有的流程定义文件进行
	 * @param fileName 需要备份的文件的完整路径
	 * @throws IOException 
	 */
	private void XMLBackup(String fileName) throws IOException {
		File originalFile = new File(fileName);
		//如果之前不存在流程定义文件，则新建一个文件
		if (!originalFile.exists()) {
			originalFile.createNewFile();
		}else {
			String bakFileName = getBakFileName(fileName);
			originalFile.renameTo(new File(bakFileName));
		}
	}
	
	
	/**
	 * 给原有文件名加上时间戳和随机数，生成备份文件的文件名
	 * @param fileName
	 * @return
	 */
	private String getBakFileName(String fileName) {
		StringBuffer strBuf = new StringBuffer(fileName);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		//添加当前时间
		strBuf.append(sdf.format(new Date()));
		//添加三位随机数
		for(int i = 0; i < 3; i++){
			strBuf.append(random.nextInt(10));
		}
		return strBuf.toString();
	}
	
	
	/**
	 * 保留指定数量的最新的流程定义文件,将更早期的流程备份文件删除
	 * @param path 流程定义文件存放的路径(目录)
	 * @throws Exception 
	 */
	private void clearRedundantBackups(String path, int copies) 
			throws InvalidParamException {
		List<String> versions = getXMLHistory(path);
		TreeSet set = new TreeSet(new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				long time1 = 0l;
				long time2 = 0l;
				//TreeSet元素的比较规则
				if (o1 instanceof String && o2 instanceof String) {
					int length1 = ((String)o1).length();
					int length2 = ((String)o2).length();
					String timeStamp1 = ((String)o1).substring(length1 - 20, length1 - 3);
					String timeStamp2 = ((String)o2).substring(length2 - 20, length2 - 3);
					time1 = Long.parseLong(timeStamp1);
					time2 = Long.parseLong(timeStamp2);
				}
				if(time1 < time2) {
					return -1;
				}else if(time1 == time2) {
					return 0;
				}else {
					return 1;
				}
			}
			
		});
		set.addAll(versions);
		int index = 0;
		//按照上面的比较器规则，对HashSet中的元素进行降序迭代，对规定数目之后的文件删除
		for(Iterator<String> ite = set.descendingIterator(); ite.hasNext();) {
			String fileName = ite.next();
			if(index++ >= copies) {
				File file = new File(path + fileName);
				file.delete();
			}
		}
	}
	
	/**
	 * 保留5个最新的流程定义文件,将更早期的流程备份文件删除
	 * @param path
	 * @throws Exception 
	 */
	private void clearRedundantBackups(String path) throws InvalidParamException {
		clearRedundantBackups(path, DEF_BAK_NUM);
	}
	
	/**
	 * 获取流程定义文件的所有历史版本的文件名
	 * @param path 所有版本流程定义文件所在的父目录的路径
	 * @return
	 * @throws Exception 
	 */
	private List<String> getXMLHistory(String path) throws InvalidParamException {
		List<String> hisVersions = new ArrayList<String>();
		File dir = new File(path);
		if (! dir.isDirectory()) {
			throw new InvalidParamException(path + " is not a directory");
		}else {
			File[] files = dir.listFiles(new FilenameFilter() {
				private Pattern pattern = Pattern.compile("[\\d\\D]*.xml\\d{20}");
				@Override
				public boolean accept(File dir, String name) {
					return pattern.matcher(name).matches();
				}
			});
			for(File file : files) {
				hisVersions.add(file.getName());
			}
		}
		return hisVersions;
	}
	
	/**
	 * 通过一个Schema文件验证传递过来的流程定义对象所构成的Document是否是有效的
	 * @param doc
	 * @param schemaPath Schema文件的路径
	 * @return
	 */
	private boolean validateWithSchema(Document doc, String schemaPath) {
		boolean flag = false;
		XMLErrorHandler handler = new XMLErrorHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		try {
			SAXParser parser = factory.newSAXParser();
			parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    "file:" + schemaPath); 
            
            SAXValidator validator = new SAXValidator(parser.getXMLReader());
            validator.setErrorHandler(handler);
            validator.validate(doc);
            if(handler.getErrors().hasContent()) {
            	StringBuffer sbErrs = new StringBuffer();
            	List errList = handler.getErrors().content();
            	for(int i = 0; i < errList.size(); i++) {
            		Element err = (Element) errList.get(i);
            		DefaultText errText = (DefaultText) err.content().get(0);
            		sbErrs.append(errText);
            	}
            	log.error("Schema validation failed: " + sbErrs.toString() + " ");
            }else {
            	log.info("验证成功!");
            	flag = true;
            }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			log.error(e);
		} catch (SAXException e) {
			e.printStackTrace();
			log.error("e");
		}
		
		return flag;
	}

	public int getBakNum() {
		return bakNum;
	}

	public void setBakNum(int bakNum) {
		this.bakNum = bakNum;
	}
	
	
}