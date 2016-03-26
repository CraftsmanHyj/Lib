package com.hyj.lib.tree.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyj.lib.R;
import com.hyj.lib.tree.TreeNode;

public class TreeListViewAdapter<T> extends BaseAdapter {

	protected Context mContext;
	private List<T> lDatas;// 数据源
	private int defaultExpandLevel;// 要显示的树结构层次
	private List<TreeNode> lNodes;// 处理后数据
	private List<TreeNode> lVisibleNode;// 要显示的数据

	protected LayoutInflater inflater;

	public TreeListViewAdapter(Context mContext, List<T> lDatas,
			int defaultExpandLevel) {
		this.mContext = mContext;
		this.lDatas = lDatas;
		this.defaultExpandLevel = defaultExpandLevel;
		this.inflater = LayoutInflater.from(mContext);

		processDatas(lDatas, defaultExpandLevel);

		// 设置默认显示的值
		if (lVisibleNode.size() <= 0) {
			for (int i = 1; i <= 20; i++) {
				TreeNode node = new TreeNode(i, 0, "树节点　" + i);
				lVisibleNode.add(node);
			}
		}
	}

	/**
	 * 处理源数据，让其成为可以用于展示的数据
	 * 
	 * @param lDatas
	 * @param defaultExpandLevel
	 * @throws Exception
	 */
	private void processDatas(List<T> lDatas, int defaultExpandLevel) {
		lNodes = TreeHelper.getSortedNodes(lDatas, defaultExpandLevel);
		lVisibleNode = TreeHelper.filterVisibleNode(lNodes);
	}

	/**
	 * 树节点展开或收缩
	 * 
	 * @param position
	 */
	public void expandOrCollapse(int position) {
		TreeNode node = lVisibleNode.get(position);

		if (node == null || node.isLeaf()) {
			return;
		}

		node.setExpand(!node.isExpand());
		lVisibleNode = TreeHelper.filterVisibleNode(lNodes);
		// 此处必须调用父类方法
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		processDatas(lDatas, defaultExpandLevel);
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return lVisibleNode.size();
	}

	@Override
	public Object getItem(int position) {
		return lVisibleNode.get(position);
	}

	@Override
	public long getItemId(int position) {
		return lVisibleNode.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TreeNode node = lVisibleNode.get(position);

		convertView = getConvertView(node, position, convertView, parent);

		// 设置左边距
		convertView.setPadding(30 * node.getLevel(), 3, 3, 3);

		return convertView;
	}

	/**
	 * 获取每个Item的View,若item不使用默认的可以重写此方法
	 * 
	 * @param node
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public View getConvertView(TreeNode node, int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.tree_item, parent, false);
			holder = new ViewHolder();
			convertView.setTag(holder);

			holder.imgIcon = (ImageView) convertView
					.findViewById(R.id.tree_icon);
			holder.tvNode = (TextView) convertView.findViewById(R.id.tree_node);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() <= 0) {
			holder.imgIcon.setVisibility(View.INVISIBLE);
		} else {
			holder.imgIcon.setVisibility(View.VISIBLE);
			holder.imgIcon.setImageResource(node.getIcon());
		}
		holder.tvNode.setText(node.getName());

		return convertView;
	}

	/**
	 * 添加一个树节点
	 * 
	 * @param position
	 * @param name
	 */
	public void addExtraNode(int position, String name) {
		TreeNode nodeParent = lVisibleNode.get(position);
		nodeParent.setExpand(true);

		TreeNode bean = new TreeNode((int) System.currentTimeMillis(),
				nodeParent.getId(), name);
		bean.setParent(nodeParent);
		nodeParent.getChildren().add(bean);

		int index = lNodes.indexOf(nodeParent)
				+ nodeParent.getChildren().size();
		lNodes.add(index, bean);

		lVisibleNode = TreeHelper.filterVisibleNode(lNodes);
		super.notifyDataSetChanged();
	}

	public class ViewHolder {
		public ImageView imgIcon;
		private TextView tvNode;
	}
}
