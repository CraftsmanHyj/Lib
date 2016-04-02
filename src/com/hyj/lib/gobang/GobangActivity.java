package com.hyj.lib.gobang;

import com.hyj.lib.R;
import com.hyj.lib.gobang.WuziqiPanel.OnGameOverListener;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.DialogUtils.DialogAction;

import android.app.Activity;
import android.os.Bundle;

public class GobangActivity extends Activity {

	private WuziqiPanel gobang;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gobang_main);

		myInit();
	}

	private void myInit() {
		initView();
		initListener();
	}

	private void initView() {
		gobang = (WuziqiPanel) findViewById(R.id.gobang);
	}

	private void initListener() {
		gobang.setOnGameOverListener(new OnGameOverListener() {

			@Override
			public void onTheEnd(String msg) {
				DialogUtils.showConfirmDialog(GobangActivity.this, "提示", msg,
						new DialogAction() {

							@Override
							public void action() {
								gobang.reStart();
							}
						}, new DialogAction() {

							@Override
							public void action() {
								GobangActivity.this.finish();
							}
						});
			}
		});
	}
}
