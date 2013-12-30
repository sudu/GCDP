package com.me.GCDP.workflow.model;

import java.util.Date;

public class ProcessInfoForQuery{
	private int id;
	private String processTitle;
	private String creator;
	private Date createTime;
	private Date recentModifyTime;
	private byte status;
	public String getProcessTitle() {
		return processTitle;
	}
	public void setProcessTitle(String processTitle) {
		this.processTitle = processTitle;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getRecentModifyTime() {
		return recentModifyTime;
	}
	public void setRecentModifyTime(Date recentModifyTime) {
		this.recentModifyTime = recentModifyTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	
	
}
