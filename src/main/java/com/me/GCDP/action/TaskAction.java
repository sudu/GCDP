package com.me.GCDP.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.quartz.SchedulerException;

import com.me.GCDP.mapper.TaskMapper;
import com.me.GCDP.model.Task;
import com.me.GCDP.model.TaskInfo;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.task.TaskServer;
import com.me.GCDP.util.JsonUtils;
import com.me.GCDP.util.Page;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.RedisPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author zhangzy
 * 2011-7-13
 */
public class TaskAction extends ActionSupport{
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(TaskAction.class);
	private int id;
	private String ids;
	private Task task;
	private List<Task> taskList;
	private int nodeId;
	private TaskMapper<Task> taskMapper = null;
	private TaskServer  taskUtil= null;
	private TaskInfo info=null;
	
	private ScriptService scriptService = null;
	
	private String msg = null;
	private Boolean hasError = false;
	
	
	/**
	 * 
	 * @Description:任务列表
	 * @param @return   
	 * @return String
	 * @throws
	 */
//	public String list(){
//		taskList=taskMapper.getTaskList(nodeId);
//		return "list";
//	}
	
	public void runningList(){
		taskList=taskMapper.getTaskList(nodeId);
		Page<Task> p=new Page<Task>(taskList,taskList.size());
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");// 设置字符集编码
		try {
			JsonUtils.write(p, response.getWriter());
		} catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	
	public String showCheckTaskPage(){
		return "running_task_list";
	}
	
	/**
	 * 
	 * @Description:停止任务
	 * @param    
	 * @return void
	 * @throws
	 */
	public void stopTask(){
		String taskGroup=ServletActionContext.getRequest().getParameter("taskGroup");
		String taskName=ServletActionContext.getRequest().getParameter("taskName");
		taskUtil.interrupt(taskName, taskGroup);
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().print("{success:true,msg:'成功'}");
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * 
	 * @Description:删除任务
	 * @param    
	 * @return void
	 * @throws
	 */
	public String deleteTask(){
		Task t=taskMapper.getTaskById(id);
		try {
			taskUtil.deleteTask(t);
			taskMapper.delete(t);
		} catch (SchedulerException e1) {
			log.error(e1.getMessage());
			msg = e1.getMessage();
			id=task.getId();
			hasError=true;
			return "result";
		}
		msg="删除成功";
		return "result";
	}
	
	/**
	 * 
	 * @Description:添加任务
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String add(){
		task.setTaskGroup(task.getTaskName()+"_"+(taskMapper.getLastId()+1));
		Date startTime = task.getStartTime();
		if(startTime==null){
			startTime = new Date();
			task.setStartTime(startTime);
		}
		Date endTime = task.getEndTime();
		if(endTime==null){
			endTime = new Date();
			task.setEndTime(endTime);
		}
		
		if(startTime.getTime() > endTime.getTime()){
			msg = "End time cannot be before start time";
			hasError = true;
			return "result";
		}
		
		//获取
		task.setCreator(AuthorzationUtil.getUserName());
		task.setLastModifyTime(new Date());
		
		try{
			taskMapper.insert(task);
            if(StringUtils.isNotBlank(task.getScript()))
			    scriptService.save(nodeId,task.getScript(),ScriptType.task,task.getId().toString());
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			id=task.getId();
			return "result";
		}
		if(task.getStatus()==1){
			this.run(task);
		}else{
			msg = "任务添加成功";
			id=task.getId();
		}
		return "result";
	}
	
	
	/**
	 * 
	 * @Description:查看任务
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String view(){
		task=taskMapper.getTaskById(id);
		log.info("Query task from db.id=" + id);
		try {
			String script=scriptService.openLatest(task.getNodeId(),ScriptType.task,task.getId().toString());
			task.setScript(script);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		RedisPlugin redis = (RedisPlugin) pluginFactory.getP("redis");
		String prefix = CmppConfig.getKey("cmpp.nosql.prefix");
		String infoStr = "";
		try {
			infoStr = redis.get(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup());
		} catch (Exception e) {
			log.error(e);
		}
		log.info("Get taskinfo from redis.key=" + (prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup()));
		JSONObject infoObj = JSONObject.fromObject(infoStr);
		//MorpherRegistry是单例的，但是这里为了保险起见，重复register操作
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss"}));
		info = (TaskInfo) JSONObject.toBean(infoObj, TaskInfo.class);
		log.info("Convert json to taskinfo");
		if (info==null) {
			TaskInfo ti=new TaskInfo();
			ti.setTaskName(task.getTaskName());
			try {
				redis.set(prefix + "_" + task.getTaskName() + "_" + task.getTaskGroup(), ti.toString());
			} catch (Exception e) {
				log.error(e);
			}
			info = ti;
		}
		return "view";
	}
	
	/**
	 * 
	 * @Description:更新任务
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String update(){
		
		Date startTime = task.getStartTime();
		if(startTime==null){
			startTime = new Date();
			task.setStartTime(startTime);
		}
		Date endTime = task.getEndTime();
		if(endTime==null){
			endTime = new Date();
			task.setEndTime(endTime);
		}
		
		if(startTime.getTime() > endTime.getTime()){
			msg = "End time cannot be before start time";
			hasError = true;
			return "result";
		}
		
		Task t=taskMapper.getTaskById(task.getId());
		try {
			taskUtil.deleteTask(t);
		} catch (SchedulerException e1) {
			log.error(e1.getMessage());
			msg = e1.getMessage();
			id=task.getId();
			hasError=true;
			return "result";
		}	
		task.setLastModifyTime(new Date());
		task.setTaskGroup(t.getTaskGroup());
		task.setRunCount(t.getRunCount());
		try {
            if(StringUtils.isNotBlank(task.getScript())){
                scriptService.saveDebug(nodeId,task.getScript(),ScriptType.task,task.getId().toString());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			taskMapper.update(task);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			id=task.getId();
			hasError = true;
			return "result";
		}
		if(task.getStatus()==1||task.getStatus()==2){
			this.run(task);
		}else{
			msg = "任务更新成功";
			id=task.getId();
		}
		return "result";
	}
	
	
	private void run(Task task) {
		try {
			task.setNodeId(nodeId);
			taskUtil.addTask(task);//必须带id
			id=task.getId();
			msg="成功";
		} catch (ParseException e) {
			hasError=true;
			id=task.getId();
			msg="表达式错误: " + e.getMessage();
			log.error(e);
		} catch (SchedulerException e) {
			hasError=true;
			id=task.getId();
			msg="任务执行错误: " + e.getMessage();
			log.error(e);
		} catch (Exception e) {
			hasError=true;
			id=task.getId();
			msg="缓存错误: " + e.getMessage();
			log.error(e);
		}
	}
	
	
	public void go(){

	}


	public void setTaskMapper(TaskMapper<Task> taskMapper) {
		this.taskMapper = taskMapper;
	}


	public void setTaskUtil(TaskServer taskUtil) {
		this.taskUtil = taskUtil;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}



	public void setInfo(TaskInfo info) {
		this.info = info;
	}

	public TaskInfo getInfo() {
		return info;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

}
