package com.hyj.lib.popup.pulldown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hyj.lib.R;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.view.ListViewShowAll;

/**
 * 弹出下拉列表框
 * 
 * @Author hyj
 * @Date 2016-4-2 下午4:57:59
 */
@SuppressLint("NewApi")
public class PopupSpinner extends LinearLayout {
	private List<Card> lCard;// listview显示数据集合

	// 显示部分
	private LinearLayout llSp;
	private TextView tvShow;// 控件显示内容
	private ImageView imgArrow;// 触发弹出框按钮

	// 弹出部分
	private TextView tvTitle;// 弹出框的标题
	private ListViewShowAll lvContent;// 弹出框的内容列表
	private ScrollView sv;// 整体滑动scrollview
	private SpinnerAdapter adapter;

	private LinearLayout llMedium;// 显示介质按钮
	private PopupWindow window;// 弹出对话框

	private Card selectedItem;// 选中的值

	private Map<Integer, String> mapMedium;// 存放介质名称

	/**
	 * 设置弹出框标题
	 * */
	public void setTitle(int resid) {
		setTitle(getResources().getString(resid));
	}

	/**
	 * 设置弹出框标题
	 * */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	/**
	 * 获取选中的数据
	 * 
	 * @return
	 */
	public Card getSelectedItem() {
		return selectedItem;
	}

	public PopupSpinner(Context context) {
		this(context, null);
	}

