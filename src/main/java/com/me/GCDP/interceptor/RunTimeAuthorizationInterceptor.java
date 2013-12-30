/**
 * 
 */
package com.me.GCDP.interceptor;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.me.GCDP.util.CookieUtil;
import com.me.GCDP.util.MD5;
import com.me.GCDP.util.property.CmppConfig;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * @author zhangzy
 * 2011-11-17
 */
public class RunTimeAuthorizationInterceptor extends MethodFilterInterceptor{
	private static final long serialVersionUID = -707637145818308224L;

	@Override
	protected String doIntercept(ActionInvocation arg0) throws Exception {
		HttpServletRequest request=ServletActionContext.getRequest();
		String header=request.getHeader("ajax");
		if(header!=null && header.equals("true")){
			return arg0.invoke();
		}
		if(CookieUtil.getCookieByName(request, "cmpp_user")!=null 
				&& !CookieUtil.getCookieByName(request, "cmpp_user").getValue().equals("")
				&& CookieUtil.getCookieByName(request, "cmpp_token")!=null 
				&& CookieUtil.getCookieByName(request, "cmpp_cn")!=null ){
			String user=CookieUtil.getCookieByName(request, "cmpp_user").getValue();
			String token=MD5.encode(user+"Ifeng888").toLowerCase();
			if(!CookieUtil.getCookieByName(request, "cmpp_token").getValue().equals(token)){	
				return null;
			}else{
				return arg0.invoke();
			}
		}/*else{
			String loginUrl="http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/develop/authority!loginMethod.jhtml";
			String baclUrl=request.getRequestURL()+(request.getQueryString()==null?"":"?"+request.getQueryString());
			long time=System.currentTimeMillis();
			String value="cmpp"+"N8HxBo"+time+loginUrl+baclUrl;
			String md5=AuthorzationUtil.getMD5(value);
			ServletActionContext.getResponse().sendRedirect("http://sso.staff.ifeng.com/auth/?from=cmpp&tm="+time
					+"&authurl="+URLEncoder.encode(loginUrl,"utf-8")
					+"&backurl="+URLEncoder.encode(baclUrl,"utf-8")
					+"&token1="+md5);
			return null;
		}*/
		
		/********************************************************************************************************
		  added by HANXIANQI
		    判断用户登录模式，1.  如果不是用户名-密码登录模式， 则进行SSO域认证登录；2. 如果是，则进行用户名-密码认证登录。
		 ********************************************************************************************************/
		else 
		{
			if (!isPwdLoginModeOn()) // 检查登录模式： 1. 密码认证登录  2. SSO认证登录 
			{
				String loginUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/develop/authority!loginMethod.jhtml";
				String baclUrl  = request.getRequestURL() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
				
				long   time  = System.currentTimeMillis();
				String value = "cmpp" + "N8HxBo" + time + loginUrl + baclUrl;
				String md5   = MD5.encode(value).toLowerCase();
				ServletActionContext.getResponse().sendRedirect(
						"http://sso.staff.ifeng.com/auth/?from=cmpp&tm=" + time
								+ "&authurl="
								+ URLEncoder.encode(loginUrl, "utf-8")
								+ "&backurl="
								+ URLEncoder.encode(baclUrl, "utf-8")
								+ "&token1=" + md5);
				return null;
			}
			else
			{
				/***** login cmpp by username and password *****/
				
				// http://localhost:8080/cmpp-dev/develop/index.jhtml
				String requestURL = request.getRequestURI();
				// nodeId=14
				String queryString = request.getQueryString();
				
				request.setAttribute("requestURL", requestURL);
				request.setAttribute("queryString", queryString);
				
				return "pwdLogin";
			}
		}
	}
	
	private boolean isPwdLoginModeOn()
	{
		boolean pwdLoginModeOn = true;
		
		try
		{
			String loginModeStr = CmppConfig.getKey("cmpp.username.pwd.authen.login.mode.on");
			pwdLoginModeOn = Boolean.parseBoolean(loginModeStr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("error occurs when getting login mode from cmpp.properties file");
		}
		
		return pwdLoginModeOn;
	}

}
