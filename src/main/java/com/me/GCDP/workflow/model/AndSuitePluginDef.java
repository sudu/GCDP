package com.me.GCDP.workflow.model;

import java.util.List;

/**
 * AndSuitePluginDef对应流程定义文件中的一个<plugin type="com.ifeng.common.plugin.process.AndSuite">
 * 该类的andPlugins属性对应<plugin>标签下面的一系列<and-plugin>标签
 * @author xiongfeng
 *
 */
public class AndSuitePluginDef extends PluginDef {
	private List<PluginDef> andPlugins;

	public List<PluginDef> getAndPlugins() {
		return andPlugins;
	}

	public void setAndPlugins(List<PluginDef> andPlugins) {
		this.andPlugins = andPlugins;
	}
}
