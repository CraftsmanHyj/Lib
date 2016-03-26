package com.hyj.lib.db;

import java.util.List;

import com.hyj.lib.http.download.ThreadInfo;

/**
 * 数据访问接口
 * 
 * @author async
 * 
 */
public interface ThreadDao {

	/**
	 * 插入线程信息
	 * 
	 * @param thread
	 */
	public void insertThread(ThreadInfo thread);

	/**
	 * 删除线程
	 * 
	 * @param thread
	 */
	public void deleteThread(ThreadInfo thread);

	/**
	 * 删除线程
	 * 
	 * @param url
	 *            下载地址
	 */
	public void deleteThread(String url);

	/**
	 * 更新线程下载进度
	 * 
	 * @param thread
	 */
	public void updateThread(ThreadInfo thread);

	/**
	 * 查找所有线程信息
	 * 
	 * @param url
	 * @return
	 */
	public List<ThreadInfo> queryThread(String url);

	/**
	 * 判断线程是否已经存在
	 * 
	 * @param url
	 * @param threadId
	 * @return
	 */
	public boolean isExists(String url, int threadId);
}
