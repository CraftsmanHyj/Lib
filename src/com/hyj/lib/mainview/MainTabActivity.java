package com.hyj.lib.mainview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hyj.lib.ListItem;
import com.hyj.lib.MainLibAdapter;
import com.hyj.lib.R;
import com.hyj.lib.mainview.qq5_0.SlidingActivity;
import com.hyj.lib.mainview.tabfragment.FragmentTabActivity;
import com.hyj.lib.mainview.wechat.WeChatActivivty;

public class MainTabActivity extends Activity {
	private ListView lvItem;
	private MainLibAdapter adapter;
	private List<ListItem> lItems = new ArrayList<ListItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lib_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initLisetner();
	}

	private void initView() {
		lvItem = (ListView) findViewById(R.id.mainLv);
		adapter = new MainLibAdapter(this, lItems);
		lvItem.setAdapter(adapter);
	}

	private void initData() {
		ListItem bean;

		bean = new ListItem();
		bean.setTitle("QQ5.0侧滑菜单");
		bean.setValue(SlidingActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("Fragment实现Tab功能");
		bean.setValue(FragmentTabActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("仿微信界面");
		bean.setValue(WeChatActivivty.class);
		lItems.add(bean);
	}

	private void initLisetner() {
		lvItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListItem bean = lItems.get(position);

				switch (bean.getType()) {
				case ListItem.TYPE_ACTIVITY:
					if (null != bean.getValue()) {
						Intent intent = new Intent();
						intent.setClass(MainTabActivity.this,
								(Class<?>) bean.getValue());
						startActivity(intent);
					} else {
						Toast.makeText(MainTabActivity.this, "即将开通……",
								Toast.LENGTH_SHORT).show();
					}
					break;

				case ListItem.TYPE_APP:
					break;
				}
			}
		});
	}
}
