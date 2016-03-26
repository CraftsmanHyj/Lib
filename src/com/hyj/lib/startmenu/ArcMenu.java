package com.hyj.lib.startmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.hyj.lib.R;

public class ArcMenu extends ViewGroup implements OnClickListener {
	// 菜单位置
	private final int POSITION_LEFT_TOP = 0;
	private final int POSITION_LEFT_BOTTOM = 1;
	private final int POSITION_RIGHT_TOP = 2;
	private final int POSITION_RIGHT_BOTTOM = 3;

	private int mPosition = POSITION_RIGHT_BOTTOM;
	private int mRadius;// 展开菜单半径

	private boolean mMenuItemHasOpened = false;// 菜单是否打开,默认否

	private View mCButton;// 菜单主按钮
	private onMenuItemClickListener mMenuItemClickListener;

	private int childCount = 0;// 该viewGroup中子view的个数

	public void setOnMenuItemClickListener(
			onMenuItemClickListener mMenuItemClickListener) {
		this.mMenuItemClickListener = mMenuItemClickListener;
	}

	public ArcMenu(Context context) {
		this(context, null);
	}

	public ArcMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	private void myInit(Context context, AttributeSet attrs) {
		initAttr(context, attrs);
		initView();
		initData();
		initListener();
	}

	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.arcmenu);

		mPosition = ta.getInt(R.styleable.arcmenu_position, mPosition);
		mRadius = (int) ta.getDimension(R.styleable.arcmenu_radius, mRadius);

		ta.recycle();
	}

	private void initView() {
	}

	private void initData() {
		mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				100, getResources().getDisplayMetrics());
	}

	private void initListener() {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		childCount = getChildCount();// 获取该控件下的所有子view
		for (int i = 0; i < childCount; i++) {
			// 测量child的宽高
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			layoutCButton();

			double degree = Math.PI / 2 / (childCount - 2);// // 两个菜单之间的圆心角

			// 除去主按钮mCButton
			for (int i = 0; i < childCount - 1; i++) {
				View child = getChildAt(i + 1);// 排除第0个主按钮

				child.setVisibility(View.GONE);

				// 逆时针从90°往0°算
				int cl = (int) (Math.sin(degree * i) * mRadius);// x轴坐标
				int ct = (int) (Math.cos(degree * i) * mRadius);// y轴坐标

				int cWidth = child.getMeasuredWidth();
				int cHeight = child.getMeasuredHeight();

				// 左下、右下时top位置不变
				if (POSITION_LEFT_BOTTOM == mPosition
						|| POSITION_RIGHT_BOTTOM == mPosition) {
					ct = getMeasuredHeight() - cHeight - ct;
				}

				// 右上、右下时left位置不变
				if (POSITION_RIGHT_TOP == mPosition
						|| POSITION_RIGHT_BOTTOM == mPosition) {
					cl = getMeasuredWidth() - cWidth - cl;
				}

				child.layout(cl, ct, cl + cWidth, ct + cHeight);// 绘制child
			}
		}
	}

	/**
	 * 定位主按钮位置
	 */
	private void layoutCButton() {
		mCButton = getChildAt(0);
		mCButton.setOnClickListener(this);

		int l = 0;// 左边坐标
		int t = 0;// 顶部坐标

		int width = mCButton.getMeasuredWidth();
		int height = mCButton.getMeasuredHeight();

		switch (mPosition) {
		case POSITION_LEFT_TOP:
			l = 0;
			t = 0;
			break;

		case POSITION_LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;

		case POSITION_RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;

		case POSITION_RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;
		}

		// 主按钮在布局中的位置
		mCButton.layout(l, t, l + width, t + height);
	}

	@Override
	public void onClick(View v) {
		rotateCButton(v, 0f, 360f, 300);
		toggleMenu(300);
	}

	/**
	 * 主按钮旋转角度
	 * 
	 * @param v
	 * @param fromDegrees
	 *            开始角度
	 * @param toDegrees
	 *            结束角度
	 * @param duration
	 *            持续时间
	 */
	private void rotateCButton(View v, float fromDegrees, float toDegrees,
			int duration) {
		RotateAnimation anim = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(duration);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}

	/**
	 * 为menuitem添加动画
	 * 
	 * @param duration
	 */
	private void toggleMenu(int duration) {
		double degree = Math.PI / 2 / (childCount - 2);// // 两个菜单之间的圆心角

		int xflag = 1;// x轴移动方向,默认是越来越大
		int yflag = 1;// y轴移动方向,默认是越来越大

		// 左上、左下x轴是变小移动
		if (POSITION_LEFT_TOP == mPosition || POSITION_LEFT_BOTTOM == mPosition) {
			xflag = -1;
		}

		// 左上、右上y轴是变小移动
		if (POSITION_LEFT_TOP == mPosition || POSITION_RIGHT_TOP == mPosition) {
			yflag = -1;
		}

		for (int i = 0; i < childCount - 1; i++) {
			final View child = getChildAt(i + 1);
			child.setVisibility(View.VISIBLE);

			int cl = (int) (mRadius * Math.sin(degree * i));
			int ct = (int) (mRadius * Math.cos(degree * i));

			AnimationSet animSet = new AnimationSet(true);

			// 平移动画
			Animation tranAnim = null;

			if (!mMenuItemHasOpened) {// to open
				// 子菜单开始是隐藏的，以隐藏位置为原点来计算移动的坐标
				tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
				child.setFocusable(true);
				child.setClickable(true);
			} else {// to close
				tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
				child.setFocusable(false);
				child.setClickable(false);
			}
			tranAnim.setFillAfter(true);
			tranAnim.setDuration(duration);
			tranAnim.setStartOffset(100 * i);// 设置延迟开始

			tranAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (!mMenuItemHasOpened) {
						child.setVisibility(View.GONE);
					}
				}
			});

			// 旋转动画
			RotateAnimation rotateAnim = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnim.setDuration(duration);
			rotateAnim.setFillAfter(true);

			// 这里需要先增加旋转动画然后再添加位移动画
			animSet.addAnimation(rotateAnim);
			animSet.addAnimation(tranAnim);
			child.startAnimation(animSet);

			final int pos = i + 1;// 当前点击的按钮索引
			// 设置item点击事件
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mMenuItemClickListener != null) {
						mMenuItemClickListener.onClick(v, pos);
					}

					menuItemAnim(pos - 1);
					changeSatus();
				}
			});
		}

		changeSatus();
	}

	private void changeSatus() {
		// 切换菜单状态
		mMenuItemHasOpened = !mMenuItemHasOpened;
	}

	private void menuItemAnim(int position) {
		for (int i = 0; i < childCount - 1; i++) {
			View child = getChildAt(i + 1);

			if (i == position) {
				child.startAnimation(scaleBigAnim(300));
			} else {
				child.startAnimation(scaleSmallAnim(300));
			}

			child.setClickable(false);
			child.setFocusable(false);
		}
	}

	/**
	 * 放大动画
	 * 
	 * @param duration
	 * @return
	 */
	private Animation scaleBigAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);

		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);

		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);

		return animSet;
	}

	/**
	 * 缩小动画
	 * 
	 * @param duration
	 * @return
	 */
	private Animation scaleSmallAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);

		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setDuration(duration);
		animSet.setFillAfter(true);
		return animSet;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mMenuItemHasOpened) {
			toggleMenu(300);
		}
		return super.onTouchEvent(event);
	}

	public interface onMenuItemClickListener {
		/**
		 * 子菜单点击事件
		 * 
		 * @param view
		 * @param position
		 */
		public void onClick(View view, int position);
	}
}
