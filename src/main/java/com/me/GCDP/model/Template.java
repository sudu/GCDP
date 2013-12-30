package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.me.GCDP.template.TemplateException;

public class Template extends BaseModel {
	private static final long serialVersionUID = -6561546516820881667L;
	
	private String name;
	private String content;
	private String powerPath;
	private Integer enable;
	private Integer dataFormId;
	private Integer dataId;
	private Date createDate;
	private Date modifyDate;
	
	final public static int AVAILABLE = 1;
	final public static int UNAVAILABLE = 0;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", getId());
		map.put("name", name);
		map.put("content", content);
		map.put("powerPath", powerPath);
		map.put("enable", enable);
		map.put("formId", dataFormId);
		map.put("dataId", dataId);
		if (createDate != null || modifyDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (createDate != null) {
				String _createDate = sdf.format(createDate);
				map.put("createDate", _createDate);
			}
			if (modifyDate != null) {
				String _modifyDate = sdf.format(modifyDate);
				map.put("modifyDate", _modifyDate);
			}
		}
		return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws TemplateException {
		if (name == null || name == "") {
			throw new TemplateException("模版名称不能为空或null");
		}
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) throws TemplateException {
		if (content == null || content == "") {
			throw new TemplateException("模版内容不能为空或null");
		}
		this.content = content;
	}

	public String getPowerPath() {
		return powerPath;
	}

	public void setPowerPath(String powerPath) {
		this.powerPath = powerPath;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getFormId() {
		return dataFormId;
	}

	public void setFormId(Integer formId) throws TemplateException {
		if (formId <= 0) {
			throw new TemplateException("指定formId不能为0或负数");
		}
		this.dataFormId = formId;
	}

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) throws TemplateException {
		if (dataId < 0) {
			throw new TemplateException("指定dataId不能为负数");
		}
		this.dataId = dataId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Integer getDataFormId() {
		return dataFormId;
	}

	public void setDataFormId(Integer dataFormId) {
		this.dataFormId = dataFormId;
	}

}
