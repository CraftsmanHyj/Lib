package com.hyj.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Process;

import com.hyj.lib.tools.FileUtils;
import com.hyj.lib.tools.Utils;

/**
 * <pre>
 * 程序崩溃日志处理
 * 调用方法，在Activity或Application中注册一下即可：
 * 		CrashHandler crashHandler = CrashHandler.getInstance();
 * 		crashHandler.init(getApplicationContext());
 * 		//当App启动的时候发送上一次未发送成功的崩溃日志
 * 		crashHandler.sendCrashInfoToServer();
 * </pre>
 * 
 * @Author hyj
 * @Date 2016-1-29 上午10:52:45
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/**
	 * 崩溃日志文件后缀名
	 */
	private final String EXTENTION = "-crash.txt";
	/**
	 * 文件存放根目录
	 */
	private final String BASEPATH = File.separator + Constants.DIR_LOG
			+ File.separator;

	private Context context;
	private JSONObject jsonObject;
	private UncaughtExceptionHandler exceptionHandler;

	private static CrashHandler instance;

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		if (null == instance) {
			synchronized (CrashHandler.class) {
				if (null == instance) {
					instance = new CrashHandler();
				}
			}
		}
		return instance;
	}

	public void init(Context context) {
		this.context = context;
		this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		dealCrashInfo(ex);

		// 如果用户没有处理则让系统默认的异常处理器来处理
		exceptionHandler.uncaughtException(thread, ex);

		ex.printStackTrace();
		Process.killProcess(Process.myPid());
		System.exit(10);
	}

	/**
	 * 处理崩溃信息
	 * 
	 * @param ex
	 */
	private void dealCrashInfo(Throwable ex) {
		if (null == ex) {
			return;
		}

		try {
			if (null == jsonObject) {
				jsonObject = new JSONObject();
			}

			getDeviceInfo();
			saveCrashInfo(ex);

			// 发送错误报告到服务器
			sendCrashInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取手机设备信息
	 * 
	 * @throws JSONException
	 */
	private void getDeviceInfo() throws JSONException {
		jsonObject.put("platform", "Android");
		jsonObject.put("time", Utils.getCurrentTime());
		jsonObject.put("model", Utils.getMobileType());
		jsonObject.put("osCode", Utils.getOSVersionCode());
		jsonObject.put("osName", Utils.getOSVersionName());
		jsonObject.put("appCode", Utils.getAppVersionCode(context));
		jsonObject.put("appName", Utils.getAppVersionName(context));
	}

	/**
	 * 保存崩溃日志
	 * 
	 * @param ex
	 * @throws Exception
	 */
	private void saveCrashInfo(Throwable ex) throws Exception {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);

		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		printWriter.flush();
		printWriter.close();
		writer.flush();
		writer.close();

		String result = writer.toString();

		jsonObject.put("msgKey", ex.getMessage());
		jsonObject.put("errorDetail", result);

		String path = BASEPATH + Utils.getCurrentTime() + EXTENTION;

		FileUtils.saveFileFromBytes(context, jsonObject.toString().getBytes(),
				path);
	}

	/**
	 * <pre>
	 * 	在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 * 	把错误报告发送给服务器,包含新产生的和以前没发送的
	 * </pre>
	 */
	public void sendCrashInfo() {
		String[] crFiles = getCrashInfoFiles(context);

		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = FileUtils.getAppFile(context, BASEPATH + fileName);
				if (sendToServer(cr)) {
					cr.delete();// 删除已发送的报告
				}
			}
		}
	}

	/**
	 * 向服务端发送崩溃日志
	 * 
	 * @param file
	 * @return
	 */
	private boolean sendToServer(File file) {
		// TODO 使用HTTP Post 发送错误报告到服务器
		// 这里不再详述,开发者可以根据OPhoneSDN上的其他网络操作
		// 教程来提交错误报告

		return false;
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashInfoFiles(Context ctx) {
		File filesDir = FileUtils.getAppDir(ctx, Constants.DIR_LOG);

		// 文件过滤器
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(EXTENTION);
			}
		};

		return filesDir.list(filter);
	}
}