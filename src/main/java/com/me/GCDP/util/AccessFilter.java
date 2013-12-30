package com.me.GCDP.util;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author zhangzy
 * 2011-7-26
 * 登录过滤器
 */
public class AccessFilter implements Filter {
	
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest) arg0;
		HttpServletResponse response=(HttpServletResponse) arg1;
		HttpSession session=request.getSession();
		if(session.getAttribute("isLogin")==null){
//			System.out.println(request.getRequestURI());
//			request.getParameterMap();
//			response.sendRedirect("");//sso路径
//			return;
		}
		arg2.doFilter(arg0, arg1);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	public void destroy() {

	}

}
