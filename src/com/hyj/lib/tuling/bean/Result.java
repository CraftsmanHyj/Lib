package com.hyj.lib.tuling.bean;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String text;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}