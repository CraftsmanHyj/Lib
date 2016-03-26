package com.hyj.lib.tuling;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.ViewHolder;
import com.hyj.lib.tuling.bean.ChatMessage;
import com.hyj.lib.tuling.bean.ChatMessage.Type;

public class TLAdapter extends BaseAdapter {
	private Context context;
	private List<ChatMessage> mDatas;

	public TLAdapter(Context context, List<ChatMessage> mDatas) {
		this.context = context;
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage chatMessage = mDatas.get(position);
		if (Type.INCOMING == chatMessage.getType()) {
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage chat = mDatas.get(position);
		ViewHolder holder = null;

		int layoutID = getItemViewType(position);

		if (0 == layoutID) {
			holder = ViewHolder.getInstance(context, convertView, parent,
					R.layout.tuling_item_incoming, position);
			holder.setText(R.id.tlInDate, chat.getDateStr());
			holder.setText(R.id.tlInTvMsg, chat.getMsg());
		} else {
			holder = ViewHolder.getInstance(context, convertView, parent,
					R.layout.tuling_item_outcoming, position);
			holder.setText(R.id.tlOutDate, chat.getDateStr());
			holder.setText(R.id.tlOutTvMsg, chat.getMsg());
		}

		return holder.getConvertView();
	}
}
