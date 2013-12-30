package com.me.GCDP.workflow.service;

/**
 * 枚举，对应icommon中的与流程定义相关的各种插件的类型以及我们扩展的ScriptPlugin类型
 * 和AsyncSuite类型
 * @author xiongfeng
 *
 */
public enum PluginEnum {
	AND("com.ifeng.common.plugin.process.AndSuite"),
	OR("com.ifeng.common.plugin.process.OrSuite"),
	COND("com.ifeng.common.plugin.process.ConditionSuite"),
	FOREACH("com.ifeng.common.plugin.process.ForEachSuite"),
	SCRIPT("com.me.GCDP.workflow.plugin.ScriptPlugin"),
	SWITCH("com.ifeng.common.plugin.process.SwitchSuite"),
	WHILE("com.ifeng.common.plugin.process.WhileSuite"),
	CONDSCRIPT("com.me.GCDP.workflow.plugin.ConditionScriptPlugin"),
	SWITCHCONDSCRIPT("com.me.GCDP.workflow.plugin.SwitchConditionScriptPlugin"),
	ASYNC("com.me.GCDP.workflow.plugin.AsyncSuite");
	private String pluginType;
	PluginEnum(String pluginType) {
		this.pluginType = pluginType;
	}
	@Override
	public String toString() {
		return this.pluginType;
	}
}
