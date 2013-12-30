package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :chengds
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-2-10              chengds               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public interface VersionMapper<T extends BaseModel> extends BaseMapper<T> {
	public List<T> get(com.me.GCDP.model.Version version);
	public T getById(int id);
 	public List<T> getByKey(String key);
	public int getCount(PageCondition page);
	public List<T> getByPage(PageCondition page);
}
