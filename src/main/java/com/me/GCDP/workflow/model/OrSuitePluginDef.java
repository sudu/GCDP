package com.me.GCDP.workflow.model;

import java.util.List;

/**
 * OrSuitePluginDef对应于流程定义文件中的一个<plugin type="com.ifeng.common.plugin.process.OrSuite"/>
 * 该类中的orPlugins属性对应于<plugin>标签下面的一系列<or-plugin>
 * @author xiongfeng
 *
 */
public class OrSuitePluginDef extends PluginDef {
	private List<PluginDef> orPlugins;

	public List<PluginDef> getOrPlugins() {
		return orPlugins;
	}

	public void setOrPlugins(List<PluginDef> orPlugins) {
		this.orPlugins = orPlugins;
	}
	
}
