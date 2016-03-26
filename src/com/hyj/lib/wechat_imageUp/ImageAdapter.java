package com.hyj.lib.wechat_imageUp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hyj.lib.R;
import com.hyj.lib.tools.adapter.CommonAdapter;
import com.hyj.lib.tools.adapter.ViewHolder;
import com.hyj.lib.wechat_imageUp.ImageLoader.Type;

public class ImageAdapter extends CommonAdapter<String> {
	private final String COLOR = "#77000000";// 灰暗背景颜色

	// 当切换文件夹的时候共享数据集
	public Set<String> sSelImg = new HashSet<String>();

	private ImageLoader loader;

	private PictureSelectedListener mSelListener;

	/**
	 * 设置图片选中事件
	 * 
	 * @param mSelListener
	 */
	public void setOnPictureSelectedListener(
			PictureSelectedListener mSelListener) {
		this.mSelListener = mSelListener;
	}

	public ImageAdapter(Context context, List<String> lDatas, int layoutItemID) {
		super(context, lDatas, layoutItemID);

		loader = ImageLoader.getInstance(3, Type.LIFO);
	}

	@Override
	public void getViewItem(ViewHolder holder, final String path) {
		final ImageView imgPic = holder.getView(R.id.imgupItemImageView);
		final ImageView imgSel = holder.getView(R.id.imgupItemSelect);

		// 重置状态
		imgPic.setImageResource(R.drawable.pictures_no);
		imgPic.setColorFilter(null);
		loader.loadImage(imgPic, path);
		imgSel.setImageResource(R.drawable.picture_unselected);

		if (sSelImg.contains(path)) {
			imgPic.setColorFilter(Color.parseColor(COLOR));
			imgSel.setImageResource(R.drawable.pictures_selected);
		}

		imgSel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sSelImg.contains(path)) {// 已经被选择
					sSelImg.remove(path);
					imgPic.setColorFilter(null);
					imgSel.setImageResource(R.drawable.picture_unselected);
				} else {// 未被选择
					sSelImg.add(path);
					imgPic.setColorFilter(Color.parseColor(COLOR));
					imgSel.setImageResource(R.drawable.pictures_selected);
				}

				if (null != mSelListener) {
					mSelListener.onSelected(sSelImg.size());
				}
			}
		});
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		sSelImg.clear();// 清空之前所选择的数据
	}

	/**
	 * 图片选中事件
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PictureSelectedListener {
		/**
		 * 
		 * @param SelCount
		 *            选中图片数
		 */
		public void onSelected(int SelCount);
	}
}
