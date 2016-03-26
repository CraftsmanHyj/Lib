package com.hyj.lib.image_mt;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Bitmap.Config;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.hyj.lib.R;

/**
 * 颜色矩阵
 * 
 * @author async
 * 
 */
public class ColorMatrixActivity extends Activity implements OnClickListener {

	private ImageView imgView;
	private Bitmap mBitmap;

	private GridLayout mGridLayout;

	private List<EditText> lEts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_mt_colormatrix);

		myInit();
	}

	private void myInit() {
		initView();
		initViewData();
		initListener();
	}

	private void initView() {
		imgView = (ImageView) findViewById(R.id.colormatrixImage);
		mGridLayout = (GridLayout) findViewById(R.id.colormatrixGl);
	}

	private void initViewData() {
		mBitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.test1);
		imgView.setImageBitmap(mBitmap);

		lEts = new ArrayList<EditText>();
		mGridLayout.post(new Runnable() {

			@Override
			public void run() {
				int rowCount = mGridLayout.getRowCount();
				int columnCount = mGridLayout.getColumnCount();

				int count = rowCount * columnCount;

				int windth = mGridLayout.getWidth() / columnCount;
				int height = mGridLayout.getHeight() / rowCount;

				for (int i = 0; i < count; i++) {
					EditText et = new EditText(ColorMatrixActivity.this);
					et.setGravity(Gravity.CENTER);

					if (0 == i % 6) {
						et.setText("1");
					} else {
						et.setText("0");
					}
					lEts.add(et);
					mGridLayout.addView(et, windth, height);
				}
			}
		});
	}

	private void initListener() {
		findViewById(R.id.colormatrixResert).setOnClickListener(this);
		findViewById(R.id.colormatrixApply).setOnClickListener(this);
	}

	private void resertGridLayout() {
		for (int i = 0; i < lEts.size(); i++) {
			EditText et = lEts.get(i);
			if (0 == i % 6) {
				et.setText("1");
			} else {
				et.setText("0");
			}
		}
		setImageMatrix();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.colormatrixResert:
			resertGridLayout();
			break;

		case R.id.colormatrixApply:
			setImageMatrix();
			break;
		}
	}

	private void setImageMatrix() {
		float[] colorMatrix = new float[lEts.size()];
		for (int i = 0; i < lEts.size(); i++) {
			EditText et = lEts.get(i);
			colorMatrix[i] = Float.valueOf(et.getText().toString().trim());
		}

		ColorMatrix matrix = new ColorMatrix();
		matrix.set(colorMatrix);

		Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Config.ARGB_8888);// 创建一个可以写的bitmap
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// 设置抗锯齿
		paint.setColorFilter(new ColorMatrixColorFilter(matrix));
		canvas.drawBitmap(mBitmap, 0, 0, paint);
		imgView.setImageBitmap(bmp);
	}
}
