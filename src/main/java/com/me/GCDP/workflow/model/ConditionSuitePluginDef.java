package com.me.GCDP.workflow.model;

/**
 * ConditionSuitePluginDef对应流程定义文件中的一个<plugin type="com.ifeng.common.plugin.process.ConditionSuite">标签
 * 该类的condition属性对应于<plugin>标签下的<condition>标签
 * 该类的trueCondition属性对应于<plugin>标签下的<on-true>标签
 * 该类的falseCondition属性对应于<plugin>标签下的<on-false>标签
 * @author xiongfeng
 *
 */
public class ConditionSuitePluginDef extends PluginDef{
	private PluginDef condition;
	private PluginDef trueCondition;
	private PluginDef falseCondition;
	public PluginDef getCondition() {
		return condition;
	}
	public void setCondition(PluginDef condition) {
		this.condition = condition;
	}
	public PluginDef getTrueCondition() {
		return trueCondition;
	}
	public void setTrueCondition(PluginDef trueCondition) {
		this.trueCondition = trueCondition;
	}
	public PluginDef getFalseCondition() {
		return falseCondition;
	}
	public void setFalseCondition(PluginDef falseCondition) {
		this.falseCondition = falseCondition;
	}
	
}
