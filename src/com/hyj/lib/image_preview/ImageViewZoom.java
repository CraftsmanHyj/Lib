package com.hyj.lib.image_preview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 * 图片预览控件，实现缩放，自由移动等
 * 
 * @author async
 * 
 */
public class ImageViewZoom extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {
	/**
	 * postDelayed延迟时间
	 */
	public final int POSTTIME = 16;

	private boolean isInit;// 是否已经初始化

	private float mMinScale;// 初始化时的缩放值
	private float mMidScale;// 双击放大到达的值
	private float mMaxScale;// 放大的最大值

	private Matrix mScaleMatrix;// 缩放矩阵

	/**
	 * 捕获用户多点触控时缩放的比例
	 */
	private ScaleGestureDetector mScaleGestureDetector;

	// ---------- 自由移动----------
	private int mLastPointerCount;// 记录上一次多点触控的数量

	// 多点触摸时中心点的位置
	private float mLastTouchX;
	private float mLastTouchY;

	private int mTouchSlop;// 用于做是否移动图片的比较值
	private boolean isCanDrag;// 是否可以移动图片

	private boolean isCheckLeftAndRight;// 是否检测左右
	private boolean isCheckTopAndBottom;// 是否检测上下

	// ---------- 双击放大、缩小----------
	private GestureDetector mGestureDetector;// 双击放大/缩小操作
	/**
	 * 是否正在执行双击放大/缩小操作
	 */
	private boolean isAutoScale;

	public ImageViewZoom(Context context) {
		this(context, null);
	}

