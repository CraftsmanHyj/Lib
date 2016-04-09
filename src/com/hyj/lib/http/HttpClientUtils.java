package com.hyj.lib.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient获得网络信息
 * 
 * @author Xiaoxin
 * 
 */
@SuppressWarnings("deprecation")
public class HttpClientUtils extends HttpApi {
	private static HttpClientUtils httpClientUtils = null;

	/**
	 * <pre>
	 * 使用ResponseHandler接口处理响应，
	 * HttpClient使用ResponseHandler会自动管理连接的释放，
	 * 解决对连接的释放管理
	 * </pre>
	 */
	private static ResponseHandler<String> strResponseHandler = new ResponseHandler<String>() {
		// 自定义响应处理
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				String charset = EntityUtils.getContentCharSet(entity) == null ? HttpApi.CHARSET
						: EntityUtils.getContentCharSet(entity);
				return new String(EntityUtils.toByteArray(entity), charset);
			}
			return null;
		}
	};

	/**
	 * 设置重连机制和异常自动恢复处理
	 */
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			// 设置恢复策略，在Http请求发生异常时候将自动重试3次
			if (executionCount >= 3) {
				// Do not retry if over max retry count
				return false;
			}

			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}

			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}

			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}

			return false;
		}
	};

	private HttpClientUtils() {
	}

	/**
	 * 获取一个 HttpClientUtils 实例
	 * 
	 * @return HttpClientUtils
	 */
	public static HttpClientUtils getInstance() {
		if (null == httpClientUtils) {
			synchronized (HttpClientUtils.class) {
				if (null == httpClientUtils) {
					httpClientUtils = new HttpClientUtils();
				}
			}
		}
		return httpClientUtils;
	}

	@Override
	public long getContentLength(String strUrl) {
		DefaultHttpClient httpClient = null;
		HttpGet httpGet = null;
		long length = 0;

		try {
			strUrl = urlEncode(strUrl.trim(), HttpApi.CHARSET);
			httpClient = getDefaultHttpClient(null);
			httpGet = new HttpGet(strUrl);
			putSessionID(httpGet);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			length = httpResponse.getEntity().getContentLength();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(httpGet, httpClient);
		}

		return length;
	}

	@Override
	public InputStream getInputStream(String strUrl) {
		return getInputStream(strUrl, 0, 0);
	}

	@Override
	public InputStream getInputStream(String strUrl, long start, long end) {
		DefaultHttpClient httpClient = null;
		HttpGet httpGet = null;
		InputStream is = null;

		try {
			strUrl = urlEncode(strUrl.trim(), HttpApi.CHARSET);
			httpClient = getDefaultHttpClient(null);
			httpGet = new HttpGet(strUrl);
			putSessionID(httpGet);

			if (start != end && end > 0) {
				httpGet.addHeader("Range", "bytes=" + start + "-" + end);
			}

			HttpResponse response = httpClient.execute(httpGet);
			// 判断是否支持断点续传，分段下载
			if (HttpStatus.SC_PARTIAL_CONTENT == response.getStatusLine()
					.getStatusCode()) {
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				abortConnection(httpGet, httpClient);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return is;
	}

	@Override
	public String getUrlContext(String strUrl) {
		String responseStr = null;// 发送请求，得到响应
		DefaultHttpClient httpClient = null;
		HttpGet httpGet = null;
		try {
			strUrl = urlEncode(strUrl.trim(), HttpApi.CHARSET);
			httpClient = getDefaultHttpClient(null);

			httpGet = new HttpGet(strUrl);
			putSessionID(httpGet);

			responseStr = httpClient.execute(httpGet, strResponseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(httpGet, httpClient);
		}

		return responseStr;
	}

	@Override
	public String postUrlContext(String strUrl, Map<String, String> params) {
		String responseStr = null;// 发送请求，得到响应
		DefaultHttpClient httpClient = null;
		HttpPost httpPost = null;
		try {
			// 存放上传参数
			List<NameValuePair> lPairs = new ArrayList<NameValuePair>();
			if (null != params) {
				for (Map.Entry<String, String> entity : params.entrySet()) {
					lPairs.add(new BasicNameValuePair(entity.getKey(), entity
							.getValue()));// 会自动编码
				}
			}

			strUrl = urlEncode(strUrl.trim(), HttpApi.CHARSET);
			httpClient = getDefaultHttpClient(null);

			httpPost = new HttpPost(strUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(lPairs, HttpApi.CHARSET));
			putSessionID(httpPost);

			responseStr = httpClient.execute(httpPost, strResponseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(httpPost, httpClient);
		}

		return responseStr;
	}

	/**
	 * 转码http的网址，只对中文进行转码
	 * 
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String urlEncode(String str, String charset)
			throws UnsupportedEncodingException {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher m = p.matcher(str);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
		}
		m.appendTail(b);
		return b.toString();
	}

	/**
	 * 设置请求Header中的SessionId值
	 * 
	 * @param httpRequestBase
	 */
	private void putSessionID(HttpRequestBase httpRequestBase) {
		if (null != HttpApi.sessionId) {
			httpRequestBase.setHeader("Cookie", HttpApi.SESSION + "="
					+ HttpApi.sessionId);
		}
	}

	/**
	 * 获取DefaultHttpClient实例
	 * 
	 * @param charSet
	 * @return
	 */
	private DefaultHttpClient getDefaultHttpClient(String charSet) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams();

		// params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_1);
		// 模拟浏览器(有些服务器只支持浏览器访问，这个可以模拟下~~~)
		// params.setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		// params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,Boolean.FALSE);

		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 请求超时
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000); // 读取超时
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
				charSet == null ? HttpApi.CHARSET : charSet);
		httpclient.setHttpRequestRetryHandler(requestRetryHandler);
		return httpclient;
	}

	/**
	 * 释放HttpClient连接
	 * 
	 * @param hrb
	 * @param httpClient
	 */
	private void abortConnection(HttpRequestBase httpRequestBase,
			DefaultHttpClient httpClient) {
		if (null != httpRequestBase) {
			httpRequestBase.abort();
		}

		if (null != httpClient) {
			CookieStore mCookieStore = httpClient.getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			if (null != cookies) {
				for (Cookie cookie : cookies) {
					if (HttpApi.SESSION.equals(cookie.getName())) {
						HttpApi.sessionId = cookie.getValue();
						break;
					}
				}
			}

			httpClient.getConnectionManager().shutdown();
		}
	}
}
