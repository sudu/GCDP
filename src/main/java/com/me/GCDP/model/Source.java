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

public class Source extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	//名称
	private String name = null;
	
	//表单ID
	private Integer formId = null;
	
	//字段列表
	private String fieldList = null;
	
	//描述
	private String des = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public String getFieldList() {
		return fieldList;
	}

	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}
	
}
