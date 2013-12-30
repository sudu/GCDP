package com.me.GCDP.util;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>-------------------------------------------------------------------------------</p>
 * <p>date                   author                    reason                		 </p>
 * <p>2009-7-28              Hu WeiQi               create the class         		 </p>
 * <p>-------------------------------------------------------------------------------</p>
 */

public class ToolsUtil {
	
	private static Log log = LogFactory.getLog(ToolsUtil.class);
	
	/**
	 * 获取项目根路径绝对地址
	 * @return
	 */
	public static String getRootRealPath(){
		ActionContext ac = ActionContext.getContext(); 
		ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
		String rootdir = sc.getRealPath("/");
		rootdir = rootdir.substring(0, rootdir.length()-1);
		return rootdir;
	}
	
}


