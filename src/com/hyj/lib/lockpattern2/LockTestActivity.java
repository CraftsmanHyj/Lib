package com.hyj.lib.lockpattern2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

/**
 * author : stone email : aa86799@163.com time : 15/12/25 22 53
 */
public class LockTestActivity extends Activity {

	private NineGridLockView mNineGridLockView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);
		setContentView(layout);

		mNineGridLockView = new NineGridLockView(this);
		int w = getResources().getDisplayMetrics().widthPixels;
		int h = getResources().getDisplayMetrics().heightPixels;
		w = Math.min(w, h);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, w);
		int remain = Math.max(w, h) - Math.min(w, h);
		lp.topMargin = remain / 2;
		layout.addView(mNineGridLockView, lp);

		NineGridLockView mini = new NineGridLockView(this);
		RelativeLayout.LayoutParams lpMini = new RelativeLayout.LayoutParams(
				remain / 2, remain / 2);
		lpMini.topMargin = remain / 8;
		layout.addView(mini, lpMini);
	}
}
