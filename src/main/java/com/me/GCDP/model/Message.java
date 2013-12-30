package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;


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

public class Message extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String title = null;
	
	private String content = null;
	
	private Long createDate = null;
	
	private Long endDate = null;
	
	private Long expiration = null;
	
	private String target = null;
	
	private String creator = null;
	
	private String nodeName = null;
	
	private int num = 10;
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getTitle() {
		return title;
	}
	
	public Long getCreateDate() {
		return createDate;
	}
	
	public String getCreateDateStr(){
		if(this.createDate != null){
			Date d = new Date();
			d.setTime(this.createDate*1000);
			return sdf.format(d);
		}else{
			return "";
		}
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getEndDate() {
		return endDate;
	}
	
	public String getEndDateStr(){
		if(this.endDate != null){
			Date d = new Date();
			d.setTime(this.endDate*1000);
			return sdf.format(d);
		}else{
			return "";
		}
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
}
