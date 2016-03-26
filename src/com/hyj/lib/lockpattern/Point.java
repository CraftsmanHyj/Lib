package com.hyj.lib.lockpattern;

import java.io.Serializable;

public  class Point implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int STATE_NORMAL = 0X001;// 正常状态
	public static final int STATE_PRESSED = 0X002;// 选中状态
	public static final int STATE_ERROR = 0X003;// 绘制错误

	private int state = STATE_NORMAL;// 点被按下时的状态
	private int index = 0;

	private float x;
	private float y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	/**
	 * 2点间的距离
	 * 
	 * @param a
	 * @param b
	 * @return double 两点之间的距离
	 */
	public static double distance(Point a, Point b) {
		// x轴差的平方加上y轴差的平方,对和开放
		return Math.sqrt(Math.abs(a.getX() - b.getX())
				* Math.abs(a.getX() - b.getX())
				+ Math.abs(a.getY() - b.getY())
				* Math.abs(a.getY() - b.getY()));
	}

	/**
	 * 移动点是否跟原来的点重合
	 * 
	 * @param pointX
	 *            参考点的X
	 * @param pointY
	 *            参考点的Y
	 * @param radius
	 *            圆的半径
	 * @param movingX
	 *            移动点的X
	 * @param movingY
	 *            移动点的Y
	 * @return 是否重合
	 */
	public static boolean with(float pointX, float pointY, float radius,
			float movingX, float movingY) {
		// 开方
		return Math.sqrt((pointX - movingX) * (pointX - movingX)
				+ (pointY - movingY) * (pointY - movingY)) < radius;
	}
}