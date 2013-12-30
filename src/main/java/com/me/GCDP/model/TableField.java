package com.me.GCDP.model;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-19              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class TableField extends BaseModel {
	
	private static final long serialVersionUID = 1L;

	private String Field = null;
	
	private String Type = null;
	
	private String Null = null;
	
	private String Key = null;
	
	private String Default = null;
	
	private String Extra = null;
	
	private String desc = null;
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getField() {
		return Field;
	}

	public void setField(String field) {
		Field = field;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getNull() {
		return Null;
	}

	public void setNull(String null1) {
		Null = null1;
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getDefault() {
		return Default;
	}

	public void setDefault(String default1) {
		Default = default1;
	}

	public String getExtra() {
		return Extra;
	}

	public void setExtra(String extra) {
		Extra = extra;
	}

}
