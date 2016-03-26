package com.hyj.lib.listviewindex;

import java.io.Serializable;

import android.text.TextUtils;

import com.hyj.lib.tools.CharacterParser;

/**
 * 用于排序的拼音类
 * 
 * @Author hyj
 * @Date 2015-12-23 下午2:26:50
 */
public class SortSpell implements Serializable {
	private static final long serialVersionUID = 1L;
	private CharacterParser parser;

	private String sortFile;// 字符串
	private String allLetter = "";// 全部拼音
	private String eachStartLetter = "";// 每个拼音开头
	private String startLetter = "";// 字符串拼音开头

	public SortSpell() {
		this("");
	}

	public SortSpell(String sortFile) {
		parser = CharacterParser.getInstance();
		setSortFile(sortFile);
	}

	/**
	 * 获得排序用的字符串
	 * 
	 * @return
	 */
	public String getSortFile() {
		return sortFile;
	}

	public void setSortFile(String sortFile) {
		this.sortFile = sortFile.trim();

		if (TextUtils.isEmpty(sortFile)) {
			return;
		}

		allLetter = parser.getSelling(sortFile);
		eachStartLetter = parser.getSelling(sortFile, true);
		startLetter = allLetter.substring(0, 1);
	}

	/**
	 * 全拼
	 * 
	 * @return
	 */
	public String getAllLetter() {
		return allLetter;
	}

	/**
	 * 获取每个字母开头
	 * 
	 * @return
	 */
	public String getEachStartLetter() {
		return eachStartLetter;
	}

	/**
	 * 获取字符串开头
	 * 
	 * @return
	 */
	public String getStartLetter() {
		return startLetter;
	}
}
