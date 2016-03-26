package com.hyj.lib.viewpager;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class RotatePageTransformer implements PageTransformer {
	private final float ROTATE = 20;// 旋转角度

	@Override
	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		int pageHeight = view.getHeight();

		if (position < -1) {
			view.setAlpha(0);
		} else if (position <= 0) {// A页 0~-1
			float rotate = position * ROTATE;
			view.setPivotX(pageWidth / 2);
			view.setPivotY(pageHeight);
			view.setRotation(rotate);
		} else if (position <= 1) {// B页 1~0
			float rotate = position * ROTATE;
			view.setPivotX(pageWidth / 2);
			view.setPivotY(pageHeight);
			view.setRotation(rotate);
		} else {
			view.setAlpha(0);
		}
	}
}
