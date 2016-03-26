package com.hyj.lib.imagecycle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hyj.lib.Constants;
import com.hyj.lib.R;
import com.hyj.lib.imagecycle.ImageCycleView.OnImageCycleViewListener;
import com.hyj.lib.tools.FileUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 描述：主页
 * 
 */
public class ImageCycleViewActivity extends Activity {

	private List<ADInfo> lAdInfo = new ArrayList<ADInfo>();
	private ImageCycleView imageCycleView;

	private String[] imageUrls = {
			"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
			"http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
			"http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
			"http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
			"http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagecycleview_main);

		initImageLoader();
		initialize();
	}

	@SuppressLint("NewApi")
	private void initialize() {

		imageCycleView = (ImageCycleView) findViewById(R.id.imagecycleview);
		for (int i = 0; i < imageUrls.length; i++) {
			ADInfo info = new ADInfo(imageUrls[i], "图片-->" + i);
			lAdInfo.add(info);
		}

		// 设置循环，在调用setData方法前调用
		// imageCycleView.setCycle(true);
		// 设置轮播时间，默认5000ms
		// imageCycleView.setWheelTime(10);
		// 设置圆点指示图标组居中显示，默认靠右
		// imageCycleView.setIndicatorPosition(ImageCycleView.CENTER);
		// 在加载数据前设置是否循环
		imageCycleView.setData(lAdInfo);
		imageCycleView
				.setOnImageCycleViewListener(new OnImageCycleViewListener() {

					@Override
					public void onImageClick(ADInfo info, int position,
							View imageView) {
						Toast.makeText(
								ImageCycleViewActivity.this,
								"position-->" + position + "　"
										+ info.getContent(), Toast.LENGTH_SHORT)
								.show();

					}

				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		imageCycleView.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		imageCycleView.pause();
	}

	/**
	 * 配置ImageLoder
	 */
	private void initImageLoader() {
		// 缓存文件的目录：/包名/imageCache
		String filePath = File.separator
				+ FileUtils.getAppRootDir(this).getName() + File.separator
				+ Constants.DIR_IMAGECACHE;
		File cacheDir = StorageUtils.getOwnCacheDirectory(this, filePath);

		// 初始化ImageLoader
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.img_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.img_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.img_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}
}