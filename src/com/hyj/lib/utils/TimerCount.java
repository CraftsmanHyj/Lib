package com.hyj.lib.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 倒计时器
 * 
 * @Author hyj
 * @Date 2015-12-16 上午10:46:58
 */
public class TimerCount {

	@SuppressWarnings("unused")
	private Context context;
	private Button mButton;
	private long millisInFuture;// 总时长
	private long countDownInterval;// 时间间隔

	private CountDownTimer timer;

	/**
	 * 默认时长60s,计时步长1s
	 * 
	 * @param context
	 * @param mButton
	 */
	public TimerCount(Context context, Button mButton) {
		this(context, mButton, 60, 1);
	}

	/**
	 * 倒计时器
	 * 
	 * @param context
	 * @param mButton触发计时按钮
	 * @param millisInFuture计时总时长
	 *            (秒为单位)
	 * @param countDownInterval步长
	 *            (时间间隔) (秒为单位)
	 */
	public TimerCount(Context context, Button mButton, long millisInFuture,
			long countDownInterval) {
		this.context = context;
		this.mButton = mButton;
		this.millisInFuture = millisInFuture * 1000;
		this.countDownInterval = countDownInterval * 1000;

		myInit();
	}

	private void myInit() {
		timer = new CountDownTimer(millisInFuture, countDownInterval) {

			@Override
			public void onTick(long millisUntilFinished) {
				mButton.setEnabled(false);
				mButton.setText("重新获取" + "(" + millisUntilFinished / 1000 + ")");
			}

			@Override
			public void onFinish() {
				mButton.setText("重新获取");
				mButton.setEnabled(true);
			}
		};
	}

	/**
	 * 开启计时器
	 */
	public void startTimerCount() {
		timer.start();
		mButton.setEnabled(false);
	}

	/**
	 * 关闭短信码计时器
	 */
	public void shutdownTimeCount() {
		if (timer != null) {
			timer.cancel();
			timer.onFinish();
		}
	}
}
