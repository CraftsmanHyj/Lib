package com.hyj.lib.image_mt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.hyj.lib.R;

/**
 * 图片美化主界面
 * 
 * @author async
 * 
 */
public class ImageMain extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_mt_main);

		myInit();
	}

	private void myInit() {
		initView();
		initListener();
	}

	private void initView() {

	}

	private void initListener() {
		findViewById(R.id.mtBtPrimaryColor).setOnClickListener(this);
		findViewById(R.id.mtBtColorMatrix).setOnClickListener(this);
		findViewById(R.id.mtBtPixelsEffect).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.mtBtPrimaryColor:
			intent.setClass(this, PrimaryColorActivity.class);
			break;

		case R.id.mtBtColorMatrix:
			intent.setClass(this, ColorMatrixActivity.class);
			break;

		case R.id.mtBtPixelsEffect:
			intent.setClass(this, PixelsActivity.class);
			break;
		}
		startActivity(intent);
	}
}
