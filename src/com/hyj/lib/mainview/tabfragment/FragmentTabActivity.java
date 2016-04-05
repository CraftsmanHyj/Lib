package com.hyj.lib.mainview.tabfragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.hyj.lib.R;

/**
 * <pre>
 * 通过Fragment的Tab实现主界面布局
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-5 上午11:07:17
 */
public class FragmentTabActivity extends FragmentActivity {

	private List<TextView> lTab;

	private int preIndex = 0;// 上一次点击索引

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_fragment);

		myInit();
	}

	private void myInit() {
		initView();
		initViewData();
		initListener();
	}

	private void initView() {
	}

	private void initViewData() {
		lTab = new ArrayList<TextView>();
		TextView tv = (TextView) findViewById(R.id.tabFragment01);
		tv.setTag(Tab01Fragment.class);
		lTab.add(tv);

		tv = (TextView) findViewById(R.id.tabFragment02);
		tv.setTag(Tab02Fragment.class);
		lTab.add(tv);

		tv = (TextView) findViewById(R.id.tabFragment03);
		tv.setTag(Tab03Fragment.class);
		lTab.add(tv);

		tv = (TextView) findViewById(R.id.tabFragment04);
		tv.setTag(Tab04Fragment.class);
		lTab.add(tv);

		switchFragment(preIndex, null);
	}

	private void initListener() {
		for (TextView tv : lTab) {
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switchFragment(lTab.indexOf(v), null);
				}
			});
		}
	}

	/**
	 * 选中点击的选项卡
	 * 
	 * @param index
	 */
	private void switchFragment(int index, Bundle bundle) {
		// 设置Tab点击字体颜色
		lTab.get(preIndex).setTextColor(Color.WHITE);
		lTab.get(index).setTextColor(Color.RED);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		Class<?> clsFrom = (Class<?>) lTab.get(preIndex).getTag();
		Class<?> clsTo = (Class<?>) lTab.get(index).getTag();

		// 设置加载的标签
		String tagFrom = clsFrom.getSimpleName();
		String tagTo = clsTo.getSimpleName();

		// 通过标签查找Fragment对象
		Fragment fragmentFrom = fm.findFragmentByTag(tagFrom);
		Fragment fragmentTo = fm.findFragmentByTag(tagTo);

		// 如果要切换到的Fragment不存在，则创建
		if (null == fragmentTo) {
			try {
				fragmentTo = (Fragment) clsTo.newInstance();
				fragmentTo.setArguments(bundle);// 设置传过来的参数
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 如果有参数传递
		if (null != bundle && !bundle.isEmpty()) {
			fragmentTo.getArguments().putAll(bundle);
		}

		// 设置Fragment切换效果
		transaction.setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in,
				android.R.anim.fade_out);

		/**
		 * <pre>
		 *  如果要切换到的Fragment没有被Fragment事务添加，
		 *  则隐藏被切换的Fragment，添加要切换的Fragment
		 *  否则，则隐藏被切换的Fragment，显示要切换的Fragment
		 * </pre>
		 */
		if (null != fragmentFrom) {
			transaction.hide(fragmentFrom);
		}

		if (!fragmentTo.isAdded()) {
			transaction.add(R.id.tabFragment, fragmentTo, tagTo);
		}
		transaction.show(fragmentTo);

		// 添加到返回堆栈
		// transaction.addToBackStack(tag);

		// 不保留状态提交事务
		transaction.commitAllowingStateLoss();

		preIndex = index;
	}
}
