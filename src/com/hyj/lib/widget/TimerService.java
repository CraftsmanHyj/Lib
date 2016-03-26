package com.hyj.lib.widget;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.hyj.lib.R;

/**
 * 当widget被添加到屏幕的时候启动，然后在里面执行更新时间的操作
 * 
 * @Author hyj
 * @Date 2016-2-5 上午9:50:01
 */
@SuppressLint("SimpleDateFormat")
public class TimerService extends Service {
	private Timer timer;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// 定时任务
	private TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			updateView();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		String time = sdf.format(System.currentTimeMillis());

		// 将时间传递给RemoteViews用于数据显示
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_main);
		rv.setTextViewText(R.id.widgetTvTime, time);

		// 通知provider去执行更新操作
		AppWidgetManager manager = AppWidgetManager
				.getInstance(getApplicationContext());

		ComponentName cn = new ComponentName(getApplicationContext(),
				WidgetProvider.class);
		manager.updateAppWidget(cn, rv);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 停止倒计时
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}
}
