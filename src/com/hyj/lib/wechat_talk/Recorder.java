package com.hyj.lib.wechat_talk;

import java.io.Serializable;

/**
 * 音频类
 * 
 * @author Administrator
 * 
 */
public class Recorder implements Serializable {
	private static final long serialVersionUID = 1L;

	private float time;
	private String filePath;

	public Recorder() {
	}

	public Recorder(float time, String filePath) {
		this.time = time;
		this.filePath = filePath;
	}

	public float getTime() {
		return time;
	}

	public int getTimeForInteger() {
		return Math.round(time);
	}

	public void setTime(float time) {
		this.time = time;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
