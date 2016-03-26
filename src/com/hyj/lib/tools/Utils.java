package com.hyj.lib.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

/**
 * 常用方法工具类
 * 
 * @Author hyj
 * @Date 2015-12-16 下午2:59:11
 */
public class Utils {
	/**
	 * 获取手机类型
	 * 
	 * @return String 手机型号：samsung GT-I9508
	 */
	public static String getMobileType() {
		return Build.BRAND + "　　" + Build.MODEL;
	}

	/**
	 * 获取Android系统版本号
	 * 
	 * @return int 17/19 Android SDK版本号
	 */
	public static int getOSVersionCode() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取系统版本号
	 * 
	 * @return String 4.4.4系统版本号
	 */
	public static String getOSVersionName() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取APP版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		String packageName = context.getPackageName();
		int versionCode = Integer.MAX_VALUE;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取APP版本名
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String packageName = context.getPackageName();
		String versionName = "1.0.1";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取当前App的名字
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		PackageManager pm = context.getPackageManager();
		return context.getApplicationInfo().loadLabel(pm).toString();
	}

	/**
	 * 显示软键盘
	 * 
	 * @param activity
	 *            上下文
	 * @param showKeyBoard
	 *            是否显示键盘
	 */
	public static void toggleKeyBoardStatus(Activity activity,
			boolean showKeyBoard) {
		InputMethodManager im = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (showKeyBoard) {// 显示键盘
			im.showSoftInputFromInputMethod(activity.getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			im.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					InputMethodManager.HIDE_IMPLICIT_ONLY);
		} else {// 隐藏键盘
			im.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 判断传入数据是否为空
	 * 
	 * @param Object
	 *            obj可接受数据类型有String/List
	 * @return boolean 为空：true;有值：false;
	 */
	public static boolean isEmpty(Object obj) {
		if (null == obj) {
			return true;
		}

		if (obj instanceof String) {
			String str = String.valueOf(obj);
			str = RegularUtils.replaceSpace(str);

			return TextUtils.isEmpty(str.trim());
		} else if (obj instanceof List<?>) {
			List<?> l = (List<?>) obj;
			return null == l || l.isEmpty();
		}

		return false;
	}

	/**
	 * 返回时间，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 根据指定格式返回时间
	 * 
	 * @param format时间格式
	 * @return String
	 * @throws
	 */
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	/**
	 * 生成一串唯一的数字，全球唯一，可以用于key
	 * 
	 * @return String
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
