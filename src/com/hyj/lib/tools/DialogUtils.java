package com.hyj.lib.tools;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 提示对话框工具类
 * 
 * @author Administrator
 * 
 */
public class DialogUtils {
	/**
	 * 初始化一个Toast
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            提示语
	 * @param duration
	 *            显示时间
	 */
	private static void initToast(Context context, String msg, int duration) {
		Toast toast = Toast.makeText(context, msg, duration);
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		toast.show();
	}

	/**
	 * 显示一个Toast，时间短
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            提示语
	 */
	public static void showToastShort(Context context, String msg) {
		initToast(context, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示一个Toast，时间长
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            提示语
	 */
	public static void showToastLong(Context context, String msg) {
		initToast(context, msg, Toast.LENGTH_LONG);
	}

	/**
	 * 显示一个Toast，并让EditText获取焦点且弹出软键盘
	 * 
	 * @param Activity
	 *            activity
	 * @param EditText
	 *            et
	 * @param String
	 *            msg提示信息
	 */
	public static void showToast(Activity activity, EditText et, String msg) {
		et.requestFocus();
		InputMethodManager im = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);

		showToastShort(activity, msg);
	}

	/**
	 * 弹出消息对话框
	 * 
	 * @param context
	 *            调用Activity
	 * @param title
	 *            对话框的标题
	 * @param msg
	 *            对话框信息
	 */
	public static Dialog showMessageDialog(Context context, String title,
			String msg, final DialogAction okAction) {
		Builder builder = new Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(msg);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (okAction != null) {
					okAction.action();
				}
				dialog.dismiss();
			}
		});

		Dialog dlg = builder.create();
		dlg.show();
		return dlg;
	}

	/**
	 * 弹出消息对话框,没有确认对话框
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 */
	public static Dialog showMessageDialog(Context context, String title,
			String msg) {
		return showMessageDialog(context, title, msg, null);
	}

	/**
	 * 弹出消息对话框，没有标题，没有确认对话框
	 * 
	 * @param context
	 * @param msg
	 */
	public static Dialog showMessageDialog(Context context, String msg) {
		return showMessageDialog(context, null, msg, null);
	}

	/**
	 * 显示异常对话框,有确认接口
	 * 
	 * @param context
	 * @param title
	 * @param e
	 */
	public static Dialog showExceptionDialog(Context context, String title,
			Throwable e, final DialogAction okAction) {
		String msg = e.getMessage();
		msg = TextUtils.isEmpty(msg) ? "数据异常" : msg;
		return showMessageDialog(context, title, msg, okAction);
	}

	/**
	 * 显示异常对话框，没有确认接口
	 * 
	 * @param context
	 * @param title
	 * @param e
	 */
	public static Dialog showExceptionDialog(Context context, String title,
			Throwable e) {
		return showExceptionDialog(context, title, e, null);
	}

	/**
	 * 显示异常对话框,无标题，有确认事件
	 * 
	 * @param context
	 * @param e
	 * @param okAction
	 */
	public static Dialog showExceptionDialog(Context context, Throwable e,
			final DialogAction okAction) {
		return showExceptionDialog(context, null, e, okAction);
	}

	/**
	 * 显示异常对话框,无标题，无确认事件
	 * 
	 * @param context
	 * @param e
	 */
	public static Dialog showExceptionDialog(Context context, Throwable e) {
		return showExceptionDialog(context, null, e, null);
	}

	/**
	 * 确认对话框, 包含确认跟取消处理接口
	 * 
	 * @param context
	 *            调用Activity
	 * @param title
	 *            标题
	 * @param msg
	 *            消息
	 * @param okAction
	 *            点确认按钮后的处理接口
	 * @param cancelAction
	 *            点取消按钮后的处理接口
	 * @return
	 */
	public static Dialog showConfirmDialog(Context context, String title,
			String msg, final DialogAction okAction,
			final DialogAction cancelAction) {
		Builder builder = new Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(msg);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (okAction != null) {
					okAction.action();
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (cancelAction != null) {
					cancelAction.action();
					dialog.dismiss();
				}
				dialog.dismiss();
			}
		});

		Dialog dlg = builder.create();
		dlg.setCanceledOnTouchOutside(false);
		dlg.show();
		return dlg;
	}

	/**
	 * 确认对话框, 只有确认处理接口
	 * 
	 * @param context
	 *            调用Activity
	 * @param title
	 *            标题
	 * @param msg
	 *            消息
	 * @param okAction
	 *            点确认按钮后的处理接口
	 * @return
	 */
	public static Dialog showConfirmDialog(Context context, String title,
			String msg, final DialogAction okAction) {
		return showConfirmDialog(context, title, msg, okAction, null);
	};

	/**
	 * 显示指定view的对话框，处理确认、取消事件
	 * 
	 * @param context
	 * @param title
	 * @param view
	 * @param okAction
	 * @param cancelAction
	 */
	public static Dialog showDialog(Context context, String title, View view,
			final DialogAction okAction, final DialogAction cancelAction) {
		Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setView(view);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (okAction != null) {
					okAction.action();
				}
				dialog.dismiss();
			}
		});

		if (null != cancelAction) {
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (cancelAction != null) {
								cancelAction.action();
							}
							dialog.dismiss();
						}
					});
		}

		Dialog dlg = builder.create();
		dlg.show();
		return dlg;
	}

	/**
	 * 显示指定view的对话框，处理确认事件
	 * 
	 * @param context
	 * @param title
	 * @param view
	 */
	public static Dialog showDialog(Context context, String title, View view,
			DialogAction okAction) {
		return showDialog(context, title, view, okAction, null);
	}

	/**
	 * 显示指定view的对话框，不处理确定事件
	 * 
	 * @param context
	 * @param title
	 * @param view
	 */
	public static Dialog showDialog(Context context, String title, View view) {
		return showDialog(context, title, view, null);
	}

	/**
	 * 设置对话框透明
	 * 
	 * @param dlg
	 */
	public static void setDialogAlpha(Dialog dlg) {
		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 1f;// 透明度
		lp.dimAmount = 0f;// 黑暗度
		window.setAttributes(lp);
	}

	/**
	 * 弹出对话框按钮操作事件
	 * 
	 * @author Xiaoxin
	 * 
	 */
	public interface DialogAction {
		/**
		 * Action所执行的操作
		 */
		public void action();
	}
}
