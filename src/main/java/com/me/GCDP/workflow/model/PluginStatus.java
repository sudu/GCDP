package com.me.GCDP.workflow.model;

import java.util.Date;

import com.me.GCDP.workflow.CmppProcessContext;

public class PluginStatus {
	private long pluginStatusId;
	private CmppProcessContext processContext;
	private String activityName;
	private String pluginName;
	private Date pluginStartTime;
	private Date pluginEndTime;
	private byte status;
	
	//与CmppPluginDef形成多对一关联
	private CmppPluginDef pluginDef;
	
	public long getPluginStatusId() {
		return pluginStatusId;
	}
	public void setPluginStatusId(long pluginStatusId) {
		this.pluginStatusId = pluginStatusId;
	}
	public CmppProcessContext getProcessContext() {
		return processContext;
	}
	public void setProcessContext(CmppProcessContext processContext) {
		this.processContext = processContext;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public Date getPluginStartTime() {
		return pluginStartTime;
	}
	public void setPluginStartTime(Date pluginStartTime) {
		this.pluginStartTime = pluginStartTime;
	}
	public Date getPluginEndTime() {
		return pluginEndTime;
	}
	public void setPluginEndTime(Date pluginEndTime) {
		this.pluginEndTime = pluginEndTime;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public CmppPluginDef getPluginDef() {
		return pluginDef;
	}
	public void setPluginDef(CmppPluginDef pluginDef) {
		this.pluginDef = pluginDef;
	}
	
	
}
