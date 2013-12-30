package com.me.GCDP.workflow.model;

/**
 * <pre>
 * ActivityDef代表一个流程中的一个流程节点,也叫活动，对应于配置文件中的<activity/>元素。
 * ActivityDef类的name属性对应<activity>标签的name属性；
 * ActivityDef类的title属性对应<activity>标签的title属性；
 * ActivityDef类的description属性对应<activity>标签的description属性；
 * ActivityDef类的plugin属性对应<activity>标签子标签<plugin>；
 * </pre>
 * 
 * @author xiongfeng
 *
 */
public class ActivityDef {
	
	private String name;
	private String title;
	private String description;
	private PluginDef plugin;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PluginDef getPlugin() {
		return plugin;
	}
	public void setPlugin(PluginDef plugin) {
		this.plugin = plugin;
	}
}
