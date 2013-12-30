package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.PageCondition;

public interface ResourceFileMapper<T extends BaseModel> extends BaseMapper<T>
{
	// 查询
	/** 获取某节点下的所有记录 **/
	public List<T> getFilesByNodeID(T entry);
	/** 获取可用的记录 **/
	public List<T> getFilesByRecordStatus(T entity);
	/** 获取某文件夹下的文件记录 **/
	public List<T> getFilesByParentAndNode(T entry);
	// 删除
	public void deleteByParentID(T entity);
	// 更改记录
	public void disablbeRecord(T entity);
	public void disablbeRecordByParentID(T entity);
	
	/** 获取记录（分页） **/
	public List<T> getPagenationFiles(PageCondition pc);
	public List<T> getPagenationFilesWithoutLimit(PageCondition pc);
	public int getFileCount(PageCondition pc);
	/** 按文件名搜索，按文件创建时间排序 **/
	public List<T> getPagenationFilesBySearch(PageCondition pc);
	
	/** 获取某节点的某记录 **/
	public List<T> getFilesByIdAndNode(T entity);
	
	public List<T> getFilesByDistributionAddress(T entity);
	
	public List<T> getFilesById(T entity);
	
	public void deleteByDistributionAddress(T entity);
	
    /* 更新fileDesc字段 */
	public int updateFileDesc(T entity);
	
}
