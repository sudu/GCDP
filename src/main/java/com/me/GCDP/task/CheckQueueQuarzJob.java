package com.me.GCDP.task;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;

import com.me.GCDP.model.Task;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.plugin.RedisPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;

/**
 * 后台检查Redis中的新增任务以及删除任务队列的Job
 * @author xiongfeng
 *
 */
public class CheckQueueQuarzJob implements StatefulJob,InterruptableJob{
	private static Log log = LogFactory.getLog(QuartzJob.class);
	private ScriptPluginFactory factory = null;
	@Override
	public void execute(JobExecutionContext quarzCtx) throws JobExecutionException {
		if (factory == null) {
			factory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		}
		RedisPlugin redis = (RedisPlugin) factory.getP("redis");
		String prefix = CmppConfig.getKey("cmpp.nosql.prefix");
		SchedulerFactory schedulerFactory = null;
		Scheduler scheduler = null;
		try {
			schedulerFactory = new StdSchedulerFactory("quartz.properties");
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
			
			while (redis.llen(prefix + "_del_queue") > 0) {
				String taskStr = redis.rpop(prefix + "_del_queue");
				JSONObject taskObj = JSONObject.fromObject(taskStr);
				JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss"}));
				Task task = (Task) JSONObject.toBean(taskObj, Task.class);
				scheduler.deleteJob(task.getTaskName(), task.getTaskGroup());
			}
			
			while (redis.llen(prefix + "_add_queue") > 0) {
				String taskStr = redis.rpop(prefix + "_add_queue");
				JSONObject taskObj = JSONObject.fromObject(taskStr);
				//MorpherRegistry是单例的，但是这里为了保险起见，重复register操作
				JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss"}));
				Task task = (Task) JSONObject.toBean(taskObj, Task.class);
				JobDetail jobDetail = new JobDetail(task.getTaskName(),task.getTaskGroup(),QuartzJob.class);
				JobDataMap map=new JobDataMap();
				map.put("task", task);
				jobDetail.setJobDataMap(map);
				SimpleTrigger simpleTrigger=new SimpleTrigger(task.getTaskName(),task.getTaskGroup());
				simpleTrigger.setStartTime(task.getStartTime());
				simpleTrigger.setEndTime(task.getEndTime());
				simpleTrigger.setJobGroup(task.getTaskGroup());
				simpleTrigger.setJobName(task.getTaskName());
				simpleTrigger.setRepeatInterval(task.getRepeatInterval()*1000*60);
				if(task.getRepeatCount()<=0){
					simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
				}else{
					simpleTrigger.setRepeatCount(task.getRepeatCount()-1);
				}
				scheduler.addJob(jobDetail, true);
				scheduler.scheduleJob(simpleTrigger);
			}
			
			
		} catch (SchedulerException e1) {
			log.error(e1);
		} catch (Exception e) {
			log.error(e);
		}
		
			
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		
	}
	
}
