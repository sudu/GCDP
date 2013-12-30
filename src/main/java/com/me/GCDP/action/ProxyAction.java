package com.me.GCDP.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.HttpUtil;
import com.opensymphony.xwork2.ActionSupport;

public class ProxyAction extends ActionSupport {

	/**
	 * 页面代理类抓取所传参数的url页面内容并输出
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ProxyAction.class);
	private String url;
	private String content;
	public String getContent()
	{
		return content;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String execute(){
		content="";
		try
		{
			if(url!=null&&(!"".equals(url)))
			{
				content = HttpUtil.get(url,"").get("content").toString();
			}
		}
		catch (Exception e) {
			log.error(e);			
		}
		return "index";
	}

}
