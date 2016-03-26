package com.hyj.lib.listviewrefresh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.hyj.lib.R;
import com.hyj.lib.listviewrefresh.ListViewRefresh.OnRefreshListener;

public class ListViewRfreshActivity extends Activity implements
		OnRefreshListener {
	private ListViewRefresh lvContent;
	private ListViewAdapter adapter;
	private List<Integer> lDatas;

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listviewrefresh_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		lDatas = new ArrayList<Integer>();
		lvContent = (ListViewRefresh) findViewById(R.id.refreshLV);
		adapter = new ListViewAdapter(this, lDatas,
				R.layout.listviewrefresh_item);
		lvContent.setAdapter(adapter);
	}

	private void initData() {
		for (int i = 0; i < 10; i++) {
			lDatas.add(i);
		}
		adapter.notifyDataSetChanged();
	}

	private void initListener() {
		lvContent.setOnRefreshListener(this);
	}

	@Override
	public void onPullDownLisetner() {

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				int index = lDatas.size();
				for (int i = index; i < index + 2; i++) {
					lDatas.add(0, i);
				}
				adapter.notifyDataSetChanged();

				lvContent.refreshComplete();
			}
		}, 1000);

	}

	@Override
	public void onPullUpListener() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				int index = lDatas.size();
				for (int i = index; i < index + 2; i++) {
					lDatas.add(i);
				}
				adapter.notifyDataSetChanged();

				lvContent.refreshComplete();
			}
		}, 5000);
	}
}
