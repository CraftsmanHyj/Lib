package com.hyj.lib.listviewrefresh;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyj.lib.R;

/**
 * 实现下/上拉刷新listview
 * 
 * @author async
 * 
 */
@SuppressLint("SimpleDateFormat")
public class ListViewRefresh extends ListView implements OnScrollListener {
	private final int NORMAIL = 0X001;// 正常状态
	private final int PULL = 0X002;// 下拉状态
	private final int RELEASE = 0X003;// 松开释放状态
	private final int REFRESHING = 0X004;// 正在刷新

	private int stateRefresh = NORMAIL;// 当前刷新状态
	private int stateScroll;// 当前滚动状态

	private View header;// 顶部标题header
	private int headerHeight;// 顶部布局文件的
	private int firstVisibleItem = 0;// 当前第一个可见Item的位置

	private boolean isPullDownRefresh = false;// 是否是下拉刷新
	private int startY = 0;// 记录按下是的Y值

	private View footer;// 底部布局
	private int footerHeight;// 底部布局文件高度
	private int totalItemCount;// listView中item的总数量
	private int lastVisibleItem;// 最后一个可见的Item

	// header中的view
	private ProgressBar headerPbLoadding;
	private ImageView headerIvArrow;
	private TextView headerTvTip;
	private TextView headerTvTime;

	// 控制箭头是否旋转
	private boolean arrowRotate = false;

	private OnRefreshListener refreshListener;// 刷新事件

	private RotateAnimation ra02180;// 从0转动到180
	private RotateAnimation ra18020;// 从180转动到0

