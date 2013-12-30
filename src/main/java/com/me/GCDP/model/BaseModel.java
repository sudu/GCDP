package com.me.GCDP.model;

import java.io.Serializable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-2-10              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class BaseModel implements Serializable {
	
	private static final long serialVersionUID = -8333671492822118962L;
	
	private Integer id = null;
	
	private Integer nodeid = null;
	
	//排序
    private String orderByField = null;
	
	public String getOrderByField() {
		return orderByField;
	}

	public void setOrderByField(String orderByField) {
		this.orderByField = orderByField;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
