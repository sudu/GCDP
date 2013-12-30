/**
 * 
 */
package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;



/**
 * @author zhangzy
 * 2011-7-14
 */
public class Task extends BaseModel{

	private static final long serialVersionUID = 1L;
	private String taskName;
	private String taskGroup;
	private Date startTime; 
	private Date endTime;
	private Date previousFireTime;
	private Date nextFireTime;
	private int repeatCount;
	private int runCount=0;
	private int repeatInterval;
	private String exp;
	private int status;//0-停止 ，1-等待执行，2-正在执行
	private String script;
	private int type;//特定时间执行-0,循环执行-1
	private int nodeId;
	private String creator;			//计划任务的创建者
	private Date lastModifyTime;		//计划任务的最近修改时间
	public Date getPreviousFireTime() {
		return previousFireTime;
	}
	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}
	public int getRunCount() {
		return runCount;
	}
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}
	public Date getNextFireTime() {
		return nextFireTime;
	}
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskGroup() {
		return taskGroup;
	}
	public void setTaskGroup(String taskGroup) {
		this.taskGroup = taskGroup;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getRepeatCount() {
		return repeatCount;
	}
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
	public int getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	@Override
	public String toString() {
		JsonConfig cfg = new JsonConfig();
		cfg.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
			private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			@Override
			public Object processObjectValue(String key, Object value,
					JsonConfig jsonConfig) {
				if (value != null) {
					if (value instanceof Date) {
						return sdf.format(value);
					}else {
						return value;
					}
				}
				return null;
			}
			
			@Override
			public Object processArrayValue(Object value, JsonConfig jsonConfig) {
				return value;
			}
		});
		JSONObject obj = JSONObject.fromObject(this, cfg);
		return obj.toString();
	}
	
	
}
