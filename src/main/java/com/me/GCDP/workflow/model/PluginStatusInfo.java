package com.me.GCDP.workflow.model;

import java.util.Date;

public class PluginStatusInfo {
	private Date pluginStartDate;
	
	private Date pluginEndDate;
	
	private int status;
	
	private String cfgCode;

	public Date getPluginStartDate() {
		return pluginStartDate;
	}
	
	public void setPluginStartDate(Date pluginStartDate) {
		this.pluginStartDate = pluginStartDate;
	}

	public Date getPluginEndDate() {
		return pluginEndDate;
	}

	public void setPluginEndDate(Date pluginEndDate) {
		this.pluginEndDate = pluginEndDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCfgCode() {
		return cfgCode;
	}

	public void setCfgCode(String cfgCode) {
		this.cfgCode = cfgCode;
	}
	
}
