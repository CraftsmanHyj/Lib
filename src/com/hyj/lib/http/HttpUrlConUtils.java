package com.hyj.lib.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.hyj.lib.tools.LogUtils;

/**
 * @title Http请求工具类
 * 
 * @author Xiaoxin
 */
public class HttpUrlConUtils extends HttpApi {
	/**
	 * 截取返回报文的长度
	 */
	private final int len = 500;

	private static HttpUrlConUtils httpUtils = null;

	private HttpUrlConUtils() {
	}

	/**
	 * 获取一个HttpUtils实例
	 * 
	 * @return HttpUtils
	 */
	public static HttpUrlConUtils getInstance() {
		if (null == httpUtils) {
			synchronized (HttpUrlConUtils.class) {
				if (null == httpUtils) {
					httpUtils = new HttpUrlConUtils();
				}
			}
		}
		return httpUtils;
	}

	/**
	 * 获取一个HttpURLConnection连接
	 * 
	 * @param strUrl
	 * @return HttpURLConnection
	 */
	private HttpURLConnection getConnection(String strUrl) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (null != HttpApi.sessionId) {
				conn.setRequestProperty("Cookie", HttpApi.SESSION + "="
						+ HttpApi.sessionId);
			}
			conn.setConnectTimeout(5000);// 请求超时
			conn.setReadTimeout(5000);// 读取超时
			conn.setDoOutput(true);// 设置头字段，允许对外输出数据
			conn.setDoInput(true);//
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	@Override
	public long getContentLength(String strUrl) {
		HttpURLConnection conn = null;
		int length = 0;

		try {
			conn = getConnection(strUrl);
			conn.setRequestMethod(GET);
			if (HttpStatus.SC_OK == conn.getResponseCode()) {
				length = conn.getContentLength();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(conn);
		}

		return length;
	}

	@Override
	public InputStream getInputStream(String strUrl) {
		return getInputStream(strUrl, 0, 0);
	}

	@Override
	public InputStream getInputStream(String strUrl, long start, long end) {
		HttpURLConnection conn = null;
		InputStream is = null;

		try {
			conn = getConnection(strUrl);
			conn.setRequestMethod(GET);

			if (start != end && end > 0) {
				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
			}

			if (HttpStatus.SC_PARTIAL_CONTENT == conn.getResponseCode()) {
				is = conn.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				abortConnection(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return is;
	}

	@Override
	public String getUrlContext(String strUrl) {
		HttpURLConnection conn = null;
		InputStream is = null;

		try {
			conn = getConnection(strUrl);
			conn.setRequestMethod(GET);
			conn.connect();

			// 默认不会立即发送给服务器, 只有当试图取得服务器返回信息的时候,信息才会真正的发送给服务器
			if (HttpStatus.SC_OK == conn.getResponseCode()) {
				is = conn.getInputStream();
				/**
				 * <pre>
				 * 这里指获取了500（len=500）字节，
				 * 如果想整个网页全部获取可以用conn.getContentLength()来代替len
				 * </pre>
				 */
				String responseStr = readInputStream(is, len);

				LogUtils.d(HttpUrlConUtils.class.getSimpleName()
						+ " The response is: " + responseStr);

				return responseStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				abortConnection(conn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String postUrlContext(String strUrl, Map<String, String> params) {
		if (null == params || params.isEmpty()) {
			return null;
		}

		// post获取seesionId问题http://bbs.9ria.com/thread-246113-1-1.html
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;

		try {
			StringBuilder sb = new StringBuilder();
			// 数据格式：title=postreqeust&time=90
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), HttpApi.CHARSET))
						.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
			byte[] entity = sb.toString().getBytes();// 得到实体数据

			conn = getConnection(strUrl);
			conn.setRequestMethod(POST);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");// 设置类型
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));// 设置实体长度
			os = conn.getOutputStream();// 从管道中取得输出流对象
			os.write(entity);// 写出数据

			// 默认不会立即发送给服务器, 只有当试图取得服务器返回信息的时候,信息才会真正的发送给服务器
			int status = conn.getResponseCode();
			if (HttpStatus.SC_OK == status) {
				is = conn.getInputStream();
				String responseStr = readInputStream(is, len);

				LogUtils.d(HttpUrlConUtils.class.getSimpleName()
						+ " The response is: " + responseStr);

				return responseStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				abortConnection(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 读取 InputStream 内容
	 * 
	 * @param stream
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readInputStream(InputStream stream, int len)
			throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, HttpApi.CHARSET);
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

	/**
	 * 断开网络连接
	 * 
	 * @param conn
	 */
	private void abortConnection(HttpURLConnection conn) {
		if (null != conn) {
			// 获取保存的sessionID
			HttpApi.sessionId = conn.getHeaderField("Set-Cookie");

			conn.disconnect();
		}
	}
}
