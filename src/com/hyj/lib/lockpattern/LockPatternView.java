package com.hyj.lib.lockpattern;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyj.lib.R;

public class LockPatternView extends View {
	private final int POINT_COUNT = 5;// 点的最少个数
	private final int POINTNUMBER = 3;// 每行点的个数

	private Point[][] points = new Point[POINTNUMBER][POINTNUMBER];// 九个点
	private boolean isInit = false;// 是否执行过初始化方法

	private float width, height;// 屏幕宽高
	private float offsetsX, offsetsY;// 九宫格偏移量

	private Paint paint;// 画笔
	// 图片资源
	private Bitmap bitNormal, bitPressed, bitError;
	private Bitmap bitLineNormal, bitLineError;
	private float bitRadius;// 图片半径

	private List<Point> lSelPoint = new ArrayList<Point>();// 按下点的集合
	private float movingX, movingY;// 鼠标移动坐标
	private boolean isSelect = false;// 是否可以开始绘制线条
	private boolean isFinish = false;// 绘制是否结束
	private boolean movingNoPoint = false;// 鼠标在移动但是不是九宫格里面的点
	private Matrix matrix = new Matrix();// 用户线条缩放矩阵

	private onPatterChangeListener patterListener;

	/**
	 * 设置密码监听器
	 * 
	 * @param patterListener
	 */
	public void setPatterListener(onPatterChangeListener patterListener) {
		this.patterListener = patterListener;
	}

	public LockPatternView(Context context) {
		this(context, null);
	}

