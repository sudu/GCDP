/**
 * 
 */
package com.me.GCDP.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzy
 *
 */
public class Group extends BaseModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupname;
	private String remark;
	private List<User> users = new ArrayList<User>(); 
	


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
}
