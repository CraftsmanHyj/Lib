package com.hyj.lib.flowlayout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流逝布局文件
 * 
 * @author async
 * 
 */
public class FlowLayout extends ViewGroup {

	// 存放所有的单个标签(button),以一行一行来存储
	private List<List<View>> lAllViews = new ArrayList<List<View>>();
	// 存放每一行的高度
	private List<Integer> lLineHeight = new ArrayList<Integer>();

	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);// 容器宽度
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);// 宽度测量模式

		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);// 容器高度
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);// 获取高度测量模式

		// 当宽、高设置成wrap_content时需要自己计算界面的宽高
		int width = 0;//
		int height = 0;//

		int lineWidth = 0;// 每行宽
		int lineHeight = 0;// 每行高

		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 测量子View的宽高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到子View的layoutparams,子view的layoutparams类型由父类决定
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			// 子View宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			// 子View高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;

			// 换行
			if (lineWidth + childWidth > sizeWidth - getPaddingLeft()
					- getPaddingRight()) {
				// 对比得到最大宽度
				width = Math.max(width, lineWidth);
				// 行宽度重置
				lineWidth = childWidth;
				// 行高
				height += lineHeight;
				// 重置每行高度
				lineHeight = childHeight;
			} else {// 未换行的情况
				lineWidth += childWidth;// 叠加行宽
				// 取这一行中最大的高度
				lineHeight = Math.max(lineHeight, childHeight);
			}

			// 最后一个控件
			if (cCount - 1 == i) {
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}

		width = modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width
				+ getPaddingLeft() + getPaddingRight();
		height = modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height
				+ getPaddingTop() + getPaddingBottom();
		setMeasuredDimension(width, height);// 设置控件宽高
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		lAllViews.clear();
		lLineHeight.clear();

		int width = getWidth();// 获取当前ViewGroup的宽度
		int lineWidth = 0;
		int lineHeight = 0;
		List<View> lineViews = new ArrayList<View>();

		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			int tempWidth = childWidth + lineWidth + lp.leftMargin
					+ lp.rightMargin;
			if (tempWidth > width - getPaddingLeft() - getPaddingRight()) {// 判断是否需要换行
				lLineHeight.add(lineHeight);// 记录lineHeight
				lAllViews.add(lineViews);// 记录当前行的Views

				// 重置行宽、行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				lineViews = new ArrayList<View>();
			}

			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			lineViews.add(child);
		}

		// 处理最后一行,
		lLineHeight.add(lineHeight);
		lAllViews.add(lineViews);

		// 设置子View的位置
		int left = getPaddingLeft();
		int top = getPaddingTop();

		int lineNum = lAllViews.size();// 行数
		for (int i = 0; i < lineNum; i++) {
			lineViews = lAllViews.get(i);// 当前行所有的View
			lineHeight = lLineHeight.get(i);// 当前行的高度

			for (int j = 0; j < lineViews.size(); j++) {
				View child = lineViews.get(j);
				if (View.GONE == child.getVisibility()) {
					continue;
				}

				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();
				// 拿到当前VIew的位置信息
				int cl = left + lp.leftMargin;
				int ct = top + lp.topMargin;
				int cr = cl + child.getMeasuredWidth();
				int cb = ct + child.getMeasuredHeight();
				// 设置子View的位置
				child.layout(cl, ct, cr, cb);

				left += child.getMeasuredWidth() + lp.leftMargin
						+ lp.rightMargin;
			}
			left = getPaddingLeft();
			top += lineHeight;
		}
	}

	/**
	 * 与当前ViewGroup对应的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}
}
