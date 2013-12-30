package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: 用户自定义接口模型</p>
 * <p>Description: 由二次开发人员使用</p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-2-9              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class Interface extends BaseModel {
	
	private static final long serialVersionUID = 1L;

	//接口名称
	private String name = null;
	
	//创建人
	private String creator = null;
	
	//创建日期 （10位  例：1308817245）
	private Long createdate = null;
	
	//参数描述列表（JSON）
	private String params = null;
	
	//接口描述
	private String description = null;
	
	//接口脚本
	private String script = null;
	
	//接口模板
	private String template = null;
	
	private Integer reqlogin = 0;
	
	/*
	 * getter and setter
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReqlogin() {
		return reqlogin;
	}

	public void setReqlogin(Integer reqlogin) {
		this.reqlogin = reqlogin;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Long createdate) {
		this.createdate = createdate;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getCreatedatestr(){
		String createdatestr = null;
		if(createdate != null){
			Date d = new Date();
			d.setTime(createdate*1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			createdatestr = sdf.format(d);
		}
		return createdatestr;
	}
	
}
