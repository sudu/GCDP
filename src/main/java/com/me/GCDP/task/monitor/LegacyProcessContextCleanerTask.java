package com.me.GCDP.task.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.workflow.mapper.PluginStatusMapper;
import com.me.GCDP.workflow.mapper.ProcessInstanceMapper;
import com.me.GCDP.workflow.model.IndexRange;

public class LegacyProcessContextCleanerTask extends BaseCheckTask {
	
	private static Log log = LogFactory.getLog(LegacyProcessContextCleanerTask.class);
	
	private ProcessInstanceMapper pcMapper = (ProcessInstanceMapper) SpringContextUtil.getBean("processInstanceMapper");
	
	private PluginStatusMapper mapper = (PluginStatusMapper) SpringContextUtil.getBean("pluginStatusMapper");

	@Override
	protected void runCheck(JobExecutionContext context) {
		
		long start = System.currentTimeMillis();
		String interval = CmppConfig.getKey("workflow.clean_interval");
		int intervalInt = Integer.parseInt(interval);
		log.info("Clean legacy process " + interval + " days ago.");
		if(intervalInt > 0) {
			try {
				IndexRange range = pcMapper.getLegacyInstanceRange(intervalInt);
				log.info("Delete process instances.lowInde="
						+ range.getLowIndex() + ",highIndex="
						+ range.getHighIndex() + ",count=" + range.getCount());
				mapper.deleteByPCIdRange(range);
				pcMapper.deleteProcessInstanceByIdRange(range);
				long end = System.currentTimeMillis();
				log.info("Clean legacy process.Time:" + (end - start) + " ms");
			} catch (Exception e) {
				log.error(e);
			}
			
		}
	}
}
