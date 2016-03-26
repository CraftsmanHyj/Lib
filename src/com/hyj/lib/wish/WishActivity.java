package com.hyj.lib.wish;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hyj.lib.Constants;
import com.hyj.lib.R;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.FileUtils;

public class WishActivity extends Activity {
	private ImageView imgTp;
	private EditText etYw;
	private Button btShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wish_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		imgTp = (ImageView) findViewById(R.id.wishImgTp);
		etYw = (EditText) findViewById(R.id.wishEtYw);
		btShare = (Button) findViewById(R.id.wishBtShare);
	}

	private void initData() {
		// 设置字体
		etYw.setTypeface(Typeface
				.createFromAsset(getAssets(), "fonts/test.ttf"));
	}

	private void initListener() {
		imgTp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 100);
			}
		});

		btShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share();
			}
		});
	}

	// 截屏分享给朋友
	private void share() {
		btShare.setVisibility(View.GONE);

		// 截屏
		View view = getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();// 获取到截屏

		btShare.setVisibility(View.VISIBLE);

		// 保存图片
		String path = File.separator + Constants.DIR_TEMP + File.separator
				+ "SpringCard.jpg";
		FileUtils.saveFileFromBitmap(this, bitmap, path);

		DialogUtils.showToastShort(this, "分享成功");

		// imgTp.setImageBitmap(bitmap);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode && 100 == requestCode) {
			if (null != data) {
				imgTp.setImageURI(data.getData());
			}
		}
	}
}
