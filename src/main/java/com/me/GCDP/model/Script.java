package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-12-5              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class Script extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String creator = null;
	
	private String script = null;
	
	private Long createDate = null;
	
	private String intro = null;
	
	private String name = null;
	private String name2 = null;//用于拼SQL
	
	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreateDate() {
		return createDate;
	}
	
	public String getCreateDateStr() {
		if(this.createDate != null){
			Date d = new Date();
			d.setTime(this.createDate*1000);
			return sdf.format(d);
		}else{
			return "";
		}
	}
	
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

}
