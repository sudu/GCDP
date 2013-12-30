package com.me.GCDP.action.script;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.manage.ScriptStates;
import com.me.GCDP.script.manage.ScriptThreadManager;
import com.me.GCDP.script.plugin.HttpPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.util.ObjectSerializeUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-3-19              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
@SuppressWarnings("unchecked")
public class ScriptThreadAction extends ActionSupport {
	private static Log log = LogFactory.getLog(ScriptThreadAction.class);
	private static final long serialVersionUID = 1L;
	
	private String skey = null;
	private List<ScriptStates> slist = null;

	private String msg = null;
	private Boolean hasError = false;
	
	/*
	 * 脚本线程运行在前后台的标志
	 */
	private int daemonFlag = 0;
	
	private int nodeId = 0;
	
	public String list() throws Exception{
		String daemonize = CmppConfig.getKey("cmpp.daemonize");
		String nodeStr = "" + nodeId;
		if("no".equalsIgnoreCase(daemonize) //该节点不是用来执行计划任务的
				|| "n".equalsIgnoreCase(daemonize)) {
			String url = CmppConfig.getKey("cmpp.daemonize.baseurl") + 
					"/develop/script/scriptThreadMgr!listSerializeOutput.jhtml?nodeId=" + nodeId;
			HttpServletRequest request = ServletActionContext.getRequest();
			ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
			HttpPlugin httpPlugin = (HttpPlugin) pluginFactory.getP("http");
			Map<String, String> reqProps = new HashMap<String, String>();
			reqProps.put("Cookie", request.getHeader("Cookie"));
			String listJsonStr = httpPlugin.sendGet(url, reqProps);
			
			if (listJsonStr != null && listJsonStr.length() > 0) {
				slist = (List<ScriptStates>) ObjectSerializeUtil.objDeserialize(listJsonStr);
			}
			
			if (slist == null || slist.size() <= 0) {
				//另一个节点上当前没有正在运行的脚本线程，则直接返回本节点上正在运行的脚本线程
				slist = ScriptThreadManager.getInstance().getSSList();
				for(int i = 0; i < slist.size(); i++) {
					if(! nodeStr.equals(slist.get(i).getKey().split("_")[0])) {
						slist.remove(i--);
					}
				}
			}else {
				//另一个节点上当前如果有正在运行的脚本线程，则再合并本节点上正在运行的脚本线程
				for(ScriptStates ss : slist) {
					ss.setDaemonFlag(1);
				}
				for(int i = 0; i < slist.size(); i++) {
					if(! nodeStr.equals(slist.get(i).getKey().split("_")[0])) {
						slist.remove(i--);
					}
				}
				List<ScriptStates> slistOnThisNode = ScriptThreadManager.getInstance().getSSList();
				for(int j = 0; j < slistOnThisNode.size(); j++) {
					if(! nodeStr.equals(slistOnThisNode.get(j).getKey().split("_")[0])) {
						slistOnThisNode.remove(j--);
					}
				}
				slist.addAll(slistOnThisNode);
			}
			return "list";
		}
		
		//运行计划任务的节点，直接返回本节点当前正在运行的脚本线程
		slist = ScriptThreadManager.getInstance().getSSList();
		for(ScriptStates ss : slist) {
			ss.setDaemonFlag(1);
		}
		
		//通过nodeId过滤一些脚本线程
		for(int i = 0; i < slist.size(); i++) {
			if(! nodeStr.equals(slist.get(i).getKey().split("_")[0])) {
				slist.remove(i--);
			}
		}
		return "list";
	}
	
	/**
	 * 将本节点正在运行的线程列表序列化后输出
	 */
	public void listSerializeOutput() {
		String nodeStr = "" + nodeId;
		slist = ScriptThreadManager.getInstance().getSSList();
		for(int i = 0; i < slist.size(); i++) {
			if(! nodeStr.equals(slist.get(i).getKey().split("_")[0])) {
				slist.remove(i--);
			}
		}
		String listJsonStr = "";
		try {
			listJsonStr = ObjectSerializeUtil.objSerialize(slist);
		} catch (UnsupportedEncodingException e1) {
			log.error(e1);
		}
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(listJsonStr);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	public void interrupt() throws Exception{
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		HttpPlugin httpPlugin = (HttpPlugin) pluginFactory.getP("http");
		
		if(skey == null || skey.equals("")){
			hasError = true;
			response.getWriter().print("{success:false,msg:'失败'}");
		}
		
		if (daemonFlag == 0) {
			if ("no".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))
					|| "n".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))) {
				try {
					ScriptThreadManager.getInstance().interruptScript(skey);
					response.getWriter().print("{success:true,msg:'成功'}");
				} catch (Exception e) {
					response.getWriter().print("{success:false,msg:'失败'}");
					log.error(e);
				}
			}else if("yes".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))
					|| "y".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))) {
				String interruptUrl = CmppConfig.getKey("cmpp.daemonize.baseurl") + 
						"/develop/script/scriptThreadMgr!interrupt.jhtml?skey=" 
						+ skey + "&daemonFlag=" + daemonFlag;
				
				Map<String, String> reqProps = new HashMap<String, String>();
				reqProps.put("Cookie", request.getHeader("Cookie"));
				
				try {
					String ret = httpPlugin.sendGet(interruptUrl, reqProps);
					response.getWriter().print(ret);
				} catch (Exception e) {
					response.getWriter().print("{success:false,msg:'失败'}");
					log.error(e);
				}
			}
		}else {
			if ("no".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))
					|| "n".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))) {
				String interruptUrl = CmppConfig.getKey("cmpp.daemonize.baseurl") + 
						"/develop/script/scriptThreadMgr!interrupt.jhtml?skey=" 
						+ skey + "&daemonFlag=" + daemonFlag;
				
				Map<String, String> reqProps = new HashMap<String, String>();
				reqProps.put("Cookie", request.getHeader("Cookie"));
				
				try {
					String ret = httpPlugin.sendGet(interruptUrl, reqProps);
					response.getWriter().print(ret);
				} catch (Exception e) {
					response.getWriter().print("{success:false,msg:'失败'}");
					log.error(e);
				}
				
			}else if("yes".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))
					|| "y".equalsIgnoreCase(CmppConfig.getKey("cmpp.daemonize"))) {
				try {
					ScriptThreadManager.getInstance().interruptScript(skey);
					response.getWriter().print("{success:true,msg:'成功'}");
				} catch (Exception e) {
					response.getWriter().print("{success:false,msg:'失败'}");
					log.error(e);
				}
			}
		}
	}
	
	/*
	 * getter and setter
	 */
	public String getSkey() {
		return skey;
	}
	
	public void setSkey(String skey) {
		this.skey = skey;
	}
	
	public List<ScriptStates> getSlist() {
		return slist;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public Boolean getHasError() {
		return hasError;
	}

	public int getDaemonFlag() {
		return daemonFlag;
	}

	public void setDaemonFlag(int daemonFlag) {
		this.daemonFlag = daemonFlag;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	
}
