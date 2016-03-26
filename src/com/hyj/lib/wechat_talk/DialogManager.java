package com.hyj.lib.wechat_talk;

import com.hyj.lib.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 录音提示界面
 * 
 * @author Administrator
 * 
 */
public class DialogManager {
	private Context mContext;
	private Dialog mDialog;

	private ImageView imgIcon;
	private ImageView imgVoice;
	private TextView tvLable;

	public DialogManager(Context context) {
		this.mContext = context;

		mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.wechat_talk_dialog_recorder, null);
		mDialog.setContentView(view);

		imgIcon = (ImageView) view.findViewById(R.id.talkImgIcon);
		imgVoice = (ImageView) view.findViewById(R.id.talkImgVoice);
		tvLable = (TextView) view.findViewById(R.id.talkTvLable);
	}

	/**
	 * 显示录音界面
	 */
	public void showRecordingDilaog() {
		imgVoice.setVisibility(View.VISIBLE);

		imgIcon.setImageResource(R.drawable.recorder);
		tvLable.setText("手指上滑，取消发送");

		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	/**
	 * 取消录音界面
	 */
	public void showWantCancelDialog() {
		if (mDialog.isShowing()) {
			imgVoice.setVisibility(View.GONE);

			imgIcon.setImageResource(R.drawable.cancel);
			tvLable.setText("松开手指，取消发送");
		}
	}

	/**
	 * 录音时间过短界面
	 */
	public void showTooShortDialog() {
		if (mDialog.isShowing()) {
			imgVoice.setVisibility(View.GONE);

			imgIcon.setImageResource(R.drawable.voice_to_short);
			tvLable.setText("录音时间过短");
		}
	}

	public void dismissDialog() {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/**
	 * 更新voice图片
	 * 
	 * @param level
	 *            1~7
	 */
	public void updateVoiceLevel(int level) {
		if (mDialog.isShowing()) {
			// 获取资源ID
			int resId = mContext.getResources().getIdentifier("v" + level,
					"drawable", mContext.getPackageName());
			imgVoice.setImageResource(resId);
		}
	}
}
