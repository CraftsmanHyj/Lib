package com.hyj.lib.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 单位转化工具类
 * 
 * @Author hyj
 * @Date 2015-12-16 下午3:02:43
 */
public class DensityUtils {
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dp转换成px<br/>
	 * 把指定单位的值转化为像素
	 * 
	 * @param res
	 * @param dp
	 * @return
	 */
	public static int dpToPx(Context context, int dp) {
		// 相关资料：http://www.cnblogs.com/xilinch/p/4444833.html
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 讲sp转换成px
	 * 
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int spToPx(Context context, int sp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 获取屏幕宽、高，返回数组int[] a，width=a[0];height=a[1];
	 * 
	 * @param activity
	 * @return
	 */
	private static int[] getScreenSize(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();// 获取分辨率
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); // 当前屏幕像素

		int[] size = new int[2];
		size[0] = dm.widthPixels;
		size[1] = dm.heightPixels;
		return size;
	}

	/**
	 * 获取屏幕的宽
	 * 
	 * @param activity
	 * @return 屏幕宽(像素)
	 */
	public static int getScreenWidth(Activity activity) {
		return getScreenSize(activity)[0];
	}

	/**
	 * 获取屏幕高
	 * 
	 * @param activity
	 * @return 屏幕高(像素)
	 */
	public static int getScreenHeight(Activity activity) {
		return getScreenSize(activity)[1];
	}

}
