package com.hyj.lib.tools.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 通用ViewHolder
 * 
 * @author async
 * 
 */
public class ViewHolder {
	private SparseArray<View> saView;

	private int position;
	private View convertView;

	/**
	 * 获取当前Item的索引位置
	 * 
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 获取实例化的ConvertView
	 * 
	 * @return
	 */
	public View getConvertView() {
		return convertView;
	}

	private ViewHolder(Context context, ViewGroup parent, int layoutID,
			int position) {
		this.saView = new SparseArray<View>();

		this.position = position;
		convertView = LayoutInflater.from(context).inflate(layoutID, parent,
				false);
		convertView.setTag(this);
	}

	/**
	 * 获取ViewHolder的实例
	 * 
	 * @return
	 */
	public static ViewHolder getInstance(Context context, View convertView,
			ViewGroup parent, int layoutID, int position) {

		if (convertView == null) {
			return new ViewHolder(context, parent, layoutID, position);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		// convertView可以复用但是position是变化的,在此更新
		holder.position = position;
		return holder;
	}

	/**
	 * 通过ViewID获取组件
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = saView.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			saView.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为textview设置显示文字
	 * 
	 * @param viewId
	 *            组件ID
	 * @param strId
	 *            要显示的字符串的整形值
	 */
	public void setText(int viewId, int strId) {
		setText(viewId, convertView.getContext().getResources()
				.getString(strId));
	}

	/**
	 * 为textview设置显示文字
	 * 
	 * @param viewId
	 *            组件ID
	 * @param str
	 *            要显示的字符串
	 */
	public void setText(int viewId, String str) {
		TextView tv = getView(viewId);
		tv.setText(str);
	}

	/**
	 * 设置progressBar进度条的值
	 * 
	 * @param viewId
	 *            组件ID
	 * @param progress
	 *            当前进度值
	 */
	public void setProgress(int viewId, int progress) {
		ProgressBar pb = getView(viewId);
		pb.setProgress(progress);
	}

	/**
	 * 设置组件是否可以点击
	 * 
	 * @param viewId
	 *            组件ID
	 * @param enabled
	 *            是否可用
	 */
	public void setEnabled(int viewId, boolean enabled) {
		getView(viewId).setEnabled(enabled);
	}
}
