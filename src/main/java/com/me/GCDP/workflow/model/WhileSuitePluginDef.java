package com.me.GCDP.workflow.model;

/**
 * WhileSuitePluginDef对应一个<plugin type="com.ifeng.common.plugin.process.WhileSuite"/>标签
 * 该类的whileConfition属性对应于<plugin>标签下的<condition>标签
 * 该类的onTruePlugin属性对应于<plugin>标签下的<on-true>标签
 * @author xiongfeng
 *
 */
public class WhileSuitePluginDef extends PluginDef {
	private PluginDef whileConfition;
	private PluginDef onTruePlugin;
	public PluginDef getWhileConfition() {
		return whileConfition;
	}
	public void setWhileConfition(PluginDef whileConfition) {
		this.whileConfition = whileConfition;
	}
	public PluginDef getOnTruePlugin() {
		return onTruePlugin;
	}
	public void setOnTruePlugin(PluginDef onTruePlugin) {
		this.onTruePlugin = onTruePlugin;
	}
}
