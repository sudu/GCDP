package com.me.GCDP.action.online;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.CookiesAware;

import com.me.GCDP.util.oscache.OSCache;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-8-15              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class OnlineAction extends ActionSupport implements CookiesAware {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> cookieMap = null;
	
	private static Log log = LogFactory.getLog(OnlineAction.class);
	
	private OSCache oscache_online = null;
	
	private String url = null;
	private String username = null;
	
	private String msg = null;
	private Boolean hasError = false;
	
	private List<DataModel> ulist = null;
	
	private String remove = "false";
	
	@SuppressWarnings("unchecked")
	public String list() throws Exception {
		if(url == null || url.equals("")){
			HttpServletRequest request = ServletActionContext.getRequest();
			url= request.getHeader("Referer");
		}
		if(url == null || url.equals("")){
			hasError = true;
			msg = "url is null";
			log.warn("url is null");
			return "result";
		}
		
		if(url.startsWith("http:") && url.indexOf("?") > 0){
			//按参数名排序
			String params = url.substring(url.indexOf("?")+1);
			String[] pairs = params.split("&");
			List<String> paramList = new ArrayList<String>();
			for(String str : pairs){
				paramList.add(str);
			}
			Collections.sort(paramList);
			
			url = url.substring(0,url.indexOf("?")+1);
			
			int length = url.length();
			for(String param : paramList){
				if(length == url.length()){
					url += param;
				}else{
					url += "&" + param;
				}
			}
		}
		
		if(oscache_online.get(url) == null){
			ulist = new ArrayList<DataModel>();
		}else{
			ulist = (List<DataModel>)oscache_online.get(url);
			this.clear(ulist);
		}
		oscache_online.put(url, ulist);
		
		hasError = false;
		return "result";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		
		if(url == null || url.equals("")){
			HttpServletRequest request = ServletActionContext.getRequest();
			url= request.getHeader("Referer");
		}
		if(url == null || url.equals("")){
			hasError = true;
			msg = "url is null";
			log.warn("url is null");
			return "result";
		}
		
		if(url.startsWith("http:") && url.indexOf("?") > 0){
			//按参数名排序
			String params = url.substring(url.indexOf("?")+1);
			String[] pairs = params.split("&");
			List<String> paramList = new ArrayList<String>();
			for(String str : pairs){
				paramList.add(str);
			}
			Collections.sort(paramList);
			
			url = url.substring(0,url.indexOf("?")+1);
			
			int length = url.length();
			for(String param : paramList){
				if(length == url.length()){
					url += param;
				}else{
					url += "&" + param;
				}
			}
		}
		
		DataModel model = new DataModel();
		//获取用户名
		if(username == null || username.equals("")){
			username = cookieMap.get("cmpp_cn");
			if(username != null && !username.equals("")){
				username = URLDecoder.decode(username,"UTF-8");
				model.setUsername(username);
			}else{
				hasError = true;
				msg = "cookie['cmpp_cn'] is null";
				log.warn("cookie['cmpp_cn'] is null");
				return "result";
			}
		}else{
			model.setUsername(username);
		}
		if(oscache_online.get(url) == null){
			ulist = new ArrayList<DataModel>();
		}else{
			ulist = (List<DataModel>)oscache_online.get(url);
			this.clear(ulist);
			ulist.remove(model);
		}
		if(!remove.equals("true")){
			ulist.add(model);
		}
		oscache_online.put(url, ulist);
		
		hasError = false;
		return "result";
	}
	
	private void clear(List<DataModel> list){
		for(int i = 0 ; i < list.size() ; i ++){
			DataModel m = list.get(i);
			if((System.currentTimeMillis() - m.getDatetime()) > 5*60*1000){
				list.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 浏览数据信息
	 * @author huwq
	 *
	 */
	public class DataModel{
		
		private String username = null;
		
		private Long datetime = System.currentTimeMillis();

		@Override
		public boolean equals(Object obj) {
			DataModel dest = (DataModel)obj;
			if(dest.getUsername().equals(this.username)){
				return true;
			}else{
				return false;
			}
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public Long getDatetime() {
			return datetime;
		}

		public void setDatetime(Long datetime) {
			this.datetime = datetime;
		}
		
	}

	/*
	 * getter and setter
	 */
	public void setOscache_online(OSCache oscache_online) {
		this.oscache_online = oscache_online;
	}
	
	@Override
	public void setCookiesMap(Map<String, String> arg0) {
		this.cookieMap = arg0;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<DataModel> getUlist() {
		return ulist;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}
	
	public String getRemove() {
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove;
	}
	
}