	public LockPatternView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		myInit();
	}

	private void myInit() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isInit) {
			initPoints();// 初始化点
		}

		// 将点绘制到画布上
		try {
			point2Canvas(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 画线
		if (lSelPoint.size() > 0) {
			Point a = lSelPoint.get(0);
			// 绘制九宫格里面的点
			for (int i = 0; i < lSelPoint.size(); i++) {
				Point b = lSelPoint.get(i);
				line2Canvas(canvas, a, b);
				a = b;
			}

			// 绘制鼠标坐标点
			if (movingNoPoint) {
				line2Canvas(canvas, a, new Point(movingX, movingY));
			}
		}
	}

	/**
	 * 初始化九个点
	 */
	private void initPoints() {
		// 1.获取屏幕宽高
		width = getWidth();
		height = getHeight();

		// 2.偏移量
		if (width > height) {// 横屏
			offsetsX = (width - height) / 2;// 这个值就是九宫格内容区域的宽度
			width = height;
		} else {// 竖屏
			offsetsY = (height - width) / 2;
			height = width;
		}

		// 3.图片资源
		bitNormal = BitmapFactory.decodeResource(getResources(),
				R.drawable.lock_normal);
		bitPressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.lock_pressed);
		bitError = BitmapFactory.decodeResource(getResources(),
				R.drawable.lock_error);
		bitLineNormal = BitmapFactory.decodeResource(getResources(),
				R.drawable.lock_line_normal);
		bitLineError = BitmapFactory.decodeResource(getResources(),
				R.drawable.lock_line_error);

		// 4.点的坐标、设置密码
		float unitDistance = width / (points.length + 1);// 3个点将竖直/水平方向分成4分
		for (int row = 0, rCount = points.length; row < rCount; row++) {
			for (int column = 0, cCount = points[row].length; column < cCount; column++) {
				Point point = new Point(offsetsX + unitDistance * (column + 1),
						offsetsY + unitDistance * (row + 1));
				point.setIndex(row * rCount + column);
				points[row][column] = point;
			}
		}

		// 5.图片资源半径
		bitRadius = bitNormal.getWidth() / 2;

		// 6.初始化完成
		isInit = true;
	}

	/**
	 * 将点绘制到画布上
	 * 
	 * @param canvas
	 */
	private void point2Canvas(Canvas canvas) throws Exception {
		float x, y;
		Bitmap bitmap = null;
		for (int row = 0, rCount = points.length; row < rCount; row++) {
			for (int column = 0, cCount = points[row].length; column < cCount; column++) {
				Point point = points[row][column];
				x = point.getX() - bitRadius;
				y = point.getY() - bitRadius;

				switch (point.getState()) {
				case Point.STATE_NORMAL:
					bitmap = bitNormal;
					break;

				case Point.STATE_PRESSED:
					bitmap = bitPressed;
					break;

				case Point.STATE_ERROR:
					bitmap = bitError;
					break;
				}

				canvas.drawBitmap(bitmap, x, y, paint);
			}
		}
	}

	/**
	 * 画线
	 * 
	 * @param canvas
	 * @param a
	 *            第一个点
	 * @param b
	 *            第二个点
	 */
	private void line2Canvas(Canvas canvas, Point a, Point b) {
		// 线的长度
		float lineLength = (float) Point.distance(a, b);

		// 旋转角度
		float degrees = getDegrees(a, b);
		canvas.rotate(degrees, a.getX(), a.getY());

		Bitmap bitmap = null;
		if (Point.STATE_PRESSED == a.getState()) {
			bitmap = bitLineNormal;
		} else {
			bitmap = bitLineError;
		}

		// x轴缩放比例=两点间距离/资源的宽
		matrix.setScale(lineLength / bitmap.getWidth(), 1);// 指定x轴缩放,y轴不需要缩放
		matrix.postTranslate(a.getX() - bitmap.getWidth() / 2, a.getY()
				- bitmap.getHeight() / 2);
		// 把线条画出来
		canvas.drawBitmap(bitmap, matrix, paint);

		// 画完线之后把角度旋转回来
		canvas.rotate(-degrees, a.getX(), a.getY());
	}

	/**
	 * 获取角度
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private float getDegrees(Point a, Point b) {
		return (float) Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX()
				- a.getX()));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		movingNoPoint = false;
		isFinish = false;
		movingX = event.getX();
		movingY = event.getY();

		Point point = null;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (null != patterListener) {
				patterListener.onPatterStart(true);
			}

			resetPoint();
			point = checkSelectPoint();
			if (null != point) {
				isSelect = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (isSelect) {
				point = checkSelectPoint();
				if (null == point) {
					movingNoPoint = true;
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			isSelect = false;
			isFinish = true;
			break;
		}

		// 选中重复检查
		if (isSelect && !isFinish && null != point) {
			if (crossPoint(point)) {
				movingNoPoint = true;
			} else {
				point.setState(Point.STATE_PRESSED);
				lSelPoint.add(point);
			}
		}

		// 绘制结束
		if (isFinish) {
			int pointCount = lSelPoint.size();

			if (1 == pointCount) {// 绘制不成立
				resetPoint();
			} else if (0 < pointCount && pointCount < POINT_COUNT) {
				// 绘制错误
				errorPoint();
				if (null != patterListener) {
					patterListener.onChangeListener(null);
				}
			} else {// 绘制成功
				if (null != patterListener) {
					String pwd = "";
					for (Point bean : lSelPoint) {
						pwd += bean.getIndex();
					}
					patterListener.onChangeListener(pwd);
				}
			}
		}

		// 刷新界面
		postInvalidate();
		return true;
	}

	/**
	 * 交叉点
	 * 
	 * @param point
	 *            点
	 * @return 是否交差
	 */
	private boolean crossPoint(Point point) {
		if (lSelPoint.size() <= 2) {
			return false;
		}

		if (lSelPoint.contains(point)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置绘制不成立
	 */
	private void resetPoint() {
		// 还原状态
		for (Point point : lSelPoint) {
			point.setState(Point.STATE_NORMAL);
		}
		lSelPoint.clear();
	}

	/**
	 * 设置绘制错误
	 */
	private void errorPoint() {
		for (Point point : lSelPoint) {
			point.setState(Point.STATE_ERROR);
		}
	}

	/**
	 * 检查鼠标的坐标和九宫格上的点是否吻合
	 */
	private Point checkSelectPoint() {
		for (Point[] bean : points) {
			for (Point point : bean) {
				if (Point.with(point.getX(), point.getY(), bitRadius, movingX,
						movingY)) {
					return point;
				}
			}
		}
		return null;
	}

	/**
	 * 监听器
	 * 
	 * @author async
	 * 
	 */
	public interface onPatterChangeListener {
		/**
		 * 图案改变
		 * 
		 * @param pwd
		 */
		public void onChangeListener(String pwd);

		/**
		 * 图案重新绘制
		 * 
		 * @param isStart
		 *            是否重新绘制
		 */
		public void onPatterStart(boolean isStart);
	}
}
