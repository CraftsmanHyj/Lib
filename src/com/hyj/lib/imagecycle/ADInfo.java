package com.hyj.lib.imagecycle;

import java.io.Serializable;

/**
 * 描述：广告信息</br>
 */
public class ADInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String url;
	private String content;
	private String type;

	public ADInfo() {
	}

	public ADInfo(String url, String content) {
		this.url = url;
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
