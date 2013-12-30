package com.me.GCDP.workflow;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.workflow.dm.PersistManagerFactoryHibernateForCmppImpl;
import com.me.GCDP.workflow.model.CmppProcessDef;
import com.me.GCDP.workflow.service.WorkflowMgrService;
import com.me.GCDP.workflow.service.XMLToObjectService;
import com.ifeng.common.conf.ConfigRoot;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.workflow.Workflow;
import com.ifeng.common.workflow.WorkflowException;

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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class WorkflowService {
	
	private static Log log = LogFactory.getLog(WorkflowService.class);
	
	private ConfigRoot configRoot = null;
	
	private Workflow wf = null;
	
	private CmppWorkflowEngine engine;
	
	private String flowDefFileDir = "";
	
	private String flowDefFilename = "";
	
	private XMLToObjectService _service = new XMLToObjectService();
	
	private Timer flowTimer;
	
	private Map map;
	
	private String workflowDefFile;
	
	/**
	 * 初始化方法 
	 */
	public void init(){
		try {
			//获取流程定义文件的完整路径
			workflowDefFile = flowDefFileDir + flowDefFilename;
			File dir = new File(flowDefFileDir);
			File f = new File(workflowDefFile);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!f.exists()) {
				WorkflowMgrService workflowMgrService = (WorkflowMgrService) SpringContextUtil.getBean("workflowMgrService");
				workflowMgrService.releaseWorkflowToDefaultXML();
			}
			configRoot = new ConfigRoot(workflowDefFile, null);
			List<String> processNames = _service.getProcessList(workflowDefFile);
			map = new HashMap();
			for(String process : processNames) {
				map.put(process, configRoot.getValue(process));
			}
			PersistManagerFactoryHibernateForCmppImpl factory = new PersistManagerFactoryHibernateForCmppImpl();
			PersistDataManager dm = new PersistDataManager(CmppProcessContext.class, 
					factory);
			PersistDataManager processDefinitionDm = new PersistDataManager(CmppProcessDef.class
					, factory);
			wf = new WorkflowImpl(map, dm);
			((WorkflowImpl)wf).setProcessDefDm(processDefinitionDm);
			engine = new CmppWorkflowEngine(wf);
			
			/**
			 * 开启一个定时器，10秒之后启动。每隔一分钟，重新读取流程定义文件，
			 * 更新上线下线的流程
			 */
//			InputStream in = WorkflowService.class.getResourceAsStream("/conf/workflow.properties");
//			Properties p = new Properties();
//			p.load(in);
//			int interval = Integer.parseInt(p.getProperty("read_flowdef_file_interval")
//					== null ? "2" : p.getProperty("read_flowdef_file_interval"));
//			
//			flowTimer = new Timer();
//			log.info("Start the timer:");
//			flowTimer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					log.info("flowTimer.run()");
//					File f = new File(workflowDefFile);
//					CmppDBClientService nosql = (CmppDBClientService) SpringContextUtil.getBean("cmppDBService");
//					long lastModified = 0L;
//					if (nosql.get("lastmodify:" + workflowDefFile) != null) {
//						lastModified = (Long) nosql.get("lastmodify:" + workflowDefFile);
//					}
//					if (lastModified != f.lastModified() && lastModified != 0) {
//						log.info("lastModified:" + lastModified);
//						log.info("Read flow definition file");
//						configRoot = new ConfigRoot(workflowDefFile, null);
//						List<String> processNames = _service.getProcessList(workflowDefFile);
//						log.info("Reading process definition file: " + workflowDefFile);
//						map.clear();
//						for(String process : processNames) {
//							map.put(process, configRoot.getValue(process));
//						}
//						nosql.put("lastmodify:" + workflowDefFile, f.lastModified());
//						log.info("Put lastModified to nosql:" + f.lastModified());
//					}
//				}
//			}, 30L * 1000, interval * 1000L);
			
		}catch (IOException e) {
			log.error("Exception occured while parsing properties to get read_flowdef_file_interval",e);
		}catch (Exception e) {
			log.error("Exception occured while init the WorkflowService", e);
		}
		
	}
	
	/**
	 * 运行工作流
	 * @param wfname 工作流名称
	 * @return 工作流实例id
	 * @throws WorkflowException
	 */
	
	public long startWorkFLow(String wfname) throws WorkflowException{
		Map contextData = new HashMap();
		Map dataPool = new HashMap();
		contextData.put("dataPool", dataPool);
		return startWorkFlow(wfname, contextData);
	}
	
	
	
	/**
	 * 运行工作流
	 * @param wfname 工作流名称
	 * @param contextData 上下文业务数据
	 * @return 工作流实例id
	 * @throws WorkflowException
	 */
	public long startWorkFlow(String wfname, Map dataPool) throws WorkflowException{
		Map contextData = new HashMap();
		contextData.put("dataPool", dataPool);
		long runid = wf.startProcess(wfname, contextData);
		engine.runWorkflowInstance(runid, dataPool);
//		log.info("Workflow is running... | ID : " + runid);
		return runid;
	}
	

	public void setFlowDefFileDir(String flowDefFileDir) {
		this.flowDefFileDir = flowDefFileDir;
	}

	public void setFlowDefFilename(String flowDefFilename) {
		this.flowDefFilename = flowDefFilename;
	}

	public Workflow getWf() {
		return wf;
	}

	public ConfigRoot getConfigRoot() {
		return configRoot;
	}
	
	
}
