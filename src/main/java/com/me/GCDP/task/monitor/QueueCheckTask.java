package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.script.plugin.QueuePlugin;


public class QueueCheckTask extends BaseCheckTask{

	private static Log log = LogFactory.getLog(QueueCheckTask.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void runCheck(JobExecutionContext context){
		
		//待检测的 Redis plugin
		QueuePlugin qp = (QueuePlugin) pf.getP("queue");
		
		String msg = null;
		BigDecimal result = new BigDecimal(0);
//		BigDecimal eq = null;
//		BigDecimal dq = null;
		
		MonitorMapper mm = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");
		List<String> tasks = mm.getTaskByName("queue");
		String[] param = null;
		Long st = null;
		
		
		if(tasks != null && tasks.size() > 0){	
			for(String taskName:tasks){
				boolean checkResult = false;
				param = taskName.split("/");
				try{
					if(param[0].equals("queue")){						
						st= System.currentTimeMillis();
						if(qp.send("cmpp_monitor_test_queue", "cmpp_monitor_test")){
							qp.receive("cmpp_monitor_test_queue");
							checkResult = true;
						};
						result = new BigDecimal(System.currentTimeMillis() - st);
					}else if(param[0].equals("queue_ls")){
						if(param[1]!=null && !param[1].equals("")){
							result = new BigDecimal(qp.getPendingCount(param[1]));
							checkResult = true;
						}else{
							result = new BigDecimal(-1);
							msg = "no queue name";
						}
					}else{
						continue;
					}				
				}catch(Exception e){
					result = new BigDecimal(-1);
					msg = e.getMessage();
//					log.error("error --> QueueCheck: "+ taskName + " || " + msg);
					checkResult = false;
				}finally{
					reusltCheck(result,taskName,msg, null);
					if(checkResult){
						log.info("QueueCheck: " + taskName + " result="+result);
					}else{
						log.error("error:QueueCheck|| " + msg);
					}
				}
			}
		}
	}
}
