package com.hyj.lib.wechat_talk;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.CommonAdapter;
import com.hyj.lib.tools.adapter.ViewHolder;

public class TalkAdapter extends CommonAdapter<Recorder> {

	private int mMinItemWidth;// item最小宽度
	private int mMaxItemWidth;// item最大宽度

	private View preViewAnim;// 上一个播放的动画

	public TalkAdapter(Context context, List<Recorder> lDatas, int layoutItemID) {
		super(context, lDatas, layoutItemID);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
		mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
	}

	@Override
	public void getViewItem(ViewHolder holder, final Recorder item) {
		holder.setText(R.id.talkTvRecorderTime, item.getTimeForInteger() + "″");

		View flLength = holder.getView(R.id.talkFlRecorderLength);
		ViewGroup.LayoutParams params = flLength.getLayoutParams();

		// 假设录制时间最长为60秒
		int width = (int) (mMinItemWidth + mMaxItemWidth / 60f * item.getTime());
		width = Math.max(width, mMinItemWidth);
		width = Math.min(width, mMaxItemWidth);
		params.width = width;
		flLength.setLayoutParams(params);

		final View animView = holder.getView(R.id.talkVRecorderAnim);
		flLength.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopPreRecorderPlay();

				// 播放动画
				animView.setBackgroundResource(R.drawable.wechat_talk_play);
				AnimationDrawable anim = (AnimationDrawable) animView
						.getBackground();
				anim.start();

				preViewAnim = animView;

				// 播放音频
				MediaManager.playSound(item.getFilePath(),
						new MediaPlayer.OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								animView.setBackgroundResource(R.drawable.adj);
							}
						});
			}
		});
	}

	/**
	 * 停止上一条音频播放
	 */
	public void stopPreRecorderPlay() {
		if (null != preViewAnim) {
			preViewAnim.setBackgroundResource(R.drawable.adj);
			preViewAnim = null;
		}
		MediaManager.release();
	}
}
