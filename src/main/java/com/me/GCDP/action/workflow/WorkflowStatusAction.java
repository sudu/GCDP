package com.me.GCDP.action.workflow;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.mapper.PluginStatusMapper;
import com.me.GCDP.workflow.mapper.ProcessInstanceMapper;
import com.me.GCDP.workflow.model.PluginStatusInfo;
import com.me.GCDP.workflow.model.ProcessInstanceInfo;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class WorkflowStatusAction extends ActionSupport {
	private static final Log log = LogFactory.getLog(WorkflowStatusAction.class);
	private ProcessInstanceMapper processInstanceMapper;
	private PluginStatusMapper pluginStatusMapper;
	
	/*
	 * 以下属性由请求提供
	 */
	private int id;
	private int instanceId;
	private int start;
	private int limit;
	private int nodeId;
	private int formId;
	private long articleId;
	
	/*
	 * 以下属性提供给响应
	 */
	private List<ProcessInstanceInfo> processInstanceList;
	private List<PluginStatusInfo> pluginStatusList;
	private int totalCount;
	
	private String msg = "";
	private boolean hasError = false;
	
	public String list(){
		return "list";
	}
	
	/**
	 * 查询某个流程定义的所有流程运行实例
	 * @return
	 */
	public String queryProcessInstance() {
		log.info("Query process instances.processName:" + id);
		try {
			if (id > 0) {
				getProcessInstanceCount();
				queryProcessInstanceLimit();
			}else {
				throw new Exception("Process id < 0");
			}
		} catch (Exception e) {
			msg = e.getMessage();
			hasError = true;
			log.error("Exception occured while query process instances of:" + id, e);
		}
		return "instanceList";
	}
	
	
	/**
	 * 根据id查询流程实例的数量
	 * @return
	 */
	public String getProcessInstanceCount() {
		log.info("Query instance count.processName:" + id);
		try {
			if (id > 0) {
				totalCount = processInstanceMapper.getInstanceCount(id);
			}else {
				throw new Exception("Process id < 0");
			}
		} catch (Exception e) {
			msg = e.getMessage();
			hasError = true;
			log.error("Exception occured while query process instance count of:" + id, e);
		}
		return "";
	}
	
	/**
	 * 分页查询流程实例
	 * @return
	 */
	public String queryProcessInstanceLimit() {
		log.info("Query process instances(limit).processName:" + id);
		try {
			if (id > 0) {
				processInstanceList = processInstanceMapper.getProcessInstanceLimit(id, start, limit);
			}else {
				throw new Exception("Process id < 0");
			}
		} catch (Exception e) {
			msg = e.getMessage();
			hasError = true;
			log.error("Exception occured while query process instances of:" + id, e);
		}
		return "instanceList";
	}
	
	
	
	
	/**
	 * 查询某个流程实例所有插件的运行状况
	 * @return
	 */
	public String queryPluginStatus() {
		log.info("Query plugin status.instanceId:" + instanceId);
		try {
			if (instanceId > 0) {
				pluginStatusList = pluginStatusMapper.getPluginStatus(instanceId);
			}else if(nodeId > 0 && articleId > 0) {
				int iId = (int)processInstanceMapper.getProcessInstance0(nodeId, formId, (int)articleId)
						.get(0).getId();
				pluginStatusList = pluginStatusMapper.getPluginStatus(iId);
			}else {
				throw new Exception("instanceId小于0或者nodeId、articleId小于0");
			}
		} catch (Exception e) {
			msg = e.getMessage();
			hasError = true;
			log.error("Exception occured while query plugin status of:" + instanceId, e);
		}
		return "statusInfo";
	}

	public ProcessInstanceMapper getProcessInstanceMapper() {
		return processInstanceMapper;
	}

	public void setProcessInstanceMapper(ProcessInstanceMapper processInstanceMapper) {
		this.processInstanceMapper = processInstanceMapper;
	}

	public PluginStatusMapper getPluginStatusMapper() {
		return pluginStatusMapper;
	}

	public void setPluginStatusMapper(PluginStatusMapper pluginStatusMapper) {
		this.pluginStatusMapper = pluginStatusMapper;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public List<ProcessInstanceInfo> getProcessInstanceList() {
		return processInstanceList;
	}

	public void setProcessInstanceList(List<ProcessInstanceInfo> processInstanceList) {
		this.processInstanceList = processInstanceList;
	}

	public List<PluginStatusInfo> getPluginStatusList() {
		return pluginStatusList;
	}

	public void setPluginStatusList(List<PluginStatusInfo> pluginStatusList) {
		this.pluginStatusList = pluginStatusList;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}



	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setStart(int start) {
		this.start = start;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}
	
	
}