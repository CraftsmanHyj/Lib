package com.hyj.lib.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.hyj.lib.R;
import com.hyj.lib.db.DBHelper;

/**
 * 与文件相关操作工具类
 * 
 * @author Administrator
 * 
 */
@SuppressLint("SdCardPath")
public class FileUtils {
	/**
	 * 文件不存在
	 */
	private static final int FILENOTEXISTS = -1;
	/**
	 * SD可用大小
	 */
	public static final int SD_SIZE_AVAILABLE = 0X001;
	/**
	 * SD已用大小
	 */
	public static final int SD_SIZE_USED = 0x002;
	/**
	 * SD总大小
	 */
	public static final int SD_SIZE_TOTAL = 0x003;

	/**
	 * 获取应用在内存中的缓存目录：/data/data/packageName/cache/
	 * 
	 * @param context
	 *            上下文
	 * @return File
	 */
	private static File getCacheDirInternal(Context context) {
		return context.getApplicationContext().getCacheDir();
	}

	/**
	 * 获取应用在SD卡中的缓存目录：/mnt/sdcard/Anaroid/data/packageName/cache/
	 * 
	 * @param context
	 * @return File
	 */
	private static File getCacheDirExternal(Context context) {
		return context.getApplicationContext().getExternalCacheDir();
	}

	/**
	 * <pre>
	 *  获取应用的缓存目录
	 *  	若SD存在：/mnt/sdcard/Anaroid/data/packageName/cache/
	 * 		SD不存在：/data/data/packageName/cache/
	 * </pre>
	 * 
	 * @param context
	 * @return File
	 */
	public static File getCacheDir(Context context) {
		File cacheDir = getCacheDirExternal(context);
		if (null == cacheDir) {
			cacheDir = getCacheDirInternal(context);
		}
		return cacheDir;
	}

	/**
	 * <pre>
	 * 获取应用中files文件,此文件夹中一般放一些长时间保存的数据，应用卸载时被删除
	 * 路径：/data/data/packageName/files/
	 * </pre>
	 * 
	 * @param context
	 * @return
	 */
	private static File getFilesDirInternal(Context context) {
		context = context.getApplicationContext();
		return context.getFilesDir();
	}

	/**
	 * <pre>
	 * 获取应用中files文件,此文件夹中一般放一些长时间保存的数据，应用卸载时被删除
	 * 路径：/mnt/sdcard/Anaroid/data/packageName/files/
	 * </pre>
	 * 
	 * @param context
	 * @return
	 */
	private static File getFilesDirExternal(Context context) {
		context = context.getApplicationContext();
		return context.getExternalFilesDir(null);
	}

	/**
	 * <pre>
	 * 获取应用中files文件,此文件夹中一般放一些长时间保存的数据，应用卸载时被删除
	 * 
	 * 若SD卡存在：/mnt/sdcard/Anaroid/data/packageName/files/
	 * SD卡不存在：/data/data/packageName/files/
	 * </pre>
	 * 
	 * @param context
	 * @return
	 */
	public static File getFilesDir(Context context) {
		File files = getFilesDirExternal(context);
		if (null == files) {
			files = getFilesDirInternal(context);
		}
		return files;
	}

	/**
	 * 获取手机内存数据存储目录：/data
	 * 
	 * @return
	 */
	private static File getDirInternal() {
		return Environment.getDataDirectory();
	}

	/**
	 * 获取外部存储卡路径：/mnt/sdcard
	 * 
	 * @return
	 */
	public static File getDirExternal() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取手机存放数据的根目录
	 * 若SD卡存在：/mnt/sdcard 
	 * SD卡不存在：/data
	 * </pre>
	 * 
	 * @return File
	 */
	public static File getDataDir() {
		File dir = getDirExternal();
		if (null == dir) {
			dir = getDirInternal();
		}
		return dir;
	}

