package com.me.GCDP.workflow.mapper;

import java.util.List;

import com.me.GCDP.workflow.model.ProcessInfoForQuery;

/**
 * 根据节点id来获取该节点所有流程列表信息的Mapper
 * @author xiongfeng
 *
 * @param <T>
 */
public interface ProcessListMapper {

	public List<ProcessInfoForQuery> getProcessListByNodeId(int nodeId);
}
