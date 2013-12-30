package com.me.GCDP.workflow;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ifeng.common.workflow.ProcessContext;
import com.ifeng.common.workflow.Workflow;
import com.ifeng.common.workflow.WorkflowException;
import com.me.GCDP.script.ScriptThreadLocal;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CmppWorkflowEngine {
	private static final Log log= LogFactory.getLog(CmppWorkflowEngine.class);
	private Workflow workflow;
	
	public CmppWorkflowEngine(Workflow flow) {
		this.workflow = flow;
	}
	
/*
	public void runWorkflowInstance(long wfId) {
		List list = null;
        try {
            list = ((WorkflowImpl)workflow).queryReadyProcessContext(wfId);
        } catch (WorkflowException e) {
            log.error("Exception occured while query ready process", e);
        } 
        for (int i = 0; i < list.size(); i++) {
            ProcessContext pc = (ProcessContext)list.get(i);
            //根据流程上下文id判断到底执行哪个流程
            if (pc.getId() == wfId) {
            	Map dataMap = pc.getData();
            	Map dataPool = (Map) dataMap.get("dataPool");
            	dataPool.put("__instanceId__", pc.getId());
            	pc.setData(dataMap);//每次修改了ProcessContext中的data后都需要手工设置一下
                try {
                	log.info("Process Instance " + pc.getId() + " of " +"ProcessName: " 
                			+ pc.getProcessDefinitionName() + " starts to run");
                	String ids[] = new String[3];
                	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
                	ids[1] = "process";
                	ids[2] = pc.getProcessDefinitionName();
                	ScriptThreadLocal.setScriptIDS(ids);
//                	ScriptThreadLocal.setOutput(true);
    				workflow.runReadyProcessContext(pc);
    				log.info("Process Instance " + pc.getId() + " of " +"ProcessName: " 
                			+ pc.getProcessDefinitionName() + " ends running");
    				return;
    			} catch (WorkflowException e) {
    				log.error(e);
    				return;
    			}    
			}
        }  
	}
*/
	
	/**
	 * 运行一个流程实例
	 * @param wfId
	 * @param dataPoolMap
	 */
	public void runWorkflowInstance(long wfId, Map dataPoolMap) {
		List list = null;
        try {
            list = ((WorkflowImpl)workflow).queryReadyProcessContext(wfId);
        } catch (WorkflowException e) {
            log.error("Exception occured while query ready process", e);
        } 
        for (int i = 0; i < list.size(); i++) {
            ProcessContext pc = (ProcessContext)list.get(i);
            //根据流程上下文id判断到底执行哪个流程
            if (pc.getId() == wfId) {
            	Map dataMap = pc.getData();
//            	Map dataPool = (Map) dataMap.get("dataPool");
            	dataPoolMap.put("__instanceId__", pc.getId());
            	dataMap.put("dataPool", dataPoolMap);
            	pc.setData(dataMap);//每次修改了ProcessContext中的data后都需要手工设置一下
            	
            	//2012.12.25
                //解决日志丢失问题
            	String[] old_ids = ScriptThreadLocal.getScriptIDS();
            	Long old_instanseId = ScriptThreadLocal.getInstanceId();
            	
                try {
                	log.debug("Process Instance " + pc.getId() + " of " +"ProcessName: " 
                			+ pc.getProcessDefinitionName() + " starts to run");
                	String ids[] = new String[3];
                	ids[0] = ((CmppProcessContext)pc).getNodeid() + "";
                	ids[1] = "process";
                	ids[2] = pc.getProcessDefinitionName();
                	ScriptThreadLocal.setScriptIDS(ids);
                	ScriptThreadLocal.setInstanceId(pc.getId());
    				workflow.runReadyProcessContext(pc);
    				log.debug("Process Instance " + pc.getId() + " of " +"ProcessName: " 
                			+ pc.getProcessDefinitionName() + " ends running");
    				return;
    			} catch (WorkflowException e) {
    				log.error(e);
    				return;
    			} finally {
    				if(old_instanseId == null){
    					ScriptThreadLocal.removeInstanceId();
    				}else{
    					ScriptThreadLocal.setInstanceId(old_instanseId);
    				}
    				if(old_ids == null){
    					ScriptThreadLocal.removeScriptIds();
    				}else{
    					ScriptThreadLocal.setScriptIDS(old_ids);
    				}
    			}
                
			}
        }  
	}
}
