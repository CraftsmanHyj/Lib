package com.hyj.lib.listviewindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * 带索引条的ListView
 * 
 * @Author hyj
 * @Date 2015-12-17 下午10:14:31
 */
public class ListViewIndex extends ListView {
	private static final int STATE_HIDDEN = 0;// 已隐藏
	private static final int STATE_SHOWING = 1;// 正在显示
	private static final int STATE_SHOWN = 2;// 已显示
	private static final int STATE_HIDING = 3;// 正在隐藏

	private int curState = STATE_HIDDEN; // 状态

	private float indexBarWidth; // 索引条宽度
	private float indexBarMargin; // 索引条外边距
	private float indexBarPadding; // 索引条字体到边框的间距
	private float indexBarRound;// 索引条圆角角度
	private float textSizeDialog;// dialog框上的字体大小
	private float textSizeIndexBar;// 索引条上的字体大小

	private float alphaRate; // 透明度
	private int lvWidth; // ListView宽度
	private int lvHeight; // ListView高度

	private int curSectionsIndex = -1; // 当前部分
	private boolean isIndexing = false; // 是否正在索引

	private SectionIndexer indexerAdapter = null;
	private String[] mSections = null;
	private RectF indexBarRect;

	private GestureDetector mGestureDetector = null;

	private Paint paintIndexBar;// 索引条画笔
	private Paint paintIndexBarText;// 绘制索引条文字
	private Paint paintPreview;// 预览Dialog背景画笔
	private Paint paintPreviewText;// 预览Dialog文字画笔

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (curState) {
			case STATE_SHOWING:// 淡进效果
				alphaRate += (1 - alphaRate) * 0.2;
				if (alphaRate > 0.9) {
					alphaRate = 1;
					setState(STATE_SHOWN);
				}

				invalidate();
				fade(10);
				break;

			case STATE_SHOWN:
				setState(STATE_HIDING);
				break;

			case STATE_HIDING:// 淡出效果
				alphaRate -= alphaRate * 0.2;
				if (alphaRate < 0.1) {
					alphaRate = 0;
					setState(STATE_HIDDEN);
				}

				invalidate();
				fade(10);
				break;
			}
		}
	};

	public ListViewIndex(Context context) {
		this(context, null);
	}

	public ListViewIndex(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ListViewIndex(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit();
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		if (!enabled) {
			hide();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (curState == STATE_HIDDEN) {
			return;
		}

		// 动态改变索引条透明度
		paintIndexBar.setAlpha((int) (64 * alphaRate));
		// 画右侧字母索引的圆矩形
		canvas.drawRoundRect(indexBarRect, indexBarRound, indexBarRound,
				paintIndexBar);

		if (null == mSections && mSections.length <= 0) {
			return;
		}

		// 绘制预览Dialog
		if (curSectionsIndex >= 0) {
			// 文本的宽度
			float previewTextWidth = paintPreviewText
					.measureText(mSections[curSectionsIndex]);

			// 基准点：baseline;
			// ascent：是baseline之上至字符最高处的距离,取得的值为负数
			// descent：是baseline之下至字符最低处的距离
			float previewSize = 2 * indexBarPadding
					+ paintPreviewText.descent() - paintPreviewText.ascent();
			float left = (lvWidth - previewSize) / 2;
			float top = (lvHeight - previewSize) / 2;
			RectF previewRect = new RectF(left, top, left + previewSize, top
					+ previewSize);

			// 绘制预览Dialog
			canvas.drawRoundRect(previewRect, indexBarRound, indexBarRound,
					paintPreview);
			// 绘制预览Dialog中的文字
			canvas.drawText(mSections[curSectionsIndex], previewRect.left
					+ (previewSize - previewTextWidth) / 2 - 1, previewRect.top
					+ indexBarPadding - paintPreviewText.ascent() + 1,
					paintPreviewText);
		}

		// 绘画右侧索引条的字母
		paintIndexBarText.setAlpha((int) (255 * alphaRate));

		float sectionHeight = (indexBarRect.height() - indexBarMargin * 2)
				/ mSections.length;
		float paddingTop = (sectionHeight - (paintIndexBarText.descent() - paintIndexBarText
				.ascent())) / 2;
		for (int i = 0; i < mSections.length; i++) {
			float paddingLeft = (indexBarWidth - paintIndexBarText
					.measureText(mSections[i])) / 2;
			canvas.drawText(mSections[i], indexBarRect.left + paddingLeft,
					indexBarRect.top + indexBarMargin + sectionHeight * i
							+ paddingTop - paintIndexBarText.ascent(),
					paintIndexBarText);
		}
	}

	private void myInit() {
		initView();
		initDatas();
		initPaint();
	}

	private void initView() {
		// 创建一个GestureDetector（手势探测器）
		mGestureDetector = new GestureDetector(getContext(),
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						show();
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	private void initDatas() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		indexBarWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				20, metrics);
		indexBarMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, metrics);
		indexBarPadding = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
		indexBarRound = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, metrics);
		textSizeDialog = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				50, metrics);
		textSizeIndexBar = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 12, metrics);
	}

	private void initPaint() {
		// IndexBar画笔
		paintIndexBar = new Paint();
		paintIndexBar.setAntiAlias(true);
		paintIndexBar.setColor(Color.BLACK);

		paintIndexBarText = new Paint();
		paintIndexBarText.setColor(Color.WHITE);
		paintIndexBarText.setAntiAlias(true);
		paintIndexBarText.setTextSize(textSizeIndexBar);

		// 用来绘画预览Dialog背景的画笔
		paintPreview = new Paint();
		paintPreview.setColor(Color.BLACK);// 设置画笔颜色为黑色
		paintPreview.setAlpha(96); // 设置透明度
		paintPreview.setAntiAlias(true);// 设置抗锯齿
		paintPreview.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0)); // 设置阴影层

		// 预览Dialog字体画笔
		paintPreviewText = new Paint(); // 用来绘画索引字母的画笔
		paintPreviewText.setColor(Color.WHITE); // 设置画笔为白色
		paintPreviewText.setAntiAlias(true); // 设置抗锯齿
		paintPreviewText.setTextSize(textSizeDialog); // 设置字体大小
	}

	private void show() {
		if (curState == STATE_HIDDEN) {
			setState(STATE_SHOWING);
		} else if (curState == STATE_HIDING) {
			setState(STATE_HIDING);
		}
	}

	private void hide() {
		if (curState == STATE_SHOWN) {
			setState(STATE_HIDING);
		}
	}

	/**
	 * 设置IndexBar当前状态
	 * 
	 * @param state
	 */
	private void setState(int state) {
		curState = state;

		switch (curState) {
		case STATE_HIDDEN:
			// 取消渐退的效果
			mHandler.removeMessages(0);
			break;

		case STATE_SHOWING:
			// 开始渐进效果
			alphaRate = 0;
			fade(0);
			break;

		case STATE_SHOWN:
			// 取消渐退的效果
			mHandler.removeMessages(0);
			break;
		case STATE_HIDING:
			// 隐藏3秒钟
			alphaRate = 1;
			fade(3000);
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (STATE_HIDDEN != curState) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN: // 按下，开始索引
				if (contains(ev.getX(), ev.getY())) {
					setState(STATE_SHOWN);

					isIndexing = true;
					curSectionsIndex = getSectionByPoint(ev.getY());
					setSelection(indexerAdapter
							.getPositionForSection(curSectionsIndex));
					return true;
				}
				break;

			case MotionEvent.ACTION_MOVE: // 移动
				if (isIndexing) {
					if (contains(ev.getX(), ev.getY())) {
						curSectionsIndex = getSectionByPoint(ev.getY());
						setSelection(indexerAdapter
								.getPositionForSection(curSectionsIndex));
					}
					return true;
				}
				break;

			case MotionEvent.ACTION_UP: // 抬起
				if (isIndexing) {
					isIndexing = false;
					curSectionsIndex = -1;
				}

				if (curState == STATE_SHOWN) {
					setState(STATE_HIDING);
				}
				break;
			}
		}

		mGestureDetector.onTouchEvent(ev);

		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (contains(ev.getX(), ev.getY())) {
			return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);

		if (adapter instanceof SectionIndexer) {
			indexerAdapter = (SectionIndexer) adapter;
			mSections = (String[]) indexerAdapter.getSections();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		lvWidth = w;
		lvHeight = h;
		indexBarRect = new RectF(w - indexBarMargin - indexBarWidth,
				indexBarMargin, w - indexBarMargin, h - indexBarMargin);
	}

	/**
	 * 验证手指是否在索引条上
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean contains(float x, float y) {
		return (x >= indexBarRect.left && y >= indexBarRect.top && y <= indexBarRect.top
				+ indexBarRect.height());
	}

	/**
	 * 获取sections中字母的索引
	 * 
	 * @param y
	 * @return
	 */
	private int getSectionByPoint(float y) {
		if (mSections == null || mSections.length == 0)
			return 0;

		if (y < indexBarRect.top + indexBarMargin)
			return 0;

		if (y >= indexBarRect.top + indexBarRect.height() - indexBarMargin)
			return mSections.length - 1;

		return (int) ((y - indexBarRect.top - indexBarMargin) / ((indexBarRect
				.height() - indexBarMargin * 2) / mSections.length));
	}

	/**
	 * 渐变方法
	 * 
	 * @param delay
	 */
	private void fade(long delay) {
		mHandler.removeMessages(0);
		// SystemClock.uptimeMillis()：从开机到现在的毫秒数
		mHandler.sendEmptyMessageDelayed(0, delay);
	}
}
