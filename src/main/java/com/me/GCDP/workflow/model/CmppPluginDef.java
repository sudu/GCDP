package com.me.GCDP.workflow.model;

public class CmppPluginDef {
	private int pluginId;
	private String pluginName;
	private byte pluginType;
	private int nodeId;
	private String ids;
	private String exceptionIds;
	private String mailTo;
	private String rtxTo;
	private String smsTo;
	private String scriptType;
	private char stopOnException;
	private String nActivity;
	private CmppActivityDef activity;
	//在插件的定义中标示出其子流程
	private String subRoutine = "";
	private String executeCase = "";
	/*
	 * 是否启用邮件和短信报警
	 */
	private char mailToEnable;
	private char smsToEnable;
	private String cfgCode;

	
	public int getPluginId() {
		return pluginId;
	}
	public void setPluginId(int pluginId) {
		this.pluginId = pluginId;
	}
	public byte getPluginType() {
		return pluginType;
	}
	public void setPluginType(byte pluginType) {
		this.pluginType = pluginType;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getExceptionIds() {
		return exceptionIds;
	}
	public void setExceptionIds(String exceptionIds) {
		this.exceptionIds = exceptionIds;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public String getRtxTo() {
		return rtxTo;
	}
	public void setRtxTo(String rtxTo) {
		this.rtxTo = rtxTo;
	}
	public String getSmsTo() {
		return smsTo;
	}
	public void setSmsTo(String smsTo) {
		this.smsTo = smsTo;
	}
	public String getScriptType() {
		return scriptType;
	}
	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}
	public char getStopOnException() {
		return stopOnException;
	}
	public void setStopOnException(char stopOnException) {
		this.stopOnException = stopOnException;
	}
	public String getnActivity() {
		return nActivity;
	}
	public void setnActivity(String nActivity) {
		this.nActivity = nActivity;
	}
	public CmppActivityDef getActivity() {
		return activity;
	}
	public void setActivity(CmppActivityDef activity) {
		this.activity = activity;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getCfgCode() {
		return cfgCode;
	}
	public void setCfgCode(String cfgCode) {
		this.cfgCode = cfgCode;
	}
	public String getSubRoutine() {
		return subRoutine;
	}
	public void setSubRoutine(String subRoutine) {
		this.subRoutine = subRoutine;
	}
	public String getExecuteCase() {
		return executeCase;
	}
	public void setExecuteCase(String executeCase) {
		this.executeCase = executeCase;
	}
	public char getMailToEnable() {
		return mailToEnable;
	}
	public void setMailToEnable(char mailToEnable) {
		this.mailToEnable = mailToEnable;
	}
	public char getSmsToEnable() {
		return smsToEnable;
	}
	public void setSmsToEnable(char smsToEnable) {
		this.smsToEnable = smsToEnable;
	}
	
}
