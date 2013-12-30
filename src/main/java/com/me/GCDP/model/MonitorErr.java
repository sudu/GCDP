package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitorErr extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private String errType;
	private Date issueDate;
	private String details;
	private int logId;
	
	

	public String getErrType() {
		return errType;
	}
	public void setErrType(String errType) {
		this.errType = errType;
	}
	public String getIssueDate() {
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return tempDate.format(this.issueDate);
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	

}
