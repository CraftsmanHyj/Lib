package com.hyj.lib.listviewrefresh;

import java.util.List;

import android.content.Context;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.CommonAdapter;
import com.hyj.lib.tools.adapter.ViewHolder;

public class ListViewAdapter extends CommonAdapter<Integer> {

	public ListViewAdapter(Context context, List<Integer> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(ViewHolder holder, Integer item) {
		holder.setText(R.id.refreshTvName, "默认用户　" + item);
	}
}
