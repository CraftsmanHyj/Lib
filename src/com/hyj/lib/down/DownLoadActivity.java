package com.hyj.lib.down;

import com.hyj.lib.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 下载文件
 * 
 * @author async
 * 
 */
public class DownLoadActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

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
		findViewById(R.id.downBtStart).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.downBtStart:
			myDown();
			break;
		}
	}

	private void myDown() {
		new Thread() {
			public void run() {
				String url = "http://downmobile.kugou.com/Android/KugouPlayer/7840/KugouPlayer_219_V7.8.4.apk";
				DownLoad down = new DownLoad(DownLoadActivity.this);
				down.downLoadFile(url);
			};
		}.start();
	}
}
