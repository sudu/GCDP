package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;

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

public interface BaseMapper<T extends BaseModel> {

	public List<T> get(T entity);
	
	public int delete(T entity);
	
	public int insert(T entity);
	
	public int update(T entity);
	
}
