package com.me.GCDP.model;

import java.util.Date;

public class DynPageStatus extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	private Integer dynID;
	private String pageUrl;
	private Integer requireCount;
	private Double respTime;
	private Integer code200Count;
	private Integer code400Count;
	private Integer code500Count;
	private Date issueDate;
	
	private String startTime;
	private String endTime;
		
	public Integer getDynID() {
		return dynID;
	}
	public void setDynID(Integer dynID) {
		this.dynID = dynID;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public Integer getRequireCount() {
		return requireCount;
	}
	public void setRequireCount(Integer requireCount) {
		this.requireCount = requireCount;
	}
	public Double getRespTime() {
		return respTime;
	}
	public void setRespTime(Double respTime) {
		this.respTime = respTime;
	}
	public Integer getCode200Count() {
		return code200Count;
	}
	public void setCode200Count(Integer code200Count) {
		this.code200Count = code200Count;
	}
	public Integer getCode400Count() {
		return code400Count;
	}
	public void setCode400Count(Integer code400Count) {
		this.code400Count = code400Count;
	}
	public Integer getCode500Count() {
		return code500Count;
	}
	public void setCode500Count(Integer code500Count) {
		this.code500Count = code500Count;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
