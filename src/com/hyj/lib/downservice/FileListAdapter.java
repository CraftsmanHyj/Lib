package com.hyj.lib.downservice;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hyj.lib.R;
import com.hyj.lib.http.download.DownService;
import com.hyj.lib.http.download.FileInfo;
import com.hyj.lib.tools.adapter.CommonAdapter;
import com.hyj.lib.tools.adapter.ViewHolder;

public class FileListAdapter extends CommonAdapter<FileInfo> {

	public FileListAdapter(Context context, List<FileInfo> lDatas,
			int layoutItemID) {
		super(context, lDatas, layoutItemID);
	}

	@Override
	public void getViewItem(ViewHolder holder, final FileInfo file) {
		holder.setText(R.id.downTvFileName, file.getFileName());
		holder.setProgress(R.id.downPbProgress, file.getProgress());

		final Button btStart = holder.getView(R.id.downBtBegin);
		final Button btPause = holder.getView(R.id.downBtPause);

		btStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btStart.setEnabled(false);
				btPause.setEnabled(true);

				Intent intent = new Intent(context, DownService.class);
				intent.setAction(DownService.ACTION_PREPARE);
				intent.putExtra(DownService.DOWNINFO, file);
				context.startService(intent);
			}
		});

		btPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btStart.setEnabled(true);
				btPause.setEnabled(false);

				Intent intent = new Intent(context, DownService.class);
				intent.setAction(DownService.ACTION_PAUSE);
				intent.putExtra(DownService.DOWNINFO, file);
				context.startService(intent);
			}
		});
	}
}
