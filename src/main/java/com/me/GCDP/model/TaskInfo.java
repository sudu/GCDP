/**
 * 
 */
package com.me.GCDP.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;


/**
 * @author zhangzy
 * 任务信息类
 * 2011-7-19
 */
public class TaskInfo implements Serializable{

	private static final long serialVersionUID = -2391886503562889900L;

	private String taskName=null;
	
	private Date previousFireTime=null;
	
	private Date nextFireTime=null;
	
	private int errCount;
	
	private int runCount;
	
	private String status="尚未启用";
	
	private Date lastErrTime=null;
	
	private long runTime;
	
	private List<String> errInfo=new ArrayList<String>(); //保存最新5个错误信息
	
	public List<String> getErrInfo() {
		return errInfo;
	}
	public void setErrInfo(List<String> errInfo) {
		this.errInfo = errInfo;
	}
	public long getRunTime() {
		return runTime;
	}
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}
	public Date getLastErrTime() {
		return lastErrTime;
	}
	public void setLastErrTime(Date lastErrTime) {
		this.lastErrTime = lastErrTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getRunCount() {
		return runCount;
	}
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}
	public Date getPreviousFireTime() {
		return previousFireTime;
	}
	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}
	public Date getNextFireTime() {
		return nextFireTime;
	}
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	public int getErrCount() {
		return errCount;
	}
	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@Override
	public String toString() {
		JsonConfig cfg = new JsonConfig();
		cfg.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
			private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			@Override
			public Object processObjectValue(String key, Object value,
					JsonConfig jsonConfig) {
				if (value != null) {
					if (value instanceof Date) {
						return sdf.format(value);
					}else {
						return value;
					}
				}
				return null;
			}
			
			@Override
			public Object processArrayValue(Object value, JsonConfig jsonConfig) {
				return value;
			}
		});
		JSONObject obj = JSONObject.fromObject(this, cfg);
		return obj.toString();
	}
}
