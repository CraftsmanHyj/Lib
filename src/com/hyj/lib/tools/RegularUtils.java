package com.hyj.lib.tools;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 正则表达式校验工具类
 * 
 * @Author hyj
 * @Date 2015-12-16 下午3:23:30
 */
public class RegularUtils {
	private static final String REGEX_SFZ = "^[1-9](\\d{14}|\\d{17}|\\d{16}(\\d|x|X))$";
	private static final String REGEX_MAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	private static final String REGEX_MOBILE = "^[1][3,4,5,8,7][0-9]{9}$";
	private static final String REGEX_TEL = "^\\d{3-4}-?\\d{7-8}$";
	private static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]+$";
	private static final String REGEX_SPACE = "(\\n|\\s|\\t\\r)+";

	// 用于身份证最后一位校验位计算
	private static final int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,
			5, 8, 4, 2, 1 };
	private static final int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };

	// 每月最大天数
	private static HashMap<String, Integer> dateMap;
	// 身份证中地区代码
	private static Hashtable<String, String> hashtable;

	static {
		dateMap = new HashMap<String, Integer>();
		dateMap.put("01", 31);
		dateMap.put("02", null);
		dateMap.put("03", 31);
		dateMap.put("04", 30);
		dateMap.put("05", 31);
		dateMap.put("06", 30);
		dateMap.put("07", 31);
		dateMap.put("08", 31);
		dateMap.put("09", 30);
		dateMap.put("10", 31);
		dateMap.put("11", 30);
		dateMap.put("12", 31);

		hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
	}

	/**
	 * 验证手机号码
	 * 
	 * @param String
	 *            str
	 * @return boolean
	 */
	public static boolean isMobile(String str) {
		Pattern pattern = Pattern.compile(REGEX_MOBILE);
		return pattern.matcher(str).matches();
	}

	/**
	 * 验证座机号码是否正确
	 * 
	 * @param String
	 *            str
	 * @return boolean
	 */
	public static boolean isTel(String str) {
		Pattern pattern = Pattern.compile(REGEX_TEL);
		return pattern.matcher(str).matches();
	}

	/**
	 * 验证邮箱
	 * 
	 * @param String
	 *            email
	 * @return boolean
	 */
	public static boolean isEmail(String email) {
		Pattern pattern = Pattern.compile(REGEX_MAIL);
		return pattern.matcher(email).matches();
	}

	/**
	 * 判断输入的字符串是否为中文
	 * 
	 * @param String
	 *            str
	 * @return boolean
	 */
	public static boolean isChinese(String str) {
		Pattern pattern = Pattern.compile(REGEX_CHINESE);
		return pattern.matcher(str).matches();
	}

	/**
	 * 将字符串中的中/英文空格、\n、\r、\t全部替换掉成非空格
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceSpace(String str) {
		Pattern pattern = Pattern.compile(REGEX_SPACE);
		return pattern.matcher(str).replaceAll("");
	}

	/**
	 * 验证身份证号码是否合法，验证项：长度、省份代码、出生日期合法性、最后一位校验位
	 * 
	 * @param String
	 *            idCode
	 * @return boolean
	 */
	@SuppressLint("DefaultLocale")
	public static boolean verifyIDCard(String idCode) {
		idCode = idCode.trim().toUpperCase();
		if (TextUtils.isEmpty(idCode)) {
			return false;
		}

		Pattern pattern = Pattern.compile(REGEX_SFZ);
		if (!pattern.matcher(idCode).matches()) {
			return false;
		}

		String eifhteencard = idCode;
		if (15 == idCode.length()) {
			eifhteencard = uptoeighteen(idCode);
		}

		if (!verifyAreaCode(eifhteencard)) {
			return false;
		}

		if (!verifyDate(eifhteencard.substring(6, 14))) {
			return false;
		}

		if (!verifyMOD(eifhteencard)) {
			return false;
		}

		return true;
	}

	/**
	 * 验证身份证中省区是否合法
	 * 
	 * @param String
	 *            code
	 * @return boolean
	 */
	private static boolean verifyAreaCode(String code) {
		String areaCode = code.substring(0, 2);
		return hashtable.containsKey(areaCode);
	}

	/**
	 * 验证输入的年月日是否合法
	 * 
	 * @param String
	 *            date 20150126
	 * @return
	 */
	public static boolean verifyDate(String date) {
		String month = date.substring(4, 6);
		if (!dateMap.containsKey(month)) {
			return false;
		}

		Integer dayCode = Integer.parseInt(date.substring(6, 8));
		Integer day = dateMap.get(month);
		Integer year = Integer.valueOf(date.substring(0, 4));

		if (null != day) {
			if (dayCode > day || dayCode < 1) {
				return false;
			}
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				if (dayCode > 29 || dayCode < 1) {
					return false;
				}
			} else {
				if (dayCode > 28 || dayCode < 1) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 将15位身份证转换成18位
	 * 
	 * @param fifteencardid
	 * @return
	 */
	private static String uptoeighteen(String fifteencardid) {
		String eightcardid = fifteencardid.substring(0, 6);
		eightcardid = eightcardid + "19";
		eightcardid = eightcardid + fifteencardid.substring(6, 15);
		eightcardid = eightcardid + getVerify(eightcardid);
		return eightcardid;
	}

	/**
	 * 验证身份证最后一位与算法算出来的最后一位是否一致
	 * 
	 * @param code
	 * @return
	 */
	private static boolean verifyMOD(String code) {
		String verify = code.substring(17, 18);
		if ("x".equals(verify)) {
			code = code.replaceAll("x", "X");
			verify = "X";
		}

		String verifyIndex = getVerify(code);
		if (verify.equals(verifyIndex)) {
			return true;
		}
		return false;
	}

	/**
	 * 计算第18位保留位的值
	 * 
	 * @param eightcardid
	 * @return
	 */
	private static String getVerify(String eightcardid) {
		int remaining = 0;

		if (eightcardid.length() == 18) {
			eightcardid = eightcardid.substring(0, 17);
		}

		if (eightcardid.length() == 17) {
			int[] ai = new int[17];
			int sum = 0;
			for (int i = 0; i < 17; i++) {
				String k = eightcardid.substring(i, i + 1);
				ai[i] = Integer.parseInt(k);
			}

			for (int i = 0; i < 17; i++) {
				sum = sum + wi[i] * ai[i];
			}
			remaining = sum % 11;
		}

		return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
	}
}
