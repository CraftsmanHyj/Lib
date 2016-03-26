package com.hyj.lib.viewpager;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class DepthPageTransformer implements ViewPager.PageTransformer {
	private static final float MIN_SCALE = 0.75f;

	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();

		Log.i("aaa", position + "");

		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			ViewHelper.setAlpha(view, 0);

		} else if (position <= 0) { // A页 [-1,0]
			// Use the default slide transition when moving to the left page
			ViewHelper.setAlpha(view, 1);
			ViewHelper.setTranslationX(view, 0);
			ViewHelper.setScaleX(view, 1);
			ViewHelper.setScaleY(view, 1);

		} else if (position <= 1) { // B页 (0,1]
			// Fade the page out.
			ViewHelper.setAlpha(view, 1 - position);

			// Counteract the default slide transition
			ViewHelper.setTranslationX(view, pageWidth * -position);

			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - Math.abs(position));
			ViewHelper.setScaleX(view, scaleFactor);
			ViewHelper.setScaleY(view, scaleFactor);

		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			ViewHelper.setAlpha(view, 0);
		}
	}
}