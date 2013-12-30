package com.me.GCDP.workflow.model;

import java.util.Date;
import java.util.List;

/**
 * Activity定义，对应于cmpp_activitydef表
 * @author xiongfeng
 *
 */
public class CmppActivityDef {
	private int activityId;
	private CmppProcessDef process;
	private String activityName;
	private String activityTitle;
	private String activityDesc;
	private Date createTime;
	private byte activityType;
	
	//以下属性不需要进行持久化
	private List<CmppPluginDef> plugins;
	//用来标示该活动是否属于子流程中的活动
	private boolean belongsToSubRoutine = false;
	
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public CmppProcessDef getProcess() {
		return process;
	}
	public void setProcess(CmppProcessDef process) {
		this.process = process;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getActivityTitle() {
		return activityTitle;
	}
	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}
	public String getActivityDesc() {
		return activityDesc;
	}
	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public byte getActivityType() {
		return activityType;
	}
	public void setActivityType(byte activityType) {
		this.activityType = activityType;
	}
	public List<CmppPluginDef> getPlugins() {
		return plugins;
	}
	public void setPlugins(List<CmppPluginDef> plugins) {
		this.plugins = plugins;
	}
	public boolean isBelongsToSubRoutine() {
		return belongsToSubRoutine;
	}
	public void setBelongsToSubRoutine(boolean belongsToSubRoutine) {
		this.belongsToSubRoutine = belongsToSubRoutine;
	}
	
}