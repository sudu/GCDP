package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.search.SearchPlugin;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.search.util_V2.Page;



public class SearchCheckTask extends BaseCheckTask{

	private static Log log = LogFactory.getLog(SearchCheckTask.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void runCheck(JobExecutionContext context){
		
		MonitorMapper mm = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");
		List<String> tasks = mm.getTaskByName("search");

		SearchPlugin sp = (SearchPlugin) pf.getP("search");
		BigDecimal total = new BigDecimal(0);
		String msg = null;
		boolean checkResult = false;
		String[] param = null;
		
		int nodeid = 0,formid=0;
		
		Page r = null;
		
		if(tasks != null && tasks.size() > 0){
			for(String taskName:tasks){
				checkResult = false;
				//taskName格式："nodeId:10,formId:130"
				param = taskName.split("/");
				if(param.length > 1){
					try{
						
						nodeid = Integer.parseInt((param[1].split(",")[0].split(":")[1]));
						formid = Integer.parseInt((param[1].split(",")[1].split(":")[1]));
//						log.info("node: " + nodeid + " | formid: " + formid);
						
						Long st= System.currentTimeMillis();
						r = sp.getData(nodeid,formid,"*:*","*",null,0,1);
						total = new BigDecimal(System.currentTimeMillis() - st);
//						log.info(r.getTotalCount());
						checkResult = true;
						
					} catch (Exception e) {
						total = new BigDecimal(-1);
						msg = e.getMessage();
						checkResult = false;
					}finally{
						reusltCheck(total,taskName,msg, null);
						if(checkResult){
							log.info("SearchCheckTask: " + "node: " + nodeid + " | formid: " + formid +" | runTime=" + total );
						}else{
							log.error("error:SearchCheckTask > " + "node: " + nodeid + " | formid: " + formid + " | msg: "+ msg);
						}
					}
				}
			}
		}
		
	}
}
