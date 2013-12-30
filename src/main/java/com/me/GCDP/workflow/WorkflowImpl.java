package com.me.GCDP.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.model.CmppProcessDef;
import com.ifeng.common.dm.DataManager;
import com.ifeng.common.dm.DataManagerException;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.dm.QueryResult;
import com.ifeng.common.dm.queryField.NormalQueryField;
import com.ifeng.common.workflow.AbstractWorkflow;
import com.ifeng.common.workflow.ProcessContext;
import com.ifeng.common.workflow.ProcessDefinition;
import com.ifeng.common.workflow.WorkflowException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-8-9              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class WorkflowImpl extends AbstractWorkflow {
	
	private static final Log log = LogFactory.getLog(WorkflowImpl.class);
	
	private PersistDataManager processDefDm;
	
	public WorkflowImpl(){
		super();
	}
	
	public WorkflowImpl(Map processDefinitionMap, DataManager dm) {
	    super(processDefinitionMap,dm);
	    
	}

	public long startProcess(String processName, Map contextData, String lockKey)
			throws WorkflowException {
		return super.startProcess(processName, contextData, lockKey);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public long startProcess(String processName, Map contextData)
			throws WorkflowException {
		long retId = 0;
		log.debug("Save processcontext before run a instance.processName:" + processName);
		Object pdObject = processDefinitionMap.get(processName);
		// 如果上层抛错，把得到的异常继续抛出去。
		if (pdObject instanceof Exception) {
			throw new WorkflowException("Get process activityDefinition " + processName + " catch a exception!",
					(Exception) pdObject);
		}
		if (null == pdObject) {
			throw new WorkflowException("ProcessDefinition " + processName + " not exist！(Maybe name is wrong)");
		} else {
			// new ProcessContext
			ProcessDefinition processDefinition = (ProcessDefinition) pdObject;
			ProcessContext pc = new CmppProcessContext();
			pc.setLockKey((String) contextData.get(LOCK_KEY));
			contextData.remove(LOCK_KEY);
			pc.setProcessDefinitionName(processName);
			pc.setState(ProcessContext.READY);
			pc.setActivity(processDefinition.getFirstActivityName());
			pc.setNextActivity(processDefinition.getFirstActivityName());
			pc.setProcessStartDate(Calendar.getInstance().getTime());
			pc.setActivityStartDate(Calendar.getInstance().getTime());
			pc.setStateStartDate(Calendar.getInstance().getTime());
			Map dataPool = (Map) contextData.get("dataPool");
			
//			if (dataPool.get("nodeId") == null || dataPool.get("formId") == null
//					|| dataPool.get("articleId") == null || dataPool.get("instanceDesc") == null) {
//				try {
//					throw new Exception("nodeId, formId, articleId, instanceDesc can not be null");
//				} catch (Exception e) {
//					log.error("nodeId, formId, articleId, instanceDesc can not be null", e);
//				}
//			}
			
			if (dataPool.get("nodeId") instanceof Double) {
				((CmppProcessContext)pc).setNodeid(((Double)dataPool.get("nodeId")).intValue());
			}else if (dataPool.get("nodeId") instanceof Integer) {
				((CmppProcessContext)pc).setNodeid((Integer)dataPool.get("nodeId"));
			}else if (dataPool.get("nodeId") instanceof String) {
				((CmppProcessContext)pc).setNodeid(Integer.parseInt((String) dataPool.get("nodeId")));
			}
			
			if (dataPool.get("formId") instanceof Double) {
				((CmppProcessContext)pc).setFormId(((Double)dataPool.get("formId")).intValue());
			}else if (dataPool.get("formId") instanceof Integer) {
				((CmppProcessContext)pc).setFormId((Integer)dataPool.get("formId"));
			}else if (dataPool.get("formId") instanceof String) {
				((CmppProcessContext)pc).setFormId(Integer.parseInt((String)dataPool.get("formId")));
			}
			
			if (dataPool.get("articleId") instanceof Double) {
				((CmppProcessContext)pc).setArticleId(((Double)dataPool.get("articleId")).longValue());
			}else if (dataPool.get("articleId") instanceof Integer) {
				((CmppProcessContext)pc).setArticleId(((Integer)dataPool.get("articleId")).longValue());
			}else if (dataPool.get("articleId") instanceof String) {
				((CmppProcessContext)pc).setArticleId(Long.parseLong((String)dataPool.get("articleId")));
			}
			
			String instanceDesc = (String) dataPool.get("instanceDesc");
			((CmppProcessContext)pc).setInstanceDesc(instanceDesc);
			CmppProcessDef processDef = null;
			try {
				processDef = (CmppProcessDef) processDefDm
						.queryById(Integer.parseInt(processName));
				
			} catch (DataManagerException e1) {
				log.error("Exception occured while query process definition:" + processName 
						+ " for process context");
			}
			if (processDef != null) {
				((CmppProcessContext)pc).setDefinitionVersion(processDef.getRecentModifyTime());
			}
			pc.setData(contextData);
            try {
				retId = startProcess(pc);
			} catch (DataManagerException e) {
				throw new WorkflowException(pc.getActivity()
						+ "Process activity error (maybe db error)。ProcessContext：" + pc, e);
			}
			// save into database
			
		}
		log.debug("Save processcontext success.instanceId:" + retId);
		return retId;
	}

	@Override
	public long startProcess(ProcessContext processContext)
			throws WorkflowException, DataManagerException {
		long retId=0;
		Object objId = dm.add(processContext, null);
		retId = Long.parseLong(objId.toString());
		return retId;
	}

	@Override
	public void continueProcess(ProcessContext processContext)
			throws WorkflowException, DataManagerException {
		dm.modify(processContext, null, null);
	}

	@Override
	public boolean afterRunProcess(ProcessContext processContext)
			throws WorkflowException {
		boolean ret = true;
		try {
			if (processContext.getState() == ProcessContext.SUSPENDED) {
				if (log.isDebugEnabled()) {
					log.debug("A activeity is SUSPENDED，ProcessContext：" + processContext);
				}
				dm.modify(processContext, null, null);
			} else if (null == processContext.getNextActivity()) {
				if (log.isDebugEnabled()) {
					log.debug("A activeity is processed，ProcessContext：" + processContext);
				}
				Map dataMap = processContext.getData();
				Map dataPool = (Map) dataMap.get("dataPool");
				if (dataPool.get("__isPending__") == null ? Boolean.FALSE : (Boolean)dataPool.get("__isPending__")) {
					processContext.setState(CmppProcessContext.PENDING);
					processContext.setNextActivity((String) dataPool.get("__nextActivity__"));
					processContext.setActivity((String) dataPool.get("__nextActivity__"));
					Long processInstanceId = 0L;
					if (dataPool.get("__instanceId__") instanceof Integer) {
						processInstanceId =  ((Integer)dataPool.get("__instanceId__")).longValue();
					}else if (dataPool.get("__instanceId__") instanceof Long) {
						processInstanceId = (Long)dataPool.get("__instanceId__");
					}
					log.info("Process instance is pending:" + processInstanceId);
				}else {
					log.info("Process instance is not pending and ends");
					if (processContext.getState() != 5) {
						processContext.setState(ProcessContext.SUSPENDED);
					}
					((CmppProcessContext)processContext).setInstanceEndDate(new Date());
				}
				log.info("Begins to modify processcontext. Activity:" + processContext.getActivity() 
						+ ",nextActivity:" + processContext.getNextActivity());
				dm.modify(processContext, null, null);
				//dm.delete(processContext, null);
				ret = false;
			} else {
				throw new WorkflowException("A activeity is processed ,but it's not nomal exit！ProcessContext："
						+ processContext);
			}
		} catch (JSONException e) {
			log.error("Can not convert dataPool to JSONObject", e);
			throw new WorkflowException("Can not convert dataPool to JSONObject");
		} catch (DataManagerException e) {
			log.error("Exception occured while modify processcontext", e);
			throw new WorkflowException("Exception occured while modify processcontext");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WorkflowException("A activeity is processed,but catch a error when save state！ProcessContext："
					+ processContext, ex);
		}
		return ret;
	}
	
	public List queryReadyProcessContext(long wfId) throws WorkflowException {
		List ret = new ArrayList();
		final int intStart = 60 * 60 * 24 * 365;

		// 构造查询条件
//		Calendar endTime = Calendar.getInstance();
//		endTime.add(Calendar.SECOND, 0 - second);
//		Calendar startTime = Calendar.getInstance();
//		startTime.add(Calendar.SECOND, 0 - intStart);

		Map query = new HashMap();
		query.put("state", new NormalQueryField(ProcessContext.READY));
		query.put("id", new NormalQueryField(wfId));
		//query.put("activityStartDate", new RangeQueryField(startTime.getTime(), endTime.getTime()));

		// 查询
		QueryResult queryResult = null;
		try {
			queryResult = dm.query(query, null);
		} catch (DataManagerException e) {
			throw new WorkflowException("Haven't process for this query:" + query, e);
		}

		// 激活
		if (queryResult != null) {
			List list = queryResult.getData(0, queryResult.getRowCount());
			for (Iterator it = list.iterator(); it.hasNext();) {
				ProcessContext temp = (ProcessContext) it.next();

				// 只激活当前引擎启动时读取的流程定义文件中包含的流程。
				if (temp.isExist(this)) {
					temp.activation(this);
					ret.add(temp);
					if (log.isDebugEnabled()) {
						log.debug("Find a ready process: " + temp);
					}
				}
			}
		}
		return ret;
	}
	
	
	@Override
	public void setProcessDefinitionMap(Map map) {
		this.processDefinitionMap = map;
	}
	
	public Map getProcessDefinitionMap() {
		return processDefinitionMap;
	}

	public void setProcessDefDm(PersistDataManager processDefDm) {
		this.processDefDm = processDefDm;
	}
}
