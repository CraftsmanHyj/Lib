package com.hyj.lib.tools;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

/**
 * 服务是否运行检测帮助类
 * 
 * @Author hyj
 * @Date 2015-12-16 下午2:53:56
 */
public class ServiceUtils {

	/**
	 * 判断服务是否后台运行
	 * 
	 * @param context
	 *            Context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = activityManager
				.getRunningServices(40);

		for (RunningServiceInfo serviceInfo : serviceList) {
			LogUtils.w(serviceInfo.service.getClassName());
			if (className.equals(serviceInfo.service.getClassName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 停止正在运行的Service
	 * 
	 * @param context
	 *            上下文
	 * @param Class
	 *            clzs 任意长度数组
	 */
	public static void stopService(Context context, Class<?>... clzs) {
		for (Class<?> clz : clzs) {
			if (isServiceRunning(context, clz.getName())) {
				Intent intent = new Intent(context, clz);
				context.stopService(intent);
			}
		}
	}

	/**
	 * <pre>
	 * 判断应用是否处于后台
	 * BACKGROUND=400;EMPTY=500;
	 * FOREGROUND=100;GONE=1000;
	 * PERCEPTIBLE=130;SERVICE=300;ISIBLE=200
	 * </pre>
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> lTasks = am.getRunningAppProcesses();
		for (RunningAppProcessInfo task : lTasks) {
			if (task.processName.equals(context.getPackageName())) {
				if (task.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND
						|| task.importance == RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE
						|| task.importance == RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
