package com.hyj.lib.recyclerview;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyj.lib.R;

public class RecyclerAdapter extends
		RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

	private LayoutInflater inflater;
	protected List<String> lDatas;

	private OnItemclickListener onItemclickListener;

	/**
	 * Item点击事件
	 * 
	 * @param onItemclickListener
	 */
	public void setOnItemclickListener(OnItemclickListener onItemclickListener) {
		this.onItemclickListener = onItemclickListener;
	}

	public RecyclerAdapter(Context context, List<String> lDatas) {
		this.lDatas = lDatas;

		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		return lDatas.size();
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {
		holder.tv.setText(lDatas.get(position));

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != onItemclickListener) {
					onItemclickListener.onItemClick(v, holder.getPosition());
				}
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (null != onItemclickListener) {
					onItemclickListener.onItemLongClick(v, holder.getPosition());
				}
				return false;
			}
		});
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
		View view = inflater.inflate(R.layout.recycler_item, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	/**
	 * 添加一条数据
	 * 
	 * @param pos
	 */
	public void addData(int pos) {
		lDatas.add(pos, "Insert one");

		// 使用这个方法插入、更新Item数据
		notifyItemInserted(pos);
	}

	/**
	 * 删除一条数据
	 * 
	 * @param pos
	 */
	public void delData(int pos) {
		lDatas.remove(pos);

		// 使用这个方法删除、更新Item数据
		notifyItemRemoved(pos);
	}

	public interface OnItemclickListener {
		/**
		 * 点击事件
		 * 
		 * @param view
		 * @param position
		 */
		public void onItemClick(View view, int position);

		/**
		 * 长按点击事件
		 * 
		 * @param view
		 * @param position
		 */
		public void onItemLongClick(View view, int position);
	}

	class MyViewHolder extends ViewHolder {
		TextView tv;

		public MyViewHolder(View view) {
			super(view);

			tv = (TextView) view.findViewById(R.id.recycItemTv);
		}
	}
}
