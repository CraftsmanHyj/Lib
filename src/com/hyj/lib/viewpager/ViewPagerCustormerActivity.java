package com.hyj.lib.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hyj.lib.R;

public class ViewPagerCustormerActivity extends Activity {

	private ViewPagerTransformer mViewPager;
	private List<Integer> lImageId;
	private List<ImageView> lImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpager_custormer);

		myInit();
	}

	private void myInit() {
		lImageId = new ArrayList<Integer>();
		lImageId.add(R.drawable.guide_image1);
		lImageId.add(R.drawable.guide_image2);
		lImageId.add(R.drawable.guide_image3);

		lImage = new ArrayList<ImageView>();

		mViewPager = (ViewPagerTransformer) findViewById(R.id.viewpagercustormer);
		// 为3.0以后的viewpager添加动画效果
		// mViewPager.setPageTransformer(true, new RotatePageTransformer());

		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(lImage.get(position));
				mViewPager.removeView(position);
			}

			/**
			 * 初始化Item
			 */
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ImageView img = new ImageView(ViewPagerCustormerActivity.this);
				img.setScaleType(ScaleType.CENTER_CROP);
				img.setImageResource(lImageId.get(position));
				container.addView(img);
				lImage.add(img);
				mViewPager.addView(position, img);
				return img;
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return lImageId.size();
			}
		});
	}
}
