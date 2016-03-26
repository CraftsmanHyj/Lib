package com.hyj.lib.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyj.lib.http.download.ThreadInfo;

public class ThreadDaoImpl implements ThreadDao {

	private DBHelper dbHelper;

	public ThreadDaoImpl(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	@Override
	public synchronized void insertThread(ThreadInfo thread) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "insert into thread_info(threadid,url,start,end,progress) values(?,?,?,?,?)";
		Object[] args = new Object[] { thread.getThreadId(), thread.getUrl(),
				thread.getStart(), thread.getEnd(), thread.getProgress() };
		db.execSQL(sql, args);
		db.close();
	}

	@Override
	public synchronized void deleteThread(ThreadInfo thread) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from thread_info where threadid=? and url=?";
		Object[] args = new Object[] { thread.getThreadId(), thread.getUrl() };
		db.execSQL(sql, args);
		db.close();
	}

	@Override
	public synchronized void deleteThread(String url) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "delete from thread_info where url=?";
		Object[] args = new Object[] { url };
		db.execSQL(sql, args);
		db.close();
	}

	@Override
	public synchronized void updateThread(ThreadInfo thread) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update thread_info set start=?,end=?,progress=? where threadid=? and url=?";
		Object[] args = new Object[] { thread.getStart(), thread.getEnd(),
				thread.getProgress(), thread.getThreadId(), thread.getUrl() };
		db.execSQL(sql, args);
		db.close();
	}

	@Override
	public List<ThreadInfo> queryThread(String url) {
		List<ThreadInfo> lThread = new ArrayList<ThreadInfo>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select threadid,url,start,end,progress from thread_info where url=?";
		String[] args = new String[] { url };
		Cursor cursor = db.rawQuery(sql, args);

		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setThreadId(cursor.getInt(cursor.getColumnIndex("threadid")));
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
			lThread.add(thread);
		}
		cursor.close();
		db.close();
		return lThread;
	}

	@Override
	public boolean isExists(String url, int threadId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select threadid,url,start,end,progress from thread_info where url=? and threadid=?";
		String[] args = new String[] { url, String.valueOf(threadId) };
		Cursor cursor = db.rawQuery(sql, args);
		boolean flag = cursor.moveToNext();
		cursor.close();
		db.close();
		return flag;
	}
}
