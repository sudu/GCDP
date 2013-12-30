package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

public interface ResourceFolderMapper<T extends BaseModel> extends BaseMapper<T>
{
	public List<T> getByNode(T entry);
	public List<T> getByParentID(T entity);
	public List<T> getByFolderPath(T entity);
	public List<T> getByDistributionPath(T entity);
	public List<T> getByDistributionPathLike(T entity);
	
	// 分页查询（查询所有）
	public List<T> getPagenationFolders(PageCondition pc);
	public List<T> getPagenationFoldersWithoutLimit(PageCondition pc);
	// 获取文件夹总数
	public int getFolderCount(PageCondition pc);
	
	// 带分页的查询（搜索）
	public List<T> getPagenationFoldersBySearch(PageCondition pc);
	
}
