/**
 * 表单版本信息
 */
package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author chengds
 * 
 */
public class Version extends BaseModel{
	
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;

	private String username=null;
	
	private Date lastmodify=null;
	
	private String lastmodifyStr=null;
	
	private long timestamp;
	
	private String key=null;
	
	private String data=null;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getLastmodify() {
		return lastmodify;
	}

	public void setLastmodify(Date lastmodify) {
		this.lastmodify = lastmodify;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}	
	
	public long getTimestamp(){
		this.timestamp = this.lastmodify.getTime()/1000;
		return timestamp;
	}
	
	public String getLastmodifyStr() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		lastmodifyStr = formatter.format(this.lastmodify);
		return lastmodifyStr;
	}

}
