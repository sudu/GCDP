package com.me.GCDP.model;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-18              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class FormConfig extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	//表单名称
	private String name = null;
	
	//数据库表名
	private String tableName = null;
	
	//表单配置信息
	private String config = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
	
}
