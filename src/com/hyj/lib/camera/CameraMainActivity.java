package com.hyj.lib.camera;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hyj.lib.Constants;
import com.hyj.lib.R;
import com.hyj.lib.tools.FileUtils;

public class CameraMainActivity extends Activity implements OnClickListener {
	private static final int REQ_SLT = 1;// 返回的为一个缩略图
	private static final int REQ_YT = 2;// 返回一个原图路径

	public static final String Path = File.separator + Constants.DIR_TEMP
			+ File.separator + "temp.png";

	private String filePath;

	private ImageView imgPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initData() {
		filePath = FileUtils.getAppFile(this, Path).getAbsolutePath();
	}

	private void initView() {
		imgPreview = (ImageView) findViewById(R.id.cameraIvPreviewe);
	}

	private void initListener() {
		findViewById(R.id.cameraBtSlt).setOnClickListener(this);
		findViewById(R.id.cameraBtYt).setOnClickListener(this);
		findViewById(R.id.cameraBtCustom).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.cameraBtSlt:
			// 这样获取的是相片的缩略图
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQ_SLT);
			break;

		case R.id.cameraBtYt:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			// 指定拍完照片所保存的路径
			Uri photoUri = Uri.fromFile(new File(filePath));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, REQ_YT);
			break;

		case R.id.cameraBtCustom:
			intent.setClass(CameraMainActivity.this, CamaraCustomActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode) {
			if (REQ_SLT == requestCode) {
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");
				imgPreview.setImageBitmap(bitmap);
			} else if (REQ_YT == resultCode) {
				// 读取文件
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(filePath);
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					imgPreview.setImageBitmap(bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (null != fis) {
							fis.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
	}
}
