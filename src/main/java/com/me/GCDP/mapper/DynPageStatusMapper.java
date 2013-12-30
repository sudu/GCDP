package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

public interface DynPageStatusMapper<T extends BaseModel> extends BaseMapper<T>{
	public List<T> getPageStatusByTime(T entity);
	
	public List<T> getAllPageStatus(PageCondition pc);
	public int getAllPageStatusCount(PageCondition pc);
	
	public int getLastPageCount(PageCondition pc);
	public List<T> getLastPages(PageCondition pc);

}
