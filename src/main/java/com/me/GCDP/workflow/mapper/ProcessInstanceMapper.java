package com.me.GCDP.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.me.GCDP.workflow.model.IndexRange;
import com.me.GCDP.workflow.model.ProcessInstanceInfo;


public interface ProcessInstanceMapper {
	public List<ProcessInstanceInfo> getProcessInstance(int processId);
	
	public int getInstanceCount(int processId);
	
	public List<ProcessInstanceInfo> getProcessInstanceLimit(@Param(value="processId")int processId, @Param(value="startPos")int startPos, @Param(value="recordNum")int recordNum);

	public List<ProcessInstanceInfo> getProcessInstance0(@Param(value="nodeId")int nodeId, @Param(value="formId")int formId, @Param(value="articleId")int articleId);
	
	public IndexRange getLegacyInstanceRange(int interval);
	
	public void deleteProcessInstanceByIdRange(IndexRange range);
}
