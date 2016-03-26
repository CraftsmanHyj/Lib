package com.hyj.lib.image_mt;

import com.hyj.lib.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class PixelsActivity extends Activity {

	private ImageView img1, img2, img3, img4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_mt_pixels);

		myInit();
	}

	private void myInit() {
		initView();
		initViewData();
		initListener();
	}

	private void initView() {
		img1 = (ImageView) findViewById(R.id.pixelsImg1);
		img2 = (ImageView) findViewById(R.id.pixelsImg2);
		img3 = (ImageView) findViewById(R.id.pixelsImg3);
		img4 = (ImageView) findViewById(R.id.pixelsImg4);
	}

	private void initViewData() {

		Bitmap bitMap = BitmapFactory.decodeResource(getResources(),
				R.drawable.test2);
		img1.setImageBitmap(bitMap);
		// 底片效果
		img2.setImageBitmap(ImageHelper.handleImageNegative(bitMap,
				ImageHelper.PIXELS_NEGATIVE));
		// 怀旧效果
		img3.setImageBitmap(ImageHelper.handleImageNegative(bitMap,
				ImageHelper.PIXELS_OLDPHOTO));
		// 浮雕效果
		img4.setImageBitmap(ImageHelper.handleImageNegative(bitMap,
				ImageHelper.PIXELS_RELIEF));
	}

	private void initListener() {

	}
}
