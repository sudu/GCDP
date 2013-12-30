package com.me.GCDP.workflow.model;

import java.util.Date;
import java.util.List;

/**
 * 一个流程定义，对应cmpp_processdef表
 * @author xiongfeng
 *
 */
public class CmppProcessDef {
	private int processId;
	private String processName;
	private String processTitle;
	private String processDesc;
	private String creator;
	private Date createTime;
	private String authPath;
	/**
	 * processDefInfo表示本流程对应的从前端传递过来的JSON字符串
	 */
	private String processDefInfo;
	private Date recentModifyTime = null;//最近一次的修改时间
	private int nodeId = 0;//流程定义所属的节点编号
	
	private byte status;
	
	//以下属性不需要进行持久化
	private List<CmppActivityDef> activities;
	
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getProcessTitle() {
		return processTitle;
	}
	public void setProcessTitle(String processTitle) {
		this.processTitle = processTitle;
	}
	public String getProcessDesc() {
		return processDesc;
	}
	public void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
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
	public String getAuthPath() {
		return authPath;
	}
	public void setAuthPath(String authPath) {
		this.authPath = authPath;
	}
	public List<CmppActivityDef> getActivities() {
		return activities;
	}
	public void setActivities(List<CmppActivityDef> activities) {
		this.activities = activities;
	}
	public String getProcessDefInfo() {
		return processDefInfo;
	}
	public void setProcessDefInfo(String processDefInfo) {
		this.processDefInfo = processDefInfo;
	}
	public Date getRecentModifyTime() {
		return recentModifyTime;
	}
	public void setRecentModifyTime(Date recentModifyTime) {
		this.recentModifyTime = recentModifyTime;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
}