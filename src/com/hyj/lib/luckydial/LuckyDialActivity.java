package com.hyj.lib.luckydial;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hyj.lib.R;
import com.hyj.lib.luckydial.LuckyDial.onRotationDownListener;
import com.hyj.lib.tools.DialogUtils;

public class LuckyDialActivity extends Activity {

	private LuckyDial dial;
	/**
	 * 开始抽奖按钮，已经集成到LuckyDial里面
	 */
	private ImageView ivBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.luckydial_main);

		myInit();
	}

	private void myInit() {
		initView();
		initDatas();
		initListener();
	}

	private void initView() {
		dial = (LuckyDial) findViewById(R.id.dial);
		ivBtn = (ImageView) findViewById(R.id.dialIvBtn);
	}

	private void initDatas() {
		List<PrizeInfo> lPrizeInfo = new ArrayList<PrizeInfo>();
		lPrizeInfo = new ArrayList<PrizeInfo>();
		lPrizeInfo.add(new PrizeInfo(R.drawable.f015, "苹果表", 0));
		lPrizeInfo.add(new PrizeInfo(R.drawable.meizi, "时装", 1));
		lPrizeInfo.add(new PrizeInfo(R.drawable.xiaonian, "恭喜发财"));
		lPrizeInfo.add(new PrizeInfo(R.drawable.ipad, "IPAD", 0));
		lPrizeInfo.add(new PrizeInfo(R.drawable.f015, "现金大奖", 1));
		lPrizeInfo.add(new PrizeInfo(R.drawable.iphone, "IPHONE", 0));
		lPrizeInfo.add(new PrizeInfo(R.drawable.xiaonian, "恭喜发财"));
		lPrizeInfo.add(new PrizeInfo(R.drawable.danfan, "单反相机", 0));
		dial.setPrizeInfo(lPrizeInfo);
	}

	private void initListener() {
		dial.setonRotationDownListener(new onRotationDownListener() {

			@Override
			public void onRotationDown(PrizeInfo prize) {
				DialogUtils.showToastShort(LuckyDialActivity.this,
						prize.getLabel());
			}
		});

		ivBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dial.isRunning()) {
					dial.luckyStart();
					ivBtn.setImageResource(R.drawable.stop);
				} else {
					if (!dial.isStopping()) {
						dial.luckyStop();
						ivBtn.setImageResource(R.drawable.start);
					}
				}
			}
		});
	}
}
