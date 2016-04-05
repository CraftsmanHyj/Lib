package com.hyj.lib.popup.pulldown;

import java.io.Serializable;

/**
 * 介质类型
 * 
 * @author Administrator
 * 
 */
public class Medium implements Serializable {

	private static final long serialVersionUID = 1L;
	public int img;// 显示的图片
	public String name;// 显示的文字
	public Class<?> cls;// 点击执行的操作

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
}
