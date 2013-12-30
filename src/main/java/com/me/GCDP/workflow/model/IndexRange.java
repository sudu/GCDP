package com.me.GCDP.workflow.model;

/**
 * IndexRange类表示一个范围，通过属性lowIndex和highIndex来划分。
 * count表示该范围内"对象"数量
 * @author xiongfeng
 *
 */
public class IndexRange {
	private int lowIndex = 0;
	private int highIndex = 0;
	private int count = 0;
	public int getLowIndex() {
		return lowIndex;
	}
	public void setLowIndex(int lowIndex) {
		this.lowIndex = lowIndex;
	}
	public int getHighIndex() {
		return highIndex;
	}
	public void setHighIndex(int highIndex) {
		this.highIndex = highIndex;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
