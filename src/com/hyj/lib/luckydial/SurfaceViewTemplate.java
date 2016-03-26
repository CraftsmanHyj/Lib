package com.hyj.lib.luckydial;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * SurfaceView模板
 * 
 * @Author hyj
 * @Date 2015-12-23 下午9:04:06
 */
public class SurfaceViewTemplate extends SurfaceView implements Callback,
		Runnable {

	private SurfaceHolder holder;
	private Canvas canvas;

	/**
	 * 用于绘制的线程
	 */
	private Thread thread;

	/**
	 * 线程控制开关
	 */
	private boolean isRunning;

	public SurfaceViewTemplate(Context context) {
		this(context, null);
	}

	public SurfaceViewTemplate(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit();
	}

	private void myInit() {
		holder = getHolder();
		holder.addCallback(this);

		setFocusable(true);// 可获得焦点
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);// 设置常量
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		// 不断进行绘制
		while (isRunning) {
			draw();
		}
	}

	private void draw() {
		canvas = holder.lockCanvas();
		if (null == canvas) {
			return;
		}

		try {

		} finally {
			if (null != canvas) {
				// 释放canvas
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
