package com.me.GCDP.workflow.plugin;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.exception.ExecutionTimesOverproofException;
import com.me.GCDP.workflow.exception.InvalidConfigurationException;
import com.me.GCDP.workflow.model.PluginStatus;
import com.me.GCDP.script.ScriptType;


@SuppressWarnings({"unchecked","rawtypes"})
/**
 * 执行Condition类型中条件判断脚本的插件
 * @author xiongfeng
 *
 */
public class ConditionScriptPlugin extends AbstractScriptPlugin{
	private static Log log = LogFactory.getLog(ConditionScriptPlugin.class);
	
	public ConditionScriptPlugin() {
		getMaxExecutionTimesFromConfig();
	}

	/**
	 * 覆盖execute方法
	 */
	public Object execute(Object context) {
		if (isExecutionTimeExceeded() == false) {
			try {
				throw new ExecutionTimesOverproofException("Plugin Execution Times Overproof!");
			} catch (ExecutionTimesOverproofException e) {
				log.error("Plugin Execution Times Overproof. PluginId = " + pluginDefId, e);
			}
			return context;
		}
		CmppProcessContext processContext = (CmppProcessContext) context;
		prepareData();
		
		PluginStatus pluginStatus = initPluginStatus(processContext);
		log.info("Plugin " + pluginDefId + " starts to run");
		Map dataMap = processContext.getData();
		Map<String, Object> dataPool = (Map<String, Object>) dataMap.get("dataPool");
		try {
			if (idsArr != null) {
				scriptService.run(Integer.parseInt(nodeId), dataPool, 0, 
						ScriptType.getInstance("process"), idsArr);
			}else {
				throw new InvalidConfigurationException("ids attribute of " +
						"this plugin was not set");
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occured when running the script " + pluginDefId, e);
			doExceptionAlarm(processContext, e);
			try {
				if (exceptionidsArr != null) {
					scriptService.run(Integer.parseInt(nodeId), dataPool, 0, 
							ScriptType.getInstance("process_exception"), exceptionidsArr);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error("Exception occured when executing the exception-handle script " + pluginDefId, e1);
			}
			modifyExceptionPluginStatus(pluginStatus);
			processContext.setData(dataMap);
			log.info("Plugin " + pluginDefId + " ends running with exception");
			return processContext;
		}

		/**
		 * 获取条件判断脚本的执行结果
		 */
		Boolean flag = (Boolean)dataPool.get("flag");
		dataPool.remove("flag");
		modifyNormalTerminatedPluginStatus(pluginStatus);
		processContext.setData(dataMap);
		log.info("Plugin " + pluginDefId + " ends running");
		return flag;
	}
}