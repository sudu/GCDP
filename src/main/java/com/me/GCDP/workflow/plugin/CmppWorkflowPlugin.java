package com.me.GCDP.workflow.plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.WorkflowImpl;
import com.me.GCDP.workflow.WorkflowService;
import com.me.GCDP.workflow.dm.PersistManagerFactoryHibernateForCmppImpl;
import com.me.GCDP.workflow.model.CmppPluginDef;
import com.me.GCDP.workflow.model.PluginStatus;
import com.ifeng.common.dm.DataManagerException;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.dm.QueryResult;
import com.ifeng.common.dm.persist.HQuery;
import com.ifeng.common.plugin.core.itf.IntfPlugin;
import com.ifeng.common.plugin.process.AndSuite;
import com.ifeng.common.plugin.process.SwitchSuite;
import com.ifeng.common.workflow.ActivityDefinition;
import com.ifeng.common.workflow.ProcessContext;
import com.ifeng.common.workflow.ProcessDefinition;
import com.ifeng.common.workflow.WorkflowException;
import com.me.GCDP.script.ScriptThreadLocal;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.plugin.UtilPlugin;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;

@SuppressWarnings({"rawtypes", "unchecked"})


@PluginClass(author = "xiongfeng", intro = "工作流运行插件",tag="工作流")
@PluginExample(intro = "//运行一个流程实例<br/> " +
		"var wf = pluginFactory.getP(\"wf\");<br/> " +
		"dataPool.put(\"instanceDesc\",\"test\");<br/>" +
		"wf.runProcess(\"24\", dataPool,3,4,5);<br/>")
public class CmppWorkflowPlugin extends ScriptPlugin {
	private static Log log = LogFactory.getLog(CmppWorkflowPlugin.class);
	
	@Override
	public void init() {
	}

	@PluginIsPublic
	@PluginMethod(intro = "指定流程id,节点id,表单id,表单记录id,传入dataPool,运行流程",
					paramIntro = { "流程id", "已经准备好初始数据的dataPool","节点id","表单id","表单记录id"}
					,returnIntro = "流程运行完之后的dataPool")
	public Map runProcess(String processId, Map dataPool, int nodeId, int formId, int articleId) 
			throws Exception {
		if (nodeId < 0 || articleId < 0) {
			throw new Exception("nodeId或者articleId小于0");
		}
		if (processId == null || "".equals(processId)) {
			throw new Exception("processId不允许为null或者空字符串");
		}
		SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
				.getBean("sessionFactory");
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory); 
		PersistDataManager processContextDm = new 
				PersistDataManager(CmppProcessContext.class, dmFactory);
		dataPool.put("nodeId", nodeId);
		dataPool.put("formId", formId);
		dataPool.put("articleId", articleId);
		long id = 0L;
		try {
			log.info("Starts to run process:" + processId + " from plugin.nodeId:" 
					+ nodeId + "|formId:" + formId + "|articleId:" + articleId);
			WorkflowService workflowService = (WorkflowService) SpringContextUtil
					.getBean("workflowService");
			id = workflowService.startWorkFlow(processId.trim(), dataPool);
			//ScriptThreadLocal.removeScriptIds();
			log.info("Ends to run process:" + processId + " from plugin successfully");
		} catch (WorkflowException e) {
			log.error("Exception occured while run process:" + processId, e);
			throw e;
		}
		CmppProcessContext pc = null;
		if (id > 0) {
			pc = (CmppProcessContext) processContextDm.queryById(id);
		}
		
