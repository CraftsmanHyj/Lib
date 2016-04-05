package com.hyj.lib.gobang;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyj.lib.R;

/**
 * <pre>
 * 五子棋棋盘
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-5 上午10:21:31
 */
public class WuziqiPanel extends View {
	private final int MAX_LINE = 10;// 最大行数
	private final int MAX_COUNT_IN_LINE = 5;// 连子数

	private int panelWidth;// 棋盘宽度
	private float lineHeight;// 每行高度

	private Paint paint;

	private Bitmap whitePiece;// 白色棋子
	private Bitmap blackPiece;// 黑色棋子
	private float ratioPieceOfLineHeight = 3 * 1.0f / 4;// 棋子大小比例

	private boolean isWhite = true;// 当前轮到白棋
	// 用于存放用户点击的坐标
	private ArrayList<Point> lWhiteArray = new ArrayList<Point>();
	private ArrayList<Point> lBlackArray = new ArrayList<Point>();

	private boolean isGameOver;// 判断游戏是否结束
	private boolean isWhiteWinner;// 白子赢了

	private OnGameOverListener listener;

	/**
	 * 设置游戏结束监听事件
	 * 
	 * @param listener
	 */
	public void setOnGameOverListener(OnGameOverListener listener) {
		this.listener = listener;
	}

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
		paint = new Paint();
		paint.setColor(0x88000000);
		paint.setAntiAlias(true);
		paint.setDither(true);//
		paint.setStyle(Paint.Style.STROKE);// 画线

		whitePiece = BitmapFactory.decodeResource(getResources(),
				R.drawable.stone_w2);
		blackPiece = BitmapFactory.decodeResource(getResources(),
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

		panelWidth = w;
		lineHeight = panelWidth * 1.0f / MAX_LINE;

		// 根据值动态缩放图片大小
		int pieceWidth = (int) (lineHeight * ratioPieceOfLineHeight);
		whitePiece = Bitmap.createScaledBitmap(whitePiece, pieceWidth,
				pieceWidth, false);
		blackPiece = Bitmap.createScaledBitmap(blackPiece, pieceWidth,
				pieceWidth, false);

	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isGameOver) {// 若游戏结束，不响应事件
			return false;
		}

		int action = event.getAction();
		if (MotionEvent.ACTION_UP == action) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			// 将点击的点转换成落子的点的坐标
			x = (int) (x / lineHeight);
			y = (int) (y / lineHeight);
			Point p = new Point(x, y);

			// 已经下过的点不再落子
			if (lWhiteArray.contains(p) || lBlackArray.contains(p)) {
				return true;
			}

			if (isWhite) {
				lWhiteArray.add(p);
			} else {
				lBlackArray.add(p);
			}

