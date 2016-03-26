package com.hyj.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类(单例模式)<br/>
 * 将helper设置成单例模式,防止多线程访问数据库的时候造成数据异常
 * 
 * @Author hyj
 * @Date 2015-12-16 下午3:03:02
 */
public class DBHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "lib.db";
	private static final int VERSION = 1;

	private final String SQL_CREATE_THREAD = "create table thread_info("
			+ "id integer primary key autoincrement,"
			+ "threadid integer,url string,start integer,"
			+ "end integer,progress integer)";
	private final String SQL_DROP_THREAD = "drop table if exists thread_info";

	private static DBHelper helper;

	private DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/**
	 * 获取DBHelper对象
	 * 
	 * @param context
	 * @return
	 */
	public static DBHelper getInstance(Context context) {
		if (null == helper) {
			synchronized (DBHelper.class) {
				if (null == helper) {
					helper = new DBHelper(context);
				}
			}
		}
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_THREAD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP_THREAD);
		db.execSQL(SQL_CREATE_THREAD);
	}
}