		if (pc != null) {
			return (Map) pc.getData().get("dataPool");
		}else {
			return null;
		}
		
		
	}
	
	@PluginIsPublic
	@PluginMethod(intro="当某个流程实例执行过程中出现异常，可以从异常的那个插件开始重试执行"
				,paramIntro={"需要重试的流程实例id"})
	public void retryProcess(long instanceId) throws Exception{
		SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
				.getBean("sessionFactory");
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory); 
		PersistDataManager processContextDm = new 
				PersistDataManager(CmppProcessContext.class, dmFactory);
		log.info("Starts to retry instance.instanceId:" + instanceId);
		CmppProcessContext pc = null;
		QueryResult exPluginResult = null;
		try {
			log.debug("Query ProcessContext to retry.instanceId:" + instanceId);
			pc = (CmppProcessContext) processContextDm.queryById(instanceId);
		} catch (DataManagerException e) {
			log.error("Exception occured while query processcontext to retry.instanceId:" + instanceId, e);
			throw new Exception("查询指定的流程实例出错",e);
		}
		if (pc != null && pc.getState() == 5) {
			HQuery exPluginQuery = new HQuery();
			exPluginQuery.setQueryString("from PluginStatus p where p.processContext.id = ? and p.status = ? order by p.pluginStartTime desc");
			List paramList = new ArrayList();
			paramList.add(instanceId);
			paramList.add((byte)2);
			exPluginQuery.setParalist(paramList);
			try {
				log.debug("Query Exception PluginStatus(retryProcess).instanceId:" + instanceId);
				exPluginResult = processContextDm.query(exPluginQuery);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginStatus(retryProcess).instanceId:" + instanceId);
				throw new Exception("查询插件运行状态出错",e);
			}
			if (exPluginResult != null) {
				List exPluginList = exPluginResult.getData(0, exPluginResult.getRowCount());
				if (exPluginList.size() > 0) {
					PluginStatus status = (PluginStatus) exPluginList.get(0);
					WorkflowService workflowService = (WorkflowService) SpringContextUtil
							.getBean("workflowService");
					Map pdMap = ((WorkflowImpl)workflowService.getWf()).getProcessDefinitionMap();
					ProcessDefinition pd = (ProcessDefinition) pdMap.get(pc.getProcessDefinitionName());
					log.debug("Query activity definition of the exception plugin");
					ActivityDefinition ad = null;
					if (status.getActivityName() == null) {
						ad = pd.getActivity(status.getPluginDef().getActivity().getActivityId() + "");
					}else {
						ad = pd.getActivity(status.getActivityName());
					}
					
					IntfPlugin plugin = ad.getPlugin();
					
					if (plugin instanceof SwitchSuite) {
						//异常插件所在的活动是分支活动
						log.debug("Exception plugin belongs to SwitchSuite");
						Map modules = ((SwitchSuite)plugin).getStepModulesMap();
						boolean isCondition = true;
						for(Object obj : modules.keySet()) {
							com.me.GCDP.workflow.plugin.ScriptPlugin module = 
									(com.me.GCDP.workflow.plugin.ScriptPlugin) modules.get(obj);
							if (module.getPluginDefId().equals(status.getPluginDef().getPluginId() + "")) {
								isCondition = false;
								String ids[] = new String[3];
				            	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
				            	ids[1] = "process";
				            	ids[2] = pc.getProcessDefinitionName();
				            	ScriptThreadLocal.setScriptIDS(ids);
								ScriptThreadLocal.setInstanceId(pc.getId());
								log.info("Retry exception plugin.instanceId:" + instanceId);
								pc.setState(ProcessContext.RUN);
								module.execute(pc);
								Map contextData = pc.getData();
								Map currentDataPool = (Map) contextData.get("dataPool");
								boolean isPending = currentDataPool.get("__isPending__") == null 
										? Boolean.FALSE : (Boolean)currentDataPool.get("__isPending__");
								if (!isPending) {
									pc.setActivity(pc.getNextActivity());
									try {
										workflowService.getWf().runReadyProcessContext(pc);
									} catch (WorkflowException e) {
										log.error("Exception occured while retry remain process.instanceId:" + instanceId, e);
										throw e;
									} finally {
										ScriptThreadLocal.removeInstanceId();
										ScriptThreadLocal.removeScriptIds();
									}
								}else {
									pc.setState(4);
									currentDataPool.put("__isPending__", Boolean.TRUE);
									currentDataPool.put("__nextActivity__", module.getNextActivity());
									contextData.put("dataPool", currentDataPool);
									try {
										((WorkflowImpl)workflowService.getWf()).afterRunProcess(pc);
									} catch (WorkflowException e) {
										log.error("Exception occured while retry remain process.instanceId:" + instanceId, e);
										throw e;
									} finally {
										ScriptThreadLocal.removeInstanceId();
										ScriptThreadLocal.removeScriptIds();
									}
								}
								break;
							}
						}
						if (isCondition) {
							log.error("异常发生在分支活动的条件插件上，该情况下目前不支持重试操作");
							throw new Exception("异常发生在分支活动的条件插件上，该情况下目前不支持重试操作");
						}
					}else if (plugin instanceof AndSuite) {
						//异常插件所在的活动是顺序活动
						log.debug("Exception plugin belongs to AndSuite");
						String ids[] = new String[3];
		            	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
		            	ids[1] = "process";
		            	ids[2] = pc.getProcessDefinitionName();
		            	ScriptThreadLocal.setScriptIDS(ids);
						ScriptThreadLocal.setInstanceId(pc.getId());
						IntfPlugin andPlugin = ((AndSuite)plugin).getStepModules().get(0);
						log.info("Retry exception plugin.instanceId:" + instanceId);
						pc.setState(ProcessContext.RUN);
						andPlugin.execute(pc);
						Map contextData = pc.getData();
						Map currentDataPool = (Map) contextData.get("dataPool");
						boolean isPending = currentDataPool.get("__isPending__") == null 
								? Boolean.FALSE : (Boolean)currentDataPool.get("__isPending__");
						if (!isPending) {
							pc.setActivity(pc.getNextActivity());
							try {
								workflowService.getWf().runReadyProcessContext(pc);
							} catch (WorkflowException e) {
								log.error("Exception occured while retry remain process.instanceId:" + instanceId, e);
								throw e;
							} finally {
								ScriptThreadLocal.removeInstanceId();
								ScriptThreadLocal.removeScriptIds();
							}
						}else {
							pc.setState(4);
							currentDataPool.put("__isPending__", Boolean.TRUE);
							currentDataPool.put("__nextActivity__", ((com.me.GCDP.workflow.plugin.ScriptPlugin)andPlugin).getNextActivity());
							contextData.put("dataPool", currentDataPool);
							try {
								((WorkflowImpl)workflowService.getWf()).afterRunProcess(pc);
							} catch (WorkflowException e) {
								log.error("Exception occured while retry remain process.instanceId:" + instanceId, e);
								throw e;
							} finally {
								ScriptThreadLocal.removeInstanceId();
								ScriptThreadLocal.removeScriptIds();
							}
						}
						
						
					}else if (plugin instanceof AsyncSuite) {
						//异常插件所在的活动是异步活动
						log.debug("Exception plugin belongs to AsyncSuite");
						log.error("异常发生在异步活动的条件插件上，该情况下目前不支持重试操作");
						throw new Exception("异常发生在异步活动的条件插件上，该情况下目前不支持重试操作");						
					}
				}
			}
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro="当某个流程实例执行过程中出现异常，可以从异常的那个插件开始重试执行", 
				paramIntro = { "nodeId","formId","articleId" })
	public void retryProcess(int nodeId, int formId, int articleId) throws Exception{
		SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
				.getBean("sessionFactory");
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory); 
		PersistDataManager processContextDm = new 
				PersistDataManager(CmppProcessContext.class, dmFactory);
		log.info("Starts to retry instance.nodeId:" + nodeId + 
				"|formId:" + formId + "|articleId:" + articleId);
		CmppProcessContext pc = null;
		QueryResult exPluginResult = null;
		try {
			log.debug("Query ProcessContext to retry.nodeId:" + nodeId + 
					"|formId:" + formId + "|articleId:" + articleId);
//			pc = (CmppProcessContext) processContextDm.queryById(instanceId);
			HQuery pcQuery = new HQuery();
			pcQuery.setQueryString("from CmppProcessContext pc where pc.nodeid = ? " +
					"and pc.formId = ? and pc.articleId = ? and pc.state = 5 " +
					"order by pc.id desc");
			List pcParamList = new ArrayList();
			pcParamList.add(nodeId);
			pcParamList.add(formId);
			pcParamList.add((long)articleId);
			pcQuery.setParalist(pcParamList);
			QueryResult pcQueryResult = processContextDm.query(pcQuery);
			List pcResultList = pcQueryResult.getData(0, pcQueryResult.getRowCount());
			if (pcResultList.size() > 0) {
				pc = (CmppProcessContext) pcResultList.get(0);
			}
		} catch (DataManagerException e) {
			log.error("Exception occured while query processcontext to retry.nodeId:" + nodeId + 
					"|formId:" + formId + "|articleId:" + articleId, e);
			throw new Exception("查询指定的流程实例出错",e);
		}
		if (pc != null) {
			HQuery exPluginQuery = new HQuery();
			exPluginQuery.setQueryString("from PluginStatus p where p.processContext.id = ? and p.status = ? order by p.pluginStartTime desc");
			List paramList = new ArrayList();
			paramList.add(pc.getId());
			paramList.add((byte)2);
			exPluginQuery.setParalist(paramList);
			try {
				log.debug("Query Exception PluginStatus(retryProcess).nodeId:" + nodeId + 
						"|formId:" + formId + "|articleId:" + articleId);
				exPluginResult = processContextDm.query(exPluginQuery);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginStatus(retryProcess).nodeId:" + nodeId + 
						"|formId:" + formId + "|articleId:" + articleId);
				throw new Exception("查询插件运行状态出错",e);
			}
			if (exPluginResult != null) {
				List exPluginList = exPluginResult.getData(0, exPluginResult.getRowCount());
				if (exPluginList.size() > 0) {
					PluginStatus status = (PluginStatus) exPluginList.get(0);
					WorkflowService workflowService = (WorkflowService) SpringContextUtil
							.getBean("workflowService");
					Map pdMap = ((WorkflowImpl)workflowService.getWf()).getProcessDefinitionMap();
					ProcessDefinition pd = (ProcessDefinition) pdMap.get(pc.getProcessDefinitionName());
					log.debug("Query activity definition of the exception plugin");
					ActivityDefinition ad = null;
					if (status.getActivityName() == null) {
						ad = pd.getActivity(status.getPluginDef().getActivity().getActivityId() + "");
					}else {
						ad = pd.getActivity(status.getActivityName());
					}
					
					IntfPlugin plugin = ad.getPlugin();
					
					if (plugin instanceof SwitchSuite) {
						//异常插件所在的活动是分支活动
						log.debug("Exception plugin belongs to SwitchSuite");
						Map modules = ((SwitchSuite)plugin).getStepModulesMap();
						boolean isCondition = true;
						for(Object obj : modules.keySet()) {
							com.me.GCDP.workflow.plugin.ScriptPlugin module = 
									(com.me.GCDP.workflow.plugin.ScriptPlugin) modules.get(obj);
							if (module.getPluginDefId().equals(status.getPluginDef().getPluginId() + "")) {
								isCondition = false;
								String ids[] = new String[3];
				            	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
				            	ids[1] = "process";
				            	ids[2] = pc.getProcessDefinitionName();
				            	ScriptThreadLocal.setScriptIDS(ids);
								ScriptThreadLocal.setInstanceId(pc.getId());
								log.info("Retry exception plugin.nodeId:" + nodeId + 
										"|formId:" + formId + "|articleId:" + articleId);
								pc.setState(ProcessContext.RUN);
								module.execute(pc);
								Map contextData = pc.getData();
								Map currentDataPool = (Map) contextData.get("dataPool");
								boolean isPending = currentDataPool.get("__isPending__") == null 
										? Boolean.FALSE : (Boolean)currentDataPool.get("__isPending__");
								if (!isPending) {
									pc.setActivity(pc.getNextActivity());
									try {
										workflowService.getWf().runReadyProcessContext(pc);
									} catch (WorkflowException e) {
										log.error("Exception occured while retry remain process.nodeId:" + nodeId + 
												"|formId:" + formId + "|articleId:" + articleId, e);
										throw e;
									} finally {
										ScriptThreadLocal.removeInstanceId();
										ScriptThreadLocal.removeScriptIds();
									}
								}else {
									pc.setState(4);
									currentDataPool.put("__isPending__", Boolean.TRUE);
									currentDataPool.put("__nextActivity__", module.getNextActivity());
									contextData.put("dataPool", currentDataPool);
									try {
										((WorkflowImpl)workflowService.getWf()).afterRunProcess(pc);
									} catch (WorkflowException e) {
										log.error("Exception occured while retry remain process.nodeId:" + nodeId + 
												"|formId:" + formId + "|articleId:" + articleId, e);
										throw e;
									} finally {
										ScriptThreadLocal.removeInstanceId();
										ScriptThreadLocal.removeScriptIds();
									}
								}
								break;
							}
						}
						if (isCondition) {
							log.error("异常发生在分支活动的条件插件上，该情况下目前不支持重试操作");
							throw new Exception("异常发生在分支活动的条件插件上，该情况下目前不支持重试操作");
						}
					}else if (plugin instanceof AndSuite) {
						//异常插件所在的活动是顺序活动
						log.debug("Exception plugin belongs to AndSuite");
						String ids[] = new String[3];
		            	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
		            	ids[1] = "process";
		            	ids[2] = pc.getProcessDefinitionName();
		            	ScriptThreadLocal.setScriptIDS(ids);
						ScriptThreadLocal.setInstanceId(pc.getId());
						IntfPlugin andPlugin = ((AndSuite)plugin).getStepModules().get(0);
						log.info("Retry exception plugin.nodeId:" + nodeId + 
								"|formId:" + formId + "|articleId:" + articleId);
						pc.setState(ProcessContext.RUN);
						andPlugin.execute(pc);
						Map contextData = pc.getData();
						Map currentDataPool = (Map) contextData.get("dataPool");
						boolean isPending = currentDataPool.get("__isPending__") == null 
								? Boolean.FALSE : (Boolean)currentDataPool.get("__isPending__");
						if (!isPending) {
							pc.setActivity(pc.getNextActivity());
							try {
								workflowService.getWf().runReadyProcessContext(pc);
							} catch (WorkflowException e) {
								log.error("Exception occured while retry remain process.nodeId:" + nodeId + 
										"|formId:" + formId + "|articleId:" + articleId, e);
								throw e;
							} finally {
								ScriptThreadLocal.removeInstanceId();
								ScriptThreadLocal.removeScriptIds();
							}
						}else {
							pc.setState(4);
							currentDataPool.put("__isPending__", Boolean.TRUE);
							currentDataPool.put("__nextActivity__", ((com.me.GCDP.workflow.plugin.ScriptPlugin)andPlugin).getNextActivity());
							contextData.put("dataPool", currentDataPool);
							try {
								((WorkflowImpl)workflowService.getWf()).afterRunProcess(pc);
							} catch (WorkflowException e) {
								log.error("Exception occured while retry remain process.nodeId:" + nodeId + 
										"|formId:" + formId + "|articleId:" + articleId, e);
								throw e;
							} finally {
								ScriptThreadLocal.removeInstanceId();
								ScriptThreadLocal.removeScriptIds();
							}
						}
						
						
					}else if (plugin instanceof AsyncSuite) {
						//异常插件所在的活动是异步活动
						log.debug("Exception plugin belongs to AsyncSuite");
						log.error("异常发生在异步活动的条件插件上，该情况下目前不支持重试操作");
						throw new Exception("异常发生在异步活动的条件插件上，该情况下目前不支持重试操作");						
					}
				}
			}
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro="挂起当前流程实例", paramIntro = { "当前的dataPool" })
	public void suspendProcess(Map dataPool) {
		long instanceId = 0L;
		if (dataPool.get("__instanceId__") instanceof Integer) {
			instanceId = ((Long) dataPool.get("__instanceId__")).longValue();
		}else if (dataPool.get("__instanceId__") instanceof Long) {
			instanceId = (Long) dataPool.get("__instanceId__");
		}
		log.info("Suspend instance:" + instanceId);
		dataPool.put("__isPending__", Boolean.TRUE);
	}
	
	@PluginIsPublic
	@PluginMethod(intro="挂起当前流程实例,并设置超时时间(分钟),一旦设置的超时时长到了，该流程实例还没有恢复，则邮件、短信报警"
	, paramIntro = { "当前的dataPool", "超时时间(分钟)", "报警信息" })
	public void suspendProcess(final Map dataPool, int min, final String msg) {
		long instanceId = 0L;
		if (dataPool.get("__instanceId__") instanceof Integer) {
			instanceId = ((Long) dataPool.get("__instanceId__")).longValue();
		}else if (dataPool.get("__instanceId__") instanceof Long) {
			instanceId = (Long) dataPool.get("__instanceId__");
		}
		log.info("Suspend instance:" + instanceId);
		dataPool.put("__isPending__", Boolean.TRUE);
		final Timer suspendTimoutChecker = new Timer();
		suspendTimoutChecker.schedule(new TimerTask() {
			@Override
			public void run() {
				SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
						.getBean("sessionFactory");
				PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
						PersistManagerFactoryHibernateForCmppImpl();
				dmFactory.setSessionFactory(sessionFactory); 
				PersistDataManager processContextDm = new 
						PersistDataManager(CmppProcessContext.class, dmFactory);
				PersistDataManager pluginStatusDm = new 
						PersistDataManager(PluginStatus.class, dmFactory);
				PersistDataManager pluginDefDm = new 
						PersistDataManager(CmppPluginDef.class, dmFactory);
				CmppProcessContext processContext = null;
				QueryResult pluginStatusResult = null;
				long instanceId = 0L;
				if (dataPool.get("__instanceId__") instanceof Integer) {
					instanceId = ((Long) dataPool.get("__instanceId__")).longValue();
				}else if (dataPool.get("__instanceId__") instanceof Long) {
					instanceId = (Long) dataPool.get("__instanceId__");
				}
				log.info("Suspend instance and run suspendTimoutChecker.instanceId:" + instanceId);
				String curPlugin = (String) (dataPool.get("__curPlugin__") 
						== null ? "0" : dataPool.get("__curPlugin__"));
				int curPluginId = Integer.parseInt(curPlugin);
				if (instanceId > 0) {
					log.debug("Query processcontext in suspendTimoutChecker.instanceId:" + instanceId);
					try {
						processContext = (CmppProcessContext) processContextDm
								.queryById(instanceId);
					} catch (DataManagerException e) {
						log.error("Exception occured while query processcontext " +
								"to check if suspend time is out. instanceid:" + instanceId);
					}
					if (processContext != null) {
						log.debug("Query PluginStatus in suspendTimoutChecker:pluginId=" + curPluginId + "|instancId:" + instanceId);
						HQuery pendingPluginStatusQuery = new HQuery();
						pendingPluginStatusQuery.setQueryString("from PluginStatus p " +
								"where p.pluginDef.pluginId = ? and p.processContext.id = ?");
						List paramList = new ArrayList();
						paramList.add(curPluginId);
						paramList.add(instanceId);
						pendingPluginStatusQuery.setParalist(paramList);
						try {
							pluginStatusResult = pluginStatusDm.query(pendingPluginStatusQuery);
						} catch (DataManagerException e) {
							log.error("Exception occured while query PluginStatus of " +
									"pending plugin to check if suspend time is out.instanceid:" 
									+ instanceId);
						}
						if (pluginStatusResult != null) {
							List pluginStatusList = pluginStatusResult
									.getData(0, pluginStatusResult.getRowCount());
							if (pluginStatusList.size() > 0) {
								PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
								if (pluginStatus.getStatus() != (byte)3 && curPluginId > 0) {
									log.debug("Query plugindef in suspendTimoutChecker.instanceId:" + instanceId);
									CmppPluginDef pluginDef = null;
									try {
										pluginDef = (CmppPluginDef) pluginDefDm.queryById(curPluginId);
									} catch (DataManagerException e) {
										log.error("Exception occured while query CmppPluginDef " +
												"to do timeout alarm.pluginid:" + curPluginId);
									}
									if (pluginDef != null) {
										log.info("Suspend timeout, starts to alarm.instanceId:" + instanceId);
										String mailTo = pluginDef.getMailTo();
										String smsTo = pluginDef.getSmsTo();
										ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil
												.getBean("pluginFactory");
										UtilPlugin util = (UtilPlugin) pluginFactory.getP("util");
										if (mailTo != null && !mailTo.trim().equals("")) {
											mailTo = mailTo.trim().replace("；", ",").replace(";", ",").replace("，", ",");
											util.sendMail(mailTo, "实例：" + instanceId + "挂起超时", msg, null);
//											String[] mailAddrs = mailTo.split(";");
//											for (String str : mailAddrs) {
//												util.sendMail(str, "实例：" + instanceId + "挂起超时", msg, null);
//											}
										}
										
										if (smsTo != null && !smsTo.trim().equals("")) {
											smsTo = smsTo.trim().replace("；", ";").replace(",", ";").replace("，", ";");
											String[] mobiles = smsTo.split(";");
											for (String str : mobiles) {
												//util.sendSMS(str, msg);
											}
										}
									}
								}
							}
						}
					}
				}
				suspendTimoutChecker.cancel();
			}
		}, (long)(min * 60 * 1000));
		
		return;
	}
	
	@PluginIsPublic
	@PluginMethod(intro="传入流程实例id，恢复流程实例", 
					paramIntro = { "流程实例id", "是否以同步的方式恢复流程实例(true:同步;false:异步)" }, 
					exceptionInfo="可能抛出工作流异常、数据库操作异常、JSON转换异常")
	public void resumeProcess(Long pInstanceId, boolean isSync) 
			throws Exception {
		resumeProcess(pInstanceId, new HashMap(), isSync);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "传入流程实例id,以及需要被合并的dataPool,恢复该流程实例",
				paramIntro = { "流程实例id", "需要被合并的dataPool", "是否以同步的方式恢复流程实例(true:同步;false:异步)"})
	public void resumeProcess(final Long pInstanceId
			, final Map dataPool, boolean isSync) throws Exception {
		if (pInstanceId <= 0) {
			throw new Exception("流程id不能小于0");
		}
		log.info("Starts to resume process instance.instanceId:" + pInstanceId);
		if (isSync) {
			SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
					.getBean("sessionFactory");
			PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
					PersistManagerFactoryHibernateForCmppImpl();
			dmFactory.setSessionFactory(sessionFactory); 
			PersistDataManager processContextDm = new 
					PersistDataManager(CmppProcessContext.class, dmFactory);
			PersistDataManager pluginStatusDm = new 
					PersistDataManager(PluginStatus.class, dmFactory);
			CmppProcessContext processContext = null;
			log.debug("Query processcontext to be resumed." + pInstanceId);
			try {
				processContext = (CmppProcessContext) processContextDm
						.queryById(pInstanceId);
			}catch(DataManagerException e) {
				log.error("Exception occured while query process instance for resume:"+ pInstanceId, e);
			}
			QueryResult pluginStatusResult = null;
			Map oldDataPool = null;
			
			if (processContext != null && processContext.getState() == 4) {
				try{
					log.info("Modify state of process instance to 2.instanceId:" + pInstanceId);
					processContext.setState(2);
					processContextDm.modify(processContext, new String[]{"state"}, null);
					String ids[] = new String[3];
	            	ids[0] = ((CmppProcessContext)processContext).getNodeid() + "";
	            	ids[1] = "process";
	            	ids[2] = processContext.getProcessDefinitionName();
	            	ScriptThreadLocal.setScriptIDS(ids);
	            	ScriptThreadLocal.setInstanceId(processContext.getId());
	            	
	            	Map oldDataMap = processContext.getData();
					oldDataPool = (Map) oldDataMap.get("dataPool");
					oldDataPool.putAll(dataPool);
					oldDataPool.remove("__isPending__");
	            	
					HQuery query = new HQuery();
					log.debug("Query PluginStatus of the pending plugin.instanceId:" + pInstanceId);
					query.setQueryString("from PluginStatus p where p.pluginDef.pluginId " +
							"= ? and p.processContext.id = ? order by p.pluginStatusId desc");
					List paramList = new ArrayList();
					paramList.add(oldDataPool.get("__curPlugin__") == null ? 0 
							: Integer.parseInt((String) oldDataPool.get("__curPlugin__")));
					paramList.add(pInstanceId);
					query.setParalist(paramList);
					pluginStatusResult = pluginStatusDm.query(query);
				}catch (DataManagerException e) {
					log.error("Exception occured while query pending plugin status of "+ pInstanceId, e);
				}
				try {
					List pluginStatusList = pluginStatusResult
							.getData(0,pluginStatusResult.getRowCount());
					if (pluginStatusResult.getRowCount() > 0) {
						PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
						log.info("Modify pluginstatus to 3.pluginStatusId:" 
								+ pluginStatus.getPluginStatusId()+"|instanceId:" + pInstanceId);
						pluginStatus.setStatus((byte)3);
						pluginStatus.setPluginEndTime(new Date());
						pluginStatusDm.modify(pluginStatus, new String[]{"status", "pluginEndTime"}, null);
					}
					Map dataMap = processContext.getData();
					dataMap.put("dataPool", oldDataPool);
					processContext.setData(dataMap);
					WorkflowService workflowService = (WorkflowService) SpringContextUtil
							.getBean("workflowService");
					log.info("Relaunch pending instance.instanceId" + pInstanceId);
					workflowService.getWf().runReadyProcessContext(processContext);
				} catch (DataManagerException e) {
					log.error("Exception occured while modify plugin status.instanceId:" + pInstanceId, e);
				} catch (WorkflowException e) {
					log.error("Exception occured while resume pending process instance:" + pInstanceId, e);
				} finally {
					ScriptThreadLocal.removeInstanceId();
				}
			}
		}else {
			new Thread() {
				@Override
				public void run() {
					SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
							.getBean("sessionFactory");
					PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
							PersistManagerFactoryHibernateForCmppImpl();
					dmFactory.setSessionFactory(sessionFactory); 
					PersistDataManager processContextDm = new 
							PersistDataManager(CmppProcessContext.class, dmFactory);
					PersistDataManager pluginStatusDm = new 
							PersistDataManager(PluginStatus.class, dmFactory);
					CmppProcessContext processContext = null;
					log.debug("Query processcontext to be resumed." + pInstanceId);
					try {
						processContext = (CmppProcessContext) processContextDm
								.queryById(pInstanceId);
					}catch(DataManagerException e) {
						log.error("Exception occured while query process instance for resume:"+ pInstanceId, e);
					}
					QueryResult pluginStatusResult = null;
					Map oldDataPool = null;
					
					if (processContext != null && processContext.getState() == 4) {
						try{
							log.info("Modify state of process instance to 2.instanceId:" + pInstanceId);
							processContext.setState(2);
							processContextDm.modify(processContext, new String[]{"state"}, null);
							String ids[] = new String[3];
			            	ids[0] = ((CmppProcessContext)processContext).getNodeid() + "";
			            	ids[1] = "process";
			            	ids[2] = processContext.getProcessDefinitionName();
			            	ScriptThreadLocal.setScriptIDS(ids);
			            	ScriptThreadLocal.setInstanceId(processContext.getId());
			            	
			            	Map oldDataMap = processContext.getData();
							oldDataPool = (Map) oldDataMap.get("dataPool");
							oldDataPool.putAll(dataPool);
							oldDataPool.remove("__isPending__");
			            	
							log.debug("Query PluginStatus of the pending plugin.instanceId:" + pInstanceId);
							HQuery query = new HQuery();
							query.setQueryString("from PluginStatus p where p.pluginDef.pluginId " +
									"= ? and p.processContext.id = ? order by p.pluginStatusId desc");
							List paramList = new ArrayList();
							paramList.add(oldDataPool.get("__curPlugin__") == null ? 0 
									: Integer.parseInt((String) oldDataPool.get("__curPlugin__")));
							paramList.add(pInstanceId);
							query.setParalist(paramList);
							pluginStatusResult = pluginStatusDm.query(query);
						}catch (DataManagerException e) {
							log.error("Exception occured while query pending plugin status of "+ pInstanceId, e);
						}
						try {
							if (pluginStatusResult != null) {
								List pluginStatusList = pluginStatusResult
										.getData(0,pluginStatusResult.getRowCount());
								if (pluginStatusResult.getRowCount() > 0) {
									PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
									log.info("Modify pluginstatus to 3.pluginStatusId:" 
											+ pluginStatus.getPluginStatusId()+"|instanceId:" + pInstanceId);
									pluginStatus.setStatus((byte)3);
									pluginStatus.setPluginEndTime(new Date());
									pluginStatusDm.modify(pluginStatus, new String[]{"status", "pluginEndTime"}, null);
									Map dataMap = processContext.getData();
									dataMap.put("dataPool", oldDataPool);
									processContext.setData(dataMap);
									WorkflowService workflowService = (WorkflowService) SpringContextUtil
											.getBean("workflowService");
									log.info("Relaunch pending instance.instanceId" + pInstanceId);
									workflowService.getWf().runReadyProcessContext(processContext);
								}								
							}
						} catch (DataManagerException e) {
							log.error("Exception occured while modify plugin status.instanceId:" + pInstanceId, e);
						} catch (WorkflowException e) {
							log.error("Exception occured while resume pending process instance:" + pInstanceId, e);
						} finally {
							ScriptThreadLocal.removeInstanceId();
						}
					}
				}
				
			}.start();
			return;
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro="传入nodeId, formId, articleId，恢复这三者所能定位到的最新的一个流程实例",
		paramIntro = { "节点id","表单id","表单中的记录的id","是否以同步的方式恢复流程实例(true:同步;false:异步)" },
		exceptionInfo="可能抛出工作流异常、数据库操作异常、JSON转换异常")
	public void resumeProcess(int nodeId, int formId, int articleId, boolean isSync) 
			throws Exception {
		 resumeProcess(nodeId, formId, articleId, new HashMap(), isSync);
	}
	
	@PluginIsPublic
	@PluginMethod(intro="传入nodeId, formId, articleId，以及发生了变化的dataPool" +
			"，恢复这三者所能定位到的最新的一个流程实例",
		paramIntro = { "节点id","表单id","表单中的记录的id","发生了变化了的dataPool","是否以同步的方式恢复流程实例(true:同步;false:异步)" })
	public void resumeProcess(final int nodeId, final int formId, final int articleId, final Map dataPool, boolean isSync) throws Exception {
		if (nodeId <= 0 || articleId <= 0) {
			throw new Exception("nodeId和articleId不能小于0");
		}
		log.info("Starts to resume process.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
		if (isSync) {
			SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
					.getBean("sessionFactory");
			PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
					PersistManagerFactoryHibernateForCmppImpl();
			dmFactory.setSessionFactory(sessionFactory); 
			PersistDataManager processContextDm = new 
					PersistDataManager(CmppProcessContext.class, dmFactory);
			PersistDataManager pluginStatusDm = new 
					PersistDataManager(PluginStatus.class, dmFactory);
			CmppProcessContext processContext = null;
			try {
				HQuery pcQuery = new HQuery();
				log.debug("Query processcontext.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
				pcQuery.setQueryString("from CmppProcessContext pc where pc.nodeid = ? " +
						"and pc.formId = ? and pc.articleId = ? and pc.state = 4 " +
						"order by pc.id desc");
				List pcParamList = new ArrayList();
				pcParamList.add(nodeId);
				pcParamList.add(formId);
				pcParamList.add((long)articleId);
				pcQuery.setParalist(pcParamList);
				QueryResult pcQueryResult = processContextDm.query(pcQuery);
				List pcResultList = pcQueryResult.getData(0, pcQueryResult.getRowCount());
				if (pcResultList.size() > 0) {
					processContext = (CmppProcessContext) pcResultList.get(0);
				}
			}catch (DataManagerException e) {
				log.error("Exception occured while query process instance. nodeId:" 
						+ nodeId + "|formId:" + formId + "|articleId:" + articleId, e);
			}
			try {
				if (processContext != null) {
					log.info("Modify state of pending process instance to 2.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
					processContext.setState(2);
					processContextDm.modify(processContext, new String[]{"state"}, null);
					String ids[] = new String[3];
	            	ids[0] = ((CmppProcessContext)processContext).getNodeid() + "";
	            	ids[1] = "process";
	            	ids[2] = processContext.getProcessDefinitionName();
	            	ScriptThreadLocal.setScriptIDS(ids);
	            	ScriptThreadLocal.setInstanceId(processContext.getId());
					
					Map oldDataMap = processContext.getData();
					Map oldDataPool = (Map) oldDataMap.get("dataPool");
					oldDataPool.putAll(dataPool);
					oldDataPool.remove("__isPending__");
					
					HQuery query = new HQuery();
					log.debug("Query PluginStatus of the pending plugin.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
					query.setQueryString("from PluginStatus p where p.pluginDef.pluginId = ? " +
							"and p.processContext.id = ? order by p.pluginStatusId desc");
					List paramList = new ArrayList();
					paramList.add(oldDataPool.get("__curPlugin__") == null ? 0 
							: Integer.parseInt((String) oldDataPool.get("__curPlugin__")));
					paramList.add(processContext.getId());
					query.setParalist(paramList);
					QueryResult pluginStatusResult = pluginStatusDm.query(query);
					List pluginStatusList = pluginStatusResult.getData(0, pluginStatusResult.getRowCount());
					if (pluginStatusResult.getRowCount() > 0) {
						PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
						log.info("Modify pluginstatus to 3.pluginStatusId:" 
								+ pluginStatus.getPluginStatusId()+"|nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
						pluginStatus.setStatus((byte)3);
						pluginStatus.setPluginEndTime(new Date());
						pluginStatusDm.modify(pluginStatus, new String[]{"status", "pluginEndTime"}, null);
					}
					Map dataMap = processContext.getData();
					dataMap.put("dataPool", oldDataPool);
					processContext.setData(dataMap);
					WorkflowService workflowService = (WorkflowService) SpringContextUtil
							.getBean("workflowService");
					log.info("Relaunch pending instance.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
					workflowService.getWf().runReadyProcessContext(processContext);
				}
			} catch (DataManagerException e) {
				log.error("Exception occured while query and modify plugin status", e);
			} catch (WorkflowException e) {
				log.error("Exception occured while resume pending process instance:" + processContext.getId(), e);
			} finally {
				ScriptThreadLocal.removeInstanceId();
			}
		}else {
			new Thread() {
				public void run() {
					SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
							.getBean("sessionFactory");
					PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
							PersistManagerFactoryHibernateForCmppImpl();
					dmFactory.setSessionFactory(sessionFactory); 
					PersistDataManager processContextDm = new 
							PersistDataManager(CmppProcessContext.class, dmFactory);
					PersistDataManager pluginStatusDm = new 
							PersistDataManager(PluginStatus.class, dmFactory);
					CmppProcessContext processContext = null;
					try {
						HQuery pcQuery = new HQuery();
						log.debug("Query processcontext.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
						pcQuery.setQueryString("from CmppProcessContext pc where pc.nodeid = ? " +
								"and pc.formId = ? and pc.articleId = ? and pc.state = 4 " +
								"order by pc.id desc");
						List pcParamList = new ArrayList();
						pcParamList.add(nodeId);
						pcParamList.add(formId);
						pcParamList.add((long)articleId);
						pcQuery.setParalist(pcParamList);
						QueryResult pcQueryResult = processContextDm.query(pcQuery);
						List pcResultList = pcQueryResult.getData(0, pcQueryResult.getRowCount());
						if (pcResultList.size() > 0) {
							processContext = (CmppProcessContext) pcResultList.get(0);
						}
					}catch (DataManagerException e) {
						log.error("Exception occured while query process instance. nodeId:" 
								+ nodeId + ",formId:" + formId + ",articleId:" + articleId, e);
					}
					try {
						if (processContext != null) {
							processContext.setState(2);
							log.info("Modify state of pending process instance to 2.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
							processContextDm.modify(processContext, new String[]{"state"}, null);
							String ids[] = new String[3];
			            	ids[0] = ((CmppProcessContext)processContext).getNodeid() + "";
			            	ids[1] = "process";
			            	ids[2] = processContext.getProcessDefinitionName();
			            	ScriptThreadLocal.setScriptIDS(ids);
			            	ScriptThreadLocal.setInstanceId(processContext.getId());
							
							Map oldDataMap = processContext.getData();
							Map oldDataPool = (Map) oldDataMap.get("dataPool");
							oldDataPool.putAll(dataPool);
							oldDataPool.remove("__isPending__");
							
							HQuery query = new HQuery();
							log.debug("Query PluginStatus of the pending plugin.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
							query.setQueryString("from PluginStatus p where p.pluginDef.pluginId = ? " +
									"and p.processContext.id = ? order by p.pluginStatusId desc");
							List paramList = new ArrayList();
							paramList.add(oldDataPool.get("__curPlugin__") == null ? 0 
									: Integer.parseInt((String) oldDataPool.get("__curPlugin__")));
							paramList.add(processContext.getId());
							query.setParalist(paramList);
							QueryResult pluginStatusResult = pluginStatusDm.query(query);
							List pluginStatusList = pluginStatusResult.getData(0, pluginStatusResult.getRowCount());
							if (pluginStatusResult.getRowCount() > 0) {
								PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
								log.info("Modify pluginstatus to 3.pluginStatusId:" 
										+ pluginStatus.getPluginStatusId()+"|nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
								pluginStatus.setStatus((byte)3);
								pluginStatus.setPluginEndTime(new Date());
								pluginStatusDm.modify(pluginStatus, new String[]{"status", "pluginEndTime"}, null);
							}
							Map dataMap = processContext.getData();
							dataMap.put("dataPool", oldDataPool);
							processContext.setData(dataMap);
							WorkflowService workflowService = (WorkflowService) SpringContextUtil
									.getBean("workflowService");
							log.info("Relaunch pending instance.nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
							workflowService.getWf().runReadyProcessContext(processContext);
						}
					} catch (DataManagerException e) {
						log.error("Exception occured while query and modify plugin status", e);
					} catch (WorkflowException e) {
						log.error("Exception occured while resume pending process instance:" + processContext.getId(), e);
					} finally {
						ScriptThreadLocal.removeInstanceId();
					}
					
				}
			}.start();
			return;
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro="远端服务出现错误时，终止该流程实例并报警",
				paramIntro = { "流程实例id", "错误信息"},
				exceptionInfo="可能抛出工作流异常、数据库操作异常、JSON转换异常")
	public void alarmOnRemoteServiceError(Long pInstanceId, String msg) 
			throws Exception{
		if (pInstanceId <= 0) {
			throw new Exception("pInstanceId不能小于等于0");
		}
		if (msg == null || "".equals(msg.trim())) {
			throw new Exception("msg不能为null或空字符串");
		}
		log.info("Alarm on remote service error.instanceId:" + pInstanceId);
		SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
				.getBean("sessionFactory");
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory); 
		PersistDataManager processContextDm = new 
				PersistDataManager(CmppProcessContext.class, dmFactory);
		PersistDataManager pluginDefDm = new 
				PersistDataManager(CmppPluginDef.class, dmFactory);
		PersistDataManager pluginStatusDm = new 
				PersistDataManager(PluginStatus.class, dmFactory);
		CmppProcessContext processContext;
		try {
			log.debug("Query process context(alarmOnRemoteServiceError)" +
					".instanceId:" + pInstanceId);
			processContext = (CmppProcessContext) processContextDm
					.queryById(pInstanceId);
		} catch (DataManagerException e) {
			log.error("Exception occured while query pc(alarmOnRemoteServiceError)" +
					".instanceId" + pInstanceId, e);
			throw e;
		}
		if (processContext != null && processContext.getState() == 4) {
			Map oldDataPool = (Map) processContext.getData().get("dataPool");
			int curPlugin = oldDataPool.get("__curPlugin__") == 
					null ? 0 : Integer.parseInt((String)oldDataPool.get("__curPlugin__"));
			log.debug("Query current PluginDef(alarmOnRemoteServiceError)" +
					".instanceId:" + pInstanceId + "|pluginId:" + curPlugin);
			CmppPluginDef curPluginDef;
			try {
				curPluginDef = (CmppPluginDef) pluginDefDm.queryById(curPlugin);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginDef" +
						"(alarmOnRemoteServiceError).instanceId" + pInstanceId 
						+ "|pluginId:" + curPlugin, e);
				throw e;
			}
			//远端服务出错报警
			if (curPluginDef != null) {
				String mailTo = curPluginDef.getMailTo();
				String smsTo = curPluginDef.getSmsTo();
				ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil
						.getBean("pluginFactory");
				UtilPlugin util = (UtilPlugin) pluginFactory.getP("util");
				if (mailTo != null && !mailTo.trim().equals("")) {
					log.debug("Send mail on remote service error.instanceId:" + pInstanceId);
					mailTo = mailTo.trim().replace('，', ',').replace(';', ',').replace('；', ',');
					util.sendMail(mailTo, "远端服务出错", msg, null);
//					String[] mailAddrs = mailTo.split(";");
//					for (String str : mailAddrs) {
//						util.sendMail(str, "远端服务出错", msg, null);
//					}
				}
				if (smsTo != null && !smsTo.trim().equals("")) {
					log.debug("Send sms on remote service error.instanceId:" + pInstanceId);
					smsTo = smsTo.trim().replace('，', ';').replace(',', ';').replace('；', ';');
					String[] mobiles = smsTo.split(";");
					for (String str : mobiles) {
						//util.sendSMS(str, msg);
					}
				}
			}
			//修改该插件的运行状态为异常状态
			HQuery query = new HQuery();
			query.setQueryString("from PluginStatus p where p.pluginDef.pluginId = ? " +
					"and p.processContext.id = ? order by p.pluginStatusId desc");
			List paramList = new ArrayList();
			paramList.add(curPlugin);
			paramList.add(pInstanceId);
			query.setParalist(paramList);
			QueryResult pluginStatusResult = null;
			try {
				pluginStatusResult = pluginStatusDm.query(query);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginStatus(alarmOnRemoteServiceError).instanceId" + pInstanceId + "|plugindefid:" + curPlugin, e);
				throw e;
			}
			if (pluginStatusResult != null) {
				List pluginStatusList = pluginStatusResult.getData(0, 
						pluginStatusResult.getRowCount());
				if (pluginStatusList.size() > 0) {
					PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
					log.info("Modify pluginstatus to 2(alarmOnRemoteServiceError)." +
						"intanceId:" + pInstanceId + "|pluginStatusId:" 
						+ pluginStatus.getPluginStatusId());
					pluginStatus.setStatus((byte)2);
					try {
						pluginStatusDm.modify(pluginStatus, new String[]{"status"}, null);
					} catch (DataManagerException e) {
						log.error("Exception occured while modify PluginStatus(alarmOnRemoteServiceError).instanceId" + pInstanceId + "|pluginstatusid:" + pluginStatus.getPluginStatusId(), e);
						throw e;
					}
				}
			}
			
			//修改流程实例的状态为异常结束状态
			processContext.setState(5);
			log.info("Modify process state to 5(alarmOnRemoteServiceError).instanceId:" + pInstanceId);
			try {
				processContextDm.modify(processContext, new String[] {"state"}, null);
			} catch (DataManagerException e) {
				log.error("Exception occured while modify pc(alarmOnRemoteServiceError).instanceId" + pInstanceId, e);
				throw e;
			}
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro="远端服务出现错误时，终止该流程实例并报警", 
		paramIntro = { "节点id", "表单id", "表单记录id", "错误信息"},
		exceptionInfo="可能抛出工作流异常、数据库操作异常、JSON转换异常")
	public void alarmOnRemoteServiceError(int nodeId, int formId, int articleId, String msg) 
			throws Exception{
		if (nodeId <= 0 || articleId <= 0) {
			throw new Exception("nodeId、articleId不能小于等于0");
		}
		if (msg == null || "".equals(msg.trim())) {
			throw new Exception("msg不能为null或空字符串");
		}
		log.info("Alarm on remote service error.nodeId:" 
				+ nodeId + "|formId:" + formId + "|articleId:" + articleId);
		SessionFactory sessionFactory = (SessionFactory) SpringContextUtil
				.getBean("sessionFactory");
		PersistManagerFactoryHibernateForCmppImpl dmFactory = new 
				PersistManagerFactoryHibernateForCmppImpl();
		dmFactory.setSessionFactory(sessionFactory); 
		PersistDataManager processContextDm = new 
				PersistDataManager(CmppProcessContext.class, dmFactory);
		PersistDataManager pluginDefDm = new 
				PersistDataManager(CmppPluginDef.class, dmFactory);
		PersistDataManager pluginStatusDm = new 
				PersistDataManager(PluginStatus.class, dmFactory);
		CmppProcessContext processContext = null;
		
		HQuery pcQuery = new HQuery();
		pcQuery.setQueryString("from CmppProcessContext pc where pc.nodeid = ? " +
				"and pc.formId = ? and pc.articleId = ? and pc.state = 4 " +
				"order by pc.id desc");
		List pcParamList = new ArrayList();
		pcParamList.add(nodeId);
		pcParamList.add(formId);
		pcParamList.add((long)articleId);
		pcQuery.setParalist(pcParamList);
		QueryResult pcQueryResult;
		try {
			log.debug("Query process context(alarmOnRemoteServiceError)" +
					".nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
			pcQueryResult = processContextDm.query(pcQuery);
		} catch (DataManagerException e) {
			log.error("Exception occured while modify pc(alarmOnRemoteServiceError)" +
					".nodeId:" + nodeId + "|formId:" + formId + "|articleId:" + articleId);
			throw e;
		}
		List pcResultList = pcQueryResult.getData(0, pcQueryResult.getRowCount());
		if (pcResultList.size() > 0) {
			processContext = (CmppProcessContext) pcResultList.get(0);
		}
		if (processContext != null) {
			Map oldDataPool = (Map) processContext.getData().get("dataPool");
			int curPlugin = oldDataPool.get("__curPlugin__") == null ? 0 
					: Integer.parseInt((String) oldDataPool.get("__curPlugin__"));
			log.debug("Query current PluginDef(alarmOnRemoteServiceError)" +
					".nodeId:" + nodeId + "|formId:" + formId + "|articleId:" 
					+ articleId + "pluginId:" + curPlugin);
			CmppPluginDef curPluginDef = null;
			try {
				curPluginDef = (CmppPluginDef) pluginDefDm.queryById(curPlugin);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginDef(alarmOnRemoteServiceError)" +
						".nodeId:" + nodeId + "|formId:" + formId + "|articleId:" 
						+ articleId + "pluginId:" + curPlugin, e);
				throw e;
			}
			
			//远端服务出错报警
			if (curPluginDef != null) {
				String mailTo = curPluginDef.getMailTo();
				String smsTo = curPluginDef.getSmsTo();
				ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil
						.getBean("pluginFactory");
				UtilPlugin util = (UtilPlugin) pluginFactory.getP("util");
				if (mailTo != null && !mailTo.trim().equals("")) {
					log.debug("Send mail on remote service error." + 
							".nodeId:" + nodeId + "|formId:" + formId 
							+ "|articleId:" + articleId);
					mailTo = mailTo.trim().replace('，', ',').replace(';', ',').replace('；', ',');
					util.sendMail(mailTo, "远端服务出错", msg, null);
//					String[] mailAddrs = mailTo.split(";");
//					for (String str : mailAddrs) {
//						util.sendMail(str, "远端服务出错", msg, null);
//					}
				}
				
				if (smsTo != null && !smsTo.trim().equals("")) {
					log.debug("Send sms on remote service error." + 
							".nodeId:" + nodeId + "|formId:" + formId 
							+ "|articleId:" + articleId);
					smsTo = smsTo.trim().replace('，', ';').replace(',', ';').replace('；', ';');;
					String[] mobiles = smsTo.split(";");
					for (String str : mobiles) {
						//util.sendSMS(str, msg);
					}
				}
			}
			//修改该插件的运行状态为异常状态
			HQuery query = new HQuery();
			query.setQueryString("from PluginStatus p where p.pluginDef.pluginId = ? " +
					"and p.processContext.id = ? order by p.pluginStatusId desc");
			List paramList = new ArrayList();
			paramList.add(curPlugin);
			paramList.add(processContext.getId());
			query.setParalist(paramList);
			QueryResult pluginStatusResult = null;
			try {
				pluginStatusResult = pluginStatusDm.query(query);
			} catch (DataManagerException e) {
				log.error("Exception occured while query PluginStatus(alarmOnRemoteServiceError)" +
						".nodeId" + nodeId + "|formId:" + formId + "|articleId:" + articleId 
						+ "|plugindefid:" + curPlugin, e);
				throw e;
			}
			List pluginStatusList = pluginStatusResult.getData(0, 
													pluginStatusResult.getRowCount());
			if (pluginStatusList.size() > 0) {
				PluginStatus pluginStatus = (PluginStatus) pluginStatusList.get(0);
				log.info("Modify pluginstatus to 2(alarmOnRemoteServiceError)." +
						"nodeId:" + nodeId + "|formId:" + formId + "|articleId:" 
						+ articleId + "|pluginStatusId:" + pluginStatus.getPluginStatusId());
				pluginStatus.setStatus((byte)2);
				try {
					pluginStatusDm.modify(pluginStatus, new String[]{"status"}, null);
				} catch (DataManagerException e) {
					log.error("Exception occured while modify PluginStatus(alarmOnRemoteServiceError)" + 
							".nodeId" + nodeId + "|formId:" + formId + "|articleId:" 
							+ articleId + "|pluginStatusId:" + pluginStatus.getPluginStatusId(), e);
					throw e;
				}
			}
			//修改流程实例的状态为异常结束状态
			log.info("Modify process state to 5(alarmOnRemoteServiceError).nodeId:" + nodeId + "|formId:" 
					+ formId + "|articleId:" + articleId);
			processContext.setState(5);
			try {
				processContextDm.modify(processContext, new String[] {"state"}, null);
			} catch (DataManagerException e) {
				log.error("Exception occured while modify pc(alarmOnRemoteServiceError)" +
						".nodeId" + nodeId + "|formId:" + formId + "|articleId:" 
						+ articleId, e);
				throw e;
			}
		}
		
	}
	
}
