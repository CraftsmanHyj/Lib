package com.hyj.lib.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.hyj.lib.tools.DialogUtils;

/**
 * 显示进度条加载类
 * 
 * @Author xiaoxin
 * @Date 2015-12-17 下午5:45:23
 * @param <T>
 */
abstract public class ProgressExecutor<T> {
	private static final String TAG = "TAG";

	public static final int MESSAGE_WHAT_OK = 0; // 处理完成
	public static final int MESSAGE_WHAT_PROCESS = 1; // 处理中
	public static final int MESSAGE_WHAT_EXCEPTION = 9; // 处理异常

	private Context context;
	private Dialog progressDialog;
	private boolean canCancelDialog; // 是否能取消进度条

	/**
	 * 消息处理
	 */
	public static final Handler handler = new ProgressHandler();

	/**
	 * 是否异步执行
	 * 
	 * @return
	 */
	public boolean isAsync() {
		return true;
	}

	/**
	 * 关联的Activity
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * 是否显示进度条
	 * 
	 * @return
	 */
	public boolean isShowProgressDialog() {
		return true;
	}

	/**
	 * 设置是否能取消进度条
	 * 
	 * @param canCancelDialog
	 */
	public void setCanCancelDialog(boolean canCancelDialog) {
		this.canCancelDialog = canCancelDialog;
	}

	public ProgressExecutor(Context context) {
		this.context = context;
		canCancelDialog = true;
	}

	/**
	 * 长时间操作实现方法
	 * 
	 * @throws Exception
	 */
	abstract public T execute() throws Exception;

	/**
	 * 返回结果的正常处理
	 * 
	 * @param t
	 */
	abstract public void doResult(T t);

	/**
	 * 返回结果的异常处理
	 * 
	 * @param ex
	 */
	public void doException(Exception ex) {
		String msg = ex.getMessage();
		if (TextUtils.isEmpty(msg)) {
			msg = ex.getClass().getName();
		}

		if (context != null) {
			Log.d(TAG, msg);
			DialogUtils.showMessageDialog(context, msg);
		}
	}

	/**
	 * 处理处理消息
	 * 
	 * @param name
	 * @param obj
	 */
	protected void doProcessMessage(String name, Object obj) {
	}

	/**
	 * 发送处理消息
	 * 
	 * @param name
	 * @param obj
	 */
	protected void sendProcessMessage(String name, Object obj) {
		Message msg = new Message();
		msg.setTarget(handler);
		msg.what = MESSAGE_WHAT_PROCESS;
		msg.obj = new Object[] { this, name, obj };
		msg.sendToTarget();
	}

	/**
	 * 进度对话框
	 * 
	 * @return
	 */
	public Dialog getProgressDialog() {
		if (context != null && progressDialog == null) {
			progressDialog = new DefaultProgressDialog(context);
		}
		return progressDialog;
	}

	/**
	 * 可以取消进度条执行
	 */
	public void start() {
		start(true);
	}

	/**
	 * 执行加载进度
	 * 
	 * @param canCancelDialog
	 *            是否可以取消进度条执行
	 */
	public void start(boolean canCancelDialog) {
		setCanCancelDialog(canCancelDialog);
		// 显示进度条
		showDialog();
		if (isAsync()) { // 异步执行
			Thread async = new Thread() {
				public void run() {
					Looper.prepare();
					syncExecute();
					Looper.loop();
				}
			};
			async.start();
		} else {
			syncExecute();
		}
	}

	private void syncExecute() {
		Message msg = new Message();
		msg.setTarget(handler);
		// 执行长时间操作
		Object result = null;
		try {
			result = execute();
			msg.what = MESSAGE_WHAT_OK;
		} catch (Throwable e) {
			result = e;
			msg.what = MESSAGE_WHAT_EXCEPTION;
		}
		msg.obj = new Object[] { this, result };

		// 处理返回结果
		msg.sendToTarget();
	}

	/**
	 * 显示执行进度条
	 */
	private void showDialog() {
		if (isShowProgressDialog()) {
			progressDialog = getProgressDialog();
			if (progressDialog != null) {
				progressDialog.show();
			}
		}
	}

	/**
	 * 关闭进度条
	 */
	private void closeDialog() {
		if (isShowProgressDialog()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * 处理类
	 * 
	 * @author xzh
	 * 
	 */
	private static class ProgressHandler extends Handler {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void handleMessage(Message msg) {
			Object[] objs = (Object[]) msg.obj;
			ProgressExecutor exe = (ProgressExecutor) objs[0];
			Object result = objs[1];

			switch (msg.what) {
			case MESSAGE_WHAT_OK:
				exe.doResult(result);
				break;

			case MESSAGE_WHAT_EXCEPTION:
				exe.doException((Exception) result);
				break;

			case MESSAGE_WHAT_PROCESS:
				String name = (String) objs[1];
				Object obj = objs[2];
				exe.doProcessMessage(name, obj);
				break;
			}

			exe.closeDialog();
		}
	}

	/**
	 * 自定义的ProgressDialog
	 * 
	 * @author xzh
	 * 
	 */
	private class DefaultProgressDialog extends AlertDialog {
		private int layoutId;

		public DefaultProgressDialog(Context context) {
			super(context);
		}

		public DefaultProgressDialog(Context context, int themeId, int layoutId) {
			super(context, themeId);
			this.layoutId = layoutId;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if (0 != layoutId) {
				setContentView(layoutId);
			}

			// 允许取消
			setCanceledOnTouchOutside(canCancelDialog);
			setCancelable(canCancelDialog);

			ProgressBar bar = new ProgressBar(getContext());
			setContentView(bar);
			ViewParent vp = bar.getParent();
			while (null != vp) {
				ViewParent vp0 = vp.getParent();
				if (vp0 == null) {
					if (vp instanceof View) {
						((View) vp).setBackgroundColor(Color.LTGRAY);
					}
					break;
				} else {
					vp = vp0;
				}
			}

			/** 设置透明度 */
			Window window = getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 1f;// 透明度
			lp.dimAmount = 0f;// 黑暗度
			window.setAttributes(lp);
			window.getDecorView().setBackgroundResource(Color.TRANSPARENT);
		}
	}
}
