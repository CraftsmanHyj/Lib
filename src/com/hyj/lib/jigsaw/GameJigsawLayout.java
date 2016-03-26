package com.hyj.lib.jigsaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyj.lib.R;

/**
 * 拼图游戏布局
 * 
 * @Author hyj
 * @Date 2016-1-26 下午10:14:41
 */
public class GameJigsawLayout extends RelativeLayout implements OnClickListener {
	private final String SPLIT = "_";// 分割符
	private final int TIME_CHANGED = 0X001;// 检查时间
	private final int NEXT_LEVEL = 0x002;// 游戏过关

	private int mColumn = 3;// 3*3拼图
	private int mPadding;// 容器内边距
	private int mMargin = 3;// 每张小图之间的距离
	private int itemWidth;
	private ImageView[] items;

	private Bitmap mBitmap;// 游戏原图
	private List<ImagePiece> lItemBitmap;

	private boolean isInit;// 是否已经初始化
	private int gameWidth;// 游戏面板宽度

	private int level = 1;// 当前关卡
	private int mTime;// 关卡时间
	private boolean isTimeLimit = true;// 是否启动时间限制

	/**
	 * 动画层
	 */
	private RelativeLayout rlAnim;
	/**
	 * 是否正在执行动画切换
	 */
	private boolean isAnim;

	private OnGameJigsawListener listener;

