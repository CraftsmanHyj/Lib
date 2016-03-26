package com.hyj.lib.http.download;

import java.io.Serializable;






/**
 * 保存下载文件信息
 * 
 * @author async
 * 
 */
public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;// 文件ID
	private String url;// 文件下载地址
	private String fileName;// 文件名字
	private int length;// 文件总长度
	private int progress;// 下载进度
	private String downStatus;// 文件下载状态

	public FileInfo() {
		this(0, "", "");
	}

	/**
	 * 直接传URL，FileName从URL中最后一个'/'到结尾截取
	 * 
	 * @param id
	 *            文件ID
	 * @param url
	 *            下载地址
	 */
	public FileInfo(int id, String url) {
		this(id, url, url.substring(url.lastIndexOf("/") + 1));
	}

	/**
	 * @param id
	 *            文件ID
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            文件的保存名字(QQ.apk)
	 */
	public FileInfo(int id, String url, String fileName) {
		this(id, url, fileName, 0, 0);
	}

	/**
	 * @param id
	 *            文件ID
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            文件的保存名字(QQ.apk)
	 * @param length
	 *            文件总长度
	 * @param progress
	 *            文件当前下载进度
	 */
	public FileInfo(int id, String url, String fileName, int length,
			int progress) {
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.progress = progress;
	}

	/**
	 * 获取文件ID
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 设置文件ID
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 获取文件下载地址
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置文件下载地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 设置文件下载地址
	 * 
	 * @param url
	 * @param boolean setFileName是否从URL中截取文件名
	 */
	public void setUrl(String url, boolean setFileName) {
		this.url = url;

		if (setFileName) {
			setFileName(url.substring(url.lastIndexOf("/") + 1));
		}
	}

	/**
	 * 获取文件保存名字
	 * 
	 * @return String 文件保存名，例：QQ.apk
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件保存名字
	 * 
	 * @param fileName
	 *            文件的保存名字(QQ.apk)
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件总长度
	 * 
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置文件长度大小
	 * 
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 获取文件当前下载进度
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * 设置文件当前下载进度
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * 获取文件下载状态
	 * 
	 * @return
	 */
	public String getDownStatus() {
		return downStatus;
	}

	/**
	 * 设置文件下载状态
	 * 
	 * @param downStatus
	 */
	public void setDownStatus(String downStatus) {
		this.downStatus = downStatus;
	}

	/**
	 * 文件是否正在下载中
	 * 
	 * @return
	 */
	public boolean isDowning() {
		if (DownService.ACTION_START.equals(downStatus)) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return fileName;
	}
}
