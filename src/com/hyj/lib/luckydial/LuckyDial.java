package com.hyj.lib.luckydial;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.hyj.lib.R;

/**
 * 幸运轮盘
 * 
 * @Author hyj
 * @Date 2015-12-23 下午9:04:06
 */
public class LuckyDial extends SurfaceView implements Callback, Runnable {
	private final Bitmap bitmapBg = getBitmap(R.drawable.bg2);// 转盘背景
	private final Bitmap bitmapStart = getBitmap(R.drawable.start);// 开始按钮图片
	private final Bitmap bitmapStop = getBitmap(R.drawable.stop);// 停止按钮图片
	private final float mTextSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

	private SurfaceHolder holder;
	private Canvas canvas;

	private Thread thread;// 用于绘制的线程
	private boolean isRunning;// 线程控制开关

	private List<PrizeInfo> lPrizeInfo;// 中奖信息
	private Bitmap[] mImgsBitmap;// 与图片对应的Bitmap数组
	private int[] mColor;
	private int prizeCount;// 一共有几个奖项

	private RectF mRange;// 盘块的范围
	private int mRadius;// 盘块的直径

	private Rect rectBtn;// 按钮的范围

	private Paint mArcPaint;// 绘制盘块画笔
	private Paint mTextPaint;// 绘制文本画笔

	private double mSpeed;// 滚动速度
	// volatile声明可以保证多个线程之间的可见性
	private volatile float mStartAngle = 0;// 旋转起始角度
	private boolean isStopping;// 是否点击了停止按钮
	private int mCenter;// 转盘中心位置
	private int mPadding;