	/**
	 * <pre>
	 * 获取应用存放数据的根目录
	 * 若SD卡存在：/mnt/sdcard/packageName 
	 * SD卡不存在：/data/packageName
	 * </pre>
	 * 
	 * @param context_上下文
	 * @return
	 */
	public static File getAppRootDir(Context context) {
		context = context.getApplicationContext();
		String rootDirName = context.getPackageName();

		File dir = getDataDir();
		File rootDir = new File(dir, rootDirName);
		if (!rootDir.exists()) {
			rootDir.mkdirs();

			// 创建一个文件说明文档
			String filePath = File.separator + "readme.txt";
			String appName = context.getResources()
					.getString(R.string.app_name);
			String msg = "此文件夹是应用《" + appName + "》的数据文件夹;\n";
			msg += "保存应用的一些设置、缓存、临时等数据;\n";
			msg += "不可删除;";
			saveFileFromBytes(context, msg.getBytes(), filePath);
		}

		return rootDir;
	}

	/**
	 * <pre>
	 * 在应用数据目录中获取一个文件夹
	 * 	若SD卡存在：/mnt/sdcard/packageName/dirPath
	 * 	SD卡不存在：/data/packageName/dirPath
	 * </pre>
	 * 
	 * @param dirPath文件夹名字
	 *            例：dirName或/dirName1/dirName2
	 * @return File
	 */
	public static File getAppDir(Context context, String dirPath) {
		File dir = getAppRootDir(context);// 获得数据所在文件夹
		File file = new File(dir, dirPath);// 获取数据文件
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * <pre>
	 * 在指定路径中创建一个文件
	 * 	若SD卡存在：/mnt/sdcard/packageName/filePath
	 * 	SD卡不存在：/data/packageName/filePath
	 * </pre>
	 * 
	 * @param context
	 * @param String
	 *            filePath文件路径 例：reader.txt或/download/reader.txt
	 * @return File 若果路径有误,返回null
	 */
	public static File getAppFile(Context context, String filePath) {
		File file = null;
		if (filePath.contains(File.separator)) {
			String path = filePath.substring(0,
					filePath.lastIndexOf(File.separator));
			file = getAppDir(context, path);
			file = new File(file, filePath.substring(filePath
					.lastIndexOf(File.separator)));
		} else {
			file = new File(getAppRootDir(context), filePath);
		}

		return file;
	}

	/**
	 * 把字节数组保存为文件,保存位置：……/包名/ 目录
	 * 
	 * @param context
	 * @param bytes
	 *            数据源
	 * @param filePath
	 *            filePath文件路径 例：reader.txt或/download/reader.txt
	 * @return File 返回保存的File
	 */
	public static File saveFileFromBytes(Context context, byte[] bytes,
			String filePath) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = getAppFile(context, filePath);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != stream) {
				try {
					stream.flush();
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 将bitmap对象保存为图片
	 * 
	 * @param context
	 *            上下文
	 * @param bitmap
	 *            图片源
	 * @param String
	 *            path：/temp/picture.jpg
	 * @return File保存的图片对象
	 */
	public static File saveFileFromBitmap(Context context, Bitmap bitmap,
			String path) {
		File picture = FileUtils.getAppFile(context, path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(picture);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fos) {
					fos.flush();
					fos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return picture;
	}

	/**
	 * 读取一个文件
	 * 
	 * @param String
	 *            path文件绝对路径
	 * @return String
	 */
	public static String readFile(String path) {
		File file = new File(path);
		String content = null;

		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[5 * 1024];
			while (is.read(buffer) != -1) {
				baos.write(buffer, 0, buffer.length);
			}
			content = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 删除指定目录下的文件夹、目录
	 * 
	 * @param obj
	 *            传入参数可以是：StringPath、File两种
	 */
	public static void deleteFileByDirectory(Object obj) {
		File dir = isFile(obj);
		if (null == dir) {
			return;
		}

		if (dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				deleteFileByDirectory(file);
			}
		}

		dir.delete();
	}

	/**
	 * 删除应用数据库文件
	 * 
	 * @param context
	 */
	public static void cleanDataBase(Context context) {
		context = context.getApplicationContext();
		File dbFile = context.getDatabasePath(DBHelper.DB_NAME);
		deleteFileByDirectory(dbFile);
	}

	/**
	 * 删除应用的SharedPreference(/data/data/packageName/shared_prefs)
	 * 
	 * @param context
	 */
	public static void cleanSharedPreference(Context context) {
		context = context.getApplicationContext();
		String path = "/data/data/" + context.getPackageName()
				+ "/shared_prefs";
		deleteFileByDirectory(path);
	}

	/**
	 * 删除应用缓存数据
	 * 
	 * @param context
	 */
	public static void cleanCacheData(Context context) {
		// 删除内存中的缓存文件
		deleteFileByDirectory(getCacheDirInternal(context));
		// 删除SD卡中的缓存文件
		deleteFileByDirectory(getCacheDirExternal(context));
	}

	/**
	 * <pre>
	 * 删除 ……/data/packageName/files/ 中一些长时间保存的数据
	 * 删除的文件有/data/data/packageName/files/
	 * 			 /mnt/sdcard/Anaroid/data/packageName/files/
	 * </pre>
	 * 
	 * @param context
	 */
	public static void cleanFilesDir(Context context) {
		deleteFileByDirectory(getFilesDirInternal(context));
		deleteFileByDirectory(getFilesDirExternal(context));
	}

	/**
	 * <pre>
	 * 判断传入的Object是否是一个文件,是：返回一个文件;否：创建一个文件返回
	 * Object只可为：File、String路径两种
	 * </pre>
	 * 
	 * @param obj
	 *            File/String Path
	 * @return
	 */
	private static File isFile(Object obj) {
		File file = null;
		if (obj instanceof File) {
			file = (File) obj;
		} else if (obj instanceof String) {
			file = new File((String) obj);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return file;
	}

	/**
	 * 获取一个文件/文件夹的大小
	 * 
	 * @param obj
	 *            可传File或String Path文件路径
	 * @return long 文件大小
	 */
	public static long getFileSize(Object obj) {
		File file = isFile(obj);
		if (null == file) {
			return 0;
		}

		long size = 0;
		if (file.exists() && file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					getFileSize(f);
				} else {
					size += f.length();
				}
			}
		} else {
			size += file.length();
		}
		return size;
	}

	/**
	 * 获取SD卡大小
	 * 
	 * @param sizeType
	 *            想要获取大小的类型(可用、已用、总大小)
	 * @return long 返回值<0文件不存在
	 */
	public static long getSDCardSize(int sizeType) {
		File externalFile = getDirExternal();
		if (null == externalFile) {
			return FILENOTEXISTS;
		}

		StatFs stat = new StatFs(externalFile.getPath());// 得到文件系统情况
		long blockSize = stat.getBlockSizeLong();// 获取每块的大小
		long blockCount = stat.getBlockCountLong();// 总块数
		long blockAvailable = stat.getAvailableBlocksLong();// 可用块数
		long availableSize = blockSize * blockAvailable;// 可用空间大小
		long totalSize = blockSize * blockCount;// 总大小

		long size = 0;
		switch (sizeType) {
		case SD_SIZE_AVAILABLE:
			size = availableSize;
			break;

		case SD_SIZE_USED:
			size = totalSize - availableSize;
			break;

		case SD_SIZE_TOTAL:
			size = totalSize;
			break;
		}

		return size;
	}

	/**
	 * 获取.amr录音文件的录音时长
	 * 
	 * @param path
	 * @return int 返回值<0,文件不存在;正常：1″
	 */
	public static int getAmrFileDuration(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return FILENOTEXISTS;
		}

		long duration = -1;// 录音时长，ms为单位
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;

			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? (length - 6) / 650 : 0;
					break;
				}

				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}

			duration += frameCount * 20;// 帧数*20
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != randomAccessFile) {
					randomAccessFile.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return Math.round(duration * 1.0f / 1000);
	}

	/**
	 * 将文件大小转换成合适的单位
	 * 
	 * @param fileSize
	 *            文件大小：123456B;
	 * @return
	 */
	public static String formatFileSize(double fileSize) {
		double kiloByte = fileSize / 1024;
		if (kiloByte < 1) {
			return fileSize + "Byte";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}

		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}

	/**
	 * 根据运行的操作系统格式化文件路径
	 * 
	 * @param path
	 * @return
	 */
	public static String formatFilePath(String path) {
		if ('\\' == File.separatorChar && path.contains("\\")) {
			return path.replace('/', '\\');
		} else if ('/' == File.separatorChar && path.contains("/")) {
			return path.replace('\\', '/');
		} else {
			return path;
		}
	}

	/**
	 * 获取assets文件夹中文件的数据流，返回InputStream
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件名
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getAssetsStream(Context context, String fileName)
			throws Exception {
		return context.getResources().getAssets().open(fileName);
	}
}
