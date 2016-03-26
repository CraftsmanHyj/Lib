package com.hyj.lib.tree;

import java.util.ArrayList;
import java.util.List;

import com.hyj.lib.R;
import com.hyj.lib.tree.utils.TreeListViewAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Tree<T> extends LinearLayout {
	private int expandLevel = 1;// 默认展开节点层次

	private List<T> lDatas;
	private ListView lvTree;
	private TreeListViewAdapter<T> adapter;

	private Context context;

	private OnTreeNodeClickListener onTreeNodeClickListener;// 树节点点击事件
	private onTreeNodeLongClickListener onTreeNodeLongClickListener;// 树节点长按事件

	/**
	 * 树节点点击事件
	 * 
	 * @param onTreeNodeClick
	 */
	public void setOnTreeNodeClickListener(
			OnTreeNodeClickListener onTreeNodeClickListener) {
		this.onTreeNodeClickListener = onTreeNodeClickListener;
	}

	/**
	 * 树节点长按事件
	 * 
	 * @param onTreeNodeLongClick
	 */
	public void setOnTreeNodeLongClickListener(
			onTreeNodeLongClickListener onTreeNodeLongClickListener) {
		this.onTreeNodeLongClickListener = onTreeNodeLongClickListener;
	}

	public Tree(Context context) {
		this(context, null);
	}

	public Tree(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Tree(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.context = context;

		myInit(attrs);
	}

	private void myInit(AttributeSet attrs) {
		initAttrs(attrs);
		initView();
		initDatas();
		initListener();
	}

	private void initAttrs(AttributeSet attrs) {
		this.setOrientation(LinearLayout.VERTICAL);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.tree);
		expandLevel = ta.getInteger(R.styleable.tree_expandlevel, expandLevel);
		ta.recycle();
	}

	private void initView() {
		lvTree = new ListView(context);
		lvTree.setDivider(null);
		addView(lvTree);
	}

	private void initDatas() {
		lDatas = new ArrayList<T>();
		adapter = new TreeListViewAdapter<T>(context, lDatas, expandLevel);
		lvTree.setAdapter(adapter);
	}

	private void initListener() {
		lvTree.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.expandOrCollapse(position);

				if (onTreeNodeClickListener != null) {
					TreeNode node = (TreeNode) parent
							.getItemAtPosition(position);
					onTreeNodeClickListener.onTreeNodeClick(node, position);
				}
			}
		});

		lvTree.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (onTreeNodeLongClickListener != null) {
					TreeNode node = (TreeNode) parent
							.getItemAtPosition(position);
					onTreeNodeLongClickListener.onTreeNodeLongclick(node,
							position);
				}
				return false;
			}
		});
	}

	/**
	 * 设置树的数据源
	 * 
	 * @param lDatas
	 */
	public void setDatas(List<T> lDatas) {
		this.lDatas.clear();
		this.lDatas.addAll(lDatas);
		adapter.notifyDataSetChanged();
	}

	public void addExtraNode(int position, String name) {
		adapter.addExtraNode(position, name);
	}

	/**
	 * 树节点点击事件
	 * 
	 * @author async
	 * 
	 */
	public interface OnTreeNodeClickListener {
		public void onTreeNodeClick(TreeNode node, int position);
	}

	/**
	 * 树节点长按事件
	 * 
	 * @author Administrator
	 * 
	 */
	public interface onTreeNodeLongClickListener {
		public void onTreeNodeLongclick(TreeNode node, int position);
	}
}
