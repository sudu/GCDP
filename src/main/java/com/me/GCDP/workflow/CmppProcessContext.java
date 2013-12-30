package com.me.GCDP.workflow;

import java.sql.Timestamp;
import java.util.Date;

import com.ifeng.common.workflow.ProcessContext;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-8-9              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class CmppProcessContext extends ProcessContext {
	
	private int nodeid = 0;
	
	//表单id
	private int formId = 0;
	//文章id
	private long articleId = 0; 
	
	private String instanceDesc;
	
	public static final int PENDING = 4;

	private Date definitionVersion;
	
	private Date instanceEndDate;
	
	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public String getInstanceDesc() {
		return instanceDesc;
	}

	public void setInstanceDesc(String instanceDesc) {
		this.instanceDesc = instanceDesc;
	}

	public Date getDefinitionVersion() {
		return definitionVersion;
	}

	public void setDefinitionVersion(Date definitionVersion) {
		this.definitionVersion = definitionVersion;
	}

	public Date getInstanceEndDate() {
		return instanceEndDate;
	}

	public void setInstanceEndDate(Date instanceEndDate) {
		this.instanceEndDate = instanceEndDate;
	}

	
	/*private Map<String, Object> dataPool = null;

	public Map<String, Object> getDataPool() {
		return dataPool;
	}

	public void setDataPool(Map<String, Object> dataPool) {
		this.dataPool = dataPool;
	}*/
	
	
	
}
