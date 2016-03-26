package com.hyj.lib.tools;

import java.security.MessageDigest;

import android.text.TextUtils;

/**
 * 加密工具类
 * 
 * @Author hyj
 * @Date 2015-12-16 下午3:11:06
 */
public class EncryptionUtils {
	/**
	 * 加密密钥
	 */
	private static String key = "badboy";

	/**
	 * 十六进制下数字到字符的映射数组
	 */
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 
	 * @param str加密字符串
	 * @return
	 */
	public static String encryptionPassword(String str) {
		return encodeByMD5(str);
	}

	/**
	 * 验证输入的密码是否正确
	 * 
	 * @param password加密后的密码
	 * 
	 * @param str输入的字符串
	 * @return 验证结果，TRUE:正确 FALSE:错误
	 */
	public static boolean validatePassword(String password, String str) {
		return password.equals(encodeByMD5(str));
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private static String encodeByMD5(String str) {
		if (!TextUtils.isEmpty(str)) {
			try {
				str = str + key;
				// 创建具有指定算法名称的信息摘要
				MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = md.digest(str.getBytes());
				// 将得到的字节数组变成字符串返回
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * 
	 * @param 字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 将一个字节转化成十六进制形式的字符串
	 * 
	 * @param b字节
	 * @return
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
}
