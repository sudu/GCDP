package com.me.GCDP.workflow.exception;

/**
 * 使用XMLToObjectsUtil将XML文件中配置的流程定义信息转换成List<ProcessDef>对象时，
 * 需要将xml文件对应的File或者URL对象传给XMLToObjectsUtil对应的方法，如果传过来的
 * File或者URL对象为null，则抛出该异常
 * @author xiongfeng
 *
 */
@SuppressWarnings("serial")
public class ConfigurationFileNotFoundException extends Exception{
	public ConfigurationFileNotFoundException(String msg) {
		super(msg);
	}
}
