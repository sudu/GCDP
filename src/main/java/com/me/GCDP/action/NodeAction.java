package com.me.GCDP.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.NodeMapper;
import com.me.GCDP.model.Node;
import com.me.GCDP.model.Permission;
import com.me.GCDP.security.SecurityManager;
import com.me.GCDP.util.env.NodeEnv;
import com.me.GCDP.xform.SysInitializationService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-5              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class NodeAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(NodeAction.class);
	
	private Node node = null;
	
	private List<Node> nlist = null;
	
	private NodeMapper<Node> nodeMapper = null;
	
	private String msg = null;
	
	private Boolean hasError = false;
	
	private SecurityManager securityManager = null;
	
	private SysInitializationService sysInitializationService;
	
	@Override
	public String execute() throws Exception {
		nlist = nodeMapper.get(new Node());
		return "list";
	}
	
	public String view(){
		if(node == null || node.getId() == null){
			msg = "节点ID为空";
			log.error(msg);
			hasError = true;
			return "msg";
		}
		if(!securityManager.checkSystemPermission(Permission.CHECK)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		try{
			List<Node> nlist = nodeMapper.get(node);
			node = (nlist == null || nlist.size() == 0) ? null : nlist.get(0);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "view";
	}
	
	public String update(){
		if(node == null || node.getId() == null){
			msg = "节点ID为空";
			log.error(msg);
			hasError = true;
			return "msg";
		}
		
		if(!securityManager.checkSystemPermission(Permission.MODIFY)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
				
		try{
			nodeMapper.update(node);
			NodeEnv.getNodeEnvInstance().refresh();
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		msg = "节点信息更新成功";
		return "msg";
	}
	
	public String add(){
		if(node == null || node.getName() == null || node.getName().equals("")){
			msg = "节点名称为空";
			log.error(msg);
			hasError = true;
			return "msg";
		}
		if(!securityManager.checkSystemPermission(Permission.ADD)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		try{
			nodeMapper.insert(node);
			NodeEnv.getNodeEnvInstance().refresh();
			
			// 创建节点后，初始化操作
			sysInitializationService.init(node.getId());
			msg = node.getId()+"";
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
			return "msg";
		}
		return "msg";
	}

	/*
	 * getter and setter
	 */
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setNodeMapper(NodeMapper<Node> nodeMapper) {
		this.nodeMapper = nodeMapper;
	}

	public List<Node> getNlist() {
		return nlist;
	}

	public String getMsg() {
		return msg;
	}

	public boolean getHasError() {
		return hasError;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void setSysInitializationService(SysInitializationService sysInitializationService) {
		this.sysInitializationService = sysInitializationService;
	}
	
}
