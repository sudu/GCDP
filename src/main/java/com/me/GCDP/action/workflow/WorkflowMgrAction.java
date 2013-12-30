package com.me.GCDP.action.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.mapper.ProcessInstanceMapper;
import com.me.GCDP.workflow.mapper.ProcessListMapper;
import com.me.GCDP.workflow.model.ProcessInfoForQuery;
import com.me.GCDP.workflow.model.ProcessInstanceInfo;
import com.me.GCDP.workflow.service.WorkflowMgrService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings({"serial"})
public class WorkflowMgrAction extends ActionSupport{
	
	private static final Log log = LogFactory.getLog(WorkflowMgrAction.class);
	
	private WorkflowMgrService workflowMgrService = null;
	
	/**
	 * 以下字段是请求提供的
	 */
	private String flowDefInfo = "";
	
	private int existingProcessId = 0;
	
	private int processId = 0;
	
	private int nodeId = 0;
	
	private ProcessListMapper processListMapper = null;
	
	private ProcessInstanceMapper processInstanceMapper;
	
	private int id;
	
	private int status;
	
	private int formId;
	
	private long articleId;
	
	private long instanceId;
	
	/**
	 * 以下字段是响应需要的
	 */
	private int savedOrUpdatedProcessIdRet = 0;
	
	
	private List<ProcessInfoForQuery> exsistingProcessList = null;
	
	private String flowDefInfoJSON = "";
	
	private String dateTimeStr = "";
	private String msg = "";
	private boolean hasError = false;
	
	private List<ProcessInstanceInfo> processInstanceList;
	
	public String execute() throws Exception {
		Date date=new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTimeStr = formatter.format(date); 
		
		if(processId>0){
			//获得流程配置内容
			getExistingProcessDefById();
		}
		return "index";
	}
	public String list(){
		return "list";
	}
	public String monitor(){
		if(processId>0){
			//获得流程配置内容
			getExistingProcessDefById();
		}if (nodeId > 0 && formId != 0 && articleId > 0) {
			processInstanceList = processInstanceMapper.getProcessInstance0(nodeId, formId, (int)articleId);
			if (processInstanceList != null && processInstanceList.size() > 0) {
				processId = Integer.parseInt(processInstanceList.get(0).getProcessDefinitionId());
				instanceId = (int)processInstanceList.get(0).getId();
				getExistingProcessDefById();
			}
		}
		return "monitor";
	}
	/**
	 * 添加一个新流程或者更新一个现有流程：
	 * 如果id为空或者不提供，创建一个新流程；<p>
	 * 否则根据id，更新一个已有流程
	 * @return
	 */
	public String saveOrUpdateProcess() {
		try {
			if(existingProcessId <= 0) {
				if (nodeId > 0 && ! "".equals(flowDefInfo)) {
					savedOrUpdatedProcessIdRet = workflowMgrService.interpretAndSaveProcess(nodeId,
							flowDefInfo);
				}
			}else {
				savedOrUpdatedProcessIdRet = workflowMgrService.interpretAndUpdateProcess(
						existingProcessId, flowDefInfo);
				if (savedOrUpdatedProcessIdRet == 0) {
					throw new Exception("更新流程失败!");
				}
			}
		}catch (Exception e) {
			hasError = true;
			msg = "保存失败：" + e.getMessage();
			log.error(e);
		}
		
		return "save";
	}
	
	/**
	 * 获取数据库中现有流程定义的列表(不含流程定义信息,仅仅是一个id、name的列表)
	 * @return
	 */
	public String getExistingProcessList() {
		if (nodeId > 0) {
			try {
				//exsistingProcessList = workflowMgrService.queryProcessList(nodeId);
				exsistingProcessList = processListMapper.getProcessListByNodeId(nodeId);
			} catch (Exception e) {
				hasError = true;
				msg = "查询流程列表失败";
			}
		}else {
			return "";
		}
		return "getlist";
	}
	
