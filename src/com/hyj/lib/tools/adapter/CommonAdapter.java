package com.hyj.lib.tools.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 适配器模板
 * 
 * @author async
 * 
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	private int layoutItemID;

	/**
	 * 上下文对象
	 */
	protected Context context;

	/**
	 * Item填充数据
	 */
	protected List<T> lDatas;

	public CommonAdapter(Context context, List<T> lDatas, int layoutItemID) {
		super();
		this.context = context;
		this.lDatas = lDatas;
		this.layoutItemID = layoutItemID;
	}

	@Override
	public int getCount() {
		return lDatas.size();
	}

	@Override
	public T getItem(int position) {
		return lDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getInstance(context, convertView,
				parent, layoutItemID, position);

		getViewItem(holder, getItem(position));

		return holder.getConvertView();
	}

	/**
	 * 给每个Item设置数据
	 * 
	 * @param ViewHolder
	 *            holder句柄对象
	 * @param T
	 *            item每一项数据值
	 */
	public abstract void getViewItem(ViewHolder holder, T item);
}
