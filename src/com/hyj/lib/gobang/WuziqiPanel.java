package com.hyj.lib.gobang;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyj.lib.R;

public class WuziqiPanel extends View {
	private int mPanelWidth;// 棋盘宽度
	private float mLineHeight;// 每行高度

	private int MAX_LINE = 10;// 最大行数

	private Paint mPaint = new Paint();

	private Bitmap mWhitePiece;// 白色棋子
	private Bitmap mBlackPiece;// 黑色棋子
	private float ratioPieceOfLineHeight = 3 * 1.0f / 4;// 棋子大小比例

	private boolean mIsWhite = true;// 当前轮到白棋
	// 用于存放用户点击的坐标
	private List<Point> mWhiteArray = new ArrayList<Point>();
	private List<Point> mBlackArray = new ArrayList<Point>();

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

		mWhitePiece = BitmapFactory.decodeResource(getResources(),
				R.drawable.stone_w2);
		mBlackPiece = BitmapFactory.decodeResource(getResources(),
				R.drawable.stone_b1);
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

		// 根据值动态缩放图片大小
		int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
		mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth,
				pieceWidth, false);
		mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth,
				pieceWidth, false);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (MotionEvent.ACTION_UP == action) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			Point p = getValidPint(x, y);

			if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
				return true;
			}

			if (mIsWhite) {
				mWhiteArray.add(p);
			} else {
				mBlackArray.add(p);
			}

			invalidate();// 通知重绘
			mIsWhite = !mIsWhite;
		}

		return true;// 消耗了这个事件，不往下传递
	}

	/**
	 * 将用户点击的点转化成棋子在棋盘上的坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getValidPint(int x, int y) {
		return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawBoard(canvas);
		drawPieces(canvas);
	}

	/**
	 * 绘制棋子
	 * 
	 * @param canvas
	 */
	private void drawPieces(Canvas canvas) {
		for (int i = 0; i < mWhiteArray.size(); i++) {
			Point whitePoint = mWhiteArray.get(i);
			canvas.drawBitmap(mWhitePiece,
					(whitePoint.x + (1 - ratioPieceOfLineHeight) / 2)
							* mLineHeight,
					(whitePoint.y + (1 - ratioPieceOfLineHeight) / 2)
							* mLineHeight, null);
		}

		for (int i = 0; i < mBlackArray.size(); i++) {
			Point blackPoint = mBlackArray.get(i);
			canvas.drawBitmap(mBlackPiece,
					(blackPoint.x + (1 - ratioPieceOfLineHeight) / 2)
							* mLineHeight,
					(blackPoint.y + (1 - ratioPieceOfLineHeight) / 2)
							* mLineHeight, null);
		}
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
