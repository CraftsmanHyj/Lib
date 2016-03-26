package com.hyj.lib.indicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.hyj.lib.R;

public class ViewPagerIndicatorActivity extends FragmentActivity {

	private ViewPager vpFragment;
	private ViewPagerIndicator indicator;

	private List<String> titles = Arrays.asList("短信1", "收藏2", "推荐3", "推荐4",
			"推荐5", "推荐6", "推荐7", "推荐8");
	private List<VPSimpleFragment> lFragment = new ArrayList<>();
	private FragmentPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpagerindecator);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		vpFragment = (ViewPager) findViewById(R.id.indicatorViewPager);
		indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		for (String title : titles) {
			VPSimpleFragment fragment = VPSimpleFragment.newInstance(title);
			lFragment.add(fragment);
		}

		adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return lFragment.size();
			}

			@Override
			public Fragment getItem(int position) {
				return lFragment.get(position);
			}
		};
		vpFragment.setAdapter(adapter);

		indicator.setTabItemTitles(titles);
		indicator.setViewPager(vpFragment, 0);
	}
}
