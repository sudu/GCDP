package com.me.GCDP.workflow.model;

import java.util.List;

/**
 * ForEachSuitePluginDef对应于流程定义文件中的一个<plugin type="com.ifeng.common.plugin.process.ForEachSuite"/>标签
 * 该类的forEachPlugins对应<plugin>标签下的一系列<foreach-plugin>标签
 * @author xiongfeng
 *
 */
public class ForEachSuitePluginDef extends PluginDef{
	private List<PluginDef> forEachPlugins;

	public List<PluginDef> getForEachPlugins() {
		return forEachPlugins;
	}

	public void setForEachPlugins(List<PluginDef> forEachPlugins) {
		this.forEachPlugins = forEachPlugins;
	}
}
