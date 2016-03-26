package com.hyj.lib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

/**
 * <pre>
 * 监听接收到的短信并填入相应Edittext中
 * 调用方式：
 * 	SmsObserver observer = new SmsObserver(LoginActivity.this, etVerify);
 * 注册短信变化监听器：
 * 	this.getContentResolver().registerContentObserver(observer.getSmsUri(),true, observer);
 * 注销短信变化监听器：
 * 	this.getContentResolver().unregisterContentObserver(observer);
 * </pre>
 * 
 * @Author hyj
 * @Date 2016-1-20 下午5:14:54
 */
public class SmsObserver extends ContentObserver {
	private final String TELPHONE = "106905858926";// 所要监听的电话号码

	private Activity activity;
	private EditText etVerify;

	/**
	 * 不需要对消息进行处理,无需传入handler
	 * 
	 * @param activity
	 * @param etVerify
	 */
	public SmsObserver(Activity activity, EditText etVerify) {
		this(activity, new Handler(), etVerify);
	}

	/**
	 * 需要对拿到的短信做进一步处理，所以传入handler
	 * 
	 * @param activity
	 * @param handler
	 * @param etVerify
	 */
	public SmsObserver(Activity activity, Handler handler, EditText etVerify) {
		super(handler);
		this.activity = activity;
		this.etVerify = etVerify;
	}

	/**
	 * 用于筛选短信的Uri
	 * 
	 * @return
	 */
	public Uri getSmsUri() {
		return Uri.parse("content://sms/");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor;
		// 读取收件箱中指定号码的短信
		cursor = activity.managedQuery(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "body", "read" },
				"address=? and read=?", new String[] { TELPHONE, "0" },
				"date desc");

		if (null != cursor) {// 如果短信为未读模式
			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				String smsbody = cursor
						.getString(cursor.getColumnIndex("body"));
				String regEx = "[^0-9]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(smsbody);
				String smsContent = m.replaceAll("").trim();

				etVerify.setText(smsContent);
			}
		}
	}
}
