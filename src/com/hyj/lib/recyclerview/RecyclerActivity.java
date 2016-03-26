package com.hyj.lib.recyclerview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hyj.lib.R;

/**
 * <pre>
 * 在这里有很多Item的Divider和切换动画
 * DividerItemDecoration
 *      https://gist.github.com/alexfu/0f464fc3742f134ccd1e
 * 
 * https://github.com/gabrielemariotti/RecyclerViewItemAnimators
 * </pre>
 */
public class RecyclerActivity extends Activity {
	private RecyclerView mRecyclerView;
	private List<String> lDatas;
	private RecyclerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initLisetner();
	}

	private void initView() {
		lDatas = new ArrayList<String>();
		adapter = new RecyclerAdapter(this, lDatas);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setAdapter(adapter);

		// 设置布局管理
		LinearLayoutManager llManager = new LinearLayoutManager(this,
				LinearLayoutManager.VERTICAL, false);
		mRecyclerView.setLayoutManager(llManager);

		// 添加动画效果
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		// 设置分割线
		// mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
		// DividerItemDecoration.VERTICAL_LIST));
	}

	private void initData() {
		for (int i = 'A'; i < 'z'; i++) {
			lDatas.add(String.valueOf((char) i));
		}
	}

	private void initLisetner() {
		adapter.setOnItemclickListener(new RecyclerAdapter.OnItemclickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Toast.makeText(RecyclerActivity.this, "点击事件" + position,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				Toast.makeText(RecyclerActivity.this, "长按事件" + position,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_recycler, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.recycMenuAdd:
			adapter.addData(1);
			break;

		case R.id.recycMenuDel:
			adapter.delData(1);
			break;

		case R.id.recycMenuListView:
			mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
			break;

		case R.id.recycMenuGridView:
			mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
			break;

		case R.id.recycMenuGridViewHoriz:
			mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(5,
					StaggeredGridLayoutManager.HORIZONTAL));
			break;

		case R.id.recycMenuStaggerd:
			Intent intent = new Intent(this, StaggeredGridLayoutActivity.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
