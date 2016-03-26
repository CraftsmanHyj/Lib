package com.hyj.lib.tree.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.hyj.lib.R;
import com.hyj.lib.tree.TreeNode;
import com.hyj.lib.tree.annotation.TreeNodeId;
import com.hyj.lib.tree.annotation.TreeNodeLabel;
import com.hyj.lib.tree.annotation.TreeNodePid;

public class TreeHelper {

	/**
	 * 将用户数据转化成树形数据
	 * 
	 * @param lData
	 * @return
	 * @throws Exception
	 */
	private static <T> List<TreeNode> converDatas2Nodes(List<T> lData) {
		List<TreeNode> lNodes = new ArrayList<TreeNode>();
		// lNodes.add(new NodeBean(0, 0, "根节点"));

		int id = -1, pid = -1;
		String name = "";
		for (T t : lData) {
			Class<? extends Object> clazz = t.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);// 设置字段可以访问
				try {
					if (field.getAnnotation(TreeNodeId.class) != null) {
						id = field.getInt(t);
					} else if (field.getAnnotation(TreeNodePid.class) != null) {
						pid = field.getInt(t);
					} else if (field.getAnnotation(TreeNodeLabel.class) != null) {
						name = (String) field.get(t);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			lNodes.add(new TreeNode(id, pid, name));
		}

		// 设置node间的关系
		for (int i = 0; i < lNodes.size(); i++) {
			TreeNode pNode = lNodes.get(i);
			for (int j = i + 1; j < lNodes.size(); j++) {
				TreeNode cNode = lNodes.get(j);

				if (pNode.getId() == cNode.getPid()) {
					pNode.getChildren().add(cNode);
					cNode.setParent(pNode);
				} else if (pNode.getPid() == cNode.getId()) {
					pNode.setParent(cNode);
					cNode.getChildren().add(pNode);
				}
			}
		}

		// 设置图标
		for (TreeNode node : lNodes) {
			setNodeIcon(node);
		}

		return lNodes;
	}

	/**
	 * 设置节点之前的图片
	 * 
	 * @param node
	 */
	public static void setNodeIcon(TreeNode node) {
		if (node.getChildren().size() > 0) {
			if (node.isExpand()) {
				node.setIcon(R.drawable.tree_ex);
			} else {
				node.setIcon(R.drawable.tree_ec);
			}
		} else {
			node.setIcon(-1);
		}
	}

	/**
	 * 对源数据进行排序
	 * 
	 * @param lData
	 *            源数据
	 * @param defaultExpandLevel
	 *            默认展开几层
	 * @return
	 * @throws Exception
	 */
	public static <T> List<TreeNode> getSortedNodes(List<T> lData,
			int defaultExpandLevel) {
		List<TreeNode> lResult = new ArrayList<TreeNode>();
		List<TreeNode> lNodes = converDatas2Nodes(lData);
		// 获取根节点
		List<TreeNode> lRootNodes = getRootNodes(lNodes);
		for (TreeNode node : lRootNodes) {
			addNode(lResult, node, defaultExpandLevel, node.getLevel());
		}
		return lResult;
	}

	/**
	 * 把一个节点的所有孩子节点都放入result
	 * 
	 * @param lResult
	 * @param node
	 * @param defaultExpandLevel
	 * @param currentLevel
	 */
	private static void addNode(List<TreeNode> lResult, TreeNode node,
			int defaultExpandLevel, int currentLevel) {
		lResult.add(node);
		node.setExpand(defaultExpandLevel >= currentLevel);

		if (!node.isLeaf()) {
			for (TreeNode bean : node.getChildren()) {
				addNode(lResult, bean, defaultExpandLevel, bean.getLevel());
			}
		}
	}

	/**
	 * 获取所有根节点
	 * 
	 * @param lNodes
	 * @return
	 */
	private static List<TreeNode> getRootNodes(List<TreeNode> lNodes) {
		List<TreeNode> lRoot = new ArrayList<TreeNode>();

		for (TreeNode node : lNodes) {
			if (node.isRoot()) {
				lRoot.add(node);
			}
		}

		return lRoot;
	}

	/**
	 * 过滤出所有显示出来的节点
	 * 
	 * @param lDatas
	 * @return
	 */
	public static List<TreeNode> filterVisibleNode(List<TreeNode> lDatas) {
		List<TreeNode> lResult = new ArrayList<TreeNode>();

		for (TreeNode bean : lDatas) {
			if (bean.isRoot() || bean.isParentExpand()) {
				setNodeIcon(bean);
				lResult.add(bean);
			}
		}

		return lResult;
	}
}
