package com.hyj.lib.indicator;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyj.lib.R;

public class ViewPagerIndicator extends HorizontalScrollView {
	private final int COLOR_TEXT_NORMAL = 0x77FFFFFF;// Tab正常颜色
	private final int COLOR_TEXT_HIGHLIGHT = 0xFFFFFFFF;// Tab高亮颜色

	// 三角形的底边宽是Tab宽度的1/6
	private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
	// 三角形底边的最大宽度
	private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
	// 三角形底边最小宽度
	private final int DIMENSION_TRIANGLE_WIDTH_MIN = (int) (getScreenWidth() / 5 * RADIO_TRIANGLE_WIDTH);

	private Paint paint;// 画三角形的画笔
	private Path path;// 构造三角形路径
	private int triangleWidth;// 三角形宽
	private int triangleHeight;// 三角形高
	private LinearLayout llIndicator;// TAB容器

	private int initTranslationX;// 起始偏移量
	private int translationX;// 移动偏移量

	private int visibleTabCount = 4;// 可见的Tab数，默认可见数=4

	private ViewPager viewPager;
	public OnIndicatorPageChangeListener listener;

	/**
	 * 设置可见tab的个数
	 */
	public void setVisibleTabCount(int count) {
		this.visibleTabCount = count;
	}

	/**
	 * 设置TAB切换时，触发的ViewPager的事件
	 * 
	 * @param listener
	 */
	public void setOnIndicatorPageChangeListener(
			OnIndicatorPageChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * 设置Tab显示的值
	 * 
	 * @param titls
	 */
	public void setTabItemTitles(List<String> titls) {
		if (null != titls && titls.size() > 0) {
			llIndicator.removeAllViews();

			for (String title : titls) {
				TextView tv = new TextView(getContext());
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				lp.width = getScreenWidth() / visibleTabCount;// TAB的宽度
				tv.setText(title);
				tv.setTextColor(COLOR_TEXT_NORMAL);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				tv.setGravity(Gravity.CENTER);
				tv.setLayoutParams(lp);

				llIndicator.addView(tv);
			}
			setItemClickEvent();
		}
	}

	/**
	 * 设置关联的ViewPager
	 * 
	 * @param viewPager
	 * @param position
	 */
	@SuppressWarnings("deprecation")
	public void setViewPager(ViewPager viewPager, int position) {
		this.viewPager = viewPager;

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				highLightTextView(position);

				if (null != listener) {
					listener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// tabWidth*positionOffset+tabWidth*position
				scroll(position, positionOffset);

				if (null != listener) {
					listener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (null != listener) {
					listener.onPageScrollStateChanged(state);
				}
			}
		});

		viewPager.setCurrentItem(position);
		highLightTextView(position);
	}

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	private void myInit(Context context, AttributeSet attrs) {
		initAttrs(context, attrs);
		initView();
		initData();
	}

	/**
	 * 获取设置的属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.indicator);

		int visibleNum = ta.getInt(R.styleable.indicator_visibletablecount,
				visibleTabCount);
		if (visibleNum < 0) {
			visibleNum = visibleTabCount;
		}
		setVisibleTabCount(visibleNum);

		ta.recycle();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		llIndicator = new LinearLayout(getContext());
		llIndicator.setOrientation(LinearLayout.HORIZONTAL);
		addView(llIndicator);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.parseColor("#ffffffff"));
		paint.setPathEffect(new CornerPathEffect(3));// 设置连接线处为圆角效果
	}

	/**
	 * 用于绘制子控件
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();

		canvas.translate(initTranslationX + translationX, getHeight() + 2);
		canvas.drawPath(path, paint);

		canvas.restore();
		super.dispatchDraw(canvas);
	}

	/**
	 * 当控件宽高变化的时候就会回调这个方法
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int tabWidth = w / visibleTabCount;

		triangleWidth = (int) (tabWidth * RADIO_TRIANGLE_WIDTH);
		triangleWidth = Math.min(triangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
		triangleWidth = Math.max(triangleWidth, DIMENSION_TRIANGLE_WIDTH_MIN);
		initTranslationX = tabWidth / 2 - triangleWidth / 2;

		// 初始化三角形
		triangleHeight = triangleWidth / 2;
		path = new Path();
		path.moveTo(0, 0);
		path.lineTo(triangleWidth, 0);
		path.lineTo(triangleWidth / 2, -triangleHeight);
		path.close();// 路径闭合
	}

	/**
	 * 当XML加载完成之后会回调这个方法
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		int cCount = llIndicator.getChildCount();
		if (0 == cCount) {
			return;
		}

		for (int i = 0; i < cCount; i++) {
			View view = llIndicator.getChildAt(i);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view
					.getLayoutParams();
			lp.weight = 0;
			lp.width = getScreenWidth() / visibleTabCount;
			view.setLayoutParams(lp);
		}

		setItemClickEvent();
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @return
	 */
	private int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);

		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 指示器跟随手指进行滚动
	 * 
	 * @param position
	 * @param Offset
	 */
	private void scroll(int position, float Offset) {
		// 每个TAB的宽度
		int tabWidth = getWidth() / visibleTabCount;
		translationX = (int) (tabWidth * (position + Offset));

		/**
		 * <pre>
		 * TAB移动
		 * 当现实为倒数第二个TAB的时候，整个容器不需要移动
		 * 只有当未显示的TAB数>=3的时候才移动
		 * </pre>
		 */
		int cCount = llIndicator.getChildCount();
		if (cCount > visibleTabCount && Offset > 0
				&& position >= (visibleTabCount - 2) && position < cCount - 2) {

			int x = (int) ((position - (visibleTabCount - 2)) * tabWidth + tabWidth
					* Offset);

			if (visibleTabCount == 1) {
				x = (int) (position * tabWidth + tabWidth * Offset);
			}

			// 让容器移动
			this.scrollTo(x, 0);
		}

		invalidate();
	}

	/**
	 * 重置TAB文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0, cCount = llIndicator.getChildCount(); i < cCount; i++) {
			View view = llIndicator.getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}

	/**
	 * 高亮某个Tab的文本
	 * 
	 * @param pos
	 */
	private void highLightTextView(int pos) {
		resetTextViewColor();

		View view = llIndicator.getChildAt(pos);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
		}
	}

	/**
	 * 设置TAB的点击事件
	 */
	private void setItemClickEvent() {
		for (int i = 0, cCount = llIndicator.getChildCount(); i < cCount; i++) {
			final int j = i;
			View view = llIndicator.getChildAt(i);
			if (view instanceof TextView) {
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(j);
					}
				});
			}
		}
	}

	/**
	 * <pre>
	 * ViewPager滑动事件
	 * </pre>
	 * 
	 * @author hyj
	 * @Date 2016-3-14 下午2:54:29
	 */
	public interface OnIndicatorPageChangeListener {
		public void onPageSelected(int position);

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageScrollStateChanged(int state);
	}
}
