package com.me.GCDP.workflow.plugin;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.exception.InvalidConfigurationException;
import com.me.GCDP.workflow.model.PluginStatus;
import com.me.GCDP.script.ScriptType;

@SuppressWarnings({"unchecked","rawtypes"})
/**
 * 执行Switch类型条件判断脚本的插件
 * @author xiongfeng
 *
 */
public class SwitchConditionScriptPlugin extends AbstractScriptPlugin {
	private static final Log log = LogFactory.getLog(SwitchConditionScriptPlugin.class);
	
	public SwitchConditionScriptPlugin() {
		getMaxExecutionTimesFromConfig();
	}
	
	
	@Override
	public Object execute(Object context) {
//		if (isExecutionTimeExceeded() == false) {
//			try {
//				throw new ExecutionTimesOverproofException("Plugin Execution Times Overproof!");
//			} catch (ExecutionTimesOverproofException e) {
//				log.error("Plugin Execution Times Overproof. PluginId = " + pluginDefId, e);
//			}
//			return context;
//		}
		CmppProcessContext processContext = (CmppProcessContext) context;
		prepareData();
		PluginStatus pluginStatus = initPluginStatus(processContext);
		
//		log.info("Plugin " + pluginDefId + " starts to run");
		Map dataMap = processContext.getData();
		Map<String, Object> dataPool = (Map<String, Object>) dataMap.get("dataPool");
		
		/**
		 * 将当前活动id和插件id放入dataPool中
		 */
		dataPool.put("__curActivity__", processContext.getActivity());
		dataPool.put("__curPlugin__", pluginDefId);
		processContext.setData(dataMap);//手动更新一下ProcessContext中的data
		try {
			if (idsArr != null) {
				scriptService.run(Integer.parseInt(nodeId), dataPool, 0, 
						ScriptType.getInstance("process"), idsArr);
			}else {
				throw new InvalidConfigurationException("ids attribute " +
						"of this plugin was not set");
			}
		}catch (Exception e) {
			log.error("Exception occured when running plugin " + pluginDefId, e);
			doExceptionAlarm(processContext, e);
			try {
				if (exceptionidsArr != null) {
					scriptService.run(Integer.parseInt(nodeId), dataPool, 0, 
							ScriptType.getInstance("process_exception"), exceptionidsArr);
				}
				
			} catch (Exception e1) {
				log.error("Exception occured when executing the exception-handle script of " 
							+ pluginDefId, e1);
			}
			
			modifyExceptionPluginStatus(pluginStatus);
			processContext.setData(dataMap);
			processContext.setState(5);
			log.info("Plugin " + pluginDefId + " ends running without exception");
			return processContext;
		}
		
		//返回条件switch条件
		String condition = (String)dataPool.get("__condition__");
		dataPool.remove("__condition__");
		
		//没有出现异常，修改流程状态
		modifyNormalTerminatedPluginStatus(pluginStatus);
		
		//每次ProcessContext中的data属性发生变化的时候都需要显式调用该方法
		processContext.setData(dataMap);
//		log.info("Plugin " + pluginDefId + " ends running");
		return condition;
	}
}
