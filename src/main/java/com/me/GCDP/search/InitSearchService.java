/**
 * 
 */
package com.me.GCDP.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.property.CmppConfig;

/**
 * @author zhangzy
 * 2011-9-29
 */
public class InitSearchService implements ServletContextListener{
	
	private static Log log = LogFactory.getLog(InitSearchService.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("search service start...");
		String url="http://"+CmppConfig.getKey("search.reader.ip")+":"+CmppConfig.getKey("search.reader.port")
		+"/CMPPSearch-2.0.1/core1/select?start=0&rows=0&q=*:*";
		try {
			 URL u = new URL(url);
			 URLConnection con = u.openConnection();
			 con.setConnectTimeout(3000);
			 con.setReadTimeout(10000);
	         con.setDoOutput(true);
	         con.setRequestProperty("Pragma:", "no-cache");
	         con.setRequestProperty("Cache-Control", "no-cache");
	         con.setRequestProperty("Content-Type", "text/xml");
	         BufferedReader br = new BufferedReader(new InputStreamReader(con
	                    .getInputStream()));
	         
	         br.readLine();
		} catch (Exception e) {
			log.error(e);
		}
	}
}
