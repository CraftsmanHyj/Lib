package com.hyj.lib.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hyj.lib.R;
import com.hyj.lib.tools.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class AdapterAcitivty extends Activity {

	private ListView mListView;
	private List<News> lDatas;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adapter_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
	}

	private void initView() {
		lDatas = new ArrayList<News>();
		mListView = (ListView) findViewById(R.id.adapterLv);
		adapter = new MyAdapter(this, lDatas, R.layout.adapter_item);
		mListView.setAdapter(adapter);
	}

	private void initData() {
		News bean;
		for (int i = 1; i < 15; i++) {
			bean = new News("标题" + i, "Anadroid打造万能的ListView和GridView适配器" + i,
					Utils.getCurrentTime("yyyy-MM-dd"), (18523495256l + i) + "");
			lDatas.add(bean);
		}
		adapter.notifyDataSetChanged();
	}
}