	private PrizeInfo prize = new PrizeInfo();// 获奖信息
	private onRotationDownListener mListener;// 旋转结束监听

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (null != mListener) {
				mListener.onRotationDown(prize);
			}
		};
	};

	/**
	 * 是否点击了停止按钮正在停止
	 * 
	 * @return
	 */
	public boolean isStopping() {
		return isStopping;
	}

	/**
	 * 转盘是否在旋转
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return mSpeed != 0;
	}

	/**
	 * 设置获奖显示信息
	 * 
	 * @param lPrizeInfo
	 */
	public void setPrizeInfo(List<PrizeInfo> lPrizeInfo) {
		this.lPrizeInfo = lPrizeInfo;

		initDatas();
	}

	/**
	 * 设置旋转结束监听
	 * 
	 * @param lisetner
	 */
	public void setonRotationDownListener(onRotationDownListener lisetner) {
		this.mListener = lisetner;
	}

	public LuckyDial(Context context) {
		this(context, null);
	}

	public LuckyDial(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LuckyDial(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit();
	}

	private void myInit() {
		initView();
		initDatas();
	}

	private void initView() {
		holder = getHolder();
		holder.addCallback(this);

		setFocusable(true);// 可获得焦点
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);// 设置常量
	}

	private void initDatas() {
		if (null == lPrizeInfo || lPrizeInfo.size() <= 0) {
			lPrizeInfo = new ArrayList<PrizeInfo>();
			lPrizeInfo.add(new PrizeInfo(R.drawable.ic_launcher, "test1"));
			lPrizeInfo.add(new PrizeInfo(R.drawable.ic_launcher, "test2"));
			lPrizeInfo.add(new PrizeInfo(R.drawable.ic_launcher, "test3"));
			lPrizeInfo.add(new PrizeInfo(R.drawable.ic_launcher, "test4"));
		}

		lPrizeInfo = PrizeInfo.setPrizePercent(lPrizeInfo);

		prizeCount = lPrizeInfo.size();
		mColor = new int[prizeCount];
		mImgsBitmap = new Bitmap[prizeCount];

		for (int i = 0; i < lPrizeInfo.size(); i++) {
			mColor[i] = i % 2 == 0 ? 0xFFFFC300 : 0xFFF17E01;
			mImgsBitmap[i] = getBitmap(lPrizeInfo.get(i).getImgId());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

		mPadding = getPaddingLeft();
		mRadius = width - mPadding * 2;
		// 中心点
		mCenter = width / 2;

		setMeasuredDimension(width, width);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 初始化绘制盘块的画笔
		mArcPaint = new Paint();
		// 抗边缘锯齿
		mArcPaint.setAntiAlias(true);
		// 绘制图像时抗抖动，使颜色更加平滑、饱满
		mArcPaint.setDither(true);

		// 绘制文本的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xFFFFFFFF);
		mTextPaint.setTextSize(mTextSize);

		// 初始化盘块绘制的范围
		mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding
				+ mRadius);

		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		// 不断进行绘制
		while (isRunning) {
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();

			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		try {
			canvas = holder.lockCanvas();
			if (null == canvas) {
				return;
			}

			// 绘制背景
			drawBg();
			// 绘制盘块
			float tmpAngle = mStartAngle;
			float sweepAngle = 360 / prizeCount;

			for (int i = 0; i < prizeCount; i++) {
				PrizeInfo prize = lPrizeInfo.get(i);

				mArcPaint.setColor(mColor[i]);
				// 绘制盘块，扇形区域
				canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

				// 绘制文本
				drawText(tmpAngle, sweepAngle, prize.getLabel());

				// 绘制Icon
				drawIcon(tmpAngle, mImgsBitmap[i]);

				tmpAngle += sweepAngle;
			}
			drawButton();

			mStartAngle += mSpeed;
			// 如果点击了停止按钮
			if (isStopping) {
				mSpeed -= 1;
			}

			// 转盘将要停止
			if (mSpeed > 0 && mSpeed <= 1) {
				handler.sendEmptyMessage(0);
			}

			if (mSpeed <= 0) {
				mSpeed = 0;
				isStopping = false;
			}
		} finally {
			if (null != canvas) {
				// 释放canvas
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	/**
	 * 启动转盘
	 */
	public void luckyStart() {
		int index = PrizeInfo.getPrizeIndex(lPrizeInfo);
		luckyStart(index);
	}

	/**
	 * 启动转盘
	 * 
	 * @param index
	 *            指定中奖序号
	 */
	public void luckyStart(int index) {
		prize = lPrizeInfo.get(index);// 中奖信息

		// 计算每一项的一个角度
		float angle = 360 / prizeCount;

		// 计算每一项中奖范围(当前index范围)
		float from = 270 - (index + 1) * angle + 3;// +3°防止指针落在边界看不清
		float end = from + angle - 4;// -4°防止指针落在边界看不清

		// 设置停下来需要旋转的距离
		float targetFrom = 4 * 360 + from;
		float targetEnd = 4 * 360 + end;

		/**
		 * <pre>
		 * v1→0
		 * 且每次-1
		 * 等差数列求和公式：(v1+0)*(v1+1)/2=targetFrom
		 * v1*v1+v1-2*targetFrom=0;
		 * v1=(-1+Math.Sqrt(1-4*(-2)*targetFrom))/2
		 * </pre>
		 */

		float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
		float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

		mSpeed = v1 + Math.random() * (v2 - v1);
		isStopping = false;
	}

	/**
	 * 停止转动
	 */
	public void luckyStop() {
		mStartAngle = 0;
		isStopping = true;
	}

	private void drawButton() {
		Bitmap bitmap = bitmapStart;
		if (!isRunning()) {
			bitmap = bitmapStart;
		} else if (!isStopping()) {
			bitmap = bitmapStop;
		}

		int left = (getWidth() - bitmap.getWidth()) / 2;
		int top = (getHeight() - bitmap.getHeight()) / 2;
		int right = left + bitmap.getWidth();
		int bottom = top + bitmap.getHeight();
		rectBtn = new Rect(left, top, right, bottom);

		canvas.drawBitmap(bitmap, null, rectBtn, null);
	}

	/**
	 * 绘制Icon
	 * 
	 * @param tmpAngle
	 * @param bitmap
	 */
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		// 设置图片的宽度为mRadius/8
		int imgWidth = mRadius / 8;

		// Math.PI/180=1° 每度所占弧度大小
		float angle = (float) ((tmpAngle + 360 / prizeCount / 2) * Math.PI / 180);

		int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
		int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

		// 确定图片位置
		Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
				/ 2, y + imgWidth / 2);
		canvas.drawBitmap(bitmap, null, rect, null);
	}

	/**
	 * 绘制每个盘块的文本
	 * 
	 * @param tempAngle
	 * @param sweepAngle
	 * @param string
	 */
	private void drawText(float tempAngle, float sweepAngle, String string) {
		Path path = new Path();
		// 为绘制路劲添加弧线路劲
		path.addArc(mRange, tempAngle, sweepAngle);

		// 利用水平偏移量让文字居中
		// 弧长=弧度*半径=mRadius*Math.PI/itemCount
		float textWidth = mTextPaint.measureText(string);
		int hOffset = (int) ((mRadius * Math.PI / prizeCount) / 2 - textWidth / 2);

		int vOffset = mRadius / 2 / 6;// 垂直偏移量，半径/6
		canvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}

	/**
	 * 绘制背景
	 */
	private void drawBg() {
		canvas.drawColor(0xFFFFFFFF);
		canvas.drawBitmap(bitmapBg, null, new Rect(mPadding / 2, mPadding / 2,
				getMeasuredWidth() - mPadding / 2, getMeasuredHeight()
						- mPadding / 2), null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if ((x >= rectBtn.left && x <= rectBtn.right)
				&& (y <= rectBtn.bottom && y >= rectBtn.top)) {
			if (!isRunning()) {
				luckyStart();
			} else if (!isStopping()) {
				luckyStop();
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 获取图片bitmap对象
	 * 
	 * @param imgId
	 *            图片ID
	 * @return Bitmap
	 */
	private Bitmap getBitmap(int imgId) {
		return BitmapFactory.decodeResource(getResources(), imgId);
	}

	/**
	 * 抽奖旋转结束
	 * 
	 * @Author hyj
	 * @Date 2015-12-28 下午3:29:33
	 */
	public interface onRotationDownListener {
		/**
		 * 抽奖旋转介绍
		 * 
		 * @param prize
		 */
		public void onRotationDown(PrizeInfo prize);
	}
}
