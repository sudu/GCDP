package com.me.GCDP.template;

public class TemplateException extends Exception {

	private static final long serialVersionUID = -7173165433223681482L;

	public TemplateException() {
		super();
	}

	public TemplateException(String message) {
		super(message);
	}

	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateException(Throwable cause) {
		super(cause);
	}
}