			invalidate();// 通知重绘
			isWhite = !isWhite;
		}

		return true;// 消耗了这个事件，不往下传递
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawBoard(canvas);
		drawPieces(canvas);
		checkGameOver();
	}

	/**
	 * 绘制棋盘
	 * 
	 * @param canvas
	 */
	private void drawBoard(Canvas canvas) {
		for (int i = 0; i < MAX_LINE; i++) {
			int startX = (int) (lineHeight / 2);
			int endX = (int) (panelWidth - lineHeight / 2);
			int y = (int) ((0.5 + i) * lineHeight);
			// 绘制横线
			canvas.drawLine(startX, y, endX, y, paint);
			// 绘制纵线(因为是正方形，所以他的纵线与横线恰好原理相反)
			canvas.drawLine(y, startX, y, endX, paint);
		}
	}

	/**
	 * 绘制棋子
	 * 
	 * @param canvas
	 */
	private void drawPieces(Canvas canvas) {
		for (int i = 0; i < lWhiteArray.size(); i++) {
			Point whitePoint = lWhiteArray.get(i);
			canvas.drawBitmap(whitePiece,
					(whitePoint.x + (1 - ratioPieceOfLineHeight) / 2)
							* lineHeight,
					(whitePoint.y + (1 - ratioPieceOfLineHeight) / 2)
							* lineHeight, null);
		}

		for (int i = 0; i < lBlackArray.size(); i++) {
			Point blackPoint = lBlackArray.get(i);
			canvas.drawBitmap(blackPiece,
					(blackPoint.x + (1 - ratioPieceOfLineHeight) / 2)
							* lineHeight,
					(blackPoint.y + (1 - ratioPieceOfLineHeight) / 2)
							* lineHeight, null);
		}
	}

	/**
	 * 判断游戏是否结束
	 */
	private void checkGameOver() {
		boolean whiteWin = checkFiveInLine(lWhiteArray);
		boolean blackWin = checkFiveInLine(lBlackArray);

		int pieceCount = MAX_LINE * MAX_LINE;
		int downCount = lWhiteArray.size() + lBlackArray.size();

		if (whiteWin || blackWin) {
			isGameOver = true;
			isWhiteWinner = whiteWin;

			String msg = isWhiteWinner ? "白子胜利" : "黑子胜利";

			if (null != listener) {
				listener.onTheEnd(msg);
			}
		} else if (pieceCount == downCount) {
			String msg = "棋逢对手，平局";
			if (null != listener) {
				listener.onTheEnd(msg);
			}
		}
	}

	/**
	 * 检查是否五子连珠
	 * 
	 * @param points
	 * @return
	 */
	private boolean checkFiveInLine(List<Point> points) {
		for (Point p : points) {
			int x = p.x;
			int y = p.y;

			boolean win = checkHorizontal(x, y, points);
			if (win) {
				return true;
			}

			win = checkVertical(x, y, points);
			if (win) {
				return true;
			}

			win = checkLeftDiagonal(x, y, points);
			if (win) {
				return true;
			}

			win = checkRightDiagonal(x, y, points);
			if (win) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <pre>
	 * 检测水平位置上是否已经获胜
	 * 先在落子点往左检测，然后从落子点往右检测，
	 * 加一起获得的总数再判断是否已经获胜
	 * </pre>
	 * 
	 * @param x
	 * @param y
	 * @param points
	 * @return
	 */
	private boolean checkHorizontal(int x, int y, List<Point> points) {
		int count = 1;// 同色的棋子数

		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x - i, y))) {// 检测左边连续棋子数
				count++;
			} else {
				break;
			}
		}

		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x + i, y))) {// 从右边检测连续棋子数
				count++;
			} else {
				break;
			}
		}

		if (MAX_COUNT_IN_LINE == count) {
			return true;
		}

		return false;
	}

	/**
	 * 检测垂直位置上是否已经获胜
	 * 
	 * @param x
	 * @param y
	 * @param points
	 * @return
	 */
	private boolean checkVertical(int x, int y, List<Point> points) {
		int count = 1;// 同色的棋子数

		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {// 检测上面棋子连续棋子数
			if (points.contains(new Point(x, y - i))) {
				count++;
			} else {
				break;
			}
		}

		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x, y + i))) {// 检测下面棋子连续棋子数
				count++;
			} else {
				break;
			}
		}

		if (MAX_COUNT_IN_LINE == count) {
			return true;
		}

		return false;
	}

	/**
	 * 检测左斜方向是否已经获胜
	 * 
	 * @param x
	 * @param y
	 * @param points
	 * @return
	 */
	private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
		int count = 1;// 同色的棋子数

		// 左斜上连续棋子数
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x - i, y + i))) {
				count++;
			} else {
				break;
			}
		}

		// 左斜下连续棋子数
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x + i, y - i))) {
				count++;
			} else {
				break;
			}
		}

		if (MAX_COUNT_IN_LINE == count) {
			return true;
		}

		return false;
	}

	/**
	 * 检测右斜方向是否已经获胜
	 * 
	 * @param x
	 * @param y
	 * @param points
	 * @return
	 */
	private boolean checkRightDiagonal(int x, int y, List<Point> points) {
		int count = 1;// 同色的棋子数

		// 右斜上连续棋子数
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x - i, y - i))) {
				count++;
			} else {
				break;
			}
		}

		// 右斜下连续棋子数
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if (points.contains(new Point(x + i, y + i))) {
				count++;
			} else {
				break;
			}
		}

		if (MAX_COUNT_IN_LINE == count) {
			return true;
		}

		return false;
	}

	/*
	 * 重置棋盘,重新开始游戏
	 */
	public void reStart() {
		lWhiteArray.clear();
		lBlackArray.clear();
		isGameOver = false;
		isWhiteWinner = false;
		invalidate();
	}

	private static final String INSTANCE = "instance";// 存默认的instance
	private static final String INSTANCE_GAME_OVER = "instance_game_over";
	private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
	private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

	/**
	 * 必须给调用这个组件的xml中写上id，否则状态保存不会生效
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
		bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
		bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, lWhiteArray);
		bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, lBlackArray);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			isGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
			lWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
			lBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
			// 默认的Parcelable
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
			return;
		}
		super.onRestoreInstanceState(state);
	}

	/**
	 * 游戏结束监听事件
	 * 
	 * @Author hyj
	 * @Date 2016-4-2 上午10:03:33
	 */
	public interface OnGameOverListener {
		/**
		 * 游戏结束
		 * 
		 * @param msg
		 */
		public void onTheEnd(String msg);
	}
}
