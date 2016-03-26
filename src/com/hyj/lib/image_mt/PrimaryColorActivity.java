package com.hyj.lib.image_mt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hyj.lib.R;

/**
 * 图片色相、饱和度、亮度变化
 * 
 * @author async
 * 
 */
public class PrimaryColorActivity extends Activity implements
		OnSeekBarChangeListener {
	private ImageView mImageView;
	private Bitmap bitmap;

	private static int MID_VALUE = 127;
	private float mHue, mSaturation, mLum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_mt_primary);

		myInit();
	}

	private void myInit() {
		initView();
		initViewData();
		initListener();
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.hueImg);
	}

	private void initViewData() {
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
		mImageView.setImageBitmap(bitmap);

		mHue = 0;
		mSaturation = 1;
		mLum = 1;
	}

	private void initListener() {
		((SeekBar) findViewById(R.id.mtSbHue)).setOnSeekBarChangeListener(this);
		((SeekBar) findViewById(R.id.mtSbSaturation))
				.setOnSeekBarChangeListener(this);
		((SeekBar) findViewById(R.id.mtSbLum)).setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.mtSbHue:
			mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;
			break;
		case R.id.mtSbSaturation:
			mSaturation = progress * 1.0F / MID_VALUE;
			break;
		case R.id.mtSbLum:
			mLum = progress * 1.0F / MID_VALUE;
			break;
		}

		mImageView.setImageBitmap(ImageHelper.handleImageEffect(bitmap, mHue,
				mSaturation, mLum));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
