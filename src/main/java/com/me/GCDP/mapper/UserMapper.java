package com.me.GCDP.mapper;

import java.util.List;
import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

public interface UserMapper<T extends BaseModel> extends BaseMapper<T> {
	public List<T> getByGroupId(PageCondition groupId);
	public List<T> getNotJoinByGroupId(PageCondition page);
 	public List<T> getUserByPage(PageCondition page);
	public int getUserCount();
	public int getNotJoinUserCount(PageCondition groupId);
	public int getJoinUserCount(PageCondition groupId);
	public T getById(int userId);
	public T getByUserName(String userName);
}
