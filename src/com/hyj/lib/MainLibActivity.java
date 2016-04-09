package com.hyj.lib;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hyj.lib.a.HelloChild;
import com.hyj.lib.adapter.AdapterAcitivty;
import com.hyj.lib.annotaionsframe.AnnotationsActivity_;
import com.hyj.lib.annotation.AnnotationActivity;
import com.hyj.lib.camera.CameraMainActivity;
import com.hyj.lib.downservice.DownServiceActivity;
import com.hyj.lib.flowlayout.FlowLayoutActivity;
import com.hyj.lib.gobang.GobangActivity;
import com.hyj.lib.image_mt.ImageMain;
import com.hyj.lib.image_preview.ImagePreviewActivity;
import com.hyj.lib.imagecycle.ImageCycleViewActivity;
import com.hyj.lib.indicator.ViewPagerIndicatorActivity;
import com.hyj.lib.jigsaw.JigsawActivity;
import com.hyj.lib.largeImage.LargeImageViewActivity;
import com.hyj.lib.listviewindex.ListViewIndexActivity;
import com.hyj.lib.listviewrefresh.ListViewRfreshActivity;
import com.hyj.lib.lockpattern.LockPatternActivity;
import com.hyj.lib.lockpattern2.LockTestActivity;
import com.hyj.lib.luckydial.LuckyDialActivity;
import com.hyj.lib.mainview.MainTabActivity;
import com.hyj.lib.popup.PopupActivity;
import com.hyj.lib.recyclerview.RecyclerActivity;
import com.hyj.lib.scratch.ScratchCardActivity;
import com.hyj.lib.startmenu.StartMenu;
import com.hyj.lib.startmenu.StartMenu2;
import com.hyj.lib.title_bar.TitleBarActivity;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.LogUtils;
import com.hyj.lib.tools.Utils;
import com.hyj.lib.tree.TreeActivity;
import com.hyj.lib.tuling.TulingActivity;
import com.hyj.lib.ui.DialogActivity;
import com.hyj.lib.ui.SquareActivity;
import com.hyj.lib.ui.TimerCountActivity;
import com.hyj.lib.viewpager.ViewPagerActivity;
import com.hyj.lib.viewpager.ViewPagerCustormerActivity;
import com.hyj.lib.wechat_imageUp.ImageLoaderActivity;
import com.hyj.lib.wechat_talk.WeChatTalkActivity;
import com.hyj.lib.wish.WishActivity;

public class MainLibActivity extends Activity {
	/**
	 * 猜歌游戏APP
	 */
	private final String APP_MUSIC = "com.hyj.music";

	private ListView lvItem;
	private MainLibAdapter adapter;
	private List<ListItem> lItems = new ArrayList<ListItem>();

	private long exitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lib_main);

		myInit();

		myTest();
	}

	private void myTest() {
		LogUtils.e("继承关系测试");
		new HelloChild();

		LogUtils.e("实例化对象测试");
		String s = new String("abc");
		String s1 = "abc";
		String s2 = new String("abc");

		LogUtils.i("s==s1：" + (s == s1));
		LogUtils.i("s==s2：" + (s == s2));
		LogUtils.i("s1==s2：" + (s1 == s2));
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		lvItem = (ListView) findViewById(R.id.mainLv);
		adapter = new MainLibAdapter(this, lItems);
		lvItem.setAdapter(adapter);
	}

	private void initData() {
		ListItem bean;

		bean = new ListItem();
		bean.setTitle("设置GridView的Item正方形");
		bean.setValue(SquareActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("Popup弹出方案合集");
		bean.setValue(PopupActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("高清加载巨图方案");
		bean.setValue(LargeImageViewActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("五子棋游戏");
		bean.setValue(GobangActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("ViewPager指示器");
		bean.setValue(ViewPagerIndicatorActivity.class);
		lItems.add(bean);

		if (Utils.hasApp(this, APP_MUSIC)) {
			bean = new ListItem();
			bean.setTitle("猜歌游戏");
			bean.setType(ListItem.TYPE_APP);
			bean.setValue(APP_MUSIC);
			lItems.add(bean);
		}

		bean = new ListItem();
		bean.setTitle("注解、反射使用案例");
		bean.setValue(AnnotationActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("心愿分享");
		bean.setValue(WishActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("拼图游戏");
		bean.setValue(JigsawActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("图灵机器人测试");
		bean.setValue(TulingActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("自定义相机测试");
		bean.setValue(CameraMainActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("AndroidAnnotaions框架测试");
		bean.setValue(AnnotationsActivity_.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("RecyclerView");
		bean.setValue(RecyclerActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("转盘抽奖");
		bean.setValue(LuckyDialActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("DialogUtils工具类测试");
		bean.setValue(DialogActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("带索引条的ListView");
		bean.setValue(ListViewIndexActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("刮刮卡");
		bean.setValue(ScratchCardActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("个性图片预览与多点触控");
		bean.setValue(ImagePreviewActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("仿微信聊天");
		bean.setValue(WeChatTalkActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("仿微信图片上传");
		bean.setValue(ImageLoaderActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("九宫格解锁2");
		bean.setValue(LockTestActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("九宫格解锁");
		bean.setValue(LockPatternActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("下拉刷新组件");
		bean.setValue(ListViewRfreshActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("流式布局组件");
		bean.setValue(FlowLayoutActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("轮播图片、广告");
		bean.setValue(ImageCycleViewActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("断点续传service");
		bean.setValue(DownServiceActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("万能适配器的实现");
		bean.setValue(AdapterAcitivty.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("任一级树形控件");
		bean.setValue(TreeActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("图片处理(美图秀秀)");
		bean.setValue(ImageMain.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("自定义title测试");
		bean.setValue(TitleBarActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("ViewPager 切换动画  自定义viewpage实现向下兼容");
		bean.setValue(ViewPagerCustormerActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("ViewPager切换动画");
		bean.setValue(ViewPagerActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("主界面实现方案");
		bean.setValue(MainTabActivity.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("星型菜单(使用普通动画实现)");
		bean.setValue(StartMenu2.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("星型菜单(使用属性动画实现)");
		bean.setValue(StartMenu.class);
		lItems.add(bean);

		bean = new ListItem();
		bean.setTitle("倒计时");
		bean.setValue(TimerCountActivity.class);
		lItems.add(bean);

		adapter.notifyDataSetChanged();
	}

	private void initListener() {
		lvItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListItem bean = (ListItem) parent.getItemAtPosition(position);

				switch (bean.getType()) {
				case ListItem.TYPE_ACTIVITY:
					if (null != bean.getValue()) {
						Intent intent = new Intent();
						intent.setClass(MainLibActivity.this,
								(Class<?>) bean.getValue());
						startActivity(intent);
					} else {
						Toast.makeText(MainLibActivity.this, "即将开通……",
								Toast.LENGTH_SHORT).show();
					}
					break;

				case ListItem.TYPE_APP:
					Utils.startApp(MainLibActivity.this,
							(String) bean.getValue());
					break;
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				DialogUtils.showToastShort(this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
