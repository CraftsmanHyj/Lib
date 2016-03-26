package com.hyj.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewShowAll extends ListView {

	/**
	 * <pre>
	 * 此ListView无滚动条，会将所有的Item显示出来
	 * 用于嵌套在ScrolView中使用
	 * 若要让ScrollView置顶,设置一下代码：
	 * 		sv = (ScrollView) findViewById(R.id.scrollview);
	 * 		sv.smoothScrollTo(0, 0);
	 * </pre>
	 * 
	 * @param context
	 */
	public ListViewShowAll(Context context) {
		this(context, null);
	}

	public ListViewShowAll(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ListViewShowAll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}