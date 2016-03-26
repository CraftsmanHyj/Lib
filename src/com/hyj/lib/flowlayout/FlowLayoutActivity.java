package com.hyj.lib.flowlayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hyj.lib.R;

public class FlowLayoutActivity extends Activity {

	private String[] mValues = new String[] { "jijojoj", "fwfew", "hdhdfhdfh",
			"jijojoj", "fwfew", "hdhdfhdfh", "jijojoj", "fwfew", "hdhdfhdfh",
			"jijojoj", "fwfew", "hdhdfhdfh", "jijojoj", "fwfew", "hdhdfhdfh", };

	private FlowLayout flowLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flowlayout);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		flowLayout = (FlowLayout) findViewById(R.id.flowLayoutView);
	}

	private void initData() {
		// for (int i = 0; i < mValues.length; i++) {
		// Button bt = new Button(this);
		// MarginLayoutParams lp = new MarginLayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// bt.setText(mValues[i]);
		// //记得写上 MarginLayoutParams
		// flowLayout.addView(bt, lp);
		// }

		// 添加自定义View
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < mValues.length; i++) {
			TextView tv = (TextView) inflater.inflate(
					R.layout.flowlayout_textview, flowLayout, false);
			tv.setText(mValues[i]);
			flowLayout.addView(tv);
		}
	}

	private void initListener() {

	}
}
