package com.hyj.lib.downservice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hyj.lib.R;
import com.hyj.lib.down.DownLoad;
import com.hyj.lib.http.download.DownLoadTask;
import com.hyj.lib.http.download.DownService;
import com.hyj.lib.http.download.FileInfo;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.ServiceUtils;

public class DownServiceActivity extends Activity {
	private List<FileInfo> lFile;
	private ListView lvDownFile;
	private FileListAdapter adapter;

	/**
	 * 更新UI的广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			FileInfo fileInfo = (FileInfo) intent
					.getSerializableExtra(DownService.DOWNINFO);

			boolean isFound = false;
			for (FileInfo file : lFile) {
				if (fileInfo.getId() == file.getId()) {
					file.setDownStatus(fileInfo.getDownStatus());
					fileInfo = file;

					isFound = true;
					break;
				}
			}

			// 如果在队列中没有找到文件则退出
			if (!isFound) {
				return;
			}

			switch (intent.getAction()) {
			case DownService.ACTION_PREPARE:
				break;

			case DownService.ACTION_PAUSE:
				break;

			case DownService.ACTION_START:
				int progress = intent.getIntExtra(DownLoadTask.PROGRESS, 0);
				fileInfo.setProgress(progress);
				adapter.notifyDataSetChanged();
				break;

			case DownService.ACTION_FINISH:
				// 下载完成进度条重置
				fileInfo.setProgress(0);
				adapter.notifyDataSetChanged();

				String msg = "文件<" + fileInfo.getFileName() + ">下载完成";
				DialogUtils.showToastShort(DownServiceActivity.this, msg);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downservice_main);

		myInit();
		singleFileDown();
	}

	private void myInit() {
		initView();
		initData();
		registerBroadcast();
	}

	private void initView() {
		lFile = new ArrayList<FileInfo>();
		lvDownFile = (ListView) findViewById(R.id.downListView);
		adapter = new FileListAdapter(this, lFile, R.layout.downservice_item);
		lvDownFile.setAdapter(adapter);
	}

	private void initData() {
		String[][] urls = new String[][] {
				{ "慕课网APK", "http://www.imooc.com/mobile/imooc.apk" },
				{
						"酷狗音乐.apk",
						"http://downmobile.kugou.com/Android/KugouPlayer/7840/KugouPlayer_219_V7.8.4.apk" },
				{
						"e代驾.apk",
						"http://f2.market.xiaomi.com/download/AppStore/0d39f5b3b27bad6601aba10606b63e472f54091c1/cn.edaijia.android.client.apk" },
				{
						"界面.apk",
						"http://f1.market.mi-img.com/download/AppStore/098b6753991fd4fb414aff12e29d9d5db9a0d63d8/com.jiemian.news.apk" },
				{
						"功夫熊猫.apk",
						"http://f2.market.mi-img.com/download/AppStore/006784c9c7551b9bbe1ea0b084a24c96c9a42b795/com.qingguo.gfxiong.apk" } };

		boolean isNetWork = true;
		if (isNetWork) {
			String ip = "192.168.23.1";
			urls = new String[][] {
					{ "QQ0.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ0.apk" },
					{ "QQ1.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ1.apk" },
					{ "QQ2.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ2.apk" },
					{ "QQ3.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ3.apk" },
					{ "QQ4.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ4.apk" },
					{ "QQ5.apk",
							"http://" + ip + ":8080/AndroidProvider/QQ5.apk" },
					{ "mobile.apk",
							"http://" + ip + ":8080/AndroidProvider/mobile.apk" } };
		}

		for (int i = 0; i < urls.length; i++) {
			FileInfo fileInfo = new FileInfo(i, urls[i][1], urls[i][0]);
			lFile.add(fileInfo);
		}

		adapter.notifyDataSetChanged();
	}

	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownService.ACTION_PREPARE);
		filter.addAction(DownService.ACTION_PAUSE);
		filter.addAction(DownService.ACTION_START);
		filter.addAction(DownService.ACTION_FINISH);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		ServiceUtils.stopService(this, DownService.class);
		// 注销广播接收者
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * <pre>
	 * 单文件、多线程、在线下载文件
	 * 不在这个包里面
	 * </pre>
	 */
	private void singleFileDown() {
		Button btn = (Button) findViewById(R.id.downBtDown);
		btn.setEnabled(false);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						String url = "http://downmobile.kugou.com/Android/KugouPlayer/7840/KugouPlayer_219_V7.8.4.apk";
						DownLoad down = new DownLoad(DownServiceActivity.this);
						down.downLoadFile(url);
					};
				}.start();
			}
		});
	}
}
