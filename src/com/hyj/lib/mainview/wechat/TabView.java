package com.hyj.lib.mainview.wechat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hyj.lib.R;

/**
 * 自定义tab变色的view
 * 
 * @author async
 * 
 */
public class TabView extends View {
	// 按钮变色绘制原理,
	// 1、在bitmap上绘制纯色,
	// 2、然后设置xfermode模式；
	// 3、然后再绘制icon，之后在内存中的bitmap就为设置过颜色的icons
	// 4、通过view的canvas将带颜色的icon绘制出来

	// 存储当前view原来就有的bundler,不能将原有的bundler给覆盖了
	private final String INSTANCE_STATUS = "instance_state";
	// 存储用户自定义类容
	private final String STATUS_ALPHA = "status_alpha";

	// 自定义View的属性
	private int defaultColor = 0xFF45C01A;
	private Bitmap mIcon;
	private String mText = "微信";
	private int textSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

	// 绘制图形、字体所需要的工具
	// 通过bitmap得到canvas,通过canvas得到paint
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;

	private float mAlpha;// 通过这个透明度来改变图片颜色

	private Rect mIconRect, mTextBound;// 绘制图片、文字的矩形

	private Paint mTextPaint;

	/**
	 * 设置图标透明度
	 * 
	 * @param alpha
	 */
	public void setIconAlpha(float alpha) {
		this.mAlpha = alpha;
		invalidateView();
	}

	/**
	 * 重绘界面
	 */
	private void invalidateView() {
		// 判断是否是UI线程
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	public TabView(Context context) {
		this(context, null);
	}

	public TabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		initAttr(context, attrs);
		initTools();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 计算icon的绘制范围
		int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();// icon可以占用宽度
		int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
				- mTextBound.height();// icon可以占用的高度
		int iconWidth = Math.min(width, height);// 宽高中取最小
		// 设置绘制左上角起点
		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = (getMeasuredHeight() - mTextBound.height()) / 2 - iconWidth
				/ 2;
		// 图片绘制的矩形
		mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制无色的icon原图
		canvas.drawBitmap(mIcon, null, mIconRect, null);

		// 内存中准备mBitmap，mAlphata,纯色,xfermode,图标
		int alpha = (int) Math.ceil(255 * mAlpha);
		setupTargetBitmap(alpha);
		// 将绘制的有颜色的图片显示出来
		canvas.drawBitmap(mBitmap, 0, 0, null);

		// 绘制底部文字
		// 第一步：绘制原文字;第二步：绘制变色的文字
		drawSourceText(canvas, alpha);
		drawTargetText(canvas, alpha);

	}

	/**
	 * 当界面隐藏时保存界面状态、数据
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
		bundle.putFloat(STATUS_ALPHA, mAlpha);
		return bundle;
	}

	/**
	 * 当界面恢复的时候恢复保存的数据
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATUS_ALPHA);
			state = bundle.getParcelable(INSTANCE_STATUS);
		}
		super.onRestoreInstanceState(state);
	}

	/**
	 * 获取自定义属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initAttr(Context context, AttributeSet attrs) {
		// 获取所有自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.TabView);

		for (int i = 0, len = ta.length(); i < len; i++) {
			int attr = ta.getIndex(i);

			switch (ta.getIndex(i)) {
			case R.styleable.TabView_text_icon:
				BitmapDrawable bitmap = (BitmapDrawable) ta.getDrawable(attr);
				mIcon = bitmap.getBitmap();
				break;

			case R.styleable.TabView_text:
				mText = ta.getString(attr);
				break;

			case R.styleable.TabView_text_color:
				defaultColor = ta.getColor(attr, defaultColor);
				break;

			case R.styleable.TabView_text_size:
				textSize = (int) ta.getDimension(attr, textSize);
				break;
			}
		}
		ta.recycle();
	}

	/**
	 * 初始化绘制图形所需要的工具
	 */
	private void initTools() {
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(textSize);
		mTextPaint.setColor(0Xff555555);
		// 测量文字的范围
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}

	/**
	 * 在内存中绘制可变色的Icon
	 */
	private void setupTargetBitmap(int alpha) {
		// 需要绘制的图形
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);

		mCanvas = new Canvas(mBitmap);// 设置画布
		mPaint = new Paint();// 画图的画笔
		mPaint.setColor(defaultColor);
		mPaint.setAntiAlias(true);// 设置抗锯齿
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);// 设置图片透明度
		// 画图
		mCanvas.drawRect(mIconRect, mPaint);
		// 设置xfermode图片叠加的时候显示模式
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		// 重新绘制图标
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIcon, null, mIconRect, mPaint);
	}

	/**
	 * 绘制原文字
	 * 
	 * @param canvas
	 * @param alpha
	 */
	private void drawSourceText(Canvas canvas, int alpha) {
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255 - alpha);
		// 求绘制文本的起始位置X坐标
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		// 绘制文本的起始位置Y坐标
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	/**
	 * 绘制变色文字
	 * 
	 * @param canvas
	 * @param alpha
	 */
	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(defaultColor);
		mTextPaint.setAlpha(alpha);
		// 求绘制文本的起始位置X坐标
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		// 绘制文本的起始位置Y坐标
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}
}
