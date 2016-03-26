package com.hyj.lib.viewpager;

import java.util.HashMap;
import java.util.Map;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义实现ViewPager，以实现ViewPager切换动画时向下兼容
 * 
 * @author async
 * 
 */
// 实现步骤：1、拿到当前切换的两个view；2、动画的一个梯度值；
public class ViewPagerTransformer extends ViewPager {

	private View vLeft, vRight;

	private float mTrans;// 位移
	private float mScale;// 缩放比例

	private final float MIN_SCALE = 0.4f;// 缩放初始值

	// 存放viewPager中的所有显示view;
	private Map<Integer, View> mapView = new HashMap<Integer, View>();

	/**
	 * 添加view
	 * 
	 * @param position
	 * @param view
	 */
	public void addView(int position, View view) {
		mapView.put(position, view);
	}

	/**
	 * 移除view
	 * 
	 * @param position
	 */
	public void removeView(int position) {
		mapView.remove(position);
	}

	public ViewPagerTransformer(Context context) {
		super(context);
	}

	public ViewPagerTransformer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPageScrolled(int positoin, float offset, int offsetPixels) {
		super.onPageScrolled(positoin, offset, offsetPixels);

		vLeft = mapView.get(positoin);
		vRight = mapView.get(positoin + 1);

		animStack(vLeft, vRight, offset, offsetPixels);
	}

	/**
	 * 实现切换动画效果
	 * 
	 * @param vLeft
	 * @param vRight
	 * @param offset
	 * @param offsetPixels
	 */
	private void animStack(View vLeft, View vRight, float offset,
			int offsetPixels) {
		// 右边view动画设置
		if (null != vRight) {
			// 计算缩放比例
			mScale = (1 - MIN_SCALE) * offset + MIN_SCALE;
			ViewHelper.setScaleX(vRight, mScale);
			ViewHelper.setScaleY(vRight, mScale);

			// 计算位移
			mTrans = -getWidth() - getPageMargin() + offsetPixels;
			ViewHelper.setTranslationX(vRight, mTrans);
		}

		// 设置左边动画
		if (null != vLeft) {
			vLeft.bringToFront();// 设置vleft始终在最前面
		}
	}
}
