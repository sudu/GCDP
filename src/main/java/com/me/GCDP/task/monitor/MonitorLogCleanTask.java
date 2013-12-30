package com.me.GCDP.task.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.util.SpringContextUtil;

public class MonitorLogCleanTask extends QuartzJobBean implements StatefulJob{
	
	private static Log log = LogFactory.getLog(MonitorLogCleanTask.class);
	
	public enum CleanType {  
		  LOG_TABLE, ERROR_TABLE, IDLE
	}
	
	@SuppressWarnings("rawtypes")
	private MonitorMapper mapper = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");
	
	private SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		int result = 0;
		String liveTime = (String) context.getJobDetail().getJobDataMap().get("logLiveTime");
		
		CleanType currentJob = CleanType.IDLE;
		
		//设定日志有效时间 默认为 30 天 1000*60*60*24*30L
		long cleanDateTime = (new Date()).getTime() - 86400000*Long.parseLong(liveTime);
		
		String cleanDate = tempDate.format(cleanDateTime);
		
		try{
			
			currentJob = CleanType.LOG_TABLE;
			result = mapper.cleanLog(cleanDate);
			log.info("Log	table cleaned: " + result + " record(s) deleted.");
			
			currentJob = CleanType.ERROR_TABLE;
			result = mapper.cleanErr(cleanDate);
			log.info("Error	table cleaned: " + result + " record(s) deleted.");
		
		}catch(Exception e){
			log.error("An error occurs while in cleaning " + currentJob);
		}
	}

}
