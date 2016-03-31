package com.hyj.lib.gobang;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WuziqiPanel extends View {
	private int mPanelWidth;// 棋盘宽度
	private float mLineHeight;// 每行高度

	private int MAX_LINE = 10;// 最大行数

	private Paint mPaint = new Paint();

	public WuziqiPanel(Context context) {
		this(context, null);
	}

	public WuziqiPanel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		myInit();
	}

	private void myInit() {
		setBackgroundColor(0x44ff0000);

		mPaint.setColor(0x88000000);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);//
		mPaint.setStyle(Paint.Style.STROKE);// 画线
	}

	/**
	 * 测量棋盘大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int width = Math.min(widthSize, heightSize);

		// 避免嵌套在scrollview中的时候无法显示
		if (MeasureSpec.UNSPECIFIED == widthMode) {
			width = heightSize;
		} else if (MeasureSpec.UNSPECIFIED == heightMode) {
			width = widthSize;
		}

		setMeasuredDimension(width, width);
	}

	/**
	 * 当宽高确定了发生改变以后就会回调此方法
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mPanelWidth = w;
		mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawBoard(canvas);
	}

	/**
	 * 绘制棋盘
	 * 
	 * @param canvas
	 */
	private void drawBoard(Canvas canvas) {
		int w = mPanelWidth;
		float lineHeight = mLineHeight;

		for (int i = 0; i < MAX_LINE; i++) {
			int startX = (int) (lineHeight / 2);
			int endX = (int) (w - lineHeight / 2);
			int y = (int) ((0.5 + i) * lineHeight);
			// 绘制横线
			canvas.drawLine(startX, y, endX, y, mPaint);
			// 绘制纵线(因为是正方形，所以他的纵线与横线恰好原理相反)
			canvas.drawLine(y, startX, y, endX, mPaint);

		}
	}
}