	/**
	 * 根据id，获取某个流程定义的信息
	 */
	public String getExistingProcessDefById() {
		try{
			if (processId > 0) {
				flowDefInfoJSON = workflowMgrService.queryProcessDefinitionStr(processId);
			}else {
				throw new Exception("ProcessId < 0");
			}
		}catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			return "";
		}
		
		return "getdata";
	}
	
//	public String getProcessDefBy3Ids() {
//		try {
//			if (nodeId > 0 && articleId > 0) {
//				processInstanceList = processInstanceMapper.getProcessInstance0(nodeId, formId, (int)articleId);
//				if (processInstanceList != null && processInstanceList.size() > 0) {
//					int pId = Integer.parseInt(processInstanceList.get(0).getProcessDefinitionId());
//					flowDefInfoJSON = workflowMgrService.queryProcessDefinitionStr(pId);
//				}else {
//					throw new Exception("流程实例不存在");
//				}
//			}else {
//				throw new Exception("nodeId、articleId必须大于0");
//			}
//		} catch (Exception e) {
//			hasError = true;
//			msg = e.getMessage();
//			return "";
//		}
//		
//		return "";
//	}

	/**
	 * 将流程发布到XML文件中，发布到配置文件中给定的默认文件中
	 * @return
	 */
	public String publishWorkflow() {
		try {
			workflowMgrService.releaseWorkflowToDefaultXML();
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			return "";
		}
		return "";
	}
	
	/**
	 * 上线或者下线
	 * @return
	 */
	public String enableOrDisableProcess() {
		try {
			if (id > 0) {
				if (status == 1) {
					workflowMgrService.enableAndReleaseflow(id);
				}else if (status == -1) {
					workflowMgrService.disableAndReleaseflow(id);
				}else {
					throw new Exception("Status不合法，必须为-1或者1");
				}
			}else {
				throw new Exception("流程Id不能小于0");
			}
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			return "";
		}
		return "save";
	}
	
	/*
	 * Getters and Setters
	 */
	public WorkflowMgrService getWorkflowMgrService() {
		return workflowMgrService;
	}

	public void setWorkflowMgrService(WorkflowMgrService workflowMgrService) {
		this.workflowMgrService = workflowMgrService;
	}

	public String getFlowDefInfo() {
		return flowDefInfo;
	}

	public void setFlowDefInfo(String flowDefInfo) {
		this.flowDefInfo = flowDefInfo;
	}

	public int getExistingProcessId() {
		return existingProcessId;
	}

	public void setExistingProcessId(int existingProcessId) {
		this.existingProcessId = existingProcessId;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public int getSavedOrUpdatedProcessIdRet() {
		return savedOrUpdatedProcessIdRet;
	}

	public void setSavedOrUpdatedProcessIdRet(int savedOrUpdatedProcessIdRet) {
		this.savedOrUpdatedProcessIdRet = savedOrUpdatedProcessIdRet;
	}

	public List<ProcessInfoForQuery> getExsistingProcessList() {
		return exsistingProcessList;
	}

	public void setExsistingProcessList(List<ProcessInfoForQuery> exsistingProcessList) {
		this.exsistingProcessList = exsistingProcessList;
	}

	public String getFlowDefInfoJSON() {
		return flowDefInfoJSON;
	}

	public void setFlowDefInfoJSON(String flowDefInfoJSON) {
		this.flowDefInfoJSON = flowDefInfoJSON;
	}
	
	public String getDateTimeStr() {	
		return dateTimeStr;
	}
	public Boolean getHasError() {	
		return hasError;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getMsg() {
		return msg;
	}

	public ProcessListMapper getProcessListMapper() {
		return processListMapper;
	}

	public void setProcessListMapper(ProcessListMapper processListMapper) {
		this.processListMapper = processListMapper;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public void setFormId(int formId) {
		this.formId = formId;
	}
	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}
	public void setProcessInstanceMapper(ProcessInstanceMapper processInstanceMapper) {
		this.processInstanceMapper = processInstanceMapper;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public long getInstanceId() {
		return instanceId;
	}
	
	
}