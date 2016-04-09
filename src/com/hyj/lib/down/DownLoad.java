package com.hyj.lib.down;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;

import com.hyj.lib.Constants;
import com.hyj.lib.tools.FileUtils;
import com.hyj.lib.tools.LogUtils;

/**
 * 
 * <pre>
 * 单文件、多线程、在线下载操作
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-8 下午5:32:16
 */
public class DownLoad {
	private final int THREADCOUNT = 3;// 开启线程数
	private Executor threadPool;
	private static Context context;

	public DownLoad(Context context) {
		DownLoad.context = context;
		threadPool = Executors.newFixedThreadPool(THREADCOUNT);
	}

	public void downLoadFile(String url) {
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl
					.openConnection();
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			int fileLength = conn.getContentLength();
			int block = fileLength / THREADCOUNT;

			long start, end;
			for (int i = 0; i < THREADCOUNT; i++) {
				start = i * block;
				end = (i + 1) * block - 1;
				if (i == THREADCOUNT - 1) {
					end = fileLength;
				}

				DownLoadRunnable downThread = new DownLoadRunnable(url, start,
						end);
				threadPool.execute(downThread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class DownLoadRunnable implements Runnable {
		private String url;
		private String fileName;
		private long start;
		private long end;

		public DownLoadRunnable(String url, long start, long end) {
			super();
			this.url = url;
			this.start = start;
			this.end = end;

			this.fileName = url.substring(url.lastIndexOf("/") + 1);
		}

		@Override
		public void run() {
			URL httpUrl;
			try {
				httpUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) httpUrl
						.openConnection();
				conn.setReadTimeout(5000);
				// 指定文件下载的开始与结束位置
				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
				conn.setRequestMethod("GET");

				// 创建一个写入文件
				String path = File.separator + Constants.DIR_DOWNLOAD
						+ File.separator + fileName;
				File file = FileUtils.getAppFile(DownLoad.context, path);

				RandomAccessFile access = new RandomAccessFile(new File(file,
						fileName), "rwd");
				access.seek(start);// 将写入位置调到开始位置

				InputStream is = conn.getInputStream();
				byte[] data = new byte[4 * 1024];
				int len = 0;
				while ((len = is.read(data)) != -1) {
					access.write(data, 0, len);
				}

				if (is != null) {
					is.close();
				}

				if (access != null) {
					access.close();
				}

				LogUtils.i("线程：" + start + "　完成");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
