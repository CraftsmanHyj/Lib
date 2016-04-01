package com.hyj.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import android.app.Application;
import android.content.Context;

import com.hyj.lib.tools.FileUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * @author hyj
 * @Date 2016-3-1 上午11:09:41
 */
public class LibApplication extends Application {

	/**
	 * async-image-loading图片设置类
	 */
	private DisplayImageOptions.Builder builder;

	/**
	 * 程序配置Properties
	 */
	private Properties configProperties;

	@Override
	public void onCreate() {
		super.onCreate();

		myInit();
	}

	private void myInit() {
		// 异步图片加载框架
		initImageLoader(getApplicationContext());

		// 捕获程序崩溃日志
		initCrashHandler();

		// 初始化app配置
		initProperties();

		/**
		 * <pre>
		 * 注册Activity生命周期回调方法
		 * 通过这个ActivityLifecycleCallbacks拿到App所有Activity的生命周期回调
		 * </pre>
		 */
		registerActivityLifecycleCallbacks(new LibActivityLifecycle());
	}

	/**
	 * 注册崩溃日志捕获方法
	 */
	private void initCrashHandler() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		crashHandler.sendCrashInfo();
	}

	/**
	 * 加载APP配置文件
	 */
	private void initProperties() {
		InputStream is = null;
		try {
			is = getResources().getAssets().open("config.properties");
			configProperties = new Properties();
			configProperties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// 把配置文件里面的值读取程序中
		loadPropertyDatas();
	}

	/**
	 * 把config.properties里面的值读出来赋值到常量Constants中
	 */
	private void loadPropertyDatas() {
		// 是否打印日志
		Constants.PROP_ISDEBUG = (Boolean) getProperty("isDebug",
				Constants.PROP_ISDEBUG);
		// 输出日志的TAG标签值
		Constants.PROP_LOGTAG = (String) getProperty("logTag",
				Constants.PROP_LOGTAG);
	}

	/**
	 * 读取config.properties配置文件里面的字段信息
	 * 
	 * @param fieldName
	 *            字段名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	private Object getProperty(String fieldName, Object defaultValue) {
		String str = configProperties.getProperty(fieldName,
				String.valueOf(defaultValue));

		Class<? extends Object> clz = defaultValue.getClass();

		// 默认数据是String类型不需要转换类型
		if (String.class.equals(clz)) {
			return str;
		}

		try {
			Method method = clz.getDeclaredMethod("valueOf", String.class);
			return method.invoke(defaultValue, str);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 实例化一个DisplayImageOptions,且设置圆角角度
	 * 
	 * @param int cornerRadiusPixels圆角角度
	 * @return
	 */
	public DisplayImageOptions getImageOptions(int cornerRadiusPixels) {
		// 初始化DisplayImageOptions.Builder()
		if (null == builder) {
			synchronized (LibApplication.class) {
				if (null == builder) {
					builder = new DisplayImageOptions.Builder();
					// 设置图片下载期间显示的图片
					builder.showImageOnLoading(R.drawable.img_stub);
					// 设置图片Uri为空或是错误的时候显示的图片
					builder.showImageForEmptyUri(R.drawable.img_empty);
					// 设置图片加载或解码过程中发生错误显示的图片
					builder.showImageOnFail(R.drawable.img_error);
					// 设置下载的图片是否缓存在内存中
					builder.cacheInMemory(true);
					// 设置下载的图片是否缓存在SD卡中
					builder.cacheOnDisk(true);
				}
			}
		}

		// 设置图片角度
		builder.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels));
		DisplayImageOptions imageOptions = builder.build();
		return imageOptions;
	}

	/**
	 * <pre>
	 * 初始化ImageLoader，调用方法
	 * 方法一：声明一个Application
	 * 	ImageLoader.getInstance().displayImage(url, imageView);
	 * 
	 * 方法二：
	 *  DisplayImageOptions options = ((LibApplication) context.getApplicationContext()).getImageOptions(360);
	 *  ImageLoader.getInstance().displayImage(url, imageView, options);
	 * </pre>
	 * 
	 * @param context
	 */
	private void initImageLoader(Context context) {
		// 缓存文件的目录：/包名/imageCache
		String filePath = File.separator
				+ FileUtils.getAppRootDir(context).getName() + File.separator
				+ Constants.DIR_IMAGECACHE;
		File fileCache = StorageUtils.getOwnCacheDirectory(context, filePath);

		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context);
		// 设置值默认参数
		builder.defaultDisplayImageOptions(getImageOptions(0));

		// max width, max height，即保存的每个缓存文件的最大长宽
		builder.memoryCacheExtraOptions(480, 800);
		// 线程池内线程的数量
		builder.threadPoolSize(3);
		builder.threadPriority(Thread.NORM_PRIORITY - 2);
		builder.denyCacheImageMultipleSizesInMemory();
		// 将保存的时候的URI名称用MD5 加密
		builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());

		builder.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024));
		// 内存缓存的最大值
		builder.memoryCacheSize(2 * 1024 * 1024);
		// SD卡缓存的最大值
		builder.diskCacheSize(50 * 1024 * 1024);
		// 由原先的discCache -> diskCache
		builder.tasksProcessingOrder(QueueProcessingType.LIFO);
		// 自定义缓存路径
		builder.diskCache(new UnlimitedDiskCache(fileCache));
		// connectTimeout (5 s), readTimeout (30 s)超时时间
		builder.imageDownloader(new BaseImageDownloader(context, 5 * 1000,
				30 * 1000));
		// Remove for release app
		builder.writeDebugLogs();
		ImageLoaderConfiguration config = builder.build();

		// 全局初始化此配置
		ImageLoader.getInstance().init(config);
	}
}
