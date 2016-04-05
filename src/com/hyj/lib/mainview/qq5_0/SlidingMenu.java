package com.hyj.lib.mainview.qq5_0;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hyj.lib.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * <pre>
 * 自定义ViewGroup步骤
 * 1、实现构造方法，获取自定义属性
 * 2、在onMeasure计算view的宽、高，以及自己的宽、高
 * 3、在onLayout中设置子布局的位置
 * 4、onTouchEvent响应用户触摸事件
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-5 上午11:05:32
 */
public class SlidingMenu extends HorizontalScrollView {

	private LinearLayout mWapper;// 整个内容区域
	private ViewGroup mMenu;// 菜单
	private ViewGroup mContent;// 内容区域

	private int mMenuWidth;// 菜单宽度

	private float mScreenWidth;// 屏幕宽度
	private float mMenuRightPadding = 50;// menu与屏幕右侧的距离

	private boolean hasLoading = false;// 判断是否有重新测量过屏幕宽高值设置
	private boolean isOpen = false;// 菜单是否已经显示

	private final float defaultScale = 0.8f;// 内容区域最后大小/菜单开始时大小
	private final float defaultAlpha = 0.6f;// 默认起始透明度

	public SlidingMenu(Context context) {
		this(context, null);
	}

	/**
	 * 未使用自定义属性的时候会默认调用这一个方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	private void myInit(Context context, AttributeSet attrs) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;

		// 把dp转换成px
		mMenuRightPadding = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mMenuRightPadding, context
						.getResources().getDisplayMetrics());

		// 取出自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.qq50);
		mMenuRightPadding = ta.getDimension(R.styleable.qq50_rightPadding,
				mMenuRightPadding);
		ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!hasLoading) {
			mWapper = (LinearLayout) getChildAt(0);

			// 设置菜单的宽度
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mMenuWidth = (int) (mScreenWidth - mMenuRightPadding);
			mMenu.getLayoutParams().width = mMenuWidth;

			// 设置content的宽度
			mContent = (ViewGroup) mWapper.getChildAt(1);
			mContent.getLayoutParams().width = (int) mScreenWidth;

			hasLoading = true;
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 通过设置偏移量将menu隐藏
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed) {
			// 将enu隐藏
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();// 获取滑动x的宽度
			if (scrollX >= mMenuWidth / 2) {// 隐藏菜单
				isOpen = false;

				this.smoothScrollTo(mMenuWidth, 0);
			} else {// 打开菜单
				isOpen = true;

				this.smoothScrollTo(0, 0);
			}

			Log.i(this.getClass().getSimpleName(), "显示菜单宽度：" + mMenuWidth);

			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 切换菜单是否显示
	 */
	public void toggleMenu() {
		if (isOpen) {
			this.smoothScrollTo(mMenuWidth, 0);
		} else {
			this.smoothScrollTo(0, 0);
		}
		isOpen = !isOpen;
	}

	/**
	 * 覆盖这个类实现抽屉式侧滑
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// 1~0 mMenu可见部分值的变化梯度
		float scale = l * 1.0f / mMenuWidth;

		// 调用属性动画，设置菜单的X轴偏移量 1.0~0.0
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);// 乘以0.7表示还有0.3的部分是隐藏的
		// 设置菜单缩放 0.7~1.0
		float scaleX = 1 - (1 - defaultScale) * scale;
		ViewHelper.setScaleX(mMenu, scaleX);
		ViewHelper.setScaleY(mMenu, scaleX);

		// 菜单透明度 0.6~1.0
		float alpha = defaultAlpha + (1 - defaultAlpha) * (1 - scale);
		ViewHelper.setAlpha(mMenu, alpha);

		// 设置mContent的缩放动画,大小从1.0~0.7
		scaleX = defaultScale + (1 - defaultScale) * scale;
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, scaleX);
		ViewHelper.setScaleY(mContent, scaleX);
	}
}
