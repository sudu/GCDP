package com.me.GCDP.action.workflow;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.WorkflowService;
import com.ifeng.common.workflow.WorkflowException;
import com.me.GCDP.script.ScriptThreadLocal;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-8-8              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class WorkflowAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(WorkflowAction.class);
	
	private WorkflowService workflowService = null;
	
	private String result = null;
	
	private String name = null;
	
	
	
	private int formId;
	
	private long articleId;
	
	/*
	 * 以下信息由请求提供
	 */
	private int nodeId;
	private int processId;
	private String variables;
	private int id1;
	
	
	/**
	 * 
	 */
	private String msg = "";
	private boolean hasError = false;
	
	@Override
	/**
	 * 注入变量，调试流程
	 */
	public String execute() throws Exception {
		long runid = 0L;
		Map dataPool = new HashMap();
		try {
			if (id1 > 0 && nodeId > 0) {
				dataPool.put("nodeId", nodeId);
				ScriptThreadLocal.setOutput(true);
				if (variables != null && !"".equals(variables.trim())) {
					interpretAndInjectDataPool(dataPool, variables);
					runid = workflowService.startWorkFlow(id1 + "", dataPool);
				}else {
					runid = workflowService.startWorkFlow(id1 + "", dataPool);
				}
				result = ScriptThreadLocal.getOutputString().replaceAll("\r\n", "<br />");
			}else {
				throw new Exception("processId or nodeId < 0");
			}
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			log.error("Exception occured while startWorkflow: " + runid, e);
		} finally {
			ScriptThreadLocal.removeScriptIds();
			ScriptThreadLocal.remove();
		}
		return "debugResult";
	}

	/**
	 * 执行一个指定流程,用来测试运行流程
	 * @return
	 */
	
	public String runProcess() {
		Map dataPool = new HashMap();
		dataPool.put("formId", formId);
		dataPool.put("nodeId", nodeId);
		dataPool.put("articleId", articleId);
		long runid = 0L;
		try {
			runid = workflowService.startWorkFlow(name, dataPool);
		} catch (WorkflowException e) {
			log.error("Exception occured while startWorkflow: " + runid, e);
		}
//		result = "工作流" + name + " is running.....<br />ID : " + runid;
		result = ScriptThreadLocal.getOutputString();
		log.info(result);
		
		return "result";
	}
	
	/**
	 * 解析请求中传递过来的含有dataPool的调试变量的字符串
	 * @param dataPool
	 * @param vars
	 */
	private void interpretAndInjectDataPool(Map dataPool, String vars) {
		JSONArray varsArr = (JSONArray) JSONSerializer.toJSON(vars);
		for (int i = 0; i < varsArr.size(); i++) {
			JSONObject obj = varsArr.getJSONObject(i);
			dataPool.put(obj.get("vKey"), obj.get("vValue"));
		}
	}
	
	/*
	 * getter and setter
	 */
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

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

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public void setVariables(String variables) {
		this.variables = variables;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}
	
	
}
