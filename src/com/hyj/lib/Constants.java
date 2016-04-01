package com.hyj.lib;

import java.io.Serializable;

/**
 * 系统常量类
 * 
 * @author async
 * 
 */
public class Constants implements Serializable {
	private static final long serialVersionUID = 1L;

	/******************** 会从config.properties中读取数据覆盖这里的值 ********************/
	/**
	 * 字段：程序是否处于调试状态
	 */
	public static boolean PROP_ISDEBUG = false;

	/**
	 * 字段：日志打印时输出的tag
	 */
	public static String PROP_LOGTAG = "TAG";

	/******************** 文件夹 ********************/
	/**
	 * 录音存放文件目录
	 */
	public static final String DIR_RECORDER = "Talk";
	/**
	 * 图片缓存文件目录
	 */
	public static final String DIR_IMAGECACHE = "ImageCache";
	/**
	 * 文件下载目录
	 */
	public static final String DIR_DOWNLOAD = "Download";
	/**
	 * 临时数据文件目录
	 */
	public static final String DIR_TEMP = "Temp";
	/**
	 * 日志文件，存放app运行、崩溃等日志
	 */
	public static final String DIR_LOG = "Log";

	/******************** 文件名 ********************/
	/**
	 * SharedPreference文件名
	 */
	public static final String FILE_NAME_SHARED = "lib_shared";
}
