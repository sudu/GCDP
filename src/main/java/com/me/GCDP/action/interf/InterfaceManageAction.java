package com.me.GCDP.action.interf;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.CookiesAware;

import com.me.GCDP.mapper.InterfaceMapper;
import com.me.GCDP.model.Interface;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: 用户定义接口管理Action</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-21              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class InterfaceManageAction extends ActionSupport implements CookiesAware {
	
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(InterfaceManageAction.class);
	
	private Integer nodeId = null;
	
	private String msg = null;
	
	private Boolean hasError = false;
	
	private ScriptService scriptService = null;
	private InterfaceMapper<Interface> ifMapper = null;
	
	private List<Interface> ifList = null;
	
	private Interface interf = new Interface();
	
	private Map<String, String> cookieMap = null;

	public void setCookiesMap(Map<String, String> arg0) {
		this.cookieMap = arg0;
	}

	public String execute(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		try{
			interf.setNodeid(nodeId);
			ifList = ifMapper.get(interf);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "main";
	}
	
	/**
	 * 查看接口完整信息
	 * @return
	 */
	public String view() throws Exception{
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(interf.getId() == null || interf.getId() == 0){
			return "view";
		}
		try{
			interf.setNodeid(nodeId);
			List<Interface> ifList = ifMapper.get(interf);
			interf = ifList != null && ifList.size()>0?ifList.get(0) : null;
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		if(interf != null){
			//interf.setScript(scriptService.openInterfaceScript(interf.getId()+""));
			interf.setScript(scriptService.openLatest(nodeId, ScriptType.interf, interf.getId().toString()));
			//interf.setTemplate(scriptService.openInterfaceTemplate(interf.getId()+""));
			interf.setTemplate(scriptService.openLatest(nodeId, ScriptType.interf_template, interf.getId().toString()));
		}else{
			msg = "未知接口";
			hasError = true;
			return "msg";
		}
		return "view";
	}
	
	/**
	 * 保存接口模板
	 * @return
	 */
	public String saveTemplate(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(interf.getId()==null){
			msg = "接口ID为null";
			hasError = true;
			return "msg";
		}
		try {
			//scriptService.saveInterfaceTemplate(interf.getId().toString(), interf.getTemplate());
			scriptService.save(nodeId, interf.getTemplate(), ScriptType.interf_template, interf.getId().toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			msg = "保存失败";
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 保存接口脚本 
	 * @return
	 */
	public String saveScript(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(interf.getId()==null){
			msg = "接口ID为null";
			hasError = true;
			return "msg";
		}
		try {
			//scriptService.saveInterfaceScript(interf.getId().toString(), interf.getScript());
			scriptService.save(nodeId, interf.getScript(), ScriptType.interf, interf.getId().toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			msg = "保存失败";
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 添加接口
	 * @return
	 */
	public String add(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		if(interf.getName() == null || interf.getName().equals("")){
			msg = "接口名称不能为空";
			hasError = true;
			return "msg";
		}
		interf.setCreatedate(System.currentTimeMillis()/1000L);
		if(interf.getCreator() == null || interf.getCreator().equals("")){
			if(cookieMap.get("cmpp_cn") != null && !cookieMap.get("cmpp_cn").equals("")){
				try{
					String uname = URLDecoder.decode(cookieMap.get("cmpp_cn"),"UTF-8");
					interf.setCreator(uname);
				}catch(Exception e){
					interf.setCreator(cookieMap.get("cmpp_user"));
					log.error(e.getMessage());
				}
			}
		}
		interf.setNodeid(nodeId);
		try{
			ifMapper.insert(interf);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		
		if( interf.getScript() != null ){
			try {
				//String script = scriptService.openInterfaceScript(interf.getId().toString());
				String script = scriptService.open(nodeId, ScriptType.interf, interf.getId().toString());
				if(!interf.getScript().equals(script)){
					//scriptService.saveInterfaceScript(interf.getId().toString(), interf.getScript());
					scriptService.save(nodeId, interf.getScript(), ScriptType.interf, interf.getId().toString());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				msg = "接口脚本保存失败";
				hasError = true;
			}
		}
		
		if( interf.getTemplate() != null ){
			try {
				//String template = scriptService.openInterfaceTemplate(interf.getId().toString());
				String template = scriptService.open(nodeId, ScriptType.interf_template, interf.getId().toString());
				if(!interf.getTemplate().equals(template)){
					//scriptService.saveInterfaceTemplate(interf.getId().toString(), interf.getTemplate());
					scriptService.save(nodeId, interf.getTemplate(), ScriptType.interf_template, interf.getId().toString());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				if(msg != null)
					msg += ",接口模板保存失败";
				else
					msg = "接口模板保存失败";	
				hasError = true;
			}
		}
		return "msg";
	}
	
	/**
	 * 
	 * @return
	 */
	public String delete(){
		if(interf.getId() == null){
			msg = "接口ID为空";
			hasError = true;
			return "msg";
		}else{
			ifMapper.delete(interf);
		}
		return "msg";
	}
	
	/**
	 * 修改接口
	 * @return
	 */
	public String update(){
		if(interf.getId() == null || interf.getId() == 0){
			//id为空则为添加接口
			return this.add();
		}
		try{
			ifMapper.update(interf);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		
		if( interf.getScript() != null ){
			try {
				//String script = scriptService.openInterfaceScript(interf.getId().toString());
				String script = scriptService.open(nodeId, ScriptType.interf, interf.getId().toString());
				if(!interf.getScript().equals(script)){
					//scriptService.saveInterfaceScript(interf.getId().toString(), interf.getScript());
					scriptService.saveDebug(nodeId, interf.getScript(), ScriptType.interf, interf.getId().toString());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				msg = "接口脚本保存失败";
				hasError = true;
			}
		}
		
		if( interf.getTemplate() != null ){
			try {
				//String template = scriptService.openInterfaceTemplate(interf.getId().toString());
				String template = scriptService.open(nodeId, ScriptType.interf_template, interf.getId().toString());
				if(!interf.getTemplate().equals(template)){
					//scriptService.saveInterfaceTemplate(interf.getId().toString(), interf.getTemplate());
					scriptService.saveDebug(nodeId, interf.getTemplate(), ScriptType.interf_template, interf.getId().toString());
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				if(msg != null)
					msg += ",接口模板保存失败";
				else
					msg = "接口模板保存失败";	
				hasError = true;
			}
		}
		return "msg";
	}
	
	/*
	 * getter and setter
	 */
	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setIfMapper(InterfaceMapper<Interface> ifMapper) {
		this.ifMapper = ifMapper;
	}

	public List<Interface> getIfList() {
		return ifList;
	}

	public Interface getInterf() {
		return interf;
	}

	public void setInterf(Interface interf) {
		this.interf = interf;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	
}
