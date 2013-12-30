package com.me.GCDP.workflow.model;

import java.util.List;


/**
 * <pre>
 * ProcessDef代表一个流程定义，对应于配置文件中的
 * <config name="" type=""></config>元素
 * </pre>
 * 
 * @author xiongfeng
 *
 */
public class ProcessDef {
	private String name;
	private String title;
	private String description;
	
	
	/**
	 * 用来保存一个流程定义(ProcessDef)中的多个流程节点(ActivityDef)。其中，
	 * Map的key对应ActivityDef的name属性，value对应ActivityDef对象本身
	 */
	//private Map<String, ActivityDef> activitiesMap;
	
	private List<ActivityDef> activitiesList;

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

	public List<ActivityDef> getActivitiesList() {
		return activitiesList;
	}

	public void setActivitiesList(List<ActivityDef> activitiesList) {
		this.activitiesList = activitiesList;
	}

//	public Map<String, ActivityDef> getActivitiesMap() {
//		return activitiesMap;
//	}
//
//	public void setActivitiesMap(Map<String, ActivityDef> activitiesMap) {
//		this.activitiesMap = activitiesMap;
//	}	
}