	/**
	 * 设置刷新事件
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public ListViewRefresh(Context context) {
		this(context, null);
	}

	public ListViewRefresh(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ListViewRefresh(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	private void myInit(Context context, AttributeSet attrs) {
		initAttrs(attrs);
		initView(context);
		initData();
		initListener();
	}

	private void initAttrs(AttributeSet attrs) {

	}

	/**
	 * 厨师话界面,添加header到listview
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.listviewrefresh_header, null);

		// 设置header隐藏
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		setHeaderTopPadding(-headerHeight);
		addHeaderView(header);// 添加顶部文件

		// 实例化header里面的控件
		headerPbLoadding = (ProgressBar) header
				.findViewById(R.id.refreshHeaderPbLoading);
		headerIvArrow = (ImageView) header
				.findViewById(R.id.refreshHeaderIvArrow);
		headerTvTip = (TextView) header.findViewById(R.id.refreshHeaderTvTip);
		headerTvTime = (TextView) header.findViewById(R.id.refreshHeaderTvTime);

		// 底部布局
		footer = inflater.inflate(R.layout.listviewrefresh_footer, null);
		measureView(footer);
		footerHeight = footer.getMeasuredHeight();
		setFooterBottomPadding(-footerHeight);
		addFooterView(footer);
	}

	private void initData() {
		// 从0转到180
		ra02180 = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra02180.setDuration(500);
		ra02180.setFillAfter(true);
		// 从180转到0度
		ra18020 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra18020.setDuration(500);
		ra18020.setFillAfter(true);
	}

	private void initListener() {
		setOnScrollListener(this);
	}

	/**
	 * 通知父布局view所占的宽、高
	 * 
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if (null == params) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int width = ViewGroup.getChildMeasureSpec(0, 0, params.width);
		int height;
		int tempHeight = params.height;
		if (tempHeight > 0) {// 高度不为空，需要填充布局
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {// 高度为空则不需要填充
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		// 测量宽高
		view.measure(width, height);
	}

	/**
	 * 设置header的上边距
	 * 
	 * @param top
	 */
	private void setHeaderTopPadding(int top) {
		header.setPadding(header.getPaddingLeft(), top,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	/**
	 * 设置footer下边距
	 * 
	 * @param bottom
	 */
	private void setFooterBottomPadding(int bottom) {
		footer.setPadding(footer.getPaddingLeft(), footer.getPaddingTop(),
				footer.getPaddingRight(), bottom);
		footer.invalidate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.stateScroll = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if (REFRESHING == stateRefresh) {
				break;
			}

			int tempY = (int) ev.getY();
			int space = tempY - startY;// 移动的距离

			if (space > 0) {
				isPullDownRefresh = true;
				onTouchMove(ev, space);
			} else {
				isPullDownRefresh = false;
			}
			break;

		case MotionEvent.ACTION_UP:
			arrowRotate = false;
			if (isPullDownRefresh && 0 == firstVisibleItem) {
				if (RELEASE == stateRefresh) {
					stateRefresh = REFRESHING;
					// 加载最新数据
					if (refreshListener != null) {
						refreshListener.onPullDownLisetner();
					}
				} else if (PULL == stateRefresh) {
					stateRefresh = NORMAIL;
					isPullDownRefresh = false;
				}

				refreshViewByState();
			} else if (!isPullDownRefresh && totalItemCount == lastVisibleItem
					&& NORMAIL == stateRefresh) {
				stateRefresh = REFRESHING;
				setFooterBottomPadding(0);
				if (refreshListener != null) {
					refreshListener.onPullUpListener();
				}
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * 判断手指画滑动过程中的操作
	 * 
	 * @param ev
	 */
	private void onTouchMove(MotionEvent ev, int space) {
		// header与上边距的距离
		int topPadding = space - headerHeight;
		// 超过这个高度就刷新
		int refreshHeight = headerHeight + 50;

		switch (stateRefresh) {
		case NORMAIL:
			if (space > 0) {
				stateRefresh = PULL;
				refreshViewByState();
			}
			break;

		case PULL:
			setHeaderTopPadding(topPadding);
			arrowRotate = true;

			// 判断是否达到刷新条件
			if (space > refreshHeight
					&& SCROLL_STATE_TOUCH_SCROLL == stateScroll) {
				stateRefresh = RELEASE;
				refreshViewByState();
			}
			break;

		case RELEASE:
			setHeaderTopPadding(topPadding);

			if (space < refreshHeight) {
				stateRefresh = PULL;
				refreshViewByState();
			} else if (space <= 0) {
				stateRefresh = NORMAIL;
				isPullDownRefresh = false;
				refreshViewByState();
			}
			break;
		}
	}

	/**
	 * 根据状态改变相应文字
	 */
	private void refreshViewByState() {
		// 清除上一次的动画
		headerIvArrow.clearAnimation();

		switch (stateRefresh) {
		case NORMAIL:
			setHeaderTopPadding(-headerHeight);
			break;

		case PULL:
			headerPbLoadding.setVisibility(View.GONE);
			headerIvArrow.setVisibility(View.VISIBLE);
			if (arrowRotate) {
				headerIvArrow.startAnimation(ra18020);
			}
			headerTvTip.setText("下拉可以刷新");
			break;

		case RELEASE:
			headerPbLoadding.setVisibility(View.GONE);
			headerIvArrow.setVisibility(View.VISIBLE);
			headerIvArrow.startAnimation(ra02180);
			headerTvTip.setText("松开执行刷新");
			break;

		case REFRESHING:
			setHeaderTopPadding(0);// 正在刷新的时候有一个显示的header固定的高度
			headerPbLoadding.setVisibility(View.VISIBLE);
			headerIvArrow.setVisibility(View.GONE);
			headerTvTip.setText("正在刷新......");
			break;
		}
	}

	/**
	 * 完成刷新
	 */
	public void refreshComplete() {
		stateRefresh = NORMAIL;
		isPullDownRefresh = false;
		refreshViewByState();

		// 设置刷新完成时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		headerTvTime.setText(sf.format(new Date(System.currentTimeMillis())));

		setFooterBottomPadding(-footerHeight);
	}

	/**
	 * 数据 刷新接口
	 */
	public interface OnRefreshListener {
		/**
		 * 下拉刷新
		 */
		public void onPullDownLisetner();

		/**
		 * 上拉刷新
		 */
		public void onPullUpListener();
	}
}
