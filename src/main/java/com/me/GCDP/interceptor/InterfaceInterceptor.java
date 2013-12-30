package com.me.GCDP.interceptor;

import com.me.GCDP.mapper.InterfaceMapper;
import com.me.GCDP.model.Interface;
import com.me.GCDP.util.SpringContextUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: 获取接口ID，并反射调用setInterface_id方法注入ID</p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-23              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class InterfaceInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(InterfaceInterceptor.class);

	@Override
	public void destroy() {
	}

	@Override
	public void init() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		OgnlValueStack stack=(OgnlValueStack)request.getAttribute("struts.valueStack");
		String uri = request.getRequestURI();
		String ifid = null;
        String debug = null;
		if(uri.indexOf("_")>0){
			ifid = uri.substring(uri.lastIndexOf("_")+1, uri.lastIndexOf("."));
            int i = uri.indexOf("_"),j = uri.lastIndexOf("_");
            if(i<j)
                debug =uri.substring(uri.indexOf("_")+1,uri.lastIndexOf("_"));
		}else{
			//向Ognl栈中注入result
	        stack.set("result", "interface_id not found");
	        return "result";
		}
		
		//反射注入接口ID
        Object action = invocation.getAction();
		Class c = action.getClass();
        Method m = c.getMethod("setInterface_id", Integer.class);
        Method m2 = c.getMethod("setDebug",boolean.class);
        try{
        	Integer ifid_i = new Integer(ifid);
            Boolean debug_i = StringUtils.equals("debug",debug);
            m.invoke(action, ifid_i);
            m2.invoke(action,debug_i);
        }catch(NumberFormatException e){
        	log.error("interface_id is wrong : " + e.getMessage());
        	stack.set("result", "interface_id is not a number");
        	return "result";
        }catch(Exception e){
        	log.error(e.getMessage());
        	stack.set("result", e.getMessage());
        	return "result";
        }
        
        InterfaceMapper ifMapper = (InterfaceMapper)SpringContextUtil.getBean("ifMapper");
        Interface interf = new Interface();
        interf.setId(Integer.parseInt(ifid));
        List<Interface> ifList = ifMapper.get(interf);
        
        if(ifList != null && ifList.size()>0){
        	interf = ifList.get(0);
        	if(interf.getReqlogin() == 1){
        		RunTimeAuthorizationInterceptor auth = new RunTimeAuthorizationInterceptor();
                return auth.doIntercept(invocation);
        	}else{
        		return invocation.invoke();
        	}
        }else{
        	return invocation.invoke();
        }
        //return invocation.invoke();
	}

}
