package com.hyj.lib.scratch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hyj.lib.R;
import com.hyj.lib.luckydial.PrizeInfo;
import com.hyj.lib.scratch.ScratchCard.OnScratchCompleteListener;
import com.hyj.lib.tools.DialogUtils;

public class ScratchCardActivity extends Activity {
	private List<PrizeInfo> lPrize;

	private ScratchCard scratch;
	private Button btResert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch_main);

		myInit();
	}

	private void myInit() {
		initView();
		initDatas();
		initLisetner();
	}

	private void initView() {
		scratch = (ScratchCard) findViewById(R.id.scratchCard);
		btResert = (Button) findViewById(R.id.scratchBtResert);
	}

	private void initDatas() {
		lPrize = new ArrayList<PrizeInfo>();
		lPrize.add(new PrizeInfo("洗衣服"));
		lPrize.add(new PrizeInfo("拖地"));
		lPrize.add(new PrizeInfo("洗碗"));
		lPrize.add(new PrizeInfo("做饭"));
		lPrize.add(new PrizeInfo("倒垃圾"));
		lPrize.add(new PrizeInfo("溜小孩"));
		lPrize.add(new PrizeInfo("刷马桶"));
		lPrize.add(new PrizeInfo("休息"));
		// 要先进行排序才能随机取值
		PrizeInfo.setPrizePercent(lPrize);
		scratch.setPrize(getPrize());
	}

	private void initLisetner() {

		scratch.setOnScratchCompleteListener(new OnScratchCompleteListener() {

			@Override
			public void onScratchComplete() {
				DialogUtils.showToastShort(ScratchCardActivity.this, "全部刮完");
			}
		});

		btResert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scratch.resert(getPrize());
			}
		});
	}

	private String getPrize() {
		PrizeInfo prize = PrizeInfo.getPrize(lPrize);
		if (null == prize) {
			return "";
		}
		return prize.getLabel();
	}
}
