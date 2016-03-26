package com.hyj.lib.wechat_imageUp;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 图片加载缓存机制，只讲图片放在运存中
 * 
 * @Author hyj
 * @Date 2015-12-11 下午5:44:28
 */
public class ImageLoader {
	private static final int DEFAULT_THREAD_COUNT = 3;// 默认线程个数

	private static ImageLoader mInstance;

	/**
	 * 图片缓存核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;

	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;

	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;

	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;

	/**
	 * 后台轮询线程
	 */
	private Thread mPollThread;

	/**
	 * 给线程中的MessageQue发送消息
	 */
	private Handler mPollThreadHandler;

	/**
	 * UI线程中的Handler,将图片回调显示到UI界面上
	 */
	private Handler mUIHandler;

	/**
	 * 信号量，用于同步addTask()中与mPollThreadHandler的同步问题
	 */
	private Semaphore mSemaphorePollThreadHandler = new Semaphore(0);

	/**
	 * 控制线程池只有在空闲的时候才会去取线程执行
	 */
	private Semaphore mSemaphoreThreadPool;

	/**
	 * 队列的调度方式，图片加载策略
	 * 
	 * @author async
	 * 
	 */
	public enum Type {
		/**
		 * 先进先加载
		 */
		FIFO,
		/**
		 * 后进先加载
		 */
		LIFO
	}

	private ImageLoader(int threadCount, Type type) {
		myInit(threadCount, type);
	}

	private void myInit(int threadCount, Type type) {
		this.mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);

		// 后台轮询线程
		mPollThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPollThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 线程池去取出一个任务进行执行
						mThreadPool.execute(getTask());

						try {
							// 当定义Semaphore的permits值为3
							// 此时有3个线程在执行，当第四个线程进来的时候就会阻塞
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};

				// 释放一个信号量
				mSemaphorePollThreadHandler.release();
				Looper.loop();
			}
		};
		mPollThread.start();

		// 获取应用的最大使用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			// 此方法去测量每个bitMap的大小值
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();

		// 更新界面handler
		mUIHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 获取得到的图片,为ImageView回调设置图片
				ImageHolder holder = (ImageHolder) msg.obj;
				Bitmap bm = holder.bitmap;
				ImageView imageView = holder.imageView;
				String path = holder.path;

				// 将path与getTag存储路径对比是否一致
				if (path.equals(imageView.getTag().toString())) {
					imageView.setImageBitmap(bm);
				}
			}
		};
	}

	/**
	 * 从任务队列取出一个方法
	 * 
	 * @return
	 */
	private Runnable getTask() {
		if (Type.FIFO == mType) {
			return mTaskQueue.removeFirst();
		} else if (Type.LIFO == mType) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	/**
	 * 获取单实例，默认线程3个，默认加载方式，后进先加载Type.LIFO
	 * 
	 * @return
	 */
	public static ImageLoader getInstance() {
		return getInstance(DEFAULT_THREAD_COUNT, Type.LIFO);
	}

	/**
	 * 获取单实例，默认加载方式：Type.LIFO
	 * 
	 * @param threadCount
	 * @return
	 */
	public static ImageLoader getInstance(int threadCount) {
		return getInstance(threadCount, Type.LIFO);
	}

	/**
	 * 获取单实例
	 * 
	 * @param threadCount
	 *            线程数
	 * @param type
	 *            图片加载类型
	 * @return
	 */
	public static ImageLoader getInstance(int threadCount, Type type) {
		if (null == mInstance) {
			synchronized (ImageLoader.class) {
				if (null == mInstance) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据Path为ImageView设置图片
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final ImageView imageView, final String path) {
		imageView.setTag(path);
		// 根据path在缓存中获取bitmap
		Bitmap bm = getBitmapFromLruCache(path);
		if (null != bm) {
			refreshBitmap(path, imageView, bm);
		} else {
			addTask(new Runnable() {
				public void run() {
					// 去加载图片
					// 图片压缩
					// 1、获得图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);

					// 2、压缩图片
					Bitmap bm = decodeSampledBitmapFromPath(path, imageSize);

					// 3、吧图片加入到缓存中
					addBitmapToLruCache(path, bm);

					refreshBitmap(path, imageView, bm);

					// 释放线程占用的信号量
					mSemaphoreThreadPool.release();
				}
			});
		}
	}

	private void refreshBitmap(final String path, final ImageView imageView,
			Bitmap bm) {
		Message msg = Message.obtain();
		ImageHolder holder = new ImageHolder();
		holder.bitmap = bm;
		holder.imageView = imageView;
		holder.path = path;
		msg.obj = holder;
		mUIHandler.sendMessage(msg);
	}

	/**
	 * 将图片加入LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm) {
		if (null == getBitmapFromLruCache(path)) {
			if (null != bm) {
				mLruCache.put(path, bm);
			}
		}
	}

	/**
	 * 根据图片需要显示的尺寸进行压缩
	 * 
	 * @param path
	 * @param imageSize
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path,
			ImageSize imageSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 获取图片大小，并不把图片加载到内存中
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = caculateInSampleSize(options, imageSize);

		// 使用获取到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		return bitmap;
	}

	/**
	 * 根据需求的宽、高以及图片的实际宽、高计算SampleSize
	 * 
	 * @param options
	 * @param imageSize
	 * @return
	 */
	private int caculateInSampleSize(Options options, ImageSize imageSize) {
		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;// 压缩取样率

		if (width > imageSize.width || height > imageSize.height) {
			int widthRadio = Math.round(width * 1.0f / imageSize.width);
			int heightRadio = Math.round(height * 1.0f / imageSize.height);

			inSampleSize = Math.max(widthRadio, heightRadio);
		}

		return inSampleSize;
	}

	/**
	 * 根据ImageView获取适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewSize(ImageView imageView) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();

		LayoutParams params = imageView.getLayoutParams();

		int width = imageView.getWidth();// 获取imageview的实际宽度
		if (width <= 0) {
			width = params.width;// 获取imageView在layout中声明的宽度
		}
		if (width <= 0) {
			// width = imageView.getMaxWidth();// 检查最大值
			width = getImaeViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 获取imageview的实际宽度
		if (height <= 0) {
			height = params.height;// 获取imageView在layout中声明的宽度
		}
		if (height <= 0) {
			// height = imageView.getMaxHeight();// 检查最大值
			height = getImaeViewFieldValue(imageView, "mMaxHeight");
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}

		ImageSize imageSize = new ImageSize();
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 通过反射获取View的某个属性值,可以下兼容
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private int getImaeViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);

		// 控制同步
		// if(null==mPoolThreadHandler) wait();
		try {// 当请求的permits个数为0的时候就会阻塞
			if (null == mPollThreadHandler) {
				mSemaphorePollThreadHandler.acquire();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mPollThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 根据path在缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	private class ImageSize {
		protected int width;
		protected int height;
	}

	private class ImageHolder {
		protected Bitmap bitmap;
		protected ImageView imageView;
		protected String path;
	}
}
