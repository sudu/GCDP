package com.me.GCDP.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.me.GCDP.util.CookieUtil;
import com.me.GCDP.util.MD5;


/**
 * @author zhangzy
 * 2011-10-10
 */
public class AuthorzationUtil{

	public static boolean isLogin(){
		HttpServletRequest request=ServletActionContext.getRequest();
		if(request == null){
			return false;
		}
		Cookie userCookie = CookieUtil.getCookieByName(request, "cmpp_user");
		if(userCookie == null) {
			return false;
		}
		String user = userCookie.getValue();
		String token = MD5.encode(user+"Ifeng888").toLowerCase();
		if(CookieUtil.getCookieByName(request, "cmpp_token") == null || 
				!CookieUtil.getCookieByName(request, "cmpp_token").getValue().equals(token)){
			return false;
		}else{
			return true;
		}
	}
	
	public static String getUserId(){
		try{
			HttpServletRequest request=ServletActionContext.getRequest();
			if(request != null && CookieUtil.getCookieByName(request, "cmpp_user")!=null){
				return CookieUtil.getCookieByName(request, "cmpp_user").getValue();
			}else{
				return null;
			}
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	public static String getUserName(){
		try{
			HttpServletRequest request=ServletActionContext.getRequest();
			if(request != null && CookieUtil.getCookieByName(request, "cmpp_cn")!=null){
				try {
					return URLDecoder.decode(CookieUtil.getCookieByName(request, "cmpp_cn").getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}else{
				return null;
			}
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	
}
