package com.hyj.lib.mainview.wechat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;

import com.hyj.lib.R;

/**
 * 微信主界面
 * 
 * @author async
 * 
 */
public class WeChatActivivty extends FragmentActivity {

	private ViewPager mViewPager;
	private FragmentPagerAdapter fpAdapter;
	private List<Fragment> lFragment = new ArrayList<Fragment>();
	private List<TabView> lTab = new ArrayList<TabView>();
	private View tabLine;
	private float tabWidth;// tab宽度

	private String[] titles = new String[] { "first fragment",
			"second fragment", "third fragment", "fourth fragment" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wechat);

		myInit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_wechat, menu);
		return true;
	}

	/**
	 * 设置菜单展开的时候显示图标
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if (Window.FEATURE_ACTION_BAR == featureId && menu != null) {
			if ("MenuBuilder".equals(menu.getClass().getSimpleName())) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	private void myInit() {
		initView();
		initTabLine();
		initViewData();
		initListener();
	}

	private void initView() {
		setOverflowButtonAlways();
		// 设置actionbar左边图标不显示
		getActionBar().setDisplayShowHomeEnabled(false);

		mViewPager = (ViewPager) findViewById(R.id.chatVp);

		// 查找所有的Tab
		lTab.add((TabView) findViewById(R.id.chatTvOne));
		lTab.add((TabView) findViewById(R.id.chatTvTwo));
		lTab.add((TabView) findViewById(R.id.chatTvThird));
		lTab.add((TabView) findViewById(R.id.chatTvFour));
		lTab.get(0).setIconAlpha(1.0f);// 默认第一个选中
	}

	/**
	 * 初始化tabLine的宽度
	 */
	private void initTabLine() {
		tabLine = findViewById(R.id.chatTabLine);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		tabWidth = dm.widthPixels / lTab.size();

		LayoutParams params = (LayoutParams) tabLine.getLayoutParams();
		params.width = (int) tabWidth;
		tabLine.setLayoutParams(params);
	}

	private void initViewData() {
		for (String str : titles) {
			TabFragment tab = new TabFragment();
			Bundle bundle = new Bundle();
			bundle.putString(TabFragment.TITLE, str);
			tab.setArguments(bundle);
			lFragment.add(tab);
		}

		fpAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return lFragment.size();
			}

			@Override
			public Fragment getItem(int postion) {
				return lFragment.get(postion);
			}
		};
		mViewPager.setAdapter(fpAdapter);
	}

	@SuppressWarnings("deprecation")
	private void initListener() {
		for (int i = 0; i < lTab.size(); i++) {
			final int index = i;
			lTab.get(index).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					resertOtherTabs();
					lTab.get(index).setIconAlpha(1.0f);
					mViewPager.setCurrentItem(index, false);
				}
			});
		}

		// 设置viewpage左右切换事件
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int postion) {

			}

			@Override
			public void onPageScrolled(int postion, float postionOffset,
					int postionOffsetPixels) {
				if (postionOffset > 0) {// 左滑动
					TabView tabLeft = lTab.get(postion);
					TabView tabRight = lTab.get(postion + 1);
					tabLeft.setIconAlpha(1 - postionOffset);
					tabRight.setIconAlpha(postionOffset);
				}

				// 设置tabline动态滑动的距离
				LayoutParams params = (LayoutParams) tabLine.getLayoutParams();
				params.leftMargin = (int) ((postion + postionOffset) * tabWidth);
				tabLine.setLayoutParams(params);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	/**
	 * 让更多按钮一直显示
	 */
	private void setOverflowButtonAlways() {
		try {
			ViewConfiguration vc = ViewConfiguration.get(this);
			Field menuKey = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKey.setAccessible(true);
			menuKey.setBoolean(vc, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重置tabview的颜色
	 */
	private void resertOtherTabs() {
		for (TabView tabView : lTab) {
			tabView.setIconAlpha(0);
		}
	}
}
