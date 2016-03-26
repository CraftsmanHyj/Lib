package com.hyj.lib.tree;

import java.io.Serializable;

import com.hyj.lib.tree.annotation.TreeNodeId;
import com.hyj.lib.tree.annotation.TreeNodeLabel;
import com.hyj.lib.tree.annotation.TreeNodePid;

public class FileBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@TreeNodeId
	private int id;// 自己的id

	@TreeNodePid
	private int pId;// 父id

	@TreeNodeLabel
	private String label;// 描述

	private String desc;

	public FileBean() {
	}

	public FileBean(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public FileBean(int id, int pId, String label) {
		this.id = id;
		this.pId = pId;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
