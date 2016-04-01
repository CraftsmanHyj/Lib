package com.hyj.lib.tools;

import com.hyj.lib.Constants;

import android.util.Log;

/**
 * log工具类，改变boolean值常量控制是否打印日志
 * 
 * @Author hyj
 * @Date 2015-12-16 下午2:53:10
 */
public class LogUtils {
	/**
	 * 是否打印日志
	 */
	private final static boolean printLog = Constants.PROP_ISDEBUG;

	/**
	 * 日志TAG标签
	 */
	private final static String TAG = Constants.PROP_LOGTAG;

	/**
	 * 提醒信息
	 * 
	 * @param msg
	 */
	public static void v(String msg) {
		if (printLog) {
			Log.v(TAG, msg);
		}
	}

	/**
	 * 调试信息
	 * 
	 * @param msg
	 */
	public static void d(String msg) {
		if (printLog) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * 普通消息
	 * 
	 * @param msg
	 */
	public static void i(String msg) {
		if (printLog) {
			Log.i(TAG, msg);
		}
	}

	/**
	 * 警告消息
	 * 
	 * @param msg
	 */
	public static void w(String msg) {
		if (printLog) {
			Log.w(TAG, msg);
		}
	}

	/**
	 * 错误消息
	 * 
	 * @param msg
	 */
	public static void e(String msg) {
		if (printLog) {
			Log.e(TAG, msg);
		}
	}
}
