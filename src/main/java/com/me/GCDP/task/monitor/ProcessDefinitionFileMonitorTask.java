package com.me.GCDP.task.monitor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.workflow.WorkflowImpl;
import com.me.GCDP.workflow.WorkflowService;
import com.me.GCDP.workflow.service.XMLToObjectService;
import com.ifeng.common.conf.ConfigRoot;
import com.me.GCDP.script.plugin.CmppDBPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;

/*
 *@version 1.1 将cmppDBService替换成了cmppDBPlugin by chengds at 2013/8/28
 */
public class ProcessDefinitionFileMonitorTask extends BaseCheckTask {

	private static Log log = LogFactory.getLog(ProcessDefinitionFileMonitorTask.class);
	
	private ConfigRoot configRoot = null;
	
	private Map map = new HashMap();
	
	private XMLToObjectService _service = new XMLToObjectService();
	
	@Override
	protected void runCheck(JobExecutionContext context) {
		log.info("ProcessDefinitionFileMonitorTask.runCheck()");
//		InputStream in = WorkflowService.class.getResourceAsStream("/conf/workflow.properties");
//		Properties p = new Properties();
//		try {
//			p.load(in);
//		} catch (IOException e) {
//			log.error("Exception occured while read properties to get flow definition file path", e);
//		}
		String workflowDefFile = CmppConfig.getKey("workflow.workflowfile.dir") 
				+ CmppConfig.getKey("workflow.workflowfile.filename");
		String baseUrl = CmppConfig.getKey("cmpp.daemonize.baseurl") == null 
				? "" : CmppConfig.getKey("cmpp.daemonize.baseurl");
		File f = new File(workflowDefFile);
		/*cds modify at 2013/08/28  用CmppDB插件替换CmppDBClientService */
		ScriptPluginFactory scriptPluginFactory = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
		CmppDBPlugin cmppDBPlugin = (CmppDBPlugin)scriptPluginFactory.getP("cmppDB");
		//CmppDBClientService nosql = (CmppDBClientService) SpringContextUtil.getBean("cmppDBService");
		long lastModified = 0L;
		Object lastModifiedValue = cmppDBPlugin.get("lastmodify:" + workflowDefFile + baseUrl);
		if (lastModifiedValue != null) {
			//lastModified = (Long) nosql.get("lastmodify:" + workflowDefFile + baseUrl);
			lastModified = (Long)lastModifiedValue;//cds add
		}
		if (lastModified != f.lastModified()) {
			log.info("lastModified:" + lastModified);
			configRoot = new ConfigRoot(workflowDefFile, null);
			List<String> processNames = _service.getProcessList(workflowDefFile);
			log.info("Reading process definition file: " + workflowDefFile);
			map.clear();
			for(String process : processNames) {
				map.put(process, configRoot.getValue(process));
			}
			WorkflowService workflowService = (WorkflowService) SpringContextUtil
														.getBean("workflowService");
			((WorkflowImpl)workflowService.getWf()).setProcessDefinitionMap(map);
			//boolean putFlag = nosql.put("lastmodify:" + workflowDefFile + baseUrl, f.lastModified());
			boolean putFlag = cmppDBPlugin.put("lastmodify:" + workflowDefFile + baseUrl, f.lastModified());//cds add
			if (putFlag) {
				log.info("Put lastModified to nosql:" + f.lastModified());
			}else {
				log.info("Put lastModified to nosql:" + f.lastModified() + " failed!");
			}
			
		}
	}
}
