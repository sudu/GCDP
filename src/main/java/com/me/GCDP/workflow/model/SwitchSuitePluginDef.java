package com.me.GCDP.workflow.model;

import java.util.Map;

/**
 * SwitchSuitePluginDef对应于流程定义文件中的
 * <plugin type="com.ifeng.common.plugin.process.SwitchSuite">标签
 * 该类的switchCondition属性对应于<plugin>标签下的<switchCondition>标签
 * 该类的stepModulesMap属性对应于<plugin>标签下的<stepModulesMap>标签
 * @author xiongfeng
 */
public class SwitchSuitePluginDef extends PluginDef {
	private PluginDef switchCondition;
	private Map<String, PluginDef> stepModulesMap;
	
	public PluginDef getSwitchCondition() {
		return switchCondition;
	}
	public void setSwitchCondition(PluginDef switchCondition) {
		this.switchCondition = switchCondition;
	}
	public Map<String, PluginDef> getStepModulesMap() {
		return stepModulesMap;
	}
	public void setStepModulesMap(Map<String, PluginDef> stepModulesMap) {
		this.stepModulesMap = stepModulesMap;
	}
	
}
