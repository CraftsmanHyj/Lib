package com.hyj.lib.luckydial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 存放中奖信息实体类
 * 
 * @Author hyj
 * @Date 2015-12-28 上午9:29:21
 */
public class PrizeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	// 中奖概率基数
	private static int percentTotal = 100;

	private int imgId;// 图片ID
	private String label;// 文字信息
	private int percent = -1;// 中奖百分比
	private int startPercent;// 开始百分比
	private int endPercent;// 结束百分比

	public PrizeInfo() {
		this(0, "", -1);
	}

	/**
	 * @param label
	 *            奖项名
	 */
	public PrizeInfo(String label) {
		this(0, label, -1);
	}

	/**
	 * @param label
	 *            奖项名
	 * @param percent
	 *            中奖率
	 */
	public PrizeInfo(String label, int percent) {
		this(0, label, percent);
	}

	/**
	 * @param imgId
	 *            展示图片ID
	 * @param label
	 *            文字信息
	 */
	public PrizeInfo(int imgId, String label) {
		this(imgId, label, -1);
	}

	/**
	 * 中奖信息bean
	 * 
	 * @param imgId
	 *            图片ID
	 * @param label
	 *            显示文字信息
	 * @param percent
	 *            设置中奖百分比，设置为0永远不可能中奖
	 */
	public PrizeInfo(int imgId, String label, int percent) {
		this.imgId = imgId;
		this.label = label;
		this.percent = percent;
	}

	/**
	 * 奖项名称
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getPercent() {
		return percent;
	}

	/**
	 * 中奖百分比
	 * 
	 * @param percent
	 *            设置中奖百分比，设置为0永远不可能中奖
	 */
	public void setPercent(int percent) {
		this.percent = percent;
	}

	public int getStartPercent() {
		return startPercent;
	}

	public void setStartPercent(int startPercent) {
		this.startPercent = startPercent;
	}

	public int getEndPercent() {
		return endPercent;
	}

	public void setEndPercent(int endPercent) {
		this.endPercent = endPercent;
	}

	/**
	 * 设置中奖概率基数
	 * 
	 * @param int percent中奖概率基数，默认100
	 */
	public static void setPercentTotal(int percent) {
		percentTotal = percent;
	}

	/**
	 * <pre>
	 * 设置每个奖项开始、介绍百分比值
	 * </pre>
	 * 
	 * @param lPrize
	 *            所有奖品
	 * @return List 带有中奖区间的奖项
	 */
	public static List<PrizeInfo> setPrizePercent(List<PrizeInfo> lPrize) {
		// 设置的总比分数不能大于100
		int percentCount = 0;
		for (PrizeInfo prize : lPrize) {
			percentCount += prize.getPercent() < 0 ? 0 : prize.getPercent();
			if (percentCount > percentTotal) {
				try {
					throw new Error("所有奖品中奖率总和大于" + percentTotal + "，请检查");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 设置中奖百分比
		List<PrizeInfo> lNoPercent = new ArrayList<PrizeInfo>();
		percentCount = 0;
		for (PrizeInfo prize : lPrize) {
			if (prize.getPercent() >= 0) {
				prize.setStartPercent(percentCount);
				percentCount += prize.getPercent();
				prize.setEndPercent(percentCount);
			} else {
				lNoPercent.add(prize);
			}
		}

		if (lNoPercent.size() > 0) {
			int per = 0;
			if (percentCount < percentTotal) {
				per = (percentTotal - percentCount) / lNoPercent.size();
			}

			for (PrizeInfo prize : lNoPercent) {
				prize.setStartPercent(percentCount);
				percentCount += per;
				prize.setPercent(percentCount);
				prize.setEndPercent(percentCount);
			}
		}

		return lPrize;
	}

	/**
	 * 基于比值随机获取中奖奖项
	 * 
	 * @param lPrize
	 *            所有奖品
	 * @return
	 */
	public static PrizeInfo getPrize(List<PrizeInfo> lPrize) {
		if (null == lPrize || lPrize.size() <= 0) {
			return null;
		}

		return lPrize.get(getPrizeIndex(lPrize));
	}

	/**
	 * <pre>
	 * 基于比值随机获取中奖奖项索引
	 * </pre>
	 * 
	 * @param lPrize
	 *            所有奖品
	 * @return int 奖品List索引
	 */
	public static int getPrizeIndex(List<PrizeInfo> lPrize) {
		if (null == lPrize || lPrize.size() <= 0) {
			return -1;
		}

		// 取1~100随机数,返回指定[m,n](含头尾)范围的值,Math.random()*(n-m+1)+m;
		int index = (int) (Math.random() * percentTotal + 1);

		PrizeInfo prize;
		int start = 0, end = 0;
		boolean found = false;
		for (int i = 0; i < lPrize.size(); i++) {
			prize = lPrize.get(i);
			start = prize.getStartPercent();
			end = prize.getEndPercent();

			if (start != end && index > start && index <= end) {
				found = true;
				return i;
			}

			start = end;
		}

		if (!found) {
			getPrizeIndex(lPrize);
		}

		return new Random().nextInt(lPrize.size());
	}
}