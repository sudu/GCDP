package com.me.GCDP.xform;

public class ControlDbItem {
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fielddName) {
		this.fieldName = fielddName.trim();
	}
	private String fieldType;
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType.toLowerCase();
	}
	private int fieldLength;
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

}
