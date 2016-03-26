package com.hyj.lib.ui;

import java.util.ArrayList;
import java.util.List;

import com.hyj.lib.R;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 星型菜单
 * 
 * @author async
 * 
 */
public class StartMenuActivity extends Activity implements OnClickListener {
	private int[] menuId;
	private List<ImageView> lIv;
	private boolean hasExpend = false;// 菜单是否展开

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startmenu);

		myInit();
	}

	private void myInit() {
		initView();
		initViewData();
		initListener();
	}

	private void initView() {
		menuId = new int[] { R.id.startIv1, R.id.startIv2, R.id.startIv3,
				R.id.startIv4, R.id.startIv5, R.id.startIv6, R.id.startIv7,
				R.id.startIv8, };

		lIv = new ArrayList<ImageView>();
		for (int id : menuId) {
			lIv.add((ImageView) findViewById(id));
		}
	}

	private void initViewData() {

	}

	private void initListener() {
		for (ImageView iv : lIv) {
			iv.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startIv1:
			startAnimator();
			break;
		default:
			Toast.makeText(this, "点击：" + v.getId(), Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public void startAnimator() {
		float start = 0F, end = 100F;
		if (!hasExpend) {
			hasExpend = true;
		} else {
			hasExpend = false;
			start = 100F;
			end = 0F;
		}

		for (int i = 0; i < lIv.size(); i++) {
			ObjectAnimator oa = ObjectAnimator.ofFloat(lIv.get(i),
					"translationY", start * i, end * i);
			oa.setDuration(1000);
			oa.setStartDelay(i * 250);
			// 设置一个差值器，实现特效
			oa.setInterpolator(new BounceInterpolator());
			oa.start();
		}
	}
}
