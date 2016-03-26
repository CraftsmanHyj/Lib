package com.hyj.lib.tuling.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String msg;
	private Type type;
	private Date date;

	public enum Type {
		INCOMING, OUTCOMING
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public String getDateStr() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		return date == null ? "" : sf.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	};
}
