package com.me.GCDP.workflow.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.dm.PersistManagerFactoryHibernateForCmppImpl;
import com.me.GCDP.workflow.model.PluginStatus;
import com.ifeng.common.dm.DataManagerException;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.dm.QueryResult;
import com.ifeng.common.dm.persist.HQuery;

@SuppressWarnings({"rawtypes","unchecked"})
public class WorkflowStatusService {
	
	public WorkflowStatusService() {}
	
	private static PersistDataManager flowStatusDM;
	
	private static Log log = LogFactory.getLog(WorkflowStatusService.class);
	
	static {
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		flowStatusDM = new PersistDataManager(PluginStatus.class, dmFactory);
	}
	
	/**
	 * 查询一个流程实例的运行情况
	 * @param nodeId		节点id
	 * @param formId		表单id
	 * @param articleId		文章id
	 * @return Object数组，其中第一个元素是CmppProcessContext的实例，
	 * 			第二个元素是List<PluginStatus>对象
	 */
	public Object[] queryWorkflowInstanceStatus(int nodeId, int formId, long articleId) {
		Object[] ret = new Object[2];
		try {
			//首先通过nodeId、formId、articleId查询到唯一的CmppProcessContext
			HQuery processContextQuery = new HQuery();
			processContextQuery.setQueryString("from CmppProcessContext p where p.nodeid = ? " +
					"and p.formId = ? and p.articleId = ?");
			List processContextParamList = new ArrayList();
			processContextParamList.add(nodeId);
			processContextParamList.add(formId);
			processContextParamList.add(articleId);
			processContextQuery.setParalist(processContextParamList);
			QueryResult processContextResult = flowStatusDM.query(processContextQuery);
			List processContextList = processContextResult.getData(0, 
										processContextResult.getRowCount());
			ret[0] = processContextList.get(0);
			
			HQuery pluginStatusQuery = new HQuery();
			pluginStatusQuery.setQueryString("from PluginStatus p where p.processContext.id = ?");
			List pluginStatusParamList = new ArrayList();
			pluginStatusParamList.add(((CmppProcessContext)processContextList.get(0)).getId());
			pluginStatusQuery.setParalist(pluginStatusParamList);
			QueryResult pluginStatusResult = flowStatusDM.query(pluginStatusQuery);
			ret[1] = pluginStatusResult.getData(0, pluginStatusResult.getRowCount());
		} catch (DataManagerException e) {
			e.printStackTrace();
			log.error("Exception occured while query workflow status info of [nodeId=" 
					+ nodeId + ",formId=" + formId + ", articleId=" + articleId + "]", e);
		}
		return ret;
	}
	
	
	
}
