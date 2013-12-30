/**
 * 
 */
package com.me.GCDP.model;

/**
 * @author zhangzy
 *
 */
public class UserGroup extends BaseModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userId;
	private int groupId;
	

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	
	
}
