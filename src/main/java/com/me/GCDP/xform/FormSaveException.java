package com.me.GCDP.xform;

public class FormSaveException extends Exception {
	private static final long serialVersionUID = 5656368755358706612L;
	private String errorStep = null;
	private int nodeId;
	private int formId;
	private int dataId;

	public FormSaveException(String errorStep, int nodeId, int formId, int dataId) {
		super();
		this.errorStep = errorStep;
		this.nodeId = nodeId;
		this.formId = formId;
		this.dataId = dataId;
	}

	public FormSaveException(String message, String errorStep, int nodeId, int formId, int dataId) {
		super(message);
		this.errorStep = errorStep;
		this.nodeId = nodeId;
		this.formId = formId;
		this.dataId = dataId;
	}

	public FormSaveException(String message, Throwable cause, String errorStep, int nodeId, int formId, int dataId) {
		super(message, cause);
		this.errorStep = errorStep;
		this.nodeId = nodeId;
		this.formId = formId;
		this.dataId = dataId;
	}

	public FormSaveException(Throwable cause, String errorStep, int nodeId, int formId, int dataId) {
		super(cause);
		this.errorStep = errorStep;
		this.nodeId = nodeId;
		this.formId = formId;
		this.dataId = dataId;
	}

	@Override
	public String toString() {
		return "表单保存异常: nodeId:" + nodeId + "|formId:" + formId + "|dataId:"
				+ dataId + "|errorStep:" + errorStep + "|msg:" + getMessage();
	}

	public String getErrorStep() {
		return errorStep;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getFormId() {
		return formId;
	}

	public int getDataId() {
		return dataId;
	}

}
