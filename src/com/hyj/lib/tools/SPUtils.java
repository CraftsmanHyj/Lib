package com.hyj.lib.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences文件操作工具类
 * 
 * @Author hyj
 * @Date 2015-12-15 下午3:12:49
 */
public class SPUtils {
	/**
	 * 存放SharedPreferences对象集合
	 */
	private static Map<String, SharedPreferences> mapSP = new HashMap<String, SharedPreferences>();

	/**
	 * 获取一个SharedPrefernces对象
	 * 
	 * @param context
	 *            上下文
	 * @param spName
	 *            sp文件名
	 * @return
	 */
	private static SharedPreferences getSharedPre(Context context, String spName) {
		SharedPreferences sp = mapSP.get(spName);
		if (null == sp) {
			synchronized (SPUtils.class) {
				if (null == sp) {
					sp = context.getSharedPreferences(spName,
							Context.MODE_PRIVATE);
					mapSP.put(spName, sp);
				}
			}
		}
		return sp;
	}

	/**
	 * 保存数据到sharedPreference里面
	 * 
	 * @param context
	 *            上下文
	 * @param spName
	 *            sp文件名
	 * @param key
	 *            关键字
	 * @param value
	 *            对应的值
	 */
	public static void putParam(Context context, String spName, String key,
			Object value) {
		Map<String, Object> mValues = new HashMap<String, Object>();
		mValues.put(key, value);
		putParam(context, spName, mValues);
	}

	/**
	 * 保存数据到sharedPreference里面
	 * 
	 * @param context
	 *            上下文
	 * @param spName
	 *            sp文件名
	 * @param mValues
	 *            Map<String, Object>的格式，要保存的数据
	 */
	@SuppressWarnings("unchecked")
	public static void putParam(Context context, String spName,
			Map<String, Object> mValues) {
		SharedPreferences sp = getSharedPre(context, spName);
		SharedPreferences.Editor editor = sp.edit();

		String key, type;
		Object value;

		for (Map.Entry<String, Object> entry : mValues.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			type = value.getClass().getName();

			if (String.class.equals(type)) {
				editor.putString(key, (String) value);
			} else if (Integer.class.equals(type)) {
				editor.putInt(key, (Integer) value);
			} else if (Boolean.class.equals(type)) {
				editor.putBoolean(key, (Boolean) value);
			} else if (Float.class.equals(type)) {
				editor.putFloat(key, (Float) value);
			} else if (Long.class.equals(type)) {
				editor.putLong(key, (Long) value);
			} else if (Set.class.equals(type)) {
				editor.putStringSet(key, (Set<String>) value);
			}
		}

		editor.commit();
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * 
	 * @param context
	 *            上下文
	 * @param spName
	 *            sp文件名
	 * @param key
	 *            关键字
	 * @param defValue
	 *            默认值
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static Object getParam(Context context, String spName, String key,
			Object defValue) {
		SharedPreferences sp = getSharedPre(context, spName);
		String type = defValue.getClass().getName();

		if (String.class.equals(type)) {
			return sp.getString(key, (String) defValue);
		} else if (Integer.class.equals(type)) {
			return sp.getInt(key, (Integer) defValue);
		} else if (Boolean.class.equals(type)) {
			return sp.getBoolean(key, (Boolean) defValue);
		} else if (Float.class.equals(type)) {
			return sp.getFloat(key, (Float) defValue);
		} else if (Long.class.equals(type)) {
			return sp.getLong(key, (Long) defValue);
		} else if (Set.class.equals(type)) {
			return sp.getStringSet(key, (Set<String>) defValue);
		}

		return null;
	}
}
