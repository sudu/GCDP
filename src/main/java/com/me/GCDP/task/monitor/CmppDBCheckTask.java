package com.me.GCDP.task.monitor;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.script.plugin.CmppDBPlugin;

public class CmppDBCheckTask extends BaseCheckTask{
	private static Log log = LogFactory.getLog(CmppDBCheckTask.class);
	@Override
	protected void runCheck(JobExecutionContext context){
		CmppDBPlugin cp = (CmppDBPlugin) pf.getP("cmppDB");
		BigDecimal total = new BigDecimal(0);
		String msg = null;
		boolean checkResult = false;
		try {
			Long st= System.currentTimeMillis();
			
			if(cp.put("monitor_cmppdb_test", "monitor_cmppdb_test")){
				total = new BigDecimal(System.currentTimeMillis() - st);
				checkResult  = true;
			}else{
				total = new BigDecimal(-1);
				msg = "insert key failed.";
			}
			
			
		} catch (Exception e) {
			total = new BigDecimal(-1);
			msg = e.getMessage();
//			log.error("error:CmppDBCheckTask|| " + msg);
			checkResult = false;
		}finally{
			reusltCheck(total,"cmppdb",msg, null);
			if(checkResult){
				log.info("CmppDBCheckTask: runTime="+total);
			}else{
				log.error("error:CmppDBCheckTask|| " + msg);
			}
			
		}
	}
}
