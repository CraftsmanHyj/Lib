package com.hyj.lib.largeImage;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;

import com.hyj.lib.R;
import com.hyj.lib.largeImage.view.LargeImageView;

public class LargeImageViewActivity extends Activity {
	private LargeImageView mLargeImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_large_image_view);

		mLargeImageView = (LargeImageView) findViewById(R.id.largetImageview);
		try {
			InputStream inputStream = getAssets().open("qm.jpg");
			mLargeImageView.setInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
