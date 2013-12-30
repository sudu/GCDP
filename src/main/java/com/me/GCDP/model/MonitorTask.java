package com.me.GCDP.model;

import java.math.BigDecimal;

public class MonitorTask extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private BigDecimal warnValue;
	private BigDecimal errValue;
	private boolean isCheck;
	private String email;
	private String mobile;
	private String status;
	private String measure;
	private String description;
	private MonitorLog lastLog = null;
	private String key;
	
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public BigDecimal getWarnValue() {
		return warnValue;
	}
	public void setWarnValue(BigDecimal warnValue) {
		this.warnValue = warnValue;
	}
	public BigDecimal getErrValue() {
		return errValue;
	}
	public void setErrValue(BigDecimal errValue) {
		this.errValue = errValue;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public MonitorLog getLastLog() {
		return lastLog;
	}
	public void setLastLog(MonitorLog lastLog) {
		this.lastLog = lastLog;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	
}
