package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.mapper.MonitorMapper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.script.plugin.RedisPlugin;


public class RedisCheckTask extends BaseCheckTask{

	private static Log log = LogFactory.getLog(RedisCheckTask.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void runCheck(JobExecutionContext context){
		
		//待检测的 Redis plugin
		RedisPlugin rp = (RedisPlugin) pf.getP("redis");
		String msg = null;
		BigDecimal result = new BigDecimal(0);
		
		
		MonitorMapper mm = (MonitorMapper) SpringContextUtil.getBean("monitorMapper");
		List<String> tasks = mm.getTaskByName("redis");
		String[] param = null;
		if(tasks != null && tasks.size() > 0){		
			for(String taskName:tasks){
				boolean checkResult = false;
				param = taskName.split("/");
				try{
					if(param[0].equals("redis")){
						
						String key = ""+System.currentTimeMillis();
						Long st= System.currentTimeMillis();
						
						if(rp.rpush(key, "Moniter_check") != null){
							rp.rpop(key);
							checkResult = true;
						}
						result = new BigDecimal(System.currentTimeMillis() - st);
						
					}else if(param[0].equals("redis_qs")){
						
						result = new BigDecimal(rp.dbsize());
						checkResult = true;
						
					}else if(param[0].equals("redis_ls")){
						
						if(param[1]!= null && !param[1].equals("")){
							result = new BigDecimal(rp.llen(param[1]));
							checkResult = true;
						}else{
							result = new BigDecimal(-1);
							msg = "no key";
						}
					}else{
						continue;
					}
					
				}catch(Exception e){
					result = new BigDecimal(-1);
					msg = e.getMessage();
//					log.error("error --> RedisCheck: "+ taskName + " || " + msg);
					checkResult = false;
				}finally{
					reusltCheck(result,taskName,msg, null);
					if(checkResult){
						log.info("RedisCheck: " + taskName + " result=" + result);
					}else{
						log.error("error:RedisCheck|| " + msg);
					}
				}
			}
		}
//		try {
//			String key = ""+System.currentTimeMillis();
//			Long st= System.currentTimeMillis();
//			if(rp.rpush(key, "Moniter_check") != null){
//				rp.rpop(key);
//			}
//			total = new BigDecimal(System.currentTimeMillis() - st);
//			
//		} catch (Exception e) {
//			total = new BigDecimal(-1);
//			msg = e.getMessage();
//			log.error("error:RedisCheckTask|| " + msg);
//		}finally{
//			reusltCheck(total,"redis",msg);
//			log.info("RedisCheckTask: runTime="+total);
//			try{
//				length = new BigDecimal(rp.dbsize());
//			}catch(Exception e){
//				length = new BigDecimal(-1);
//				qs_msg = e.getMessage();
//				log.error("error:RedisCheckTask_qs|| " + msg);
//			}finally{
//				reusltCheck(length,"redis_qs",qs_msg);
//				log.info("RedisCheckTask_qs: length="+length);
//			}
//		}
	}
}
