package com.hyj.lib.imagecycle;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyj.lib.LibApplication;
import com.hyj.lib.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * <pre>
 * 实现可循环，可轮播的viewpager
 * 它的所有属性设置都应写在setData()之前,不然无效
 * </pre>
 * 
 * @author hyj
 * @Date 2016-3-1 下午2:48:57
 */
@SuppressLint({ "NewApi", "InflateParams", "Recycle" })
public class ImageCycleView extends LinearLayout {
	// 判断是否轮播图片
	private final int WHEEL = 0x001; // 转动
	private final int WHEEL_WAIT = 0x002; // 等待

	// 指示器位置
	public static final int LEFT = 0x003;// 左边
	public static final int CENTER = 0x004;// 居中
	public static final int RIGHT = 0x005;// 右边

	private Context context;

	private RelativeLayout rlRootView;
	private LinearLayout llIndicator; // 指示器
	private BaseViewPager parentViewPager;
	private BaseViewPager viewPager;
	private ViewPagerAdapter adapter;

	private boolean isCycle = false; // 是否循环
	private boolean isWheel = false; // 是否轮播
	private int wheelTime = 5000; // 默认轮播时间
	private int indicatorPosition = RIGHT;// 指示器位置

	private int currentPosition = 0; // 轮播当前位置
	private boolean isScrolling = false; // 滚动框是否滚动着
	private long releaseTime = 0; // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换

	private List<ADInfo> lAdInfo;
	private List<ImageView> lImageView;
	private OnImageCycleViewListener mImageCycleViewListener;

	private ImageCycleViewHandler handler = new ImageCycleViewHandler(context) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (lImageView.size() <= 0) {
				return;
			}

