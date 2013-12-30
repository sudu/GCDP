package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

public interface MonitorMapper <T extends BaseModel> extends BaseMapper<T> {
	/*Log表操作*/
		//基本增删改查操作
	public List<T> getFromLog(T entity);
	public void deleteFromLog(T entity);
	public void insertToLog(T entity);
	public void updateToLog(T entity);
	
		//Action操作
	public List<T> getAllFromLog(PageCondition pc);
	public List<T> getByTaskFromLog(T entity);
	public List<T> getByTimeFromLog(T entity);
	public T getLastLogByName(String taskName);
	public int getLogCount(PageCondition pc);
	public int cleanLog(String cleanDate);
	
	/*Err表操作*/
		//基本增删改查操作
	public List<T> getFromErr(T entity);
	public void deleteFromErr(T entity);
	public void insertToErr(T entity);
	public void updateToErr(T entity);
	
		//Action操作
	public List<T> getAllFromErr(PageCondition pc);
	public List<T> getByNodeIdFromErr(PageCondition pc);
	public List<T> getByTimeFromErr();
	public int getErrCount(PageCondition pc);
	public int getErrCountByNodeID(PageCondition pc);
	public int cleanErr(String cleanDate);
	
	/*Task表操作*/
		//基本增删改查操作
	public T getFromTask(T entity);
	public void deleteFromTask(T entity);
	public void insertToTask(T entity);	
	public void updateToTask(T entity);
	
		//Action操作
	public List<T> getActiveTask(String serviceType);
	public List<T> getAllTask(PageCondition pc);
	public List<T> getTaskByNodeID(PageCondition pc);
	public int getTaskCount(PageCondition pc);
	public int getTaskCountByNodeID(PageCondition pc);
	public List<String> getTaskByName(String taskName);
	public T getTaskByExactName(String taskName);

}
