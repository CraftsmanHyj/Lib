package com.hyj.lib.popup;

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

@SuppressLint("NewApi")
public class PopupSpinner extends LinearLayout {
	private Context context;
	private List<Card> lData;// listview显示数据集合

	private LinearLayout llSp;
	private TextView tvShow;// 控件显示内容
	private ImageView imgArrow;// 触发弹出框按钮

	private TextView tvTitle;// 弹出框的标题
	private ListViewShowAll lvContent;// 弹出框的内容列表
	private ScrollView sv;// 整体滑动scrollview
	private SpinnerAdapter adapter;

	private LinearLayout llMedium;// 显示介质按钮
	private PopupWindow window;// 弹出对话框

	private Card selectedItem;// 选中的值

	private Map<Integer, String> mapMedium;// 存放介质名称

	public PopupSpinner(Context context) {
		this(context, null);
	}

	public PopupSpinner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		myInit();
	}

	public void myInit() {
		initView(context);
		initData();
		initListener();

		setData(null);
	}

	/**
	 * 初始化界面、变量
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.popupspinner, this);// 控件布局

		llSp = (LinearLayout) findViewById(R.id.ppll);
		tvShow = (TextView) findViewById(R.id.ppTvShow);
		imgArrow = (ImageView) findViewById(R.id.ppImgArrow);

		View view = inflater.inflate(R.layout.popupspinnerdrop, null);// 弹出框布局
		tvTitle = (TextView) view.findViewById(R.id.drTvTitle);

		lvContent = (ListViewShowAll) view.findViewById(R.id.drLvContent);
		lData = new ArrayList<Card>();
		adapter = new SpinnerAdapter(lData, context);
		lvContent.setAdapter(adapter);

		sv = (ScrollView) view.findViewById(R.id.spSv);
		sv.smoothScrollTo(0, 0);

		llMedium = (LinearLayout) view.findViewById(R.id.drLlJz);

		window = new PopupWindow(context);
		window.setFocusable(true);

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
		// author.startAnimation(animation);

		window.setContentView(view);
		window.setWidth(LayoutParams.MATCH_PARENT);
		window.setHeight(LayoutParams.WRAP_CONTENT);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new BitmapDrawable());
	}

	private void initData() {
		mapMedium = new HashMap<Integer, String>();
		mapMedium.put(0x00000000, "SD卡");
		mapMedium.put(0x00000001, "NFC卡");
		mapMedium.put(0x00000002, "SIM卡");
		mapMedium.put(0x00000003, "蓝牙卡");

		tvTitle.setText("下拉列表测试");
	}

	private void initListener() {
		llSp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				window.showAsDropDown(v);
			}
		});

		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedItem = (Card) parent.getItemAtPosition(position);
				tvShow.setText(selectedItem.toString());

				if (window != null) {
					window.dismiss();
				}
			}
		});
	}

	/**
	 * 设置对话框标题
	 * */
	public void setTitle(String title) {
		this.tvTitle.setText(title);
	}

	/**
	 * 重载setTitle(String)
	 * */
	public void setTitle(int resid) {
		this.tvTitle.setText(getResources().getString(resid));
	}

	/**
	 * 设置显示的值
	 * 
	 * @param lData
	 */
	public void setCardsData(List<Card> lData) {
		this.lData = lData;
		adapter = new SpinnerAdapter(lData, context);
		lvContent.setAdapter(adapter);

		selectedItem = lData.get(0);
		tvShow.setText(selectedItem.toString());

		llMedium.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param lMedium
	 *            介质列表
	 */
	public void setData(List<Medium> lMedium) {
		setCardsData(getCardList());

		if (lMedium != null && !lMedium.isEmpty()) {
			llMedium.setVisibility(View.VISIBLE);

			for (final Medium bean : lMedium) {
				TextView tv = new TextView(context);
				tv.setText(bean.getName());
				tv.setPadding(10, 10, 10, 10);
				Drawable drIcon = getResources().getDrawable(bean.getImg());
				drIcon.setBounds(0, 0, drIcon.getMinimumWidth(),
						drIcon.getMinimumHeight());
				tv.setCompoundDrawables(null, drIcon, null, null);
				llMedium.addView(tv);

				tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (bean.getCls() == null) {
							DialogUtils.showToastShort(context, "即将开放");
							return;
						} else {
							DialogUtils.showToastShort(context, "点击");
						}
					}
				});
			}
		}
	}

	/**
	 * 获取选中的数据
	 * 
	 * @return
	 */
	public Card getSelectedItem() {
		return selectedItem;
	}

	/**
	 * 获取用户已有卡列表
	 * 
	 * @return
	 */
	public List<Card> getCardList() {
		List<Card> lData = new ArrayList<Card>();

		// 默认介质首选卡存放在0位置,其次是默认介质卡列表
		for (int i = 0; i < 3; i++) {
			Card bean = new Card();
			bean.setDpan("621081121000005292" + i);
			bean.setCardType(i % 2 + 1 + "");
			bean.setDefaultcard(i == 0 ? 1 : 0);
			bean.setMediumType(0x00000000);
			lData.add(bean);
		}

		for (int i = 0; i < 3; i++) {
			Card bean = new Card();
			bean.setDpan("631081121000005292" + i);
			bean.setCardType(i % 2 + 1 + "");
			bean.setDefaultcard(i == 0 ? 1 : 0);
			bean.setMediumType(0x00000001);
			lData.add(bean);
		}

		for (int i = 0; i < 3; i++) {
			Card bean = new Card();
			bean.setDpan("641081121000005292" + i);
			bean.setCardType(i % 2 + 1 + "");
			bean.setDefaultcard(i == 0 ? 1 : 0);
			bean.setMediumType(0x00000002);
			lData.add(bean);
		}
		return lData;
	}

	/**
	 * 数据适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class SpinnerAdapter extends BaseAdapter {
		private Context context;
		private List<Card> lData;

		private final int ITEM = 0;// 显示正常item
		private final int TITLE = 1;// 显示带标题的item

		public SpinnerAdapter(List<Card> list, Context context) {
			this.context = context;
			if (list != null) {
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
			Card bean = lData.get(position);
			if (0 == position) {
				return TITLE;
			} else {
				Card beanLeast = lData.get(position - 1);
				if (beanLeast.getMediumType() != bean.getMediumType()) {
					return TITLE;
				} else {
					return ITEM;
				}
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				int itemViewType = getItemViewType(position);
				switch (itemViewType) {
				case ITEM:
					convertView = LayoutInflater.from(context).inflate(
							R.layout.popupspinnerdropitem, null);
					break;

				case TITLE:
					convertView = LayoutInflater.from(context).inflate(
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