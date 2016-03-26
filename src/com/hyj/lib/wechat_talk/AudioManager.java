package com.hyj.lib.wechat_talk;

import java.io.File;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {
	private MediaRecorder mMediaRecorder;
	private String mDir;// 保存录音的文件夹
	private String mCurrentFilePath;// 录音文件path

	private static AudioManager mInstance;

	private boolean isPrepared;// 是否已经准备完毕

	private AudioStateListener mListener;

	/**
	 * 录音是否准备完毕
	 * 
	 * @return
	 */
	public boolean isPrepared() {
		return isPrepared;
	}

	/**
	 * 设置录音准备完毕时的监听事件
	 * 
	 * @param mListener
	 */
	public void setOnAudioStateListener(AudioStateListener mListener) {
		this.mListener = mListener;
	}

	private AudioManager(String dir) {
		this.mDir = dir;
	}

	/**
	 * 获取AurioManager对象
	 * 
	 * @return
	 */
	public static AudioManager getInstance(String dir) {
		if (null == mInstance) {
			synchronized (AudioManager.class) {
				if (null == mInstance) {
					mInstance = new AudioManager(dir);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 准备
	 */
	public void prepareAudio() {
		try {
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = generateFileName();
			File file = new File(dir, fileName);
			mCurrentFilePath = file.getAbsolutePath();

			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(mCurrentFilePath);
			// 设置音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);// RAW_AMR
			// 设置音频编码格式amr
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isPrepared = true;// 准备结束

			if (null != mListener) {
				mListener.wellPrepared();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 随机生成文件名称
	 * 
	 * @return
	 */
	private String generateFileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	/**
	 * 获取音量等级
	 * 
	 * @return
	 */
	public int getVoiceLevel(int maxLevel) {
		int level = 1;
		if (isPrepared && null != mMediaRecorder) {
			// 获得它最大的震幅 getMaxAmplitude()范围是1~32767
			level = maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
		}
		return level;
	}

	public void release() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
		isPrepared = false;
	}

	public void cancel() {
		if (null != mCurrentFilePath) {
			File file = new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath = null;
		}

		release();
	}

	/**
	 * 获取当前录音文件路径
	 * 
	 * @return
	 */
	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}

	/**
	 * 录音准备完毕时监听事件
	 * 
	 * @author async
	 * 
	 */
	public interface AudioStateListener {
		/**
		 * 录音准备完毕
		 */
		public void wellPrepared();
	}
}
