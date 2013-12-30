package com.me.GCDP.task.monitor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.me.GCDP.mapper.DynPageStatusMapper;
import com.me.GCDP.mapper.DynamicConfigMapper;
import com.me.GCDP.model.DynPageStatus;
import com.me.GCDP.model.DynamicConfig;
import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.SpringContextUtil;
import com.me.json.JSONArray;
import com.me.json.JSONObject;

public class DynStatusUpdateTask  extends BaseCheckTask{
	private static Log log = LogFactory.getLog(DynStatusUpdateTask.class);
	@SuppressWarnings("unchecked")
	@Override
	protected void runCheck(JobExecutionContext context){
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DynamicConfigMapper<DynamicConfig> dynamicConfigMapper = (DynamicConfigMapper<DynamicConfig> )SpringContextUtil.getBean("dynamicConfigMapper");
		DynPageStatusMapper<DynPageStatus> dynPageStatusMapper = (DynPageStatusMapper<DynPageStatus> )SpringContextUtil.getBean("dynPageStatusMapper");
	
		Map<String,String> dataMap = null;
		ArrayList<DynamicConfig> dyns = (ArrayList<DynamicConfig>) dynamicConfigMapper.getAllDyns();
		DynPageStatus dp = null;
		JSONObject dpInfo = null;
		for(DynamicConfig dc: dyns){
			if(dc.getStatus()!=0){
				try {
					dataMap = new HashMap<String,String>();
					String path = dc.getSvrPath().startsWith("/")?dc.getSvrPath():"/"+dc.getSvrPath();
					String url = "http://" + dc.getSvrIp()+path+"/dynamic/monitor.do";
					dataMap.put("actionType","dynPageStatus");
					Map<String,Object> rtn = HttpUtil.post(url, dataMap);
					String content = rtn.get("content").toString();
					JSONArray res = null;
					res = new JSONArray(content);
					if(res.length() > 0){
						for(int i = 0,j = res.length(); i < j; i++){
							dpInfo = res.getJSONObject(i);
							dp = new DynPageStatus();
							dp.setDynID(dc.getId());
							dp.setPageUrl(dpInfo.getString("pageUrl"));
							dp.setRequireCount(Integer.parseInt(dpInfo.getString("total")));
							if(dpInfo.has("execTime"))
								dp.setRespTime(Double.parseDouble(dpInfo.getString("execTime")));
							if(dpInfo.has("200"))
								dp.setCode200Count(Integer.parseInt(dpInfo.getString("200")));
							if(dpInfo.has("400"))
								dp.setCode400Count(Integer.parseInt(dpInfo.getString("400")));
							if(dpInfo.has("500"))
								dp.setCode500Count(Integer.parseInt(dpInfo.getString("500")));
							dp.setIssueDate(tempDate.parse(dpInfo.getString("issueDate")));
							
							dynPageStatusMapper.insert(dp);
							
						}
					}else{
						dp = new DynPageStatus();
						dp.setDynID(dc.getId());
						dp.setPageUrl("overview");
						dp.setIssueDate(new Date());
						dp.setRequireCount(0);
						dynPageStatusMapper.insert(dp);
					}
					
					dataMap.clear();
					dataMap.put("actionType","taskStatus");
					rtn = HttpUtil.post(url, dataMap);
					content = rtn.get("content").toString();
					
					res = new JSONArray(content);
					
					if(res.length()>0){
						JSONObject tmp = null;
						String details = null;
						
						for(int i = 0,j = res.length(); i < j ; i++){
							tmp = res.getJSONObject(i);
							details = tmp.has("details")?tmp.getString("details"):null;
							String taskName = tmp.getString("type")+"/"+dc.getId();
							
							reusltCheck(new BigDecimal(tmp.getString("result")),taskName,details, tempDate.parse(tmp.getString("date")));
							
						}
					}
					
					log.info("update status for dyn: " + dc.getId());
				} catch (Exception e) {
					log.error("failed to update status for dyn: " + dc.getId() + "for reason: " + e.getMessage());
				}
			}
		}
		
	}

}
