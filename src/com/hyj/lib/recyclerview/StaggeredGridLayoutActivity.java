package com.hyj.lib.recyclerview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.hyj.lib.R;

public class StaggeredGridLayoutActivity extends Activity {
	private RecyclerView mRecyclerView;
	private List<String> lDatas;
	private StaggeredAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_main);

		myInit();
	}

	private void myInit() {
		initData();
		initView();
		initListener();

	}

	private void initData() {
		lDatas = new ArrayList<String>();
		for (int i = 'A'; i < 'z'; i++) {
			lDatas.add(String.valueOf((char) i));
		}
	}

	private void initView() {
		adapter = new StaggeredAdapter(this, lDatas);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setAdapter(adapter);

		// 设置布局管理
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
				StaggeredGridLayoutManager.VERTICAL));

		// 设置分割线
		// mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
		// DividerItemDecoration.VERTICAL_LIST));
	}

	private void initListener() {
		adapter.setOnItemclickListener(new RecyclerAdapter.OnItemclickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Toast.makeText(StaggeredGridLayoutActivity.this,
						"点击事件" + position, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				adapter.delData(position);
			}
		});
	}
}
