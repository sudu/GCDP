/**
 * 
 */
package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;

/**
 * @author zhangzy
 * 2011-7-14
 */

public interface TaskMapper<Task extends BaseModel> extends BaseMapper<Task> {
	public Task getTaskById(int id);
	public int update(Task task);
	public List<Task> getTaskList(int nodeId);
	public int getLastId();
	public List<Task> getTaskByStatus(int status);
	public List<Task> getStartTask();
}
