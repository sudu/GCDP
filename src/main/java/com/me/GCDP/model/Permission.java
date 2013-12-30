/**
 * 
 */
package com.me.GCDP.model;

/**
 * @author zhangzy
 *
 */
public enum Permission {
	CHECK(1),ADD(2),MODIFY(4),DELETE(8),PUBLISH(16);
	
	private final int value;
	
	public int getValue(){
		return value;
	}

	private Permission(int value) {
		this.value = value;
	}
	
	public static Permission getInstance(int i){
		switch(i){
			case 1:
				return Permission.CHECK;
				
			case 2:
				return Permission.ADD;
				
			case 4:
				return Permission.MODIFY;
				
			case 8:
				return Permission.DELETE;
				
			case 16:
				return Permission.PUBLISH;
			
			default:
				return null;
		}
	}

}
