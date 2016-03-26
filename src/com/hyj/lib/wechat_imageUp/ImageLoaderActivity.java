package com.hyj.lib.wechat_imageUp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hyj.lib.R;
import com.hyj.lib.image_preview.ImagePreviewActivity;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.FileUtils;
import com.hyj.lib.wechat_imageUp.DirPopupWindow.OnDirSelectedListener;
import com.hyj.lib.wechat_imageUp.ImageAdapter.PictureSelectedListener;

@SuppressLint("HandlerLeak")
public class ImageLoaderActivity extends Activity {
	private final int DATA_LOADED = 0X110;// 数据加载完成
	private final String SEPERATOR = ",";// 分隔符

	private GridView mGridView;
	private List<String> lImgs;
	private ImageAdapter adapter;

	private TextView tvDirName;
	private TextView tvDirCount;

	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
	private ProgressDialog mProgressDialog;// 扫描图片进度条
	private DirPopupWindow mDirPopupWindow;// 弹出窗口

	/**
	 * 当前选中的文件夹
	 */
	private FolderBean curSelFolder;

	// 文件名过滤器
	private FilenameFilter filter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
					|| filename.endsWith(".png")) {
				return true;
			}
			return false;
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == DATA_LOADED) {
				data2View();
				mProgressDialog.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wechat_imageup_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		lImgs = new ArrayList<String>();
		adapter = new ImageAdapter(this, lImgs, R.layout.wechat_imageup_item);
		mGridView = (GridView) findViewById(R.id.imgupGridView);
		mGridView.setAdapter(adapter);

		tvDirName = (TextView) findViewById(R.id.imgupTvDirName);
		tvDirCount = (TextView) findViewById(R.id.imgupTvDirCount);

		mDirPopupWindow = new DirPopupWindow(this, mFolderBeans);
	}

	private void initData() {
		// 利用ContentProvider扫描手机中的所有图片
		if (null == FileUtils.getDirExternal()) {
			DialogUtils.showToastShort(this, "当前存储卡不可用");
			return;
		}

		mProgressDialog = ProgressDialog.show(this, null, "正在加载……");

		// 扫描手机中的图片
		new Thread(new Runnable() {

			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = ImageLoaderActivity.this
						.getContentResolver();

				String selection = MediaStore.Images.Media.MIME_TYPE
						+ "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?";
				String[] selectionArgs = new String[] { "image/jpeg",
						"image/png" };
				String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
				Cursor cursor = cr.query(mImgUri, null, selection,
						selectionArgs, sortOrder);

				// 存储遍历过的parentFile，防止重复遍历
				Set<String> mDirPaths = new HashSet<String>();
				FolderBean folder = null;
				while (cursor.moveToNext()) {
					// 拿到图片路径
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					File parentFile = new File(path).getParentFile();
					if (null == parentFile) {
						continue;
					}

					String dirPath = parentFile.getAbsolutePath();
					if (mDirPaths.contains(dirPath)) {
						continue;
					}

					// 获取文件夹下所有图片的名字
					String[] fileNames = parentFile.list(filter);
					if (null == fileNames) {
						continue;
					}

					mDirPaths.add(dirPath);
					folder = new FolderBean();
					folder.setDirPath(dirPath);
					folder.setFirstImgPath(path);
					folder.setImgCount(fileNames.length);
					mFolderBeans.add(folder);
				}
				cursor.close();

				curSelFolder = new FolderBean();
				curSelFolder.setDirName("所有图片");
				curSelFolder.setFirstImgPath(mFolderBeans.get(0)
						.getFirstImgPath());
				String imgPath = "";
				int imgCount = 0;
				for (FolderBean bean : mFolderBeans) {
					imgPath += bean.getDirPath() + SEPERATOR;
					imgCount += bean.getImgCount();
				}
				curSelFolder.setDirPath(imgPath.substring(0,
						imgPath.length() - 1));
				curSelFolder.setImgCount(imgCount);

				mFolderBeans.add(0, curSelFolder);
				gridViewDatas();

				// 通知handler扫描图片完成
				mHandler.sendEmptyMessage(DATA_LOADED);
			}
		}).start();
	}

	private void initListener() {
		tvDirName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDirPopupWindow.setAnimationStyle(R.style.popupwindow_anim);
				// 设置显示位置
				mDirPopupWindow.showAsDropDown(tvDirName, 0, 0);

				// 设置显示内容区域变暗
				lightSwitch(0.3f);
			}
		});

		mDirPopupWindow.setOnDirSelectedLisetner(new OnDirSelectedListener() {
			@Override
			public void onSelected(FolderBean folder) {
				curSelFolder = folder;
				gridViewDatas();
				data2View();
				mDirPopupWindow.dismiss();
			}
		});

		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				lightSwitch(1.0f);
			}
		});

		adapter.setOnPictureSelectedListener(new PictureSelectedListener() {
			@Override
			public void onSelected(int SelCount) {
				tvDirCount.setText(curSelFolder.getImgCountStr(SelCount));
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ImageLoaderActivity.this,
						ImagePreviewActivity.class);
				intent.putStringArrayListExtra(ImagePreviewActivity.IMAGE,
						(ArrayList<String>) lImgs);
				intent.putExtra(ImagePreviewActivity.INDEX, position);
				startActivity(intent);
			}
		});
	}

	/**
	 * 内容区域明暗度设置
	 */
	private void lightSwitch(float alpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = alpha;
		getWindow().setAttributes(lp);
	}

	/**
	 * 把扫描完成的数据显示在GridView中
	 */
	private void data2View() {
		if (curSelFolder.getImgCount() <= 0) {
			Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
			return;
		}

		adapter.notifyDataSetChanged();
		tvDirName.setText(curSelFolder.getDirName());
		tvDirCount.setText(curSelFolder.getImgCountStr());
	}

	/**
	 * GridView上显示的数据
	 * 
	 * @param FolderBean
	 */
	private void gridViewDatas() {
		lImgs.clear();
		String[] dirPaths = curSelFolder.getDirPath().split(SEPERATOR);

		File selDir;
		String[] fileNames;
		for (String path : dirPaths) {
			selDir = new File(path);
			fileNames = selDir.list(filter);

			for (String stra : fileNames) {
				lImgs.add(path + File.separator + stra);
			}
		}
	}
}