	public ImageViewZoom(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageViewZoom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(ScaleType.MATRIX);

		myInit(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void myInit(Context context) {
		mScaleMatrix = new Matrix();
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale) {
							return true;
						}

						// 获取图片当前缩放值
						float scale = getScale();

						if (scale < mMidScale) {
							scale = mMidScale;
						} else {
							scale = mMinScale;
						}

						postDelayed(
								new AutoScaleRunnable(scale, e.getX(), e.getY()),
								POSTTIME);
						isAutoScale = true;

						return true;
					}
				});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		// 添加监听
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 全局的布局完成之后会调用这个方法<br/>
	 * 获取ImageView加载完成的图片
	 */
	@Override
	public void onGlobalLayout() {
		if (isInit) {
			return;
		}

		// 得到控件宽、高
		int width = getWidth();
		int height = getHeight();

		// 得到图片及宽、高
		Drawable drawable = getDrawable();
		if (null == drawable) {
			return;
		}

		int dw = drawable.getIntrinsicWidth();
		int dh = drawable.getIntrinsicHeight();

		float scale = 1.0f;
		float scaleWidth = width * 1.0f / dw;
		float scaleHeight = height * 1.0f / dh;

		// 图片宽度>控件宽&&图片高度<控件宽度，将其缩小
		if (dw > width && dh < height) {
			scale = scaleWidth;
		}

		// 图片宽度<控件宽&&图片高度>控件宽度，将其缩小
		if (dw < width && dh > height) {
			scale = scaleHeight;
		}

		if ((dw > width && dh > height) || (dw < width && dh < height)) {
			scale = Math.min(scaleWidth, scaleHeight);
		}

		/**
		 * 得到初始化时缩放的比例
		 */
		mMinScale = scale;
		mMidScale = mMinScale * 2;
		mMaxScale = mMinScale * 4;

		// 将图片移动至控件的中心
		int dx = width / 2 - dw / 2;// 移动的宽度
		int dy = height / 2 - dh / 2;// 移动的高度

		// 设置缩放、平移
		mScaleMatrix.postTranslate(dx, dy);
		mScaleMatrix.postScale(mMinScale, mMinScale, width / 2, height / 2);
		setImageMatrix(mScaleMatrix);

		isInit = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}

		// 将touchEvent传给ScaleGestruedetector处理
		mScaleGestureDetector.onTouchEvent(event);

		// 多点触控的中心点
		float x = 0;
		float y = 0;

		int pointerCount = event.getPointerCount();// 获取触控点的数量
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}

		x /= pointerCount;
		y /= pointerCount;

		if (mLastPointerCount != pointerCount) {
			isCanDrag = false;
			mLastTouchX = x;
			mLastTouchY = y;
		}

		mLastPointerCount = pointerCount;
		RectF rectF = getMatrixRectF();

		float width = getWidth() + 0.01f;// +0.01是防止部分手机出现异常
		float height = getHeight() + 0.01f;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 如果处于放大状态，让手指滑动事件不被ViewPager控件拦截
			if (rectF.width() > width || rectF.height() > height) {
				if (getParent() instanceof ViewPager) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (rectF.width() > width || rectF.height() > height) {
				if (getParent() instanceof ViewPager) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			}

			float dx = x - mLastTouchX;// 偏移量
			float dy = y - mLastTouchY;// 偏移量

			if (!isCanDrag) {
				isCanDrag = isMoveAction(dx, dy);
			}

			if (isCanDrag) {
				rectF = getMatrixRectF();
				if (null != getDrawable()) {
					isCheckLeftAndRight = isCheckTopAndBottom = true;

					// 宽度小于控件宽度，不允许横向移动
					if (rectF.width() < getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}

					// 如果高度小于控件高度，不允许纵向移动
					if (rectF.height() < getHeight()) {
						isCheckTopAndBottom = false;
						dy = 0;
					}

					mScaleMatrix.postTranslate(dx, dy);
					checkBoderInTranslate();
					setImageMatrix(mScaleMatrix);
				}
			}

			mLastTouchX = x;
			mLastTouchY = y;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastPointerCount = 0;
			break;
		}

		return true;// 表示已经消费了该这个事件，不再往下传递
	}

	/**
	 * 移动时，边界检查
	 */
	private void checkBoderInTranslate() {
		RectF rectF = getMatrixRectF();

		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		if (isCheckTopAndBottom && rectF.top > 0) {
			deltaY = -rectF.top;
		}

		if (isCheckTopAndBottom && rectF.bottom < height) {
			deltaY = height - rectF.bottom;
		}

		if (isCheckLeftAndRight && rectF.left > 0) {
			deltaX = -rectF.left;
		}

		if (isCheckLeftAndRight && rectF.right < width) {
			deltaX = width - rectF.right;
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 是否足以触发Move动作
	 * 
	 * @param dx
	 *            x轴偏移量
	 * @param dy
	 *            y轴偏移量
	 * @return
	 */
	private boolean isMoveAction(float dx, float dy) {
		return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
	}

	/**
	 * 获取当前图片的缩放值
	 * 
	 * @return float
	 */
	private float getScale() {
		float[] values = new float[9];
		mScaleMatrix.getValues(values);
		// x、y的缩放比例是一致的，可以任意取一个
		return values[Matrix.MSCALE_X];
	}

	/**
	 * 获得图片放大/缩小以后的宽、高及t、r、b、l
	 * 
	 * @return RectF
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();

		Drawable d = getDrawable();
		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF);// 这样我们就可以得到缩放后的图片宽、高
		}
		return rectF;
	}

	/**
	 * 在缩放的时候进行边界控制及位置控制,让图片一致都在屏幕可见范围
	 */
	private void checkBoderAndCenterInScale() {
		RectF rectF = getMatrixRectF();

		// 图片移动的距离
		float deltaX = 0;
		float deltaY = 0;

		// 得到控件的宽、高
		int width = getWidth();
		int height = getHeight();

		// 缩放时进行边界检查，防止出现白边
		if (rectF.width() >= width) {
			if (rectF.left > 0) {// 表示图片左边与屏幕有空隙
				deltaX = -rectF.left;
			}

			if (rectF.right < width) {// 表示图片右边与屏幕有空隙
				deltaX = width - rectF.right;
			}
		}

		if (rectF.height() >= height) {
			if (rectF.top > 0) {// 表示图片上面与屏幕有空隙
				deltaY = -rectF.top;
			}

			if (rectF.bottom < height) {// 表示图片下面与屏幕有空隙
				deltaY = height - rectF.bottom;
			}
		}

		// 如果宽/高小于控件的宽/高；则让它居中
		if (rectF.width() < width) {
			deltaX = width / 2f - rectF.right + rectF.width() / 2f;
		}
		if (rectF.height() < height) {
			deltaY = height / 2f - rectF.bottom + rectF.height() / 2f;
		}

		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	// 缩放区间:initScale~maxScale
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();
		if (null == getDrawable()) {
			return true;
		}

		// 缩放范围的控制
		if ((scale < mMaxScale && scaleFactor > 1.0f)
				|| (scale > mMinScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mMinScale) {
				scaleFactor = mMinScale / scale;
			}

			if (scale * scaleFactor > mMaxScale) {
				scaleFactor = mMaxScale / scale;
			}

			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());
			checkBoderAndCenterInScale();
			setImageMatrix(mScaleMatrix);
		}

		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	/**
	 * 使图片缓慢放大/缩小 postDelay+Runnabel实现这个效果
	 * 
	 * @Author hyj
	 * @Date 2015-12-12 下午2:24:31
	 */
	private class AutoScaleRunnable implements Runnable {
		/**
		 * 缩放目标值
		 */
		private float mTargetScale;
		// 缩放中心点
		private float px;
		private float py;

		private final float BIGGER = 1.07f;// 放大梯度
		private final float SMALL = 0.93f;// 缩小梯度

		private float tempScale;

		public AutoScaleRunnable(float mTargetScale, float px, float py) {
			this.mTargetScale = mTargetScale;
			this.px = px;
			this.py = py;

			if (getScale() < mTargetScale) {
				tempScale = BIGGER;
			} else if (getScale() > mTargetScale) {
				tempScale = SMALL;
			}
		}

		@Override
		public void run() {
			// 进行缩放
			mScaleMatrix.postScale(tempScale, tempScale, px, py);
			checkBoderAndCenterInScale();
			setImageMatrix(mScaleMatrix);

			float currentScale = getScale();
			if ((tempScale > 1.0f && currentScale < mTargetScale)
					|| (tempScale < 1.0f && currentScale > mTargetScale)) {
				postDelayed(this, POSTTIME);
			} else {// 设置为目标值
				float scale = mTargetScale / currentScale;
				mScaleMatrix.postScale(scale, scale, px, py);
				checkBoderAndCenterInScale();
				setImageMatrix(mScaleMatrix);

				isAutoScale = false;
			}
		}
	}
}
