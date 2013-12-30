/**
 * 
 */
package com.me.GCDP.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;

import com.me.GCDP.mapper.TaskMapper;
import com.me.GCDP.model.Task;
import com.me.GCDP.model.TaskInfo;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.plugin.RedisPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;

/**
 * @author zhangzy
 * 任务工具类
 * 2011-7-15
 */
public class TaskServer {
	private static Log log = LogFactory.getLog(TaskServer.class);
	private ScriptService scriptService = null;
	
	private Scheduler scheduler;
	private SimpleTrigger simpleTrigger;
	
	private TaskMapper<Task> taskMapper = null;
	
	public void init() throws SchedulerException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-mm HH:mm:ss");
		String daemonize = CmppConfig.getKey("cmpp.daemonize");
		SchedulerFactory schedulerFactory = new StdSchedulerFactory("quartz.properties");
        scheduler = schedulerFactory.getScheduler();
		scheduler.start();
		if("yes".equalsIgnoreCase(daemonize) //专门用来执行计划任务
				|| "y".equalsIgnoreCase(daemonize)) {
			List<Task> tasks=taskMapper.getStartTask();//状态为启动的任务
			for(Task task : tasks){
				JobDetail jobDetail= new JobDetail(task.getTaskName()
						,task.getTaskGroup(),QuartzJob.class);
				JobDataMap map=new JobDataMap();
				map.put("task", task);
				jobDetail.setJobDataMap(map);
				simpleTrigger=new SimpleTrigger(task.getTaskName(),task.getTaskGroup());
				Date nextFireTime = task.getNextFireTime();
				if(nextFireTime == null){
					nextFireTime=new Date(System.currentTimeMillis()+task.getRepeatInterval()*1000*60);
				}
				if(nextFireTime.after(task.getEndTime())){
					simpleTrigger.setStartTime(task.getStartTime());
				}else{
					simpleTrigger.setStartTime(nextFireTime);
				}
				simpleTrigger.setEndTime(task.getEndTime());
				simpleTrigger.setJobGroup(task.getTaskGroup());
				simpleTrigger.setJobName(task.getTaskName());
				simpleTrigger.setRepeatInterval(task.getRepeatInterval()*1000*60);
				if(task.getRepeatCount()<=0){
					simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
				}else{
					if(task.getRepeatCount()-task.getRunCount()<=0){
						continue;
					}else{
						simpleTrigger.setRepeatCount(task.getRepeatCount()-task.getRunCount());
					}
				}
				scheduler.addJob(jobDetail, true);
				scheduler.scheduleJob(simpleTrigger);
			}
			
			JobDetail checkQueueJob = new JobDetail("checkQueue", 
					"daemonService", CheckQueueQuarzJob.class);
			SimpleTrigger checkQueueTrigger = new SimpleTrigger("checkQueue", "daemonService");
			checkQueueTrigger.setStartTime(new Date(System.currentTimeMillis()));
			checkQueueTrigger.setJobName("checkQueue");
			checkQueueTrigger.setJobGroup("daemonService");
			checkQueueTrigger.setRepeatCount(-1);
			try {
				checkQueueTrigger.setEndTime(sdf.parse("2050-12-31 23:59:59"));
			} catch (ParseException e) {
				log.error("Parse date error(TaskServer.init() daemonize)");
			}
			checkQueueTrigger.setRepeatInterval(30000);
			scheduler.addJob(checkQueueJob, true);
			scheduler.scheduleJob(checkQueueTrigger);
			
		}else if("no".equalsIgnoreCase(daemonize) //正常的Cmpp工程
				|| "n".equalsIgnoreCase(daemonize)) {
			
			
		}
	}
	
	
	/**
	 * 1.不立即启用定时任务，而是将新添加任务的信息放入Redis队列中
	 * 2.不使用缓存，将TaskInfo的信息以hash结构放进Redis中
	 * @throws Exception 
	 */
	public void addTask(Task task) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-mm HH:mm:ss");
		//往Redis队列中添加一个新的任务
		ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		RedisPlugin redis = (RedisPlugin) pluginFactory.getP("redis");
		String prefix = CmppConfig.getKey("cmpp.nosql.prefix");
		try {
			redis.lpush(prefix + "_add_queue", task.toString());
		} catch (Exception e) {
			log.error("Error lpush add task.taskId" + task.getId(), e);
		}
		
		//将TaskInfo信息加入Redis中
		TaskInfo info = new TaskInfo();
		info.setTaskName(task.getTaskName());
		info.setNextFireTime(task.getNextFireTime());
		info.setStatus("已经启用");
		redis.set(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup(), info.toString());
	}
	
	/**
	 * 1.将需要删除的Task放入删除队列中
	 * 2.从Redis中删除掉之前已经缓存的TaskInfo的信息
	 */
	public void deleteTask(Task task) throws SchedulerException{
		//往删除队列中添加Task
		ScriptPluginFactory pluginFactory = (ScriptPluginFactory) 
				SpringContextUtil.getBean("pluginFactory");
		RedisPlugin redis = (RedisPlugin) pluginFactory.getP("redis");
		String prefix = CmppConfig.getKey("cmpp.nosql.prefix");
		try {
			redis.lpush(prefix + "_del_queue", task.toString());
		} catch (Exception e) {
			log.error("Error lpush del task.taskId" + task.getId(), e);
		}
		/* 从Redis中删除TaskInfo信息,相当于过去的oscache.remove(obj)
		 */
		try {
			redis.del(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup());
		} catch (Exception e) {
			log.error(e);
		}
		
	}

	
	public boolean pauseTask(Task task){
		try {
			scheduler.pauseJob(task.getTaskName(), task.getTaskGroup());
			return true;
		} catch (SchedulerException e) {
			log.error(e);
			return false;
		}
	}
	
	public boolean resumeTask(Task task){
		try {
			scheduler.resumeJob(task.getTaskName(), task.getTaskGroup());
			return true;
		} catch (SchedulerException e) {
			log.error(e);
			return false;
		}
	}
	
	
	/**
	 * 
	 * @Description:中断正在进行的任务
	 * @param task
	 * @return boolean
	 * @throws
	 */
	public boolean interrupt(String taskName,String taskGroup){
		try {
			return scheduler.interrupt(taskName, taskGroup);
		} catch (UnableToInterruptJobException e) {
			log.error(e);
		}
		return false;
	}
	

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public TaskMapper<Task> getTaskMapper() {
		return taskMapper;
	}

	public void setTaskMapper(TaskMapper<Task> taskMapper) {
		this.taskMapper = taskMapper;
	}
}
