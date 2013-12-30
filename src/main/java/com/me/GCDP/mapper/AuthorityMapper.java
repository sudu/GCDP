/**
 * 
 */
package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;

/**
 * @author zhangzy
 *
 */
public interface AuthorityMapper<T extends BaseModel> extends BaseMapper<T> {
	public T getById(int authorityId);
	public List<T> getByGroupId(int groupId);
	public List<T> getByUserId(int userId);
	public List<T> getByUserName(String userName);
}