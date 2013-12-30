/**
 * 
 */
package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

/**
 * @author zhangzy
 *
 */
public interface GroupMapper<T extends BaseModel> extends BaseMapper<T> {
	public List<T> getGroupByUserId(int userId);
	public List<T> getGroupByPage(PageCondition page);
	public int getGroupCount();
	public T getById(int groupId);
	public List<T> getNotJoinGroupByUserId(PageCondition page);
	public int getNotJoinGroupCount(PageCondition page);
}