package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.model.MonitorErr;
import com.me.GCDP.model.MonitorLog;
import com.me.GCDP.model.MonitorTask;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.plugin.UtilPlugin;


public abstract class BaseCheckTask extends QuartzJobBean implements StatefulJob{


	public enum ErrorType {  
		  ERROR, WARNING 
		} 
	@SuppressWarnings("rawtypes")
	protected MonitorMapper mapper = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");
	protected ScriptPluginFactory pf = 	(ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
	protected UtilPlugin up;
	public static int flag = -1;
	

	protected void runCheck(JobExecutionContext context){}
	

	@SuppressWarnings("unchecked")
	protected void insertToErr(String taskName,int nodeid,Date current, ErrorType errType, int logID, String details){
		flag = 4;
		MonitorErr et = new MonitorErr();
		et.setTaskName(taskName);
		et.setNodeid(nodeid);
		et.setErrType(errType.toString());
		et.setIssueDate(current);
		et.setLogId(logID);
		et.setDetails(details);
		mapper.insertToErr(et);
	}
	

	@SuppressWarnings("unchecked")
	protected MonitorLog insertToLog(String taskName, BigDecimal total,Date current,String details){
		flag = 3;
		MonitorLog logTable = new MonitorLog();
		logTable.setTaskName(taskName);
		logTable.setResult(total);
		logTable.setIssueDate(current);
		logTable.setDetails(details);
		mapper.insertToLog(logTable);
		
		return logTable;
	}
	
	protected String sendMsg(String msg, ErrorType errType, String email, String mobile){
		flag = 5; 
		String[] content = msg.split("--");
		try{
			if(errType.equals(ErrorType.ERROR)){
				if(email != null && !email.equals("")){
					
					up.sendMail(email, content[0], URLEncoder.encode(content[1], "UTF-8"), "");
				}
				if(mobile != null && !mobile.equals("")){
					String[] mobiles = mobile.split(",");
					for(int i = 0; i< mobiles.length;i++){
						//up.sendSMS(mobiles[i], msg);
					}
				}
				return msg;
			}else if(errType.equals(ErrorType.WARNING)){
				if(email != null && !email.equals("")){
					up.sendMail(email, content[0], URLEncoder.encode(content[1], "UTF-8"), "");
				}
				return msg;
			}
		}catch(Exception e){
			return "fail to Sent";
		}
		
		return "";
	}
	
	/**
	 * 检查结果 并将结果添加到数据库
	 * @param total 被检查服务运行时间
	 * @param size 可能需要的队列检查（如果不检查测输入-1）
	 * @param taskName 被检查服务名称
	 */
	@SuppressWarnings("unchecked")
	protected void reusltCheck(BigDecimal result,String taskName,String msg, Date date){
		flag = 2;
		List<MonitorTask> taskModelList = mapper.getActiveTask(taskName);
		Date current = null;
		
		if(date != null){
			current = date;
		}else{
			current = new Date();
		}
		
		ErrorType errType = null;
		String sendRes = null;
		String sendBody = null;
		String ids = "";
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = tempDate.format(current);
		MonitorLog log = insertToLog(taskName, result, current, msg);
		if(taskModelList != null && taskModelList.size()>0){
			for(MonitorTask taskModel: taskModelList){
				if(result.compareTo(new BigDecimal(-1)) != 0){
					if(result.compareTo(taskModel.getWarnValue()) == 1){
						if(result.compareTo(taskModel.getErrValue()) == 1){
							errType = ErrorType.ERROR;
						}else{
							errType = ErrorType.WARNING;
						}

						sendBody = "ServiceReport--"+ 
									taskName + " has a(n) " + 
									errType + " for value: " + result + 
									" at node " + taskModel.getNodeid()+ " Time: " +datetime;
						sendRes = sendMsg(sendBody,errType, taskModel.getEmail(), taskModel.getMobile());
						insertToErr(taskName, taskModel.getNodeid(), current, errType, log.getId(), sendRes);
						ids += taskModel.getNodeid() + ", ";
					}
				}else{
					errType = ErrorType.ERROR;

					sendBody = "ServiceReport--"+ taskName + " has an error: " + msg + 
							" at node " + taskModel.getNodeid() +" Time: " +datetime;
					sendRes = sendMsg(sendBody, errType, taskModel.getEmail(), taskModel.getMobile());

					insertToErr(taskName, taskModel.getNodeid(), current, errType, log.getId(), sendRes);
					ids += taskModel.getNodeid();
				}
			}
			if(!ids.equals("")){
				log.setDetails("Err ID: " + ids);
				mapper.updateToLog(log);
			}
		}
		flag = 0;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		flag = 1;
		up = (UtilPlugin) pf.getP("util");
		runCheck(context);	
	}
	
	public int jUnitTest(){
		try{
			runCheck(null);
			return flag;
		}catch(Exception e){
			return flag;
		}
	}
}
