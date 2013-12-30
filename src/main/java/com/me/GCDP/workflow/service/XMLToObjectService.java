package com.me.GCDP.workflow.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.me.GCDP.workflow.exception.ConfigurationFileNotFoundException;
import com.me.GCDP.workflow.exception.InvalidConfigurationException;
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
 * XMLToObjectsUtil是一个通过读取流程定义XML文件，生成一个含有ProcessDef对象的List集合工具类。
 * @author xiongfeng
 *
 */
public final class XMLToObjectService {
	private static Log log = LogFactory.getLog(XMLToObjectService.class);
	
//	private static XMLToObjectService instance;
	
//	private XMLToObjectService() {}
	
//	public static synchronized XMLToObjectService getInstance() {
//		if(instance == null) {
//			instance = new XMLToObjectService();
//		}
//		return instance;
//	}
	
	public XMLToObjectService() {}
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * AndSuitePluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的AndSuitePluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的AndSuitePluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	@SuppressWarnings("rawtypes")
	private PluginDef constructAndPlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		List<PluginDef> andDefs = new ArrayList<PluginDef>();
		for(Iterator andPluginIterator = pluginElem.elementIterator("and-plugin");
				andPluginIterator.hasNext();) {
			Element andPluginElem = (Element) andPluginIterator.next();
			//<plugin>标签的type属性一旦为空，会影响到icommon中工作流组件的正常运行，
			//因此这里直接抛异常
			if (null == andPluginElem.attributeValue("type") || 
					andPluginElem.attributeValue("type").trim().equals("")) {
				throw new InvalidConfigurationException(
						"type attribute was not set");
			}else {
				PluginDef andPluginDef;
				//假设插件类型是Script插件，那么处理的方式则略有不同
				if (andPluginElem.attributeValue("type").trim()
						.equals(PluginEnum.SCRIPT.toString())) {
					andPluginDef = new ScriptPluginDef();
					andPluginDef = constructScriptPlugin(andPluginElem, andPluginDef);
				}else {
					andPluginDef = new PluginDef();
				}
				andPluginDef.setName(andPluginElem.attributeValue("name"));			
				andPluginDef.setType(andPluginElem.attributeValue("type"));
				andPluginDef.setNextActivity(andPluginElem.attributeValue("next-activity"));
				andDefs.add(andPluginDef);
			}
		}
		((AndSuitePluginDef)def).setAndPlugins(andDefs);
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * OrSuitePluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的OrSuitePluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的OrSuitePluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	@SuppressWarnings("rawtypes")
	private PluginDef constructOrPlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		List<PluginDef> orDefs = new ArrayList<PluginDef>();
		for(Iterator orPluginIterator = pluginElem.elementIterator("or-plugin");
				orPluginIterator.hasNext();) {
			Element orPluginElement = (Element) orPluginIterator.next();
			if (null == orPluginElement.attributeValue("type") || 
					orPluginElement.attributeValue("type").trim().equals("")) {
				throw new InvalidConfigurationException(
						"type attribute was not set");
			}else {
				PluginDef orPluginDef;
				//假设插件类型是Script插件，那么处理的方式则略有不同
				if (orPluginElement.attributeValue("type").trim()
						.equals(PluginEnum.SCRIPT.toString())) {
					orPluginDef = new ScriptPluginDef();
					orPluginDef = constructScriptPlugin(orPluginElement, orPluginDef);
				}else {
					orPluginDef = new PluginDef();
				}
				orPluginDef.setName(orPluginElement.attributeValue("name"));
				orPluginDef.setType(orPluginElement.attributeValue("type"));
				orPluginDef.setNextActivity(orPluginElement.attributeValue("next-activity"));
				orDefs.add(orPluginDef);
			}
		}
		((OrSuitePluginDef)def).setOrPlugins(orDefs);
		return def;
	}
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * ConditionPluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的ConditionPluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的ConditionPluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	private PluginDef constructConditionPlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		//设置condition插件
		PluginDef condDef;
		
		if (null == pluginElem.element("condition").attributeValue("type") || 
				pluginElem.element("condition").attributeValue("type")
				.trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			//若条件插件类型是Script插件，那么处理方式略有不同
			if (pluginElem.element("condition").attributeValue("type")
				.trim().equals(PluginEnum.SCRIPT.toString())) {
				condDef = new ScriptPluginDef();
				condDef = constructScriptPlugin(pluginElem.element("condition"), condDef);
			}else if (pluginElem.element("condition").attributeValue("type")
				.trim().equals(PluginEnum.CONDSCRIPT.toString())) {
				
				//若插件类型是ConditionScript,处理方式与Script插件类似
				condDef = new ScriptPluginDef();
				condDef = constructScriptPlugin(pluginElem.element("condition"), condDef);
			}else {
				condDef = new PluginDef();
			}
			condDef.setName(pluginElem.element("condition").attributeValue("name"));
			condDef.setType(pluginElem.element("condition").attributeValue("type"));
			condDef.setNextActivity(pluginElem.element("condition")
					.attributeValue("next-activity"));
		}
		((ConditionSuitePluginDef)def).setCondition(condDef);
		
		
		
		//设置on-true插件
		PluginDef trueDef;
		if (null == pluginElem.element("on-true").attributeValue("type") || 
				pluginElem.element("on-true").attributeValue("type").trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			if (pluginElem.element("on-true").attributeValue("type").trim()
					.equals(PluginEnum.SCRIPT.toString())) {
				trueDef = new ScriptPluginDef();
				trueDef = constructScriptPlugin(pluginElem.element("on-true"), trueDef);
			}else {
				trueDef = new PluginDef();
			}
			trueDef.setName(pluginElem.element("on-true").attributeValue("name"));
			trueDef.setType(pluginElem.element("on-true").attributeValue("type"));
			trueDef.setNextActivity(pluginElem.element("on-true")
					.attributeValue("next-activity"));
		}
		((ConditionSuitePluginDef)def).setTrueCondition(trueDef);
		
		
		
		//设置on-false插件
		PluginDef falseDef;
		if (null == pluginElem.element("on-false").attributeValue("type") || 
				pluginElem.element("on-false").attributeValue("type").trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			if(pluginElem.element("on-false").attributeValue("type").trim()
					.equals(PluginEnum.SCRIPT.toString())) {
				falseDef = new ScriptPluginDef();
				falseDef = constructScriptPlugin(pluginElem.element("on-false"), falseDef);
			}else {
				falseDef = new PluginDef();
			}
			falseDef.setName(pluginElem.element("on-false").attributeValue("name"));
			falseDef.setType(pluginElem.element("on-false").attributeValue("type"));
			falseDef.setNextActivity(pluginElem.element("on-false")
					.attributeValue("next-activity"));
		}
		((ConditionSuitePluginDef)def).setFalseCondition(falseDef);
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * ForEachPluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的ForEachPluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的ForEachPluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	@SuppressWarnings("rawtypes")
	private PluginDef constructForEachPlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		List<PluginDef> forEachDefs = new ArrayList<PluginDef>();
		for (Iterator forEachIterator = pluginElem.elementIterator("foreach-plugin")
				;forEachIterator.hasNext();) {
			Element forEachElement = (Element) forEachIterator.next();
			PluginDef forEachDef;			
			if (null == forEachElement.attributeValue("type") || 
					forEachElement.attributeValue("type").trim().equals("")) {
				throw new InvalidConfigurationException(
						"type attribute was not set");
			}else {
				if (forEachElement.attributeValue("type").trim()
						.equals(PluginEnum.SCRIPT.toString())) {
					forEachDef = new ScriptPluginDef();
					forEachDef = constructScriptPlugin(forEachElement, forEachDef);
				}else {
					forEachDef = new PluginDef();
				}
				forEachDef.setName(forEachElement.attributeValue("name"));
				forEachDef.setType(forEachElement.attributeValue("type"));
				forEachDef.setNextActivity(forEachElement.attributeValue("next-activity"));
				forEachDefs.add(forEachDef);
			}
		}
		((ForEachSuitePluginDef)def).setForEachPlugins(forEachDefs);
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * SwitchPluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的SwitchPluginDef对象
	 * 
	 * 
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的SwitchPluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	@SuppressWarnings("rawtypes")
	private PluginDef constructSwitchPlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		PluginDef switchCondDef;
		if (null == pluginElem.element("switchCondition").attributeValue("type") || 
				pluginElem.element("switchCondition").attributeValue("type")
				.trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			if (pluginElem.element("switchCondition").attributeValue("type")
				.trim().equals(PluginEnum.SCRIPT.toString())) {
				switchCondDef = new ScriptPluginDef();
				switchCondDef = constructScriptPlugin(pluginElem.element("switchCondition")
						, switchCondDef);
			}else if (pluginElem.element("switchCondition").attributeValue("type")
					.trim().equals(PluginEnum.SWITCHCONDSCRIPT.toString())) {
				switchCondDef = new ScriptPluginDef();
				switchCondDef = constructScriptPlugin(pluginElem.element("switchCondition")
						, switchCondDef);
			}else {
				switchCondDef = new PluginDef();
			}
			switchCondDef.setName(pluginElem.element("switchCondition").attributeValue("name"));
			switchCondDef.setType(pluginElem.element("switchCondition")
					.attributeValue("type"));
			switchCondDef.setNextActivity(pluginElem.element("switchCondition")
					.attributeValue("next-activity"));
		}
		((SwitchSuitePluginDef)def).setSwitchCondition(switchCondDef);
		
		
		
		Element stepModulesMapElem = pluginElem.element("stepModulesMap");
		Map<String, PluginDef> entryMap = new HashMap<String, PluginDef>();
		for(Iterator entryIterator = stepModulesMapElem.elementIterator("entry")
				;entryIterator.hasNext();) {
			Element entryElem = (Element) entryIterator.next();
			String entryKey = entryElem.element("key").attributeValue("value");
			PluginDef entryValueDef;
			if (null == entryElem.element("value").attributeValue("type") || 
					entryElem.element("value").attributeValue("type")
					.trim().equals("")) {
				throw new InvalidConfigurationException(
						"type attribute was not set");
			}else {
				//若插件类型是Script插件，那么处理方式略有不同
				if (entryElem.element("value").attributeValue("type")
					.trim().equals(PluginEnum.SCRIPT.toString())) {
					entryValueDef = new ScriptPluginDef();
					entryValueDef = constructScriptPlugin(entryElem.element("value"), entryValueDef);
				}else {
					entryValueDef = new PluginDef();
				}
				entryValueDef.setName(entryElem.element("value").attributeValue("name"));
				entryValueDef.setType(entryElem.element("value").attributeValue("type"));
				entryValueDef.setNextActivity(entryElem.element("value")
						.attributeValue("next-activity"));
				entryMap.put(entryKey, entryValueDef);
			}
		}
		((SwitchSuitePluginDef)def).setStepModulesMap(entryMap);
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * WhilePluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的SwitchPluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的SwitchPluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	private PluginDef constructWhilePlugin(Element pluginElem, PluginDef def) 
			throws InvalidConfigurationException {
		PluginDef whileCondDef;
		if (null == pluginElem.element("condition").attributeValue("type") || 
				pluginElem.element("condition").attributeValue("type").trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			if (pluginElem.element("condition").attributeValue("type").trim()
					.equals(PluginEnum.SCRIPT.toString())) {
				whileCondDef = new ScriptPluginDef();
				whileCondDef = constructScriptPlugin(pluginElem.element("condition"), whileCondDef);
			}else if (pluginElem.element("condition").attributeValue("type").trim()
					.equals(PluginEnum.CONDSCRIPT.toString())) {
				whileCondDef = new ScriptPluginDef();
				whileCondDef = constructScriptPlugin(pluginElem.element("condition"), whileCondDef);
			}else {
				whileCondDef = new PluginDef();
			}
			whileCondDef.setName(pluginElem.element("condition").attributeValue("name"));
			whileCondDef.setType(pluginElem.element("condition").attributeValue("type"));
			whileCondDef.setNextActivity(pluginElem.element("condition")
					.attributeValue("next-activity"));
			((WhileSuitePluginDef)def).setWhileConfition(whileCondDef);
		}
		
		
		PluginDef trueDef;
		
		if (null == pluginElem.element("on-true").attributeValue("type") || 
				pluginElem.element("on-true").attributeValue("type").trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}else {
			if (pluginElem.element("on-true").attributeValue("type").trim()
					.equals(PluginEnum.SCRIPT.toString())) {
				trueDef = new ScriptPluginDef();
				trueDef = constructScriptPlugin(pluginElem.element("on-true"), trueDef);
			} else {
				trueDef = new PluginDef();
			}
			trueDef.setName(pluginElem.element("on-true").attributeValue("name"));
			trueDef.setType(pluginElem.element("on-true").attributeValue("type"));
			trueDef.setNextActivity(pluginElem.element("on-true")
					.attributeValue("next-activity"));
			((WhileSuitePluginDef)def).setOnTruePlugin(trueDef);
		}
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * AsyncSuitePluginDef对象的各个属性赋值，最终创建一个含有<plugin>标签子标签流程定义
	 * 信息的AsyncSuitePluginDef对象
	 * @param pluginElem 流程定义文件中的<plugin>标签所对应的dom4j Element对象
	 * @param def <plugin>标签对应的AsyncSuitePluginDef对象，作为传入参数，属性均未设置值
	 * @return
	 * @throws InvalidConfigurationException
	 */
	@SuppressWarnings("rawtypes")
	private PluginDef constructAsyncPlugin(Element pluginElem,PluginDef def) 
			throws InvalidConfigurationException {
		List<PluginDef> asyncDefs = new ArrayList<PluginDef>();
		for(Iterator asyncPluginIterator = pluginElem.elementIterator("async-plugin");
				asyncPluginIterator.hasNext();) {
			Element asyncPluginElem = (Element) asyncPluginIterator.next();
			
			PluginDef asyncPluginDef;
			//<plugin>标签的type属性一旦为空，会影响到icommon中工作流组件的正常运行，因此这里直接抛异常
			if (null == asyncPluginElem.attributeValue("type") || 
					asyncPluginElem.attributeValue("type").trim().equals("")) {
				throw new InvalidConfigurationException(
						"type attribute was not set");
			}else {
				if (asyncPluginElem.attributeValue("type").trim()
						.equals(PluginEnum.SCRIPT.toString())) {
					asyncPluginDef = new ScriptPluginDef();
					asyncPluginDef = constructScriptPlugin(asyncPluginElem, asyncPluginDef);
				} else {
					asyncPluginDef = new PluginDef();
				}
				asyncPluginDef.setName(asyncPluginElem.attributeValue("name"));
				asyncPluginDef.setType(asyncPluginElem.attributeValue("type"));
				asyncPluginDef.setNextActivity(asyncPluginElem.attributeValue("next-activity"));
				asyncDefs.add(asyncPluginDef);
			}
		}
		((AsyncSuitePluginDef)def).setAsyncPlugins(asyncDefs);
		return def;
	}
	
	
	/**
	 * 根据流程定义文件中的<plugin>标签所对应的dom4j Element对象为<plugin>标签对应的
	 * ScriptPluginDef对象的各个属性赋值
	 * @param pluginElem
	 * @param def
	 * @return
	 */
	private PluginDef constructScriptPlugin(Element pluginElem, PluginDef def) {
		Element nodeIdElem = (Element)pluginElem.selectSingleNode("set-property[@name='nodeId']");
		Element idsElem = (Element)pluginElem.selectSingleNode("set-property[@name='ids']");
		Element mailElem = (Element)pluginElem.selectSingleNode("set-property[@name='mailTo']");
		Element rtxElem = (Element)pluginElem.selectSingleNode("set-property[@name='rtxTo']");
		Element smsElem = (Element)pluginElem.selectSingleNode("set-property[@name='smsTo']");
		Element typeElem = (Element)pluginElem.selectSingleNode("set-property[@name='scriptType']");
		Element suspendElem = (Element)pluginElem.selectSingleNode("set-property[@name='suspendsOnException']");
		Element expElem = (Element)pluginElem.selectSingleNode("set-property[@name='exceptionids']");
		Element nextActElem = (Element)pluginElem.selectSingleNode("set-property[@name='nextActivity']");
		Element defIdElem = (Element)pluginElem.selectSingleNode("set-property[@name='pluginDefId']");
		
		((ScriptPluginDef)def).setNodeId(nodeIdElem != null ? nodeIdElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setIds(idsElem != null ? idsElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setMailTo(mailElem != null ? mailElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setRtxTo(rtxElem != null ? rtxElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setSmsTo(smsElem != null ? smsElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setScriptType(typeElem != null ? typeElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setSuspendsOnException(suspendElem != null ? suspendElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setExpids(expElem != null ? expElem.attributeValue("value") : null);
		((ScriptPluginDef)def).setnActivity(nextActElem != null ? nextActElem.attributeValue("value") : null);
		def.setDefId(defIdElem != null ? Integer.parseInt(defIdElem.attributeValue("defIdElem")) : null);
		return def;
	}
	
	
	/**
	 * 根据配置文件中<plugin>标签的type属性，决定并创建对应的PluginDef的子类型。
	 * 并且根据配置文件中<plugin>子元素的定义，为对应的Plugin子类型实例设置属性值。
	 * 例如当type=com.ifeng.common.plugin.process.AndSuite，
	 * 创建的Plugin即为com.ifeng.flow.converter.components.AndSuitePluginDef
	 * @param type : 配置文件中<plugin>元素的type属性
	 * @param pluginElem : <plugin>标签对应的Element节点
	 * @return
	 * @throws PluginTypeNotSupportException 
	 * @throws InvalidConfigurationException 
	 */
	private PluginDef constructPluginByType(Element pluginElem, String type) 
			throws InvalidConfigurationException {
		if (type == null || type.trim().equals("")) {
			throw new InvalidConfigurationException(
					"type attribute was not set");
		}
		
		PluginDef pluginDef;		
		if (type.equals(PluginEnum.AND.toString())) {
			pluginDef = new AndSuitePluginDef();		
			pluginDef = constructAndPlugin(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.OR.toString())) {
			pluginDef = new OrSuitePluginDef();
			pluginDef = constructOrPlugin(pluginElem,pluginDef);
		}else if (type.equals(PluginEnum.COND.toString())) {
			pluginDef = new ConditionSuitePluginDef();
			pluginDef = constructConditionPlugin(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.FOREACH.toString())) {
			pluginDef = new ForEachSuitePluginDef();
			pluginDef = constructForEachPlugin(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.SWITCH.toString())) {
			pluginDef = new SwitchSuitePluginDef();
			pluginDef = constructSwitchPlugin(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.WHILE.toString())) {
			pluginDef = new WhileSuitePluginDef();
			pluginDef = constructWhilePlugin(pluginElem, pluginDef);
		}else if (type.equals(PluginEnum.ASYNC.toString())) {
			pluginDef = new AsyncSuitePluginDef();
			pluginDef = constructAsyncPlugin(pluginElem, pluginDef);
		}else {
			throw new InvalidConfigurationException(
					"There is no support for plugin type of " + type);
		}
		
		//将<plugin>标签本身对应的name、type、next-activity属性值赋给对应的PluginDef对象
		pluginDef.setName(pluginElem.attributeValue("name"));
		pluginDef.setType(type);
		pluginDef.setNextActivity(pluginElem.attributeValue("next-activity"));
		return pluginDef;
	}
	
	
	
	/**
	 * 根据流程名称从流程定义文件中读取指定的流程定义信息，并构造相应的ProcessDef对象
	 * @param name
	 * @return
	 * @throws InvalidConfigurationException 
	 * @throws PluginTypeNotSupportException 
	 */
	@SuppressWarnings("rawtypes")
	public ProcessDef buildSpecifiedProcessDef(String name, Document doc) 
			throws InvalidConfigurationException {
		Element root = doc.getRootElement();
		ProcessDef processDef = new ProcessDef();
		for(Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element configElement = (Element)iterator.next();
			if("com.ifeng.common.workflow.ProcessDefinition".
					equals(configElement.attributeValue("type"))
					&& name.equals(configElement.attributeValue("name"))) {
				
				processDef.setName(configElement.attributeValue("name"));
				processDef.setTitle(configElement.element("title").getText());
				processDef.setDescription(configElement.element("description").
						getText());
				List<ActivityDef> activitiesList = new ArrayList<ActivityDef>();
				//遍历ProcessDefinition下面的所有<Activity>
				for (Iterator activitiesIterator = configElement.elementIterator("activity")
						;activitiesIterator.hasNext();) {
					Element activityElement = (Element) activitiesIterator.next();
					ActivityDef activityDef = new ActivityDef();
					String activityName = activityElement.attributeValue("name");
					String activityTitle = activityElement.element("title").getText();
					String activityDesc = activityElement.element("description").getText();
					Element pluginElement = activityElement.element("plugin");
					PluginDef pluginDef = constructPluginByType(pluginElement,
							pluginElement.attributeValue("type"));
					activityDef.setName(activityName);
					activityDef.setTitle(activityTitle);
					activityDef.setDescription(activityDesc);
					activityDef.setPlugin(pluginDef);
					//activitiesMap.put(activityName, activityDef);
					activitiesList.add(activityDef);
				}
				processDef.setActivitiesList(activitiesList);
			}
		}
		
		return processDef;
	}
	
	/**
	 * 提取出流程定义文件中定义的所有Process名称的列表
	 * @param doc 流程定义文件对应的Document对象
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getProcessList(Document doc) {
		List<String> processList = new ArrayList<String>();
		Element root = doc.getRootElement();
		for(Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element configElement = (Element)iterator.next();
			if("com.ifeng.common.workflow.ProcessDefinition".
					equals(configElement.attributeValue("type"))) {
				processList.add(configElement.attributeValue("name"));
			}
		}
		return processList;
	}
	
	
	/**
	 * 提取出流程定义文件中定义的所有Process名称的列表
	 * @param filePath 流程定义文件的完整路径
	 * @return
	 */
	public List<String> getProcessList(String filePath) {
		SAXReader reader = new SAXReader();
		List<String> processList = new ArrayList<String>();
		Document document = null;
        try {
			document = reader.read(filePath);
			processList = getProcessList(document);
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error("Exception occured while parsing the workflow definition file", e);
		}
        
        return processList;
	}
	
	/**
	 * 根据Document对象所代表的DOM的信息，构造出一个List<ProcessDef>对象
	 * @param doc 需要读取的XML文件的Document对象
	 * @return 流程定义文件中定义的多个流程列表
	 * @throws PluginTypeNotSupportException 
	 * @throws InvalidConfigurationException 
	 */
	@SuppressWarnings("rawtypes")
	public List<ProcessDef> buildProcessDefs(Document doc) 
			throws InvalidConfigurationException {
		List<ProcessDef> processDefList = new ArrayList<ProcessDef>();
		Element root = doc.getRootElement();
		for(Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element configElement = (Element)iterator.next();
			
			/*当该<config>元素的type属性是com.ifeng.common.workflow.ActivityDefinition
			    该元素对应一个流程定义。
			*/
			if("com.ifeng.common.workflow.ProcessDefinition".
					equals(configElement.attributeValue("type"))) {
				ProcessDef processDef = new ProcessDef();
				processDef.setName(configElement.attributeValue("name"));
				processDef.setTitle(configElement.element("title").getText());
				processDef.setDescription(configElement.element("description").
						getText());
				/*Map<String, ActivityDef> activitiesMap = new 
						HashMap<String, ActivityDef>();*/
				
				List<ActivityDef> activitiesList = new ArrayList<ActivityDef>();
				//遍历ProcessDefinition下面的所有<Activity>
				for (Iterator activitiesIterator = configElement.elementIterator("activity")
						;activitiesIterator.hasNext();) {
					Element activityElement = (Element) activitiesIterator.next();
					ActivityDef activityDef = new ActivityDef();
					String activityName = activityElement.attributeValue("name");
					String activityTitle = activityElement.element("title").getText();
					String activityDesc = activityElement.element("description").getText();
					Element pluginElement = activityElement.element("plugin");
					PluginDef pluginDef = constructPluginByType(pluginElement,
							pluginElement.attributeValue("type"));
					activityDef.setName(activityName);
					activityDef.setTitle(activityTitle);
					activityDef.setDescription(activityDesc);
					activityDef.setPlugin(pluginDef);
					//activitiesMap.put(activityName, activityDef);
					activitiesList.add(activityDef);
				}
				processDef.setActivitiesList(activitiesList);
				processDefList.add(processDef);
			}
		}
		
		return processDefList;
	}

	/**
	 * 重载的buildProcessDef方法，可以接受一个URL对象作为参数
	 * @param url
	 * @return 流程定义文件中定义的多个流程列表
	 * @throws PluginTypeNotSupportException 
	 * @throws InvalidConfigurationException 
	 * @throws ConfigurationFileNotFoundException 
	 */
	public List<ProcessDef> buildProcessDefs(URL url) 
			throws InvalidConfigurationException, 
			ConfigurationFileNotFoundException {
		if (url == null) {
			throw new ConfigurationFileNotFoundException(
					"The config file can not be located, please check again!");
		}
		SAXReader reader = new SAXReader();
		Document document = null;
        try {
			document = reader.read(url);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
        return this.buildProcessDefs(document);
	}
	
	/**
	 * 重载的buildProcessDef方法，可以接受一个File对象作为参数
	 * @param file
	 * @return 流程定义文件中定义的多个流程列表
	 * @throws PluginTypeNotSupportException 
	 * @throws InvalidConfigurationException 
	 * @throws ConfigurationFileNotFoundException 
	 */
	public List<ProcessDef> buildProcessDefs(File file) 
			throws InvalidConfigurationException, 
			ConfigurationFileNotFoundException {
		if (file == null) {
			throw new ConfigurationFileNotFoundException(
					"The config file can not be located, please check again!");
		}
		SAXReader reader = new SAXReader();
		Document document = null;
        try {
			document = reader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
        return this.buildProcessDefs(document);
	} 
	
	/**
	 * 获取流程定义文件的所有历史版本的文件名
	 * @param path 所有版本流程定义文件所在的父目录的路径
	 * @return
	 * @throws Exception 
	 */
	public List<String> getXMLHistory(String path) throws Exception {
		List<String> hisVersions = new ArrayList<String>();
		File dir = new File(path);
		if (! dir.isDirectory()) {
			throw new Exception(path + " is not a directory");
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
	 * 一旦流程定义出现错误、导致icommon无法识别现有的流程定义文件，可以将当前
	 * 流程定义文件恢复到最近的历史版本。
	 * @param path 流程定义文件所在的目录
	 * @throws Exception 
	 */
	public void restoreToLatestVersion(String path, String currentVersion) {
		List<String> hisVersions;
		long latest = 0L;
		String latestVersion = null;
		try {
			hisVersions = getXMLHistory(path);
			for(String file : hisVersions) {
				long timestamp = Long.parseLong(
						file.substring(file.length()-20, file.length()-3));
				if (timestamp > latest) {
					latest = timestamp;
					latestVersion = file;
				}
			}
			restoreToSpecifiedVersion(path, currentVersion, latestVersion);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} 
	}
	
	/**
	 * 将当前版本的流程定义文件恢复到指定历史版本
	 * @param path
	 * @param currentVersion
	 * @param specifiedVersion
	 */
	public void restoreToSpecifiedVersion(String path, String currentVersion, 
			String specifiedVersion) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		byte[] buf = new byte[1024];
		try {
			fin = new FileInputStream(path + specifiedVersion);
			fout = new FileOutputStream(path + currentVersion);
			int byteReaded = 0;
			while ((byteReaded = fin.read(buf)) != -1) {
				fout.write(buf, 0, byteReaded);
			}
			fout.flush();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			try {
				if (fin != null) 
					fin.close();
				if (fout != null) 
					fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
}