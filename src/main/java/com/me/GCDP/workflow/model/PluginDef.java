package com.me.GCDP.workflow.model;

/**
 * PluginDef代表一个插件。对应于配置文件中的
 * <plugin type="" next-activity=""></plugin>元素。
 * PluginDef是所有插件类的父类
 * @author xiongfeng
 *
 */
public class PluginDef {
	protected String name;
	protected String type;
	protected String nextActivity;
	protected int defId;//保存数据库中的插件定义的id
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNextActivity() {
		return nextActivity;
	}

	public void setNextActivity(String nextActivity) {
		this.nextActivity = nextActivity;
	}

	public int getDefId() {
		return defId;
	}

	public void setDefId(int defId) {
		this.defId = defId;
	}
}
