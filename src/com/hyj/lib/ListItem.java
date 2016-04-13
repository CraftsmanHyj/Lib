package com.hyj.lib;

import java.io.Serializable;

import android.os.Bundle;

public class ListItem implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 正常的activity
	 */
	public static final int TYPE_ACTIVITY = 0x001;
	/**
	 * 打开一个新的APP
	 */
	public static final int TYPE_APP = 0x002;

	private String title;
	private int type;// item类型
	private Object value;// Class类名或APP包名
	private Bundle bundle;// 传递过去的数据

	public String getTitle() {
		return title;
	}

	public void setTitle(String itemTitle) {
		this.title = itemTitle;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * 若没有设置itemType，则默认返回TYPE_ACTIVITY
	 * 
	 * @return
	 */
	public int getType() {
		if (type <= 0) {
			return TYPE_ACTIVITY;
		}

		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
