/**
 * 
 */
package com.me.GCDP.action.version;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.PageCondition;
import com.me.GCDP.model.Version;
import com.me.GCDP.util.JsonUtils;
import com.me.GCDP.util.Page;
import com.me.GCDP.version.VersionHelper2;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author chengds
 *
 */
public class VersionAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//节点ID
	private Integer nodeId = 0;
	private int id = 0;
	private Integer formId;
	private int viewId;
	@SuppressWarnings("rawtypes")
	private VersionMapper versionMapper;
	private int start;
    private int limit;
    private Version version;
    private long timespan;
	
	/**
	 * 数据列表
	 */
	@SuppressWarnings({ "unchecked" })
	public void list() throws Exception {
		PageCondition page=new PageCondition();
		page.setFrom(start);
		page.setLimit(limit);

		page.setFilterTxt("key");
		page.setFilterValue(version.getKey());
		 
		List<Version> result=versionMapper.getByPage(page);
		Page<Version> p=new Page<Version>(result, versionMapper.getCount(page));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");// 设置字符集编码
		JsonUtils.write(p, response.getWriter());
	}
	
	public void getFormDatabyKey() throws IOException{
		VersionHelper2 fh = new VersionHelper2();
		String nosqlKey = version.getKey()+"_" + this.timespan;
		String dataStr = fh.getFormDataByKey(nosqlKey);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");// 设置字符集编码
		response.getWriter().write(dataStr);
	}
	
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getFormId() {
		return formId;
	}
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	public int getViewId() {
		return viewId;
	}
	public void setViewId(int viewId) {
		this.viewId = viewId;
	}
	@SuppressWarnings("rawtypes")
	public VersionMapper getVersionMapper() {
		return versionMapper;
	}
	@SuppressWarnings("rawtypes")
	public void setVersionMapper(VersionMapper versionMapper) {
		this.versionMapper = versionMapper;
	}	
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}
	

	public long getTimespan() {
		return timespan;
	}

	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}


}
