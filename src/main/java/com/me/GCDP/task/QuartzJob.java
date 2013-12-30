/**
 * 
 */
package com.me.GCDP.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

import com.me.GCDP.mapper.TaskMapper;
import com.me.GCDP.model.Task;
import com.me.GCDP.model.TaskInfo;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.RedisPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;

/**
 * @author zhangzy
 * 2011-11-3
 */
public class QuartzJob implements StatefulJob,InterruptableJob  {
	
	private static Log log = LogFactory.getLog(QuartzJob.class);
	private ScriptService scriptService = null;
	private TaskMapper<Task> taskMapper = null;
	private static Thread currentThread=null;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0)
			throws JobExecutionException {
		if(scriptService == null){
			scriptService = (ScriptService) SpringContextUtil.getBean("scriptService");
		}
		if(taskMapper == null){
			taskMapper = (TaskMapper<Task>) SpringContextUtil.getBean("taskMapper");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-mm HH:mm:ss");
		String prefix = CmppConfig.getKey("cmpp.nosql.prefix");
		ScriptPluginFactory pluginFactory = (ScriptPluginFactory) 
				SpringContextUtil.getBean("pluginFactory");
		RedisPlugin redis = (RedisPlugin) pluginFactory.getP("redis");
		Task task = (Task)arg0.getJobDetail().getJobDataMap().get("task");
		String infoStr = "";
		try {
			infoStr = redis.get(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup());
		} catch (Exception e1) {
			log.error(e1);
		}
		JSONObject infoObj = JSONObject.fromObject(infoStr);
		//MorpherRegistry是单例的，但是这里为了保险起见，重复register操作
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss"}));
		TaskInfo info = (TaskInfo) JSONObject.toBean(infoObj, TaskInfo.class);
		long start=System.currentTimeMillis();
		try {	
			currentThread=Thread.currentThread();
			task.setStatus(2);
			taskMapper.update(task);
			task.setPreviousFireTime(arg0.getPreviousFireTime());
			scriptService.run(task.getNodeId(),null,ScriptType.task,task.getId().toString());
			
		} catch (Exception e) {
			if (info != null) {
				info.setErrCount(info.getErrCount()+1);
				info.setStatus("脚本有误，请确认脚本");
				info.setLastErrTime(new Date(System.currentTimeMillis()));
				putErrInfo(info.getErrInfo(),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()))+"-->"+e.getMessage());
			}
			log.error(e.getMessage(),e);
		}finally{
			long end=System.currentTimeMillis();
			task.setRunCount(task.getRunCount()+1);
			task.setNextFireTime(arg0.getNextFireTime());
			task.setStatus(1);
			taskMapper.update(task);
			if (info != null) {
				info.setRunTime((end-start));
				info.setPreviousFireTime(arg0.getFireTime());
				info.setNextFireTime(arg0.getNextFireTime());
				info.setRunCount(task.getRunCount());
				try {
					redis.set(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup(), info.toString());
				} catch (Exception e) {
					log.error(e);
				}
			}
			
		}
	}
	private void putErrInfo(List<String> list,String info){
		if(list.size()==5){
			for(int i=0;i<list.size()-1;i++){
				list.set(i, list.get(i+1));
			}
			list.set(list.size()-1, info);
		}else{
			list.add(info);
		}
	}
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.info("INTERRUPTING JOB...|"+currentThread.getId());
		currentThread.interrupt();
	}

	
}
