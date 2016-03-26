package com.hyj.lib.wechat_imageUp;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.CommonAdapter;

@SuppressLint("ViewConstructor")
public class DirPopupWindow extends PopupWindow {
	private int mWidth;
	private int mHeight;

	private ListView mListView;
	private List<FolderBean> mDatas;

	private FolderBean preSelFolder;// 上一次操作选中的文件

	private OnDirSelectedListener mLisetner;

	public void setOnDirSelectedLisetner(OnDirSelectedListener mLisetner) {
		this.mLisetner = mLisetner;
	}

	public DirPopupWindow(Context context, List<FolderBean> mDatas) {
		this.mDatas = mDatas;

		calWidthAndHeight(context);
		initViews(context);
		initListener();
	}

	/**
	 * 计算popupwindow的宽、高
	 * 
	 * @param context
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels * 0.7);
	}

	@SuppressWarnings("deprecation")
	private void initViews(Context context) {
		View mConvertView = LayoutInflater.from(context).inflate(
				R.layout.wechat_imageup_pop, null);
		setContentView(mConvertView);
		setWidth(mWidth);
		setHeight(mHeight);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);// 外面可以点击
		// 作用是使他点击外面的区域使他消失
		setBackgroundDrawable(new BitmapDrawable());
		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		mListView = (ListView) mConvertView.findViewById(R.id.imgupPopLv);
		mListView.setAdapter(new DirAdapter(context, mDatas,
				R.layout.wechat_imageup_pop_item));
	}

	private void initListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (null != mLisetner) {
					if (null != preSelFolder) {
						preSelFolder.setSel(false);
					}
					FolderBean bean = (FolderBean) parent
							.getItemAtPosition(position);
					bean.setSel(true);
					preSelFolder = bean;
					mLisetner.onSelected(bean);
				}
			}
		});
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		super.showAsDropDown(anchor, xoff, yoff);

		if (null == preSelFolder) {
			preSelFolder = mDatas.get(0);
			preSelFolder.setSel(true);
		}
	}

	/**
	 * 选择文件响应事件
	 * 
	 * @author async
	 * 
	 */
	public interface OnDirSelectedListener {
		/**
		 * item选中
		 * 
		 * @param folder
		 *            FolderBean对象
		 */
		public void onSelected(FolderBean folder);
	}

	/**
	 * 适配器
	 * 
	 * @author async
	 * 
	 */
	private class DirAdapter extends CommonAdapter<FolderBean> {

		public DirAdapter(Context context, List<FolderBean> lDatas,
				int layoutItemID) {
			super(context, lDatas, layoutItemID);
		}

		@Override
		public void getViewItem(com.hyj.lib.tools.adapter.ViewHolder holder,
				FolderBean item) {
			ImageView img = holder.getView(R.id.popIvYl);
			img.setImageResource(R.drawable.pictures_no);
			ImageLoader.getInstance().loadImage(img, item.getFirstImgPath());
			holder.setText(R.id.popTvDirName, item.getDirName());
			holder.setText(R.id.popTvDirImgCount, item.getImgCountStr());

			img = holder.getView(R.id.popImgSel);
			int visible = item.isSel() ? View.VISIBLE : View.GONE;
			img.setVisibility(visible);
		}
	}
}
