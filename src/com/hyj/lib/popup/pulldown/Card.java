package com.hyj.lib.popup.pulldown;

import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dpan;// 卡号,用于显示的卡号
	private int mediumType;// 介质类型
	private String cardType;// 卡类型 0未知，1借记，2贷记
	private int defaultcard;// 只用于首选卡列表排序，1默认/0非默认

	public String getDpan() {
		return dpan;
	}

	public void setDpan(String dpan) {
		this.dpan = dpan;
	}

	public int getMediumType() {
		return mediumType;
	}

	public void setMediumType(int mediumType) {
		this.mediumType = mediumType;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public int getDefaultcard() {
		return defaultcard;
	}

	public void setDefaultcard(int defaultcard) {
		this.defaultcard = defaultcard;
	}

	@Override
	public String toString() {
		return dpan;
	}
}
