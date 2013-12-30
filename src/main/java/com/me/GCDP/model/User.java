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
public class User extends BaseModel{
	
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;

	
	
	private String username=null;
	
	private String cnname=null;
	
	private String password=null;
	
	private String email=null;
	
	private String dept=null;
	
	private String telphone=null;
	
	private String sAMAccountName=null;
	
	private List<Group> groups = new ArrayList<Group>(); 

	


	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}



	

}
