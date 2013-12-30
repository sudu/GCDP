package com.me.GCDP.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitorLog extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private BigDecimal result;
	private Date issueDate;
	private String details;
	private Date startTime;
	private Date endTime;
	
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public BigDecimal getResult() {
		return result;
	}
	public void setResult(BigDecimal result) {
		this.result = result;
	}
	public String getIssueDate() {
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return tempDate.format(this.issueDate);
		//return issueDate;
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
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	

}
