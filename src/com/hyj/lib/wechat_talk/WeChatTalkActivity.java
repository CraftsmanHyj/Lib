package com.hyj.lib.wechat_talk;

import java.io.File;
import java.io.FilenameFilter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.hyj.lib.R;
import com.hyj.lib.wechat_talk.AudioRecorderButton.OnAudioRecorderFinishListener;
import com.hyj.lib.wechat_talk.AudioRecorderButton.OnAudioRecorderLongClickLisetener;

public class WeChatTalkActivity extends Activity {
	private ListView mListView;
	private TalkAdapter adapter;
	private List<Recorder> lDatas;

	private AudioRecorderButton btRecorder;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
			if (lDatas.size() > 0) {
				mListView.setSelection(lDatas.size() - 1);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wechat_talk_main);

		myInit();
	}

	private void myInit() {
		initView();
		initDatas();
		initListener();
	}

	private void initView() {
		lDatas = new ArrayList<Recorder>();
		adapter = new TalkAdapter(this, lDatas, R.layout.wechat_talk_main_item);
		mListView = (ListView) findViewById(R.id.talkListView);
		mListView.setAdapter(adapter);

		btRecorder = (AudioRecorderButton) findViewById(R.id.talkBtRecorder);
	}

	private void initDatas() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String dirPath = btRecorder.getAudioDir();

				File audioDir = new File(dirPath);
				FilenameFilter filter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".amr")) {
							return true;
						}
						return false;
					}
				};
				String[] audioFiles = audioDir.list(filter);

				if (null != audioFiles && audioFiles.length > 0) {
					Recorder recorder;
					for (String fileName : audioFiles) {
						int duration = getAmrDuration(dirPath + File.separator
								+ fileName);
						recorder = new Recorder(duration, dirPath
								+ File.separator + fileName);
						lDatas.add(recorder);
					}
				}

				handler.sendEmptyMessage(0x001);
			}
		}).start();
	}

	private void initListener() {
		btRecorder
				.setOnAudioRecorderFinishListener(new OnAudioRecorderFinishListener() {

					@Override
					public void onFinish(float seconds, String filePath) {
						Recorder recorder = new Recorder(seconds, filePath);
						lDatas.add(recorder);
						adapter.notifyDataSetChanged();
						// 让ListView滚动到最后一条数据
						mListView.setSelection(lDatas.size() - 1);
					}
				});

		btRecorder
				.setOnAudioRecorderLongClickLisetener(new OnAudioRecorderLongClickLisetener() {
					@Override
					public void onLongClick(View v) {
						adapter.stopPreRecorderPlay();
					}
				});
	}

	/**
	 * 计算.amr文件时长
	 * 
	 * @param path
	 */
	public int getAmrDuration(String path) {
		File file = new File(path);

		long duration = -1;// 录音时长，ms为单位
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;

			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? (length - 6) / 650 : 0;
					break;
				}

				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}

			duration += frameCount * 20;// 帧数*20
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != randomAccessFile) {
					randomAccessFile.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return Math.round(duration * 1.0f / 1000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MediaManager.release();
	}
}
