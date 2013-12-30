package com.me.GCDP.workflow.exception;

/**
 * 当读取到的<plugin>标签的type属性未设置值时，该异常会被抛出
 * @author xiongfeng
 *
 */
@SuppressWarnings("serial")
public class InvalidConfigurationException extends Exception {
	public InvalidConfigurationException(String str) {
		super(str);
	}
}
