package com.me.GCDP.workflow.model;

import java.util.Date;

public class ProcessInstanceInfo {
	private long id;
	private int nodeId;
	private int formId;
	private long articleId;
	private String formName;
	private String processDefinitionId;
	private Date processStartDate;
	private int state;
	private String instanceDesc;
	
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getFormId() {
		return formId;
	}
	public void setFormId(int formId) {
		this.formId = formId;
	}
	public long getArticleId() {
		return articleId;
	}
	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public Date getProcessStartDate() {
		return processStartDate;
	}
	public void setProcessStartDate(Date processStartDate) {
		this.processStartDate = processStartDate;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getInstanceDesc() {
		return instanceDesc;
	}
	public void setInstanceDesc(String instanceDesc) {
		this.instanceDesc = instanceDesc;
	}
	
}