	/**
	 * 设置用于显示的图片
	 * 
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnGameJigsawListener(OnGameJigsawListener listener) {
		this.listener = listener;
	}

	/**
	 * 是否开启游戏时间限制
	 * 
	 * @param isTimeLimit
	 */
	public void setTimeLimit(boolean isTimeLimit) {
		this.isTimeLimit = isTimeLimit;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_CHANGED:
				if (isGameSuccess || isGameOver || isPause) {
					return;
				}

				if (null != listener) {
					listener.timeChanged(mTime);
					if (mTime <= 0) {
						isGameOver = true;
						listener.gameOver();
						return;
					}
				}

				mTime--;
				handler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
				break;

			case NEXT_LEVEL:
				if (null != listener) {
					listener.nextLevel(++level);
				} else {
					nextLevel();
				}
				break;
			}
		};
	};

	public GameJigsawLayout(Context context) {
		this(context, null);
	}

	public GameJigsawLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameJigsawLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		myInit(context, attrs);
	}

	private void myInit(Context context, AttributeSet attrs) {
		initAttrs(context, attrs);
		initDatas();
	}

	/**
	 * 初始化自定义属性
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.jigsaw);
		BitmapDrawable bd = (BitmapDrawable) ta
				.getDrawable(R.styleable.jigsaw_drawable);
		mBitmap = bd.getBitmap();
		isTimeLimit = ta.getBoolean(R.styleable.jigsaw_timelimit, true);
		ta.recycle();
	}

	/**
	 * 初始化数据
	 */
	private void initDatas() {
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mMargin, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 取宽、高中的小值
		gameWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
		if (!isInit) {
			// 进行切图、以及排序
			initBitmap();

			// 设置ImageView(Item)的宽高等属性
			initItem();

			// 判断是否开启时间
			checkTimeEnable();

			isInit = true;
		}

		// 强制调用设置view的宽高，使之成为一个正方形
		setMeasuredDimension(gameWidth, gameWidth);
	}

	/**
	 * 进行切图、以及排序
	 */
	private void initBitmap() {
		if (null == mBitmap) {
			return;
		}

		// 缩放图片至屏幕适配宽高
		mBitmap = getResizeBitmap(mBitmap, gameWidth, gameWidth);
		lItemBitmap = splitImage(mBitmap, mColumn);

		// 打乱排序方法
		Collections.sort(lItemBitmap, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}

	/**
	 * 将图片压缩至固定大小的值
	 * 
	 * @param bm
	 *            源图片
	 * @param newWidth
	 *            目标宽度
	 * @param newHeight
	 *            目标高度
	 * @return
	 */
	private Bitmap getResizeBitmap(Bitmap bm, int newWidth, int newHeight) {
		// 获取图片的宽、高
		int width = bm.getWidth();
		int height = bm.getHeight();

		// 计算缩放比例
		float scaleWidth = newWidth * 1.0f / width;
		float scaleHeight = newHeight * 1.0f / height;

		// 获取缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.setScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	}

	/**
	 * 设置ImageView(Item)的宽高等属性
	 */
	private void initItem() {
		itemWidth = (gameWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		items = new ImageView[mColumn * mColumn];

		// 生成我们的Item，设置rule
		for (int i = 0; i < items.length; i++) {
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);

			item.setImageBitmap(lItemBitmap.get(i).getBitmap());
			item.setId(i + 1);
			// 在Item的Tag中存储了index sortIndex_rightIndex
			item.setTag(i + SPLIT + lItemBitmap.get(i).getIndex());
			items[i] = item;

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					itemWidth, itemWidth);
			// 不是最后一列，设置Item间横向间隙通过rightMargin
			if ((i + 1) % mColumn != 0) {
				lp.rightMargin = mMargin;
			}
			// 不是第一列
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF, items[i - 1].getId());
			}

			// 如果不是第一行,设置topMargin和rule
			if ((i + 1) > mColumn) {
				lp.topMargin = mMargin;
				lp.addRule(RelativeLayout.BELOW, items[i - mColumn].getId());
			}

			addView(item, lp);
		}
	}

	/**
	 * 判断时间是否开启
	 */
	private void checkTimeEnable() {
		handler.removeMessages(TIME_CHANGED);

		if (isTimeLimit) {
			// 根据当前等级设置时间
			countTimeBaseLevel();
			handler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	/**
	 * 根据当前等级设置时间
	 */
	private void countTimeBaseLevel() {
		mTime = (int) Math.pow(2, level) * 60;
	}

	/**
	 * 传入bitmap,切成piece*piece块
	 * 
	 * @param bitmap
	 * @param piece
	 * @return
	 */
	public List<ImagePiece> splitImage(Bitmap bitmap, int piece) {
		List<ImagePiece> lPieces = new ArrayList<ImagePiece>();

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int pieceWidth = Math.min(width, height) / piece;
		for (int i = 0; i < piece; i++) {
			for (int j = 0; j < piece; j++) {
				ImagePiece imagePiece = new ImagePiece();
				imagePiece.setIndex(j + i * piece);

				int x = j * pieceWidth;
				int y = i * pieceWidth;

				imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y,
						pieceWidth, pieceWidth));

				lPieces.add(imagePiece);
			}
		}

		return lPieces;
	}

	/**
	 * 取各个padding数值的最小值
	 * 
	 * @param paddings
	 * @return
	 */
	private int min(int... paddings) {
		int min = paddings[0];
		for (int padding : paddings) {
			min = Math.min(min, padding);
		}
		return min;
	}

	/**
	 * 选中的第一张图片
	 */
	private ImageView ivSelFirst;
	/**
	 * 选中的第二张图片
	 */
	private ImageView ivSelSecond;

	@Override
	public void onClick(View v) {
		if (isAnim || (isTimeLimit && mTime <= 0)) {
			return;
		}

		// 两次点击同一个Item取消
		if (ivSelFirst == v) {
			ivSelFirst.setColorFilter(null);
			ivSelFirst = null;
			return;
		}

		if (null == ivSelFirst) {
			ivSelFirst = (ImageView) v;
			ivSelFirst.setColorFilter(Color.parseColor("#7F640000"));
		} else {
			ivSelSecond = (ImageView) v;
			exchangeView();
		}
	}

	/**
	 * 构造动画层，显示交换动画、图片预览
	 */
	private void initAnimLayout() {
		if (null == rlAnim) {
			rlAnim = new RelativeLayout(getContext());
			addView(rlAnim);
		}
	}

	/**
	 * 交换Item
	 */
	private void exchangeView() {
		ivSelFirst.setColorFilter(null);

		initAnimLayout();

		final Bitmap firstBitmap = getImageIdByTag(ivSelFirst.getTag());
		final Bitmap secondtBitmap = getImageIdByTag(ivSelSecond.getTag());

		// 第一张交换图片
		ImageView ivTemp = new ImageView(getContext());
		ivTemp.setImageBitmap(firstBitmap);
		RelativeLayout.LayoutParams lpTemp = new RelativeLayout.LayoutParams(
				itemWidth, itemWidth);
		lpTemp.leftMargin = ivSelFirst.getLeft() - mPadding;
		lpTemp.topMargin = ivSelFirst.getTop() - mPadding;
		ivTemp.setLayoutParams(lpTemp);
		rlAnim.addView(ivTemp);
		// 设置动画 ivFirst→ivSecond
		TranslateAnimation anim = new TranslateAnimation(0,
				ivSelSecond.getLeft() - ivSelFirst.getLeft(), 0,
				ivSelSecond.getTop() - ivSelFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		ivTemp.startAnimation(anim);

		// 第二张交换图片
		ivTemp = new ImageView(getContext());
		ivTemp.setImageBitmap(secondtBitmap);
		lpTemp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
		lpTemp.leftMargin = ivSelSecond.getLeft() - mPadding;
		lpTemp.topMargin = ivSelSecond.getTop() - mPadding;
		ivTemp.setLayoutParams(lpTemp);
		rlAnim.addView(ivTemp);
		// ivSecond→ivFirst
		anim = new TranslateAnimation(0, -ivSelSecond.getLeft()
				+ ivSelFirst.getLeft(), 0, -ivSelSecond.getTop()
				+ ivSelFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		ivTemp.startAnimation(anim);

		// 监听动画
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				ivSelFirst.setVisibility(View.INVISIBLE);
				ivSelSecond.setVisibility(View.INVISIBLE);

				isAnim = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				String firstTag = (String) ivSelFirst.getTag();
				String secondTag = (String) ivSelSecond.getTag();

				// 正真执行图片交换
				ivSelFirst.setImageBitmap(secondtBitmap);
				ivSelSecond.setImageBitmap(firstBitmap);

				ivSelFirst.setTag(secondTag);
				ivSelSecond.setTag(firstTag);

				ivSelFirst.setVisibility(View.VISIBLE);
				ivSelSecond.setVisibility(View.VISIBLE);

				ivSelFirst = ivSelSecond = null;

				rlAnim.removeAllViews();

				// 判断用户游戏是否成功
				checkSuccess();

				isAnim = false;
			}
		});
	}

	/**
	 * 判断用户游戏是否成功
	 */
	private void checkSuccess() {
		boolean isSuccess = true;
		for (int i = 0; i < items.length; i++) {
			ImageView imageView = items[i];
			if (i != getImageIndexByTag(imageView.getTag())) {
				isSuccess = false;
				break;
			}
		}

		if (isSuccess) {
			isGameSuccess = true;
			handler.removeMessages(TIME_CHANGED);
			handler.sendEmptyMessage(NEXT_LEVEL);
		}
	}

	/**
	 * 通过tag获取bitmap图片
	 * 
	 * @param tag
	 * @return
	 */
	private Bitmap getImageIdByTag(Object tag) {
		String[] split = tag.toString().split(SPLIT);
		int index = Integer.parseInt(split[0]);
		ImagePiece imagePiece = lItemBitmap.get(index);

		return imagePiece.getBitmap();
	}

	/**
	 * 通过tag获取imageview的正常索引顺序
	 * 
	 * @param tag
	 * @return
	 */
	private int getImageIndexByTag(Object tag) {
		String[] split = tag.toString().split(SPLIT);
		return Integer.parseInt(split[1]);
	}

	/**
	 * 游戏界面失去焦点时暂停游戏
	 */
	private boolean isPause;

	/**
	 * 游戏暂停
	 */
	public void pause() {
		isPause = true;
		handler.removeMessages(TIME_CHANGED);
	}

	/**
	 * 重新获取焦点
	 */
	public void resume() {
		if (isPause) {
			isPause = false;
			handler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	/**
	 * 游戏当前关卡重新开始
	 */
	public void restart() {
		mColumn--;
		nextLevel();
	}

	/**
	 * 游戏成功
	 */
	private boolean isGameSuccess;
	/**
	 * 游戏结束
	 */
	private boolean isGameOver;

	/**
	 * 游戏关卡升级
	 */
	public void nextLevel() {
		this.removeAllViews();
		rlAnim = null;
		mColumn++;
		isGameSuccess = false;
		isGameOver = false;
		checkTimeEnable();
		initBitmap();
		initItem();
	}

	/**
	 * 图片预览
	 * 
	 * @param boolean isPress是否按下预览
	 */
	public void preview(boolean isPress) {
		initAnimLayout();

		if (isPress) {
			ImageView ivTemp = new ImageView(getContext());
			ivTemp.setImageBitmap(mBitmap);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					gameWidth, gameWidth);
			lp.topMargin = -mMargin;
			lp.leftMargin = 0;
			ivTemp.setLayoutParams(lp);
			rlAnim.addView(ivTemp);
		} else {
			rlAnim.removeAllViews();
		}
	}

	/**
	 * 动画相关监听接口
	 * 
	 * @Author hyj
	 * @Date 2016-2-4 下午3:44:38
	 */
	public interface OnGameJigsawListener {
		/**
		 * 关卡升级
		 * 
		 * @param nextLevel
		 */
		public void nextLevel(int nextLevel);

		/**
		 * 当前剩余游戏时间
		 * 
		 * @param currentTime
		 */
		public void timeChanged(int currentTime);

		/**
		 * 游戏结束
		 */
		public void gameOver();
	}
}
