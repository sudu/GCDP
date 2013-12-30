package com.me.GCDP.action.script;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.debugger.MyDebugService;
import com.me.GCDP.script.debugger.MyDebugThread.ContextData;
import com.me.GCDP.script.plugin.PluginDoc;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: 脚本调试Action</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-3-16              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class ScriptDebugAction extends ActionSupport {
	
	private static Log log = LogFactory.getLog(ScriptDebugAction.class);

	private static final long serialVersionUID = 1L;
	private String script = null;
	private String result = "";
	private MyDebugService myDebugService = null;
	private String key = null;
	private Integer lineno = null;
    private String stype = null;
    private Integer nodeId = null;
    private String id1 = null;
	private String id2 = null;

	private String json = null;
	
	private ScriptPluginFactory pluginFactory = null;

	public String execute() throws Exception {
		@SuppressWarnings("unused")
		Map<String, PluginDoc> docmap = pluginFactory.getDocMap();
		key = myDebugService.preDebug();
		return "main";
	}

    public String getUserName() {
        return AuthorzationUtil.getUserName();
    }
    public String getToday(){
    	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return dataFormat.format(new Date());
    }
    public String getEmail() {
        return AuthorzationUtil.getUserId() + "@ifeng.com";
    }
    public String getCmppUrl(){
    	HttpServletRequest request = ServletActionContext.getRequest();
        String schema = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        StringBuffer url = new StringBuffer();
        url.append(schema).append("://").append(host);
        if (port != 80) {
            url.append(':').append(port);
        }
        if (ScriptType.dynPage.equals(stype)) {
            url.append("/Cmpp/develop/dynamic/view.jhtml?").append("nodeId=").append(nodeId).append('&').append("id=").append(id1);
        } else if (ScriptType.interf.equals(stype)) {
            url.append("/Cmpp/develop/interface_mgr!view.jhtml?").append("nodeId=").append(nodeId).append('&').append("interf.id=").append(id1);
        } else if (ScriptType.task.equals(stype)) {
            url.append("/Cmpp/develop/task/taskMgr.html?").append(nodeId).append('&').append("taskId=").append(id1);
        } else if (ScriptType.subscribe.equals(stype)) {
            url.append("/Cmpp/develop/source/index.jhtml?").append(nodeId).append('&').append("id=").append(id1);
        } else {
            url.append("/Cmpp/develop/scriptdebug.jhtml").append("?nodeId=").append(nodeId).append("&id1=").append(id1).append("&id2=").append(id2).append("&stype=").append(stype);
            if (ScriptType.process.equals(stype)) {
                url.append('[').append(schema).append("://").append(host);
                if (port != 80) {
                    url.append(':').append(port);
                }
                url.append("/Cmpp/develop/workflowMgr.jhtml?").append(nodeId).append('&').append("processId=").append(id1).append(']');
            }
        }
        return url.toString();
    }
    
	/**
	 * 执行脚本
	 * @return
	 */
	public String debug(){
		JSONObject jo = new JSONObject();
		try {
			myDebugService.debug(key, script);
			ContextData contextData = myDebugService.getContextData(key);
			jo.put("ret", 1);
			jo.put("key", key);
			jo.put("lineno", contextData.getLineno());
			jo.put("currentLevel", contextData.getCurrentLevel());
		} catch (Exception e) {
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String stepOver(){
		JSONObject jo = new JSONObject();
		try{
			if(key != null && !key.equals("")){
				myDebugService.stepOver(key);
				ContextData contextData = myDebugService.getContextData(key);
				/*result = key + "|" + contextData.getLineno() + "|" + contextData.getCurrentLevel();*/
				jo.put("ret", 1);
				jo.put("key", key);
				jo.put("lineno", contextData.getLineno());
				jo.put("currentLevel", contextData.getCurrentLevel());
				json = jo.toString();
			}else{
				jo.put("ret", 0);
				jo.put("msg", "key is null");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e1.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String stepInto(){
		JSONObject jo = new JSONObject();
		try{
			if(key != null && !key.equals("")){
				myDebugService.stepInto(key);
				ContextData contextData = myDebugService.getContextData(key);
				jo.put("ret", 1);
				jo.put("key", key);
				jo.put("lineno", contextData.getLineno());
				jo.put("currentLevel", contextData.getCurrentLevel());
			}else{
				jo.put("ret", 0);
				jo.put("msg", "key is null");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e1.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String stepOut(){
		JSONObject jo = new JSONObject();
		try{
			if(key != null && !key.equals("")){
				myDebugService.stepOut(key);
				ContextData contextData = myDebugService.getContextData(key);
				jo.put("ret", 1);
				jo.put("key", key);
				jo.put("lineno", contextData.getLineno());
				jo.put("currentLevel", contextData.getCurrentLevel());
			}else{
				jo.put("ret", 0);
				jo.put("msg", "key is null");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e1.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String go(){
		JSONObject jo = new JSONObject();
		try{
			if(key != null && !key.equals("")){
				myDebugService.go(key);
				ContextData contextData = myDebugService.getContextData(key);
				jo.put("ret", 1);
				jo.put("key", key);
				jo.put("lineno", contextData.getLineno());
				jo.put("currentLevel", contextData.getCurrentLevel());
			}else{
				jo.put("ret", 0);
				jo.put("msg", "key is null");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e1.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String breakk(){
		JSONObject jo = new JSONObject();
		try{
			if(key != null && !key.equals("")){
				myDebugService.breakk(key);
				ContextData contextData = myDebugService.getContextData(key);
				jo.put("ret", 1);
				jo.put("key", key);
				jo.put("lineno", contextData.getLineno());
				jo.put("currentLevel", contextData.getCurrentLevel());
			}else{
				jo.put("ret", 0);
				jo.put("msg", "key is null");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			try {
				jo.put("ret", 0);
				jo.put("msg", e.getMessage());
			} catch (JSONException e1) {
				log.error(e1.getMessage());
			}
		}
		json = jo.toString();
		log.info(json);
		return "json";
	}
	
	public String setBreakPoint(){
		myDebugService.setBreakPoint(key, lineno, true);
		Map<Integer, Boolean>  bpmap = myDebugService.getBreakPointJson(key);
		JSONObject jsonObj = new JSONObject(bpmap);
		json = jsonObj.toString();
		return "json";
	}
	
	public String removeBreakPoint(){
		myDebugService.removeBreakPoint(key, lineno);
		Map<Integer, Boolean>  bpmap = myDebugService.getBreakPointJson(key);
		JSONObject jsonObj = new JSONObject(bpmap);
		json = jsonObj.toString();
		return "json";
	}
	
	public String disableBreakPoint(){
		myDebugService.setBreakPoint(key, lineno, false);
		Map<Integer, Boolean>  bpmap = myDebugService.getBreakPointJson(key);
		JSONObject jsonObj = new JSONObject(bpmap);
		json = jsonObj.toString();
		return "json";
	}
	
	/*
	 * getter and setter
	 */
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}

	public void setLineno(Integer lineno) {
		this.lineno = lineno;
	}

	public void setMyDebugService(MyDebugService myDebugService) {
		this.myDebugService = myDebugService;
	}

	public String getResult() {
		return result;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getJson() {
		return json;
	}

	public ScriptPluginFactory getPluginFactory() {
		return pluginFactory;
	}

	public void setPluginFactory(ScriptPluginFactory pluginFactory) {
		this.pluginFactory = pluginFactory;
	}
    public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public String getId1() {
		return id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}
}
