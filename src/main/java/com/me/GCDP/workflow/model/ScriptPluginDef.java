package com.me.GCDP.workflow.model;

public class ScriptPluginDef extends PluginDef {
	private String nodeId;
	private String mailTo;
	private String rtxTo;
	private String smsTo;
	
	private String ids;

	private String scriptType;
	private String suspendsOnException;
	
	private String expids;
	private String nActivity;
	
	private String exeCase;
	private String subRoutine;
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
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

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getSuspendsOnException() {
		return suspendsOnException;
	}

	public void setSuspendsOnException(String suspendsOnException) {
		this.suspendsOnException = suspendsOnException;
	}

	public String getExpids() {
		return expids;
	}

	public void setExpids(String expids) {
		this.expids = expids;
	}

	public String getnActivity() {
		return nActivity;
	}

	public void setnActivity(String nActivity) {
		this.nActivity = nActivity;
	}

	public String getExeCase() {
		return exeCase;
	}

	public void setExeCase(String exeCase) {
		this.exeCase = exeCase;
	}

	public String getSubRoutine() {
		return subRoutine;
	}

	public void setSubRoutine(String subRoutine) {
		this.subRoutine = subRoutine;
	}
}
