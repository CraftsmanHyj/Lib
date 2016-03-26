package com.hyj.lib.scratch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hyj.lib.R;

/**
 * 刮刮卡控件
 * 
 * @Author hyj
 * @Date 2015-12-13 下午10:19:10
 */
@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
public class ScratchCard extends View {
	// ----------遮盖层变量----------
	private Paint paintOutter;
	private Path mPath;// 记录用户绘制路径
	private Canvas mCanvas;// 画布
	private Bitmap mBitmap;// 在此图片上绘图

	// 记录手指坐标
	private int mLastX;
	private int mLastY;

	private Bitmap mOutterBitmap;

	// ----------底层变量----------
	private Bitmap bitmap;// 底层图片

	private String textPrize = "谢谢惠顾";// 中奖信息
	private int textSize = 30;// 文字大小
	private int textColor = Color.BLACK;// 文字颜色
	private Rect textBound;// 记录刮奖信息文本的宽、高

	private Paint paintBack;

	// ---------- ----------
	private int mWidth;// 控件宽度
	private int mHeight;// 控件高度

	// volatile此属性可以保证如果属性在子线程里面更新，则在主线程里面是不可见的
	private volatile boolean isComplete;// 判断消除区域是否达到预值

	private OnScratchCompleteListener mListener;

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			int w = getWidth();
			int h = getHeight();

			float wipeArea = 0;// 擦除的面积
			float totalArea = w * h;// 总面积

			Bitmap bitmap = mBitmap;// 得到计算的bitmap
			int[] mPixels = new int[w * h];

			// 获得bitmap上所有的像素信息
			bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int index = i + j * w;
					if (mPixels[index] == 0) {
						wipeArea++;
					}
				}
			}

			if (wipeArea > 0 && totalArea > 0) {
				int percent = (int) (wipeArea * 100f / totalArea);
				if (percent >= 60) {
					// 清除掉图层区域
					isComplete = true;
					postInvalidate();// 重绘区域
				}
			}
		}
	};

	/**
	 * 设置刮卡之后显示的文字
	 * 
	 * @param prize
	 *            中奖信息
	 */
	public void setPrize(String prize) {
		this.textPrize = prize;

		// 需要重新测算绘制文本的宽、高
		paintBack.getTextBounds(prize, 0, textPrize.length(), textBound);
	}

	/**
	 * 设置全部刮完之后所做的操作
	 * 
	 * @param mListener
	 */
	public void setOnScratchCompleteListener(OnScratchCompleteListener mListener) {
		this.mListener = mListener;
	}

	public ScratchCard(Context context) {
		this(context, null);
	}

	public ScratchCard(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScratchCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制背景图片
		canvas.drawBitmap(bitmap, null,
				new Rect(0, 0, getWidth(), getHeight()), null);

		int x = getWidth() / 2 - textBound.width() / 2;
		int y = getHeight() / 2 + textBound.height() / 2;
		canvas.drawText(textPrize, x, y, paintBack);

		if (!isComplete) {
			drawPath();
			// 类似于在内存中准备好我们的图，然后通过canvas画出来，类似刷缓冲的感觉
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}

		if (isComplete && null != mListener) {
			mListener.onScratchComplete();
		}
	}

	/**
	 * 绘制手势路径
	 */
	private void drawPath() {
		paintOutter.setStyle(Style.STROKE);
		// 绘制画笔绘图时两个图片的叠加模式
		paintOutter.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		// 使用内存中的canvas绘制path到内存中的mbitmap上
		mCanvas.drawPath(mPath, paintOutter);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();

		// 初始化bitmap
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		// 设置画笔的一些属性
		setupOutPaint();
		// 设置中奖信息画笔
		setupBackPaint();
		// 设置刮刮卡、灰色背景，要被手势清除的图片
		setForeground();
	}

	/**
	 * 设置绘制path画笔的一些属性
	 */
	private void setupOutPaint() {
		paintOutter = new Paint();
		paintOutter.setColor(Color.parseColor("#c0c0c0"));
		paintOutter.setAntiAlias(true);// 抗锯齿
		paintOutter.setDither(true);//
		paintOutter.setStrokeJoin(Paint.Join.ROUND);// 设置线条圆角
		paintOutter.setStrokeCap(Paint.Cap.ROUND);
		paintOutter.setStyle(Style.FILL);
		paintOutter.setStrokeWidth(30);// 设置画笔宽度
	}

	/**
	 * 设置中奖信息画笔属性
	 */
	private void setupBackPaint() {
		paintBack.setColor(textColor);
		paintBack.setAntiAlias(true);
		paintBack.setStyle(Style.FILL);
		paintBack.setTextSize(textSize);

		// 测算绘制文本的宽、高
		paintBack.getTextBounds(textPrize, 0, textPrize.length(), textBound);
	}

	/**
	 * 初始化前景图片
	 * 
	 * @param width
	 * @param height
	 */
	private void setForeground() {
		// 绘制圆角的图片
		mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 30, 30,
				paintOutter);
		mCanvas.drawBitmap(mOutterBitmap, null, new Rect(10, 10, mWidth - 10,
				mHeight - 10), null);
	}

	private void myInit(Context context, AttributeSet attrs) {
		initAttrs(context, attrs);
		initDatas();
	}

	/**
	 * 初始化自定义属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.scratch);

		textPrize = ta.getString(R.styleable.scratch_text);
		textColor = ta.getColor(R.styleable.scratch_text_color, textColor);

		textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				textSize, getResources().getDisplayMetrics());
		textSize = (int) ta.getDimension(R.styleable.scratch_text_size,
				textSize);

		ta.recycle();
	}

	private void initDatas() {
		mPath = new Path();

		textBound = new Rect();
		paintBack = new Paint();

		// 初始化底层图片
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test4);

		mOutterBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.fg_guaguaka);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;

			// 代表开启了一个新的路径
			mPath.moveTo(mLastX, mLastY);
			break;

		case MotionEvent.ACTION_MOVE:
			// 获得用户移动的绝对值
			int dx = Math.abs(x - mLastX);
			int dy = Math.abs(y - mLastY);

			// 移动大于3像素的时候才绘制
			if (dx > 3 || dy > 3) {
				mPath.lineTo(x, y);// 绘制线条
			}

			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
			if (!isComplete) {
				new Thread(mRunnable).start();
			}
			break;
		}

		if (!isComplete) {
			invalidate();
		}

		return true;
	}

	/**
	 * 重置获奖信息
	 * 
	 * @param prize
	 *            获奖信息
	 */
	public void resert(String prize) {
		isComplete = false;
		setPrize(prize);

		mPath.reset();
		setupOutPaint();
		setForeground();

		invalidate();
	}

	/**
	 * 刮刮卡已经全部刮完
	 * 
	 * @author badboy
	 * 
	 */
	public interface OnScratchCompleteListener {
		/**
		 * 全部刮完
		 */
		public void onScratchComplete();
	}
}
