package com.hyj.lib.image_preview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyj.lib.R;
import com.hyj.lib.wechat_imageUp.ImageLoader;
import com.hyj.lib.wechat_imageUp.ImageLoader.Type;

public class ImagePreviewActivity extends Activity {
	/**
	 * 传过来想要显示的图片数据
	 */
	public static final String IMAGE = "img";
	/**
	 * 要显示第几张图片
	 */
	public static final String INDEX = "index";

	private List<Object> lImgs;
	private ImageView[] mImageViews;
	private PagerAdapter adapter;
	private ViewPager mViewPager;

	private ImageLoader loader;
	private int curIndex = 0;// 当前显示图片的下标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_preview_viewpager);

		myInit();
	}

	private void myInit() {
		initDatas();
		initView();
	}

	private void initDatas() {
		loader = ImageLoader.getInstance(3, Type.LIFO);

		Intent intent = getIntent();
		List<String> ldata = intent.getStringArrayListExtra(IMAGE);
		curIndex = intent.getIntExtra(INDEX, curIndex);

		if (null != ldata) {
			lImgs = new ArrayList<Object>();
			for (String str : ldata) {
				lImgs.add(str);
			}
		} else {
			lImgs = new ArrayList<Object>();
			lImgs.add(R.drawable.test1);
			lImgs.add(R.drawable.test2);
			lImgs.add(R.drawable.test3);
		}
		mImageViews = new ImageView[lImgs.size()];
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.imgPreviewViewPager);
		adapter = new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ImageViewZoom imgView = new ImageViewZoom(
						getApplicationContext());
				Object obj = lImgs.get(position);
				if (obj instanceof Integer) {
					imgView.setImageResource(Integer.parseInt(obj.toString()));
				} else if (obj instanceof String) {
					loader.loadImage(imgView, obj.toString());
				}
				container.addView(imgView);
				mImageViews[position] = imgView;
				return imgView;
			}

			// 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mImageViews.length;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mImageViews[position]);
			}
		};

		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(curIndex);
	}
}
