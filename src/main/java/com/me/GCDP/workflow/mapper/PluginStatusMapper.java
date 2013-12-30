package com.me.GCDP.workflow.mapper;

import java.util.List;

import com.me.GCDP.workflow.model.IndexRange;
import com.me.GCDP.workflow.model.PluginStatusInfo;

public interface PluginStatusMapper {
	public List<PluginStatusInfo> getPluginStatus(int instanceId);
	/**
	 * 根据流程实例id范围来删除该流程实例所对应的的流程运行状态数据
	 */
	public void deleteByPCIdRange(IndexRange range);
}
