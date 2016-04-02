package com.hyj.lib.http;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.DialogUtils.DialogAction;

/**
 * Http模块工厂
 * 
 * @author Xiaoxin
 */
public class HttpFactory {
	/**
	 * 0:HttpURLConnection 方式请求网络
	 */
	public static final int TYPE_HTTPURLCON = 0;

	/**
	 * 1:HttpClient方式请求网络
	 */
	public static final int TYPE_HTTPCLIENT = 1;

	/**
	 * <pre>
	 * 	获取一个Http网络请求
	 *  以HttpClient方式去请求网络
	 * </pre>
	 * 
	 * @param Context
	 *            context上下文
	 * @return HttpApi
	 */
	public static HttpApi getHttp(Context context) {
		return getHttp(context, TYPE_HTTPCLIENT);
	}

	/**
	 * 获取一个Http网络请求
	 * 
	 * @param Context
	 *            context上下文
	 * @param int httpType网络请求方式：TYPE_HTTPURLCON、TYPE_HTTPCLIENT
	 * @return HttpApi 返回null：网络异常
	 */
	public static HttpApi getHttp(Context context, int httpType) {
		if (!isOpenNetwork(context)) {
			return null;
		}

		HttpApi http = null;
		switch (httpType) {
		case TYPE_HTTPURLCON:
			http = HttpUrlConUtils.getInstance();
			break;

		case TYPE_HTTPCLIENT:
			http = HttpClientUtils.getInstance();
			break;
		}

		return http;
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isOpenNetwork(final Context context) {
		boolean isAvailable = hasNetwork(context);

		if (!isAvailable) {
			DialogAction okAction = new DialogAction() {

				@Override
				public void action() {
					Intent intent = null;
					int sdkVersion = Build.VERSION.SDK_INT;

					if (sdkVersion > 10) {
						intent = new Intent(Settings.ACTION_SETTINGS);
					} else {
						intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					}

					context.startActivity(intent);
				}
			};

			DialogUtils.showConfirmDialog(context, "提示", "网络未开启，是否马上设置？",
					okAction);

			return false;
		}

		return true;
	}

	/**
	 * 移动网络是否可用(4G/3G/2G网络)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasMobileNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == manager) {
			return false;
		}

		NetworkInfo info = manager.getActiveNetworkInfo();
		return null != info && info.isAvailable();
	}

	/**
	 * WIFI是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasWifiNetwork(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Service.WIFI_SERVICE);
		if (null == wifiManager) {
			return false;
		}

		return WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState();
	}

	/**
	 * 判断手机网络是否正常(包括WIFI、移动网络)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		boolean isNormal = hasWifiNetwork(context);
		isNormal = isNormal ? isNormal : hasNetwork(context);
		return isNormal;
	}
}
