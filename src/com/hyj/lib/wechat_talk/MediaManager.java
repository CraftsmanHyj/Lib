package com.hyj.lib.wechat_talk;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

/**
 * 音频播放类
 * 
 * @author async
 * 
 */
public class MediaManager {

	private static MediaPlayer mMediaPlayer;
	private static boolean isPause;// 是否暂停

	/**
	 * 播放音频
	 * 
	 * @param filePath
	 * @param onCompletionListener
	 */
	public static void playSound(String filePath,
			OnCompletionListener onCompletionListener) {
		if (null == mMediaPlayer) {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mMediaPlayer.reset();
					return false;
				}
			});
		} else {
			mMediaPlayer.reset();
		}

		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 暂停播放
	 */
	public static void pause() {
		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			isPause = true;
		}
	}

	/**
	 * 继续播放
	 */
	public static void resume() {
		if (null != mMediaPlayer && isPause) {
			mMediaPlayer.start();
			isPause = false;
		}
	}

	/**
	 * 停止并释放资源
	 */
	public static void release() {
		if (null != mMediaPlayer) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}

			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
}
