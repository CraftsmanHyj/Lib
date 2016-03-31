package com.hyj.lib.gobang;

import com.hyj.lib.R;

import android.app.Activity;
import android.os.Bundle;

public class GobangActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gobang_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {

	}

	private void initData() {

	}

	private void initListener() {

	}
}