	public PopupSpinner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context);
	}

	public void myInit(Context context) {
		initView(context);
		initData();
		initListener();

		initTestData();
	}

	/**
	 * 构造测试数据
	 */
	private void initTestData() {
		setTitle("银行卡列表");

		// 显示介质的值
		List<Medium> lMedium = new ArrayList<Medium>();
		Medium bean = new Medium();
		bean.setName("SD卡");
		bean.setImg(R.drawable.composer_camera);
		lMedium.add(bean);

		bean = new Medium();
		bean.setName("HCE");
		bean.setImg(R.drawable.adj);
		lMedium.add(bean);

		bean = new Medium();
		bean.setName("SIM");
		bean.setImg(R.drawable.img_head);
		lMedium.add(bean);

		List<Card> lData = new ArrayList<Card>();
		// 默认介质首选卡存放在0位置,其次是默认介质卡列表
		for (int i = 0; i < 3; i++) {
			Card card = new Card();
			card.setDpan("621081121000005292" + i);
			card.setCardType(i % 2 + 1 + "");
			card.setDefaultcard(i == 0 ? 1 : 0);
			card.setMediumType(0x00000000);
			lData.add(card);
		}

		for (int i = 0; i < 3; i++) {
			Card card = new Card();
			card.setDpan("531081121000005292" + i);
			card.setCardType(i % 2 + 1 + "");
			card.setDefaultcard(i == 0 ? 1 : 0);
			card.setMediumType(0x00000001);
			lData.add(card);
		}

		for (int i = 0; i < 3; i++) {
			Card card = new Card();
			card.setDpan("441081121000005292" + i);
			card.setCardType(i % 2 + 1 + "");
			card.setDefaultcard(i == 0 ? 1 : 0);
			card.setMediumType(0x00000002);
			lData.add(card);
		}

		setData(lData, lMedium);
	}

	/**
	 * 初始化界面、变量
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.popupspinner, this);// 控件布局

		// 显示部分
		llSp = (LinearLayout) findViewById(R.id.ppll);
		tvShow = (TextView) findViewById(R.id.ppTvShow);
		imgArrow = (ImageView) findViewById(R.id.ppImgArrow);

		// 弹出部分
		View view = inflater.inflate(R.layout.popupspinnerdrop, null);// 弹出框布局
		sv = (ScrollView) view.findViewById(R.id.spSv);
		tvTitle = (TextView) view.findViewById(R.id.drTvTitle);

		lvContent = (ListViewShowAll) view.findViewById(R.id.drLvContent);
		lCard = new ArrayList<Card>();
		adapter = new SpinnerAdapter(lCard, context);
		lvContent.setAdapter(adapter);

		llMedium = (LinearLayout) view.findViewById(R.id.drLlJz);

		// 获取下拉箭头的坐标
		int[] location = new int[2];
		imgArrow.getLocationOnScreen(location);
		int bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.composer_icn_plus).getWidth();// 获取图片宽度
		int toX = (int) (location[0] - bmpW / 2);
		// 根据坐标移动弹出框只是箭头位置
		Animation animation = new TranslateAnimation(0, toX, 0, 0);
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(0);
		imgArrow.startAnimation(animation);

		// 弹出承载内容的window
		window = new PopupWindow(context);
		window.setFocusable(true);
		window.setContentView(view);
		window.setWidth(LayoutParams.MATCH_PARENT);
		window.setHeight(LayoutParams.WRAP_CONTENT);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new BitmapDrawable());
	}

	@SuppressLint("UseSparseArrays")
	private void initData() {
		mapMedium = new HashMap<Integer, String>();
		mapMedium.put(0x00000000, "SD卡");
		mapMedium.put(0x00000001, "NFC卡");
		mapMedium.put(0x00000002, "SIM卡");
		mapMedium.put(0x00000003, "蓝牙卡");
	}

	private void initListener() {
		llSp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sv.smoothScrollTo(0, 0);
				window.showAsDropDown(v);
			}
		});

		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedItem = (Card) parent.getItemAtPosition(position);
				tvShow.setText(selectedItem.toString());

				if (null != window) {
					window.dismiss();
				}
			}
		});
	}

	/**
	 * 设置下拉表要显示的值
	 * 
	 * @param lCard
	 *            银行卡列表
	 * @param lMedium
	 *            介质类型列表
	 */
	public void setData(List<Card> lCard, List<Medium> lMedium) {
		setCardsData(lCard);
		setMediumData(lMedium);
	}

	/**
	 * 设置ListView中显示的值
	 * 
	 * @param lCard
	 */
	private void setCardsData(List<Card> lCard) {
		this.lCard = lCard;
		adapter = new SpinnerAdapter(lCard, getContext());
		lvContent.setAdapter(adapter);

		selectedItem = lCard.get(0);
		tvShow.setText(selectedItem.toString());
	}

	/**
	 * 设置用户介质显示列表
	 * 
	 * @param lMedium
	 */
	private void setMediumData(List<Medium> lMedium) {
		if (null == lMedium || lMedium.isEmpty()) {
			llMedium.setVisibility(View.GONE);
			return;
		}
		llMedium.setVisibility(View.VISIBLE);

		for (final Medium bean : lMedium) {
			TextView tv = new TextView(getContext());
			tv.setText(bean.getName());
			tv.setPadding(10, 10, 10, 10);
			Drawable drIcon = getResources().getDrawable(bean.getImg());
			drIcon.setBounds(0, 0, drIcon.getMinimumWidth(),
					drIcon.getMinimumHeight());
			tv.setCompoundDrawables(null, drIcon, null, null);
			tv.setGravity(Gravity.CENTER);
			llMedium.addView(tv);

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null == bean.getCls()) {
						DialogUtils.showToastShort(getContext(), "即将开放");
					} else {
						DialogUtils.showToastShort(getContext(), "点击");
					}

					window.dismiss();
				}
			});
		}
	}

	/**
	 * 下拉框数据适配器
	 * 
	 * @Author hyj
	 * @Date 2016-4-2 上午12:00:54
	 */
	private class SpinnerAdapter extends BaseAdapter {
		/**
		 * 显示正常的Item
		 */
		private final int ITEM = 0;
		/**
		 * 显示带标题的Item
		 */
		private final int TITLE = 1;

		private List<Card> lData;
		private LayoutInflater inflater;

		public SpinnerAdapter(List<Card> list, Context context) {
			inflater = LayoutInflater.from(context);
			if (null != list) {
				this.lData = list;
			} else {
				this.lData = new ArrayList<Card>();
			}
		}

		@Override
		public int getCount() {
			return lData.size();
		}

		@Override
		public Object getItem(int position) {
			return lData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			Card bean = (Card) getItem(position);
			if (0 == position) {
				return TITLE;
			}

			Card beanLeast = (Card) getItem(position - 1);
			if (beanLeast.getMediumType() != bean.getMediumType()) {
				return TITLE;
			} else {
				return ITEM;
			}
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();

				int itemViewType = getItemViewType(position);
				switch (itemViewType) {
				case ITEM:
					convertView = inflater.inflate(
							R.layout.popupspinnerdropitem, null);
					break;

				case TITLE:
					convertView = inflater.inflate(
							R.layout.popupspinnerdropitemtitle, null);
					holder.tvTitle = (TextView) convertView
							.findViewById(R.id.diTvItemTitle);
					break;
				}

				convertView.setTag(holder);

				holder.tvCardNum = (TextView) convertView
						.findViewById(R.id.diTvCardno);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Card bean = lData.get(position);
			if (holder.tvTitle != null) {
				holder.tvTitle.setText(mapMedium.get(bean.getMediumType()));
			}

			holder.tvCardNum.setText(bean.getDpan());

			return convertView;
		}

		private class ViewHolder {
			private TextView tvTitle;
			private TextView tvCardNum;
		}
	}
}