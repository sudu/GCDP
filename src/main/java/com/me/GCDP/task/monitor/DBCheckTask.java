package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.script.plugin.DataBasePlugin;

public class DBCheckTask extends BaseCheckTask{
	private static Log log = LogFactory.getLog(DBCheckTask.class);
	@Override
	protected void runCheck(JobExecutionContext context){

		//待检测的 db plugin
		DataBasePlugin dp = (DataBasePlugin) pf.getP("db");
		BigDecimal total = new BigDecimal(0);
		String msg = null;
		boolean checkResult = false;
		try {
			Long st= System.currentTimeMillis();
			String selectSql = "select max(id) from cmpp_monitor_task;";
			@SuppressWarnings("rawtypes")
			List result = dp.executeSelectSQL(selectSql);
			if(result!=null&&!"".equals(result)){
				total = new BigDecimal(System.currentTimeMillis() - st);
				checkResult = true;
			}else{
				total = new BigDecimal(-1);
			}
		} catch (Exception e) {
			total = new BigDecimal(-1);
			msg = e.getMessage();
//			log.error("error:DBCheckTask|| " + msg);
			checkResult = false;
		} finally{
			reusltCheck(total,"db",msg, null);
			if(checkResult){
				log.info("DBCheckTask: runTime="+total);
			}else{
				log.error("error:DBCheckTask|| " + msg);
			}
		}
	}

}
