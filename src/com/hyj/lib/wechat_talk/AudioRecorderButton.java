package com.hyj.lib.wechat_talk;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hyj.lib.Constants;
import com.hyj.lib.R;
import com.hyj.lib.tools.FileUtils;
import com.hyj.lib.wechat_talk.AudioManager.AudioStateListener;

/**
 * 录音按钮
 * 
 * @author Administrator
 * 
 */
public class AudioRecorderButton extends Button {
	private final int VOICE_MAX_LEVEL = 7;// 最大音量

	private final int MSG_AUDIO_PREPARED = 0x100;// 准备完毕
	private final int MSG_VOICE_CHANGED = 0x101;
	private final int MSG_DIALOG_DISMISS = 0x102;

	private final int STATE_NORMAL = 0x0001;// 正常状态
	private final int STATE_RECORDING = 0x0002;// 录音状态
	private final int STATE_WANT_CANCEL = 0x0003;// 取消
	private final int DISTANCE_CANCEL_Y = 50;// y轴方向取消时移动的距离;

	private int mCurState = STATE_NORMAL;// 当前录音状态
	private boolean isRecording = false;// 是否开始录音
	private Vibrator mVibrator;// 调用震动
	private float mTime;// 录音时间长度

	// 对外参数
	private String audioDir;// 录音存放文件

	private DialogManager mDialogManager;
	private AudioManager mAudioManager;

	private OnAudioRecorderFinishListener mFinishListener;
	private OnAudioRecorderLongClickLisetener mLongClickListener;

	/**
	 * 获取存放录音文件的路径
	 * 
	 * @return
	 */
	public String getAudioDir() {
		return audioDir;
	}

	/**
	 * 设置录音结束时监听事件
	 * 
	 * @param mFinishListener
	 */
	public void setOnAudioRecorderFinishListener(
			OnAudioRecorderFinishListener mFinishListener) {
		this.mFinishListener = mFinishListener;
	}

	/**
	 * 录音长按监听事件
	 * 
	 * @param mLongClickListener
	 */
	public void setOnAudioRecorderLongClickLisetener(
			OnAudioRecorderLongClickLisetener mLongClickListener) {
		this.mLongClickListener = mLongClickListener;
	}

	/**
	 * 获取音量大小
	 */
	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime += 0.1f;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				mDialogManager.showRecordingDilaog();
				isRecording = true;
				new Thread(mGetVoiceLevelRunnable).start();
				break;

			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevel(mAudioManager
						.getVoiceLevel(VOICE_MAX_LEVEL));
				break;

			case MSG_DIALOG_DISMISS:
				mDialogManager.dismissDialog();
				break;
			}
		};
	};

	public AudioRecorderButton(Context context) {
		this(context, null);
	}

	public AudioRecorderButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AudioRecorderButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context);
	}

	private void myInit(Context context) {
		initView(context);
		initListener();
	}

	private void initView(Context context) {
		mDialogManager = new DialogManager(getContext());

		audioDir = FileUtils.getAppDir(context, Constants.DIR_RECORDER)
				.getAbsolutePath();
		mAudioManager = AudioManager.getInstance(audioDir);

		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private void initListener() {
		mAudioManager.setOnAudioStateListener(new AudioStateListener() {

			@Override
			public void wellPrepared() {
				mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
			}
		});

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mAudioManager.prepareAudio();
				mVibrator.vibrate(30);// 震动
				if (null != mLongClickListener) {
					mLongClickListener.onLongClick(v);
				}
				return false;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			break;

		case MotionEvent.ACTION_MOVE:
			if (!isRecording) {
				break;
			}
			// 根据X、Y坐标判断是否需要取消
			if (wantToCancel(x, y)) {
				changeState(STATE_WANT_CANCEL);
			} else {
				changeState(STATE_RECORDING);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (!mAudioManager.isPrepared()) {
				reset();
				break;
			}

			if (!isRecording || mTime < 0.6f) {
				mDialogManager.showTooShortDialog();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);
			} else if (STATE_RECORDING == mCurState) {// 正常录制结束
				mDialogManager.dismissDialog();
				mAudioManager.release();

				if (null != mFinishListener) {
					mFinishListener.onFinish(mTime,
							mAudioManager.getCurrentFilePath());
				}
			} else if (STATE_WANT_CANCEL == mCurState) {
				mDialogManager.dismissDialog();
				mAudioManager.cancel();
			}

			reset();
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 恢复状态、标志位
	 */
	private void reset() {
		mTime = 0;
		isRecording = false;
		changeState(STATE_NORMAL);
	}

	/**
	 * 是否想取消发送
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {// 判断手指横坐标是否超出按钮范围
			return true;
		}

		if (y < -DISTANCE_CANCEL_Y || y > getHeight() + DISTANCE_CANCEL_Y) {
			return true;
		}

		return false;
	}

	private void changeState(int state) {
		if (state == mCurState) {// 状态相等无需改变
			return;
		}

		mCurState = state;
		switch (state) {
		case STATE_NORMAL:
			setBackgroundResource(R.drawable.btn_recorder_normal);
			setText(R.string.recorder_normal);
			break;

		case STATE_RECORDING:
			setBackgroundResource(R.drawable.btn_recorder_recording);
			setText(R.string.recorder_recording);
			if (isRecording) {
				mDialogManager.showRecordingDilaog();
			}
			break;

		case STATE_WANT_CANCEL:
			setBackgroundResource(R.drawable.btn_recorder_recording);
			setText(R.string.recorder_want_cancel);
			mDialogManager.showWantCancelDialog();
			break;
		}
	}

	/**
	 * 录音完成后的回调
	 * 
	 * @author async
	 * 
	 */
	public interface OnAudioRecorderFinishListener {
		/**
		 * 录音结束
		 * 
		 * @param seconds
		 *            录音时长
		 * @param filePath
		 *            录音路径
		 */
		public void onFinish(float seconds, String filePath);
	}

	/**
	 * 录音长按事件
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnAudioRecorderLongClickLisetener {
		/**
		 * 长按事件
		 * 
		 * @param v
		 */
		public void onLongClick(View v);
	}
}
