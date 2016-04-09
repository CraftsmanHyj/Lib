package com.hyj.lib.camera;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hyj.lib.R;
import com.hyj.lib.tools.FileUtils;

/**
 * 自定义相机界面
 * 
 * @Author hyj
 * @Date 2016-1-26 上午9:29:19
 */
@SuppressWarnings("deprecation")
public class CamaraCustomActivity extends Activity implements
		SurfaceHolder.Callback {

	private Button btPz;
	private SurfaceView mPreview;
	private SurfaceHolder mHolder;

	private Camera mCamera;

	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File temp = FileUtils.saveFileFromBytes(CamaraCustomActivity.this,
					data, CameraMainActivity.Path);

			// 将拍的图片显示出来
			Intent intent = new Intent(CamaraCustomActivity.this,
					CameraResultActivity.class);
			intent.putExtra(CameraResultActivity.PIC_PATH,
					temp.getAbsolutePath());
			startActivity(intent);
			CamaraCustomActivity.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_custom);

		myInit();
	}

	private void myInit() {
		mPreview = (SurfaceView) findViewById(R.id.camaraSv);
		mHolder = mPreview.getHolder();
		mHolder.addCallback(this);

		btPz = (Button) findViewById(R.id.camaraBtPz);

		btPz.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				capture(v);
			}
		});

		// 给相机添加点击对焦事件
		mPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCamera.autoFocus(null);
			}
		});
	}

	/**
	 * 执行拍照
	 * 
	 * @param view
	 */
	public void capture(View view) {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPictureFormat(ImageFormat.JPEG);
		parameters.setPreviewSize(800, 400);// 设置预览大小
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);// 设置对焦模式
		mCamera.autoFocus(new AutoFocusCallback() {// 在获取最清晰的焦距之后再进行拍照

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					mCamera.takePicture(null, null, mPictureCallback);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null == mCamera) {
			mCamera = getCamera();
			if (null != mHolder) {
				setStartPreview(mCamera, mHolder);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	/**
	 * 获取Camera对象
	 * 
	 * @return
	 */
	private Camera getCamera() {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return camera;
	}

	/**
	 * 开始预览相机类容
	 */
	private void setStartPreview(Camera camera, SurfaceHolder holder) {
		// 将camera与holder进行绑定
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			// 默认的相机是横屏的,将其旋转90度
			camera.setDisplayOrientation(90);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放相机资源
	 */
	private void releaseCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setStartPreview(mCamera, mHolder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mCamera.stopPreview();
		setStartPreview(mCamera, mHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}
}
