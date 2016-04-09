package com.hyj.lib.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.hyj.lib.R;
import com.hyj.lib.tree.Tree.OnTreeNodeClickListener;
import com.hyj.lib.tree.Tree.onTreeNodeLongClickListener;

public class TreeActivity extends Activity {
	private List<FileBean> lDatas;
	private Tree<FileBean> mTree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tree_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		mTree = (Tree<FileBean>) findViewById(R.id.treeLvCustom);
	}

	private void initData() {
		lDatas = new ArrayList<FileBean>();
		FileBean file = new FileBean(0, 0, "根节点");
		lDatas.add(file);

		Random random = new Random();

		int count = random.nextInt(200) + 10;
		for (int i = 1; i < count; i++) {
			file = new FileBean(i, random.nextInt(i), "树节点" + i);
			lDatas.add(file);
		}

		mTree.setDatas(lDatas);
	}

	private void initListener() {
		mTree.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

			@Override
			public void onTreeNodeClick(TreeNode node, int position) {
				if (node.isLeaf()) {
					Toast.makeText(TreeActivity.this, node.getName(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mTree.setOnTreeNodeLongClickListener(new onTreeNodeLongClickListener() {

			@Override
			public void onTreeNodeLongclick(final TreeNode node,
					final int position) {
				final EditText et = new EditText(TreeActivity.this);
				// 官方一般推荐使用DialogFragment来实现这种弹出框
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TreeActivity.this);
				builder.setTitle("添加树节点");
				builder.setView(et);
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("添加", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTree.addExtraNode(position, et.getText().toString());
					}
				});
				builder.show();
			}
		});
	}
}
