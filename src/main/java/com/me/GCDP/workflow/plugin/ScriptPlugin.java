package com.me.GCDP.workflow.plugin;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.WorkflowImpl;
import com.me.GCDP.workflow.WorkflowService;
import com.me.GCDP.workflow.exception.InvalidConfigurationException;
import com.me.GCDP.workflow.model.PluginStatus;
import com.ifeng.common.plugin.core.itf.IntfPlugin;
import com.ifeng.common.workflow.ActivityDefinition;
import com.ifeng.common.workflow.ProcessDefinition;
import com.me.GCDP.script.ScriptType;


/**
 * 用于在流程中执行脚本的插件类
 * @author xiongfeng
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ScriptPlugin extends AbstractScriptPlugin {
	static Log log = LogFactory.getLog(ScriptPlugin.class);
	private String nextActivity = null;
	
	public ScriptPlugin() {
		getMaxExecutionTimesFromConfig();
	}
	
	/**
	 * 当出现异常时，根据配置进行路由(继续下一个活动)
	 * @param processContext
	 */
	private void doTransistOnException(CmppProcessContext processContext) {
		Map dataMap = processContext.getData();
		Map dataPool = (Map) dataMap.get("dataPool");
		Boolean isPending = dataPool.get("__isPending__") == null ? Boolean.FALSE : (Boolean) dataPool.get("__isPending__");
		//如果当前实例已经挂起
		if (isPending) {
			processContext.setNextActivity(null);
			dataPool.put("__nextActivity__", nextActivity);
		}else {
			if (suspendsOnException != null && !suspendsOnException.trim().equals("")) {
				suspendsOnExceptionFlag = Boolean.parseBoolean(suspendsOnException);
			}
			if (!suspendsOnExceptionFlag) {
				if (nextActivity != null) {
					processContext.setNextActivity(nextActivity);
				}
			}
		}
		
	}
	
	
	/**
	 * 执行子流程中的内容
	 * @param processContext
	 */
	private void executeSubRoutine(CmppProcessContext processContext) {
		if (subRoutine != null && !"".equals(subRoutine.trim())) {
			ActivityDefinition aDefinition =((ProcessDefinition)((WorkflowImpl)((WorkflowService)SpringContextUtil
					.getBean("workflowService")).getWf()).getProcessDefinitionMap().get(processContext.getProcessDefinitionName())).getActivity(subRoutine);
			IntfPlugin plugin = aDefinition.getPlugin();
			if (plugin != null) {
				plugin.execute(processContext);
			}
		}
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
				throw new InvalidConfigurationException("ids attribute of " +
						"this plugin was not set");
			}
		}catch (Exception e) {
			log.error("Exception occured when running the plugin:" + pluginDefId, e);
			doExceptionAlarm(processContext, e);
			try {
				if (exceptionidsArr != null) {
					scriptService.run(Integer.parseInt(nodeId), dataPool, 0, 
							ScriptType.getInstance("process_exception"), exceptionidsArr);
				}
			} catch (Exception e1) {
				log.error("Exception occured when executing the exception-handle script " + pluginDefId, e1);
			}
			doTransistOnException(processContext);
			modifyExceptionPluginStatus(pluginStatus);
//			log.info("Plugin " + pluginDefId + " ends running with exception");
			processContext.setData(dataMap);
			processContext.setState(5);
//			return processContext;
			return null;
		}
		//finally {
			executeSubRoutine(processContext);
		//}
		
		
		if (dataPool.get("__isPending__") == null ? Boolean.FALSE : (Boolean) dataPool.get("__isPending__")) {
			processContext.setNextActivity(null);
			dataPool.put("__nextActivity__", nextActivity);
			modifyPendingPluginStatus(pluginStatus);
		}else {
			if (nextActivity != null) {
				processContext.setNextActivity(nextActivity);
			}
			modifyNormalTerminatedPluginStatus(pluginStatus);
		}
		processContext.setData(dataMap);
//		log.info("Plugin " + pluginDefId + " ends running without exception");
		return processContext;
	}
	
	public void setNextActivity(String nextActivity) {
		this.nextActivity = nextActivity;
	}

	public String getNextActivity() {
		return nextActivity;
	}
	
	
}