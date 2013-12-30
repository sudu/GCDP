package com.me.GCDP.workflow.model;

import java.util.List;



/**
 * AsyncSuitePluginDef对应流程定义文件中的一个<plugin type="...AsyncSuite">
 * 该类的asyncPlugins属性对应<plugin>标签下面的一系列<async-plugin>标签
 * @author xiongfeng
 *
 */
public class AsyncSuitePluginDef extends PluginDef {
	private List<PluginDef> asyncPlugins;

	public List<PluginDef> getAsyncPlugins() {
		return asyncPlugins;
	}

	public void setAsyncPlugins(List<PluginDef> asyncPlugins) {
		this.asyncPlugins = asyncPlugins;
	}
	
}
