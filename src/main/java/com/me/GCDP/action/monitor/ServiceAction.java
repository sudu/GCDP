package com.me.GCDP.action.monitor;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.model.MonitorErr;
import com.me.GCDP.model.MonitorLog;
import com.me.GCDP.model.MonitorTask;
import com.me.GCDP.model.PageCondition;
import com.me.GCDP.util.Page;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.chart.ChartService;
import com.me.json.JSONArray;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;



/*
 * jiangy 从数据库里面读取相关的服务
 */

@SuppressWarnings("rawtypes")
public class ServiceAction extends ActionSupport{

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(ServiceAction.class);
	private MonitorMapper monitorM = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");

	private Page logPage;
	private Page errPage;
	private Page statusPage;
	private Page taskPage;
	
	private String start;
	private String limit;
    private String filterTxt = null;
	private String filterValue = null;
	
	private String chart;
	private String taskName;
	private int logID;
	private String logRecord;
	private String nodeID = "-1";
	private int id;
	private String ids;
	private MonitorTask task;
	//日期查询的结果
	private List<MonitorLog> monitorLog = null;
	private Date startTime;
	private Date endTime;
	
	private String result;
	
	@SuppressWarnings("unchecked")
	public String pagedTask() throws Exception{
		PageCondition pc = new PageCondition();
		List<MonitorTask> result = null;
		int totalCount = 0;
		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		
		if((filterTxt != null) && 
			(filterValue != null)&&
			(!filterTxt.equals(""))&&
			(!filterValue.equals(""))){
			
			pc.setFilterTxt(filterTxt);
			pc.setFilterValue(filterValue);
		} 
		if(nodeID.equals("-1")){
			result = monitorM.getAllTask(pc);
			totalCount = monitorM.getTaskCount(pc);
		}else{		
			pc.setNodeId(Integer.parseInt(nodeID));
			result = monitorM.getTaskByNodeID(pc);
			totalCount = monitorM.getTaskCountByNodeID(pc);
		}
		
		setTaskPage(new Page<MonitorLog>(new JSONArray(result), totalCount));
		return "pagedTask";
	}
	@SuppressWarnings("unchecked")
	public String addTask(){
		if(task.getId()!=null){
			if(task.getTaskName().contains(".key")){
				task.setTaskName(task.getTaskName().split(".key")[0]+"/"+task.getKey());
			}
			monitorM.updateToTask(task);
		}else{
			if(task.getTaskName().contains(".key")){
				task.setTaskName(task.getTaskName().split(".key")[0]+"/"+task.getKey());
			}
			monitorM.insertToTask(task);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public String modifyTask() throws  Exception{
		MonitorTask tmp = new MonitorTask();
		tmp.setId(id);
		task = (MonitorTask) monitorM.getFromTask(tmp);
		
		if(task.getTaskName().contains("/")){
			String[] nameAndKey = task.getTaskName().split("/");
			task.setTaskName(nameAndKey[0]+".key");
			task.setKey(nameAndKey[1]);
		}
		JSONObject jobj = new JSONObject(task);
		result = "["+jobj.toString()+"]";
		return "taskOp";
	}
	
	@SuppressWarnings("unchecked")
	public String removeTask() throws IOException{
		for (String str : ids.split(",")) {
            try {
                int id = Integer.parseInt(str);
        		MonitorTask tmp = new MonitorTask();
        		tmp.setId(id);
                MonitorTask task = (MonitorTask) monitorM.getFromTask(tmp);
                monitorM.deleteFromTask(task);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
		result = "{success:true,msg:'删除成功'}";
		return "taskOp";
	}
	@SuppressWarnings("unchecked")
	public String pagedLog() throws Exception{
		PageCondition pc = new PageCondition();

		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		
		if((filterTxt != null) && 
			(filterValue != null)&&
			(!filterTxt.equals(""))&&
			(!filterValue.equals(""))){
			
			pc.setFilterTxt(filterTxt);
			pc.setFilterValue(filterValue);
		} 
		List<MonitorLog> result = monitorM.getAllFromLog(pc);
		setLogPage(new Page<MonitorLog>(new JSONArray(result), monitorM.getLogCount(pc)));

		
		return "pagedLog";
	}
	//显示log记录
	@SuppressWarnings("unchecked")
	public String showLogRecord(){

		MonitorLog tmp = new MonitorLog();
		tmp.setId(logID);
		List<MonitorLog> result = monitorM.getFromLog(tmp);
		logRecord = new JSONArray(result).toString();
		return "logRecord";
	}
	
	public String showLog(){
		return "logPage";
	}
	//查看err记录
	@SuppressWarnings("unchecked")
	public String pagedErr() throws Exception{
		List<MonitorErr> result = null;
		PageCondition pc = new PageCondition();
		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		
		this.taskFilter();
		
		if((filterTxt != null) && 
				(filterValue != null)&&
				(!filterTxt.equals(""))&&
				(!filterValue.equals(""))){
				
				 pc.setFilterTxt(filterTxt);
				 pc.setFilterValue(filterValue);
			}
		if(nodeID.equals("-1")){
			result = monitorM.getAllFromErr(pc);
			setErrPage(new Page<MonitorLog>(new JSONArray(result), monitorM.getErrCount(pc)));
		}else{		
			pc.setNodeId(Integer.parseInt(nodeID));
			result = monitorM.getByNodeIdFromErr(pc);
			setErrPage(new Page<MonitorLog>(new JSONArray(result), monitorM.getErrCountByNodeID(pc)));
		}
		
		//setErrPage(new Page<MonitorLog>(new JSONArray(result), monitorM.getErrCount(pc)));

		return "pagedErr";
	}
	
	public String showErr(){
		return "errPage";
	}
	
	public String showStatus(){
		
		return "statusPage";
	}
	//当前状态查看
	@SuppressWarnings("unchecked")
	public String pagedStatus(){
		List<MonitorTask> result = null;
		PageCondition pc = new PageCondition();
		pc.setFrom(new Integer(start));
		pc.setLimit(new Integer(limit));
		int totalCount = 0;
		
		this.taskFilter();
		
		if((filterTxt != null) && 
				(filterValue != null)&&
				(!filterTxt.equals(""))&&
				(!filterValue.equals(""))){
				
				 pc.setFilterTxt(filterTxt);
				 pc.setFilterValue(filterValue);
			}
		if(nodeID.equals("-1")){
			result = monitorM.getAllTask(pc);
			totalCount = monitorM.getTaskCount(pc);
		}else{		
			pc.setNodeId(Integer.parseInt(nodeID));
			result = monitorM.getTaskByNodeID(pc);
			totalCount = monitorM.getTaskCountByNodeID(pc);
		}
		//List<MonitorTask> result = monitorM.getAllTask(pc);
		List<MonitorTask> statuses = new ArrayList<MonitorTask>();
		MonitorTask mTask = null;
		MonitorLog lastLog = null;
		for(MonitorTask tmp: result){
			lastLog = (MonitorLog) monitorM.getLastLogByName(tmp.getTaskName());
			
			mTask = new MonitorTask();
			
			mTask.setId(tmp.getId());
			mTask.setNodeid(tmp.getNodeid());
			mTask.setTaskName(tmp.getTaskName());
			mTask.setIsCheck(tmp.getIsCheck());
			mTask.setMeasure(tmp.getMeasure());
			mTask.setDescription(tmp.getDescription());
			
			if(lastLog!=null){
				mTask.setLastLog(lastLog);
				if(lastLog.getResult().compareTo(tmp.getWarnValue())==0){
					mTask.setStatus("Warning");
				}else if(lastLog.getResult().compareTo(tmp.getWarnValue())==1){
					if(lastLog.getResult().compareTo(tmp.getErrValue())==0 || 
							lastLog.getResult().compareTo(tmp.getWarnValue()) == 1){
						mTask.setStatus("Error");
					}else{
						mTask.setStatus("Warning");
					}
				}else if(lastLog.getResult().compareTo(new BigDecimal(-1))==0){
					mTask.setStatus("Error");
				}else{
					mTask.setStatus("Normal");
				}
			}else{
				mTask.setLastLog(null);
				mTask.setStatus(null);
			}

			statuses.add(mTask);
		}
		this.setStatusPage(new Page<MonitorTask>(new JSONArray(statuses),totalCount));
		return "pagedStatus";
	}
	/*
	 * 时间段图片
	 */
	public String showChart(){
		if(startTime!=null&&endTime!=null){
			monitorLog = this.getLogByTime(startTime, endTime,taskName);
			try {
				this.chartGen(monitorLog,taskName);
			} catch (ParseException e) {
				log.error("ServiceActiom:"+e);
			}
		}	
		return "chart";	
	}
	/*
	 * 初始化图片
	 */
	public String showDetail(){
		Date endTime = new Date();
		Date beginTime = new Date(endTime.getTime()-24*60*60*1000);
		monitorLog = this.getLogByTime(beginTime, endTime,taskName);
		try {
			this.chartGen(monitorLog,taskName);
		} catch (ParseException e) {
			log.error("ServiceActiom:"+e);
		}
		return "detail";
	}
		
	@SuppressWarnings("unchecked")
	private List<MonitorLog> getLogByTime(Date startTime,Date endTime,String taskType){
		MonitorLog ml = new MonitorLog();
		ml.setStartTime(startTime);
		ml.setEndTime(endTime);
		ml.setTaskName(taskType);
		ml.setOrderByField("id");
		return monitorM.getByTimeFromLog(ml);
	}
//	//生成图片
	@SuppressWarnings({ "unchecked" })
	private void chartGen(List<MonitorLog> result,String taskType) throws ParseException{
		HttpSession session = ServletActionContext.getRequest().getSession();
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MonitorTask m = (MonitorTask) monitorM.getTaskByExactName(taskName);
		List<Map> data = new ArrayList<Map>();
		Map d;
		String xKey = "date";
		String yKey = "time";
		for(MonitorLog tmp: result){
			d = new HashMap();
			d.put(xKey,tempDate.parse(tmp.getIssueDate()));
			if(tmp.getResult().compareTo(new BigDecimal(-1))==0){
				d.put(yKey,new Double(20000));
			}else{
				d.put(yKey,Double.parseDouble(tmp.getResult()+""));
			}
			data.add(d);
		}
		chart = ChartService.createLineChart(session,taskName,data,new String[]{"Result Value (" +m.getMeasure()+")"},"date",xKey,"Result",new String[]{yKey},true,"yyyy-MM-dd HH:mm:ss","##",600,250);
	}
		
	public String execute(){
		return "overView";
	}
	
	private void taskFilter(){
		if(filterValue != null && filterValue.equalsIgnoreCase("dyn")){
			filterTxt = "taskName";
		}
	}

	//getters and setters
	public String getStart() {
		return start;
	}
	
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getLimit() {
		return limit;
	}
	
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public Page getLogPage() {
		return logPage;
	}
	
	public void setLogPage(Page logPage) {
		this.logPage = logPage;
	}

	public Page getErrPage() {
		return errPage;
	}

	public void setErrPage(Page errPage) {
		this.errPage = errPage;
	}
	
    public String getFilterTxt() {
		return filterTxt;
	}

	public void setFilterTxt(String filterTxt) {
		this.filterTxt = filterTxt;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	public String getChart() {
		return chart;
	}

	public void setChart(String chart) {
		this.chart = chart;
	}

	public Page getStatusPage() {
		return statusPage;
	}

	public void setStatusPage(Page statusPage) {
		this.statusPage = statusPage;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getLogID() {
		return logID;
	}

	public void setLogID(int logID) {
		this.logID = logID;
	}

	public String getLogRecord() {
		return logRecord;
	}

	public void setLogRecord(String logRecord) {
		this.logRecord = logRecord;
	}
	
	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		try {
			this.startTime = this.stringToDate(startTime);
		} catch (ParseException e) {
			log.error(e);
		}
	}
	public MonitorTask getTask() {
		return task;
	}

	public void setTask(MonitorTask task) {
		this.task = task;
	}
	
	public String showTask(){
		return "taskPage";
	}
	
	public Page getTaskPage() {
		return taskPage;
	}
	
	public void setTaskPage(Page taskPage) {
		this.taskPage = taskPage;
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
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
			try {
				this.endTime = this.stringToDate(endTime);
			} catch (ParseException e) {
				log.error(e);
			}
	}
	private Date stringToDate(String time) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date oldDate = (Date)formatter.parse(time);
		return oldDate;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
