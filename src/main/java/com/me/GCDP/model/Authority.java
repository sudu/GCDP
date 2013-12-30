/**
 * 
 */
package com.me.GCDP.model;

/**
 * @author zhangzy
 *
 */
public class Authority extends BaseModel{

	private static final long serialVersionUID = 1L;
	private int type;
	private int permission;
	private String powerpath;
	private int groupid;
	private String permissionString;
	
	
	
	public String getPermissionString() {
		return permissionString;
	}
	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public String getPowerpath() {
		return powerpath;
	}
	public void setPowerpath(String powerpath) {
		this.powerpath = powerpath;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	
	
}
