package com.hyj.lib;

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

/**
 * <pre>
 * 二级界面
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-13 下午5:15:25
 */
public class SecondaryActivity extends Activity {
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

	@SuppressWarnings("unchecked")
	private void initData() {
		ArrayList<ListItem> lData = (ArrayList<ListItem>) getIntent()
				.getSerializableExtra(MainLibActivity.DATA_BUNDLE);

		for (ListItem bean : lData) {
			lItems.add(bean);
		}
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
						intent.setClass(SecondaryActivity.this,
								(Class<?>) bean.getValue());
						startActivity(intent);
					} else {
						Toast.makeText(SecondaryActivity.this, "即将开通……",
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
