package com.hyj.lib.adapter;

import java.io.Serializable;

public class News implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private String desc;// 描述
	private String time;
	private String phone;

	public News() {
	}

	public News(String title, String desc, String time, String phone) {
		this.title = title;
		this.desc = desc;
		this.time = time;
		this.phone = phone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
