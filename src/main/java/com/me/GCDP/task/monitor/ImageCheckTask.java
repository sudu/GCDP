package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.script.plugin.ImagePlugin;

public class ImageCheckTask extends BaseCheckTask{
private static Log log = LogFactory.getLog(ImageCheckTask.class);
	
	@Override
	protected void runCheck(JobExecutionContext context){

		//待检测的 db plugin
		ImagePlugin imp = (ImagePlugin) pf.getP("image");
		String imageUrl = (String) context.getJobDetail().getJobDataMap().get("imageUrl");
		String imageL = (String) context.getJobDetail().getJobDataMap().get("imageL");
		String imageW = (String) context.getJobDetail().getJobDataMap().get("imageW");
		BigDecimal total = new BigDecimal(0);
		String msg = null;
		boolean checkResult = false;
		try {
			Long st= System.currentTimeMillis();
			Map<String,String> resultMap = imp.cut(imageUrl, imageW, imageL, null);
			String isHandleSuccess = resultMap.get("isHandleSuccess");	//只是处理图片		
			if(isHandleSuccess.equals("true")){
				total = new BigDecimal(System.currentTimeMillis() - st);
				checkResult = true;
			}else{
				total = new BigDecimal(-1);
				msg = "isHandleSuccess="+isHandleSuccess;
			}
		} catch (Exception e) {
			total = new BigDecimal(-1);
			msg = e.getMessage();
//			log.error("error:ImageCheckTask|| " + msg);
			checkResult = false;
		}finally{
			reusltCheck(total,"image",msg, null);
			if(checkResult){
				log.info("ImageCheckTask: runTime="+total);
			}else{
				log.error("error:ImageCheckTask|| " + msg);
			}
		}
	}


}
