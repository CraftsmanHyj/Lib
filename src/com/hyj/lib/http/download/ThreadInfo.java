package com.hyj.lib.http.download;

import java.io.Serializable;

/**
 * <pre>
 * 下载线程信息
 * 由id、url组成key
 * </pre>
 * 
 * @author async
 * 
 */
public class ThreadInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;// 数据库key
	private int threadId;
	private String url;
	private int start;
	private int end;
	private int progress;// 下载进度

	public ThreadInfo() {
		this(0, "", 0, 0);
	}

	public ThreadInfo(int threadId, String url, int start, int end) {
		this(threadId, url, start, end, 0);
	}

	public ThreadInfo(int threadId, String url, int start, int end, int progress) {
		this.threadId = threadId;
		this.url = url;
		this.start = start;
		this.end = end;
		this.progress = progress;
	}

	/**
	 * 数据库分配的Key
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 当前文件线程的ID
	 * 
	 * @return
	 */
	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + threadId + ", url=" + url + ", start="
				+ start + ", end=" + end + ", progress=" + progress + "]";
	}
}
