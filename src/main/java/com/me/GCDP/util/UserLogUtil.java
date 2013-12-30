package com.me.GCDP.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.me.GCDP.util.property.CmppConfig;
import com.me.json.JSONObject;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-10-27              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class UserLogUtil {
	
	/*public static void main(String[] args){
		Map m = new HashMap();
		m.put("asdfasdf", 123123123L);
		m.put(11111L, "asdfasdfaf");
		UserLogUtil.getInstance("111").log(m);
	}*/
	
	private static Object lock = new Object();
	
	private static ConcurrentHashMap<String, UserLogUtil> map = new ConcurrentHashMap<String, UserLogUtil>();
	
	private Logger logger = null; 
	
	private UserLogUtil(String nodeId){
		String path = CmppConfig.getKey("cmpp.userlog.dir");
		logger = Logger.getLogger("User_Operation_Logger_"+nodeId);
		logger.setAdditivity(false);
		Appender appender = logger.getAppender("User_Operation_Logger");
		if(appender == null){
			PatternLayout pl = new PatternLayout("%d %p - %m%n");
			DailyRollingFileAppender a;
			try {
				a = new DailyRollingFileAppender(pl,path+"//"+nodeId+"//UserOperLog","'.'yyyy-MM-dd'.log'");
				a.setName("User_Operation_Logger");
				logger.addAppender(a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static UserLogUtil getInstance(String nodeId){
		UserLogUtil util = null;
		if(!map.containsKey(nodeId)){
			synchronized (lock) {
				if(!map.containsKey(nodeId)){
					util = new UserLogUtil(nodeId);
					map.put(nodeId, util);
				}
			}
		}
		return map.get(nodeId);
	}
	
	@SuppressWarnings("rawtypes")
	public void log(Map m){
		JSONObject obj = new JSONObject(m);
		logger.info(obj.toString());
	}

}
