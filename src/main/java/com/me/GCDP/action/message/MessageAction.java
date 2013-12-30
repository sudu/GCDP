package com.me.GCDP.action.message;

import java.util.ArrayList;
import java.util.List;

import com.me.GCDP.mapper.MessageMapper;
import com.me.GCDP.model.Message;
import com.me.GCDP.model.Permission;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.security.SecurityManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-11-22              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class MessageAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	//private static Log log = LogFactory.getLog(MessageAction.class);
	
	private String msg = null;
	private Boolean hasError = false;
	
	private Message m = new Message();
	private List<Message> msgList = null;
	private List<String> idList = null;
	
	private MessageMapper<Message> messageMapper = null;
	
	private String ids = null;
	
	private Integer nodeId = null;
	
	private SecurityManager securityManager = null;
	
	public String send(){
		
		if(!securityManager.checkSystemPermission(Permission.MODIFY)){
			msg = "对不起，你没有相应权限";
			hasError = true;
			return "msg";
		}
		
		m.setCreateDate(System.currentTimeMillis()/1000);
		if(m.getExpiration() != null){
			m.setEndDate(m.getCreateDate() + m.getExpiration());
		}
		
		if(nodeId != null){
			m.setNodeid(nodeId);
		}
		try{
			m.setCreator(AuthorzationUtil.getUserName());
			/*if(m.getCreator() != null){
				m.setCreator(new String(m.getCreator().getBytes("iso8859-1"),"UTF-8"));
			}
			if(m.getTitle() != null){
				m.setTitle(new String(m.getTitle().getBytes("iso8859-1"),"UTF-8"));
			}
			if(m.getContent() != null){
				m.setContent(new String(m.getContent().getBytes("iso8859-1"),"UTF-8"));
			}*/
			messageMapper.insert(m);
		}catch(Exception e){
			hasError = true;
			msg = e.getMessage();
			return "msg";
		}
		hasError = false;
		return "msg";
	}
	
	public String list(){
		if(nodeId == null){
			msg = "nodeid为空";
			hasError = true;
			return "msg";
		}
		Message m = new Message();
		m.setNodeid(nodeId);
		msgList = messageMapper.get(m);
		return "msgList";
	}
	
	public String get(){
		msgList = new ArrayList<Message>();
		if(m.getId() == null && ids == null){
			return "message";
		}
		
		for(String id : ids.split("\\,")){
			if(id != null && !id.equals("")){
				Message m = new Message();
				m.setId(Integer.parseInt(id));
				List<Message> mList = messageMapper.get(m);
				msgList.addAll(mList);
			}
		}
		
		/*List<Message> mList = messageMapper.get(m);
		if(mList != null && mList.size()>0){
			m = mList.get(0);
		}*/
		return "message";
	}

	/*
	 * getter and setter
	 */
	public Message getM() {
		return m;
	}

	public void setM(Message m) {
		this.m = m;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setMessageMapper(MessageMapper<Message> messageMapper) {
		this.messageMapper = messageMapper;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public List<Message> getMsgList() {
		return msgList;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	
	
}
