package com.hyj.lib.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int pid;// 父id
	private String name;// 节点名

	@SuppressWarnings("unused")
	private int level;// 树的层级
	private boolean isExpand = false;// 是否展开
	private int icon;// 图标
	private TreeNode parent;// 父节点
	private List<TreeNode> children = new ArrayList<TreeNode>();

	public TreeNode() {
		super();
	}

	public TreeNode(int id, int pid, String name) {
		super();
		this.id = id;
		this.pid = pid;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 得到当前节点的层级
	 * 
	 * @return
	 */
	public int getLevel() {
		return parent == null ? 1 : parent.getLevel() + 1;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
		// 如果是折叠则将其子节点也折叠
		if (!isExpand) {
			for (TreeNode node : children) {
				node.setExpand(isExpand);
			}
		}
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	/**
	 * 是否是根节点
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return parent == null ? true : false;
	}

	/**
	 * 判断父节点是否是展开状态
	 * 
	 * @return
	 */
	public boolean isParentExpand() {
		if (parent == null) {
			return false;
		} else {
			return parent.isExpand();
		}
	}

	/**
	 * 判断是否是叶子节点
	 * 
	 * @return
	 */
	public boolean isLeaf() {
		return children.size() <= 0;
	}
}
