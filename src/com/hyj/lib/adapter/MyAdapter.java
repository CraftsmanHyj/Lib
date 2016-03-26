package com.hyj.lib.adapter;

import java.util.List;

import android.content.Context;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.CommonAdapter;
import com.hyj.lib.tools.adapter.ViewHolder;

public class MyAdapter extends CommonAdapter<News> {

	public MyAdapter(Context context, List<News> lDatas, int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(ViewHolder holder, News item) {
		holder.setText(R.id.adpItemTvTitle, item.getTitle());
		holder.setText(R.id.adpItemContent, item.getDesc());
		holder.setText(R.id.adpItemTime, item.getTime());
		holder.setText(R.id.adpItemTel, item.getPhone());
	}
}
