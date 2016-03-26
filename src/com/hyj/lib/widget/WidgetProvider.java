package com.hyj.lib.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * 类似于广播，用于向widget提供内容
 * 
 * @Author hyj
 * @Date 2016-2-5 上午9:43:38
 */
public class WidgetProvider extends AppWidgetProvider {

	/**
	 * widget小组件从屏幕被移除的时候调用
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	/**
	 * 最后一个widget从屏幕被移除时执行
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);

		// 当从屏幕移除的时候停止Service
		context.stopService(new Intent(context, TimerService.class));
	}

	/**
	 * 第一个widget添加到屏幕上执行
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		// 当添加到屏幕的时候启动Service
		context.startService(new Intent(context, TimerService.class));
	}

	/**
	 * 一般不会被重写，无论操作哪个（onDisabled、onEnabled等）方法都会调用此方法
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	/**
	 * 刷新widget的时候执行
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		// 通过remoteView和AppWidgetManager执行更新操作
	}
}
