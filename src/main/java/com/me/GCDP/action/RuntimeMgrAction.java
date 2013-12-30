package com.me.GCDP.action;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.model.Permission;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-9-23              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class RuntimeMgrAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(RuntimeMgrAction.class);
	
	private String msg = null;

	private Boolean hasError = false;
	
	private String menuConfig = null;
	private String welcomeUrl = null;
	private String headInject = null;
	private String bodyInject = null;
	
	private Integer nodeId = null;
	
	private ScriptService scriptService = null;
	private SecurityManager securityManager = null;
	
	public String execute() throws Exception {
		try {
			this._readRuntimeConfig();
		} catch (JSONException e) {
			log.error("readRuntimeConfig : " + e.getMessage());
            return "authorizationFails";
		}	
		
		return "success";
	}
	
	public String preview(){
		if(!securityManager.checkSystemPermission(Permission.CHECK)){
			return "authorizationFails";
		}
		return "index_preview";
	}
	
	public String readSysMenu(){
		try {
			menuConfig = scriptService.open(nodeId, ScriptType.sysMenu, nodeId+"");
		} catch (IOException e) {
			hasError = true;
			log.error(e.getMessage());
			msg = e.getMessage();
		}
		return "sysMenu";
	}

	public String writeSysMenu(){
		if(!securityManager.checkSystemPermission(Permission.MODIFY)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		try {
			scriptService.save(nodeId, menuConfig, ScriptType.sysMenu, nodeId+"");
			hasError = false;
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			log.error("writeSysMenu : " + e.getMessage());
		}
		return "msg";
	}

    private void _readRuntimeConfig() throws IOException,JSONException {
        menuConfig = scriptService.open(nodeId, ScriptType.sysMenu, nodeId+"");
        String runtimeCfgStr = scriptService.open(nodeId, ScriptType.runtimeConfig, nodeId+"");
        if(runtimeCfgStr!=null && !runtimeCfgStr.equals("")){
            JSONObject runtimeCfg = new JSONObject(runtimeCfgStr);
            welcomeUrl = runtimeCfg.getString("welcomeUrl");
            headInject = runtimeCfg.getString("headInject");
            bodyInject = runtimeCfg.getString("bodyInject");
        }
    }
	
	public String readRuntimeConfig() throws JSONException{
		if(!securityManager.checkSystemPermission(Permission.CHECK)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		try {
            _readRuntimeConfig();
		} catch (IOException e) {
			hasError = true;
			log.error(e.getMessage());
			msg = e.getMessage();
		}
		return "runtimeConfig";
	}
	
	public String writeRuntimeConfig(){
		if(!securityManager.checkSystemPermission(Permission.MODIFY)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		try {
			scriptService.save(nodeId, menuConfig, ScriptType.sysMenu, nodeId+"");
			
			JSONObject runtimeCfg = new JSONObject();
			runtimeCfg.put("welcomeUrl", welcomeUrl);
			runtimeCfg.put("headInject", headInject);
			runtimeCfg.put("bodyInject", bodyInject);
			scriptService.save(nodeId, runtimeCfg.toString(), ScriptType.runtimeConfig, nodeId+"");
			
			hasError = false;
		} catch (Exception e) {
			hasError = true;
			msg = e.getMessage();
			log.error("writeSysMenu : " + e.getMessage());
		}
		return "msg";
	}
	
	/*
	 * getter and setter
	 */
	
	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public String getMenuConfig() {
		return menuConfig;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setMenuConfig(String content) {
		this.menuConfig = content;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getWelcomeUrl() {
		return welcomeUrl;
	}

	public void setWelcomeUrl(String welcomeUrl) {
		this.welcomeUrl = welcomeUrl;
	}

	public String getHeadInject() {
		return headInject;
	}

	public void setHeadInject(String headInject) {
		this.headInject = headInject;
	}

	public String getBodyInject() {
		return bodyInject;
	}

	public void setBodyInject(String bodyInject) {
		this.bodyInject = bodyInject;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
}