			if (msg.what == WHEEL) {
				if (!isScrolling) {
					int position = (currentPosition + 1) % lImageView.size();
					viewPager.setCurrentItem(position, true);
				}
				releaseTime = System.currentTimeMillis();
			}
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable, wheelTime);
		}
	};

	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (context != null && isWheel) {
				long currentTime = System.currentTimeMillis();
				// 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
				if (currentTime - releaseTime > wheelTime - 500) {
					handler.sendEmptyMessage(WHEEL);
				} else {
					handler.sendEmptyMessage(WHEEL_WAIT);
				}
			}
		}
	};

	/**
	 * 设置图片点击事件
	 * 
	 * @param onImageCycleViewListener
	 */
	public void setOnImageCycleViewListener(
			OnImageCycleViewListener onImageCycleViewListener) {
		this.mImageCycleViewListener = onImageCycleViewListener;
	}

	/**
	 * 是否循环，默认不开启，开启前，请将views的最前面与最后面各加入一个视图，用于循环
	 * 
	 * @param isCycle
	 *            是否循环
	 */
	public void setCycle(boolean isCycle) {
		this.isCycle = isCycle;
	}

	/**
	 * 设置轮播暂停时间，即没多少秒切换到下一张视图.默认5000ms
	 * 
	 * @param wheelTime
	 *            毫秒为单位
	 */
	public void setWheelTime(int wheelTime) {
		this.wheelTime = wheelTime;
		// 若设置了轮播时间，则必定轮播且循环
		if (wheelTime > 0) {
			this.isWheel = true;
			setCycle(isWheel);
			handler.postDelayed(runnable, wheelTime);
		}
	}

	/**
	 * 返回当前位置,循环时需要注意返回的position包含之前在views最前方与最后方加入的视图，即当前页面试图在views集合的位置
	 * 
	 * @return
	 */
	public int getCurrentPostion() {
		if (isCycle) {
			return currentPosition - 1;
		}
		return currentPosition;
	}

	/**
	 * 返回内置的viewpager
	 * 
	 * @return viewPager
	 */
	public BaseViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * 释放指示器高度，可能由于之前指示器被限制了高度，此处释放
	 */
	public void releaseHeight() {
		getRootView().getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
		refreshData();
	}

	/**
	 * 刷新数据，当外部视图更新后，通知刷新数据
	 */
	public void refreshData() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 将整个广告栏隐藏
	 */
	public void hideAdView() {
		rlRootView.setVisibility(View.GONE);
	}

	/**
	 * 开始播放广告
	 */
	public void start() {
		handler.postDelayed(runnable, wheelTime);
	}

	/**
	 * 暂停播放广告<br/>
	 * 防止类似当返回到home桌面时还在后台一直播放广告
	 */
	public void pause() {
		handler.removeCallbacks(runnable);
	}

	public ImageCycleView(Context context) {
		this(context, null);
	}

	public ImageCycleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageCycleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.context = context;

		myInit(attrs);
	}

	private void myInit(AttributeSet attrs) {
		initAttrs(attrs);
		initView();
		initData();
		initListener();
	}

	private void initAttrs(AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.imagecycle);
		isCycle = ta.getBoolean(R.styleable.imagecycle_iscycle, isCycle);
		indicatorPosition = ta.getInteger(
				R.styleable.imagecycle_indicatorposition, indicatorPosition);
		if (ta.hasValue(R.styleable.imagecycle_wheeltime)) {
			wheelTime = ta.getInteger(R.styleable.imagecycle_wheeltime,
					wheelTime);
			setWheelTime(wheelTime);
		}

		ta.recycle();
	}

	private void initView() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.imagecycleview, null);

		viewPager = (BaseViewPager) view
				.findViewById(R.id.imageCycleView_ViewPager);
		llIndicator = (LinearLayout) view
				.findViewById(R.id.imageCycleView_Indicator);

		rlRootView = (RelativeLayout) view
				.findViewById(R.id.imageCycleView_Content);
		addView(view);

		setIndicatorPosition(indicatorPosition);
	}

	private void initData() {
		lImageView = new ArrayList<ImageView>();
		adapter = new ViewPagerAdapter();
		viewPager.setOffscreenPageLimit(3);// 设置缓存页面数
		viewPager.setAdapter(adapter);
	}

	@SuppressWarnings("deprecation")
	private void initListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int postion) {
				int max = lImageView.size() - 1;
				currentPosition = postion;
				int indicatorPosition = currentPosition;
				if (isCycle) {
					if (0 == postion) {
						currentPosition = max - 1;
					} else if (postion == max) {
						currentPosition = 1;
					}
					indicatorPosition = currentPosition - 1;
				}

				setIndicator(indicatorPosition);
			}

			@Override
			public void onPageScrolled(int postion, float postionOffset,
					int postionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (1 == state) { // viewPager在滚动
					isScrolling = true;
				} else if (0 == state) { // viewPager滚动结束
					if (parentViewPager != null) {
						parentViewPager.setScrollable(true);
					}

					releaseTime = System.currentTimeMillis();

					viewPager.setCurrentItem(currentPosition, false);

					isScrolling = false;
				}
			}
		});
	}

	/**
	 * 初始化数据
	 * 
	 * @param views
	 * @param list
	 */
	public void setData(List<ADInfo> list) {
		setData(list, 0);
	}

	/**
	 * 初始化viewpager
	 * 
	 * @param lImageview
	 *            要显示的views
	 * @param showPosition
	 *            默认显示位置
	 */
	public void setData(List<ADInfo> lAdInfo, int showPosition) {
		this.lAdInfo = lAdInfo;

		if (lAdInfo.size() <= 0) {
			hideAdView();
			return;
		}

		lImageView.clear();
		for (int i = 0; i < lAdInfo.size(); i++) {
			this.lImageView.add(getImageView(lAdInfo.get(i).getUrl()));
		}

		/**
		 * <pre>
		 * 若循环显示，则第一张需显示最后一张图片， 最后一张需显示第一张图片
		 * 这样循环显示的时候就不会有跳页的情况,也就是说iamgeview比传过来的adinfo多2
		 * </pre>
		 */
		if (isCycle) {
			lImageView.add(0, getImageView(lAdInfo.get(lAdInfo.size() - 1)
					.getUrl()));
			lImageView.add(lImageView.size(), getImageView(lAdInfo.get(0)
					.getUrl()));
		}

		llIndicator.removeAllViews();
		for (int i = 0; i < lAdInfo.size(); i++) {
			ImageView img = getImageView();
			llIndicator.addView(img);
		}
		// 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
		setIndicator(0);

		if (showPosition < 0 || showPosition >= lImageView.size()) {
			showPosition = 0;
		}
		if (isCycle) {
			// 若是循环，第0位放的是最后一张照片，所有需要+1
			showPosition = showPosition + 1;
		}
		viewPager.setCurrentItem(showPosition);

		refreshData();
	}

	/**
	 * 获取ImageView
	 * 
	 * @param url
	 *            图片显示的URL
	 * @return
	 */
	private ImageView getImageView(String url) {
		ImageView imageView = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.FIT_XY);

		// 加载图片
		// 方法一：声明一个Application
		// ImageLoader.getInstance().displayImage(url, imageView);

		// 方法二：
		DisplayImageOptions options = ((LibApplication) context
				.getApplicationContext()).getImageOptions(50);
		ImageLoader.getInstance().displayImage(url, imageView, options);

		// 方法三：在单独Acrivity中声明ImageLoad
		// ImageLoader.getInstance().displayImage(url, imageView);

		return imageView;
	}

	/**
	 * 获取ImageView
	 * 
	 * @return
	 */
	private ImageView getImageView() {
		ImageView imageView = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.FIT_XY);
		return imageView;
	}

	/**
	 * 设置指示器的位置
	 * 
	 * @param indicatorposition
	 */
	public void setIndicatorPosition(int indicatorPosition) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		switch (indicatorPosition) {
		case LEFT:
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			break;
		case CENTER:
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			break;
		case RIGHT:
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			break;
		}
		llIndicator.setLayoutParams(params);
	}

	/**
	 * 设置viewpager是否可以滚动
	 * 
	 * @param enable
	 */
	public void setScrollable(boolean enable) {
		viewPager.setScrollable(enable);
	}

	/**
	 * 设置指示器
	 * 
	 * @param selectedPosition
	 *            默认指示器位置
	 */
	private void setIndicator(int selectedPosition) {
		int count = llIndicator.getChildCount();
		for (int i = 0; i < count; i++) {
			llIndicator.getChildAt(i).setBackgroundResource(
					R.drawable.img_point);
		}

		llIndicator.getChildAt(selectedPosition).setBackgroundResource(
				R.drawable.img_point_pre);
	}

	/**
	 * 如果当前页面嵌套在另一个viewPager中，为了在进行滚动时阻断父ViewPager滚动， 可以
	 * 阻止父ViewPager滑动事件父ViewPager需要实现ParentViewPager中的setScrollable方法
	 */
	public void disableParentViewPagerTouchEvent(BaseViewPager parentViewPager) {
		if (parentViewPager != null) {
			parentViewPager.setScrollable(false);
		}
	}

	/**
	 * 页面适配器 返回对应的view
	 * 
	 * @author Administrator
	 * 
	 */
	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return lImageView.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			// 这里返回每一个fragment中的视图
			ImageView vimg = lImageView.get(position);
			if (mImageCycleViewListener != null) {
				vimg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int index = getCurrentPostion();
						mImageCycleViewListener.onImageClick(
								lAdInfo.get(index), index, v);
					}
				});
			}
			container.addView(vimg);
			return vimg;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	/**
	 * 轮播控件的监听事件
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface OnImageCycleViewListener {

		/**
		 * 单击图片事件
		 * 
		 * @param ADInfo
		 *            info广告信息
		 * @param int postion索引
		 * @param Viewi
		 *            ViewmageView点击响应控件
		 */
		public void onImageClick(ADInfo info, int postion, View imageView);
	}
}