package com.hyj.lib.recyclerview;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.hyj.lib.tools.LogUtils;

/**
 * Created by async on 2016/1/16.
 */
public class StaggeredAdapter extends RecyclerAdapter {

	private List<Integer> lHeights;

	public StaggeredAdapter(Context context, List<String> lDatas) {
		super(context, lDatas);

		lHeights = new ArrayList<Integer>();
		for (int i = 0; i < super.lDatas.size(); i++) {
			lHeights.add((int) (100 + Math.random() * 300));
		}
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		lp.height = lHeights.get(position);
		LogUtils.i("View高度：" + lHeights.get(position));
		holder.itemView.setLayoutParams(lp);

		holder.tv.setText(lDatas.get(position));
	}
}
