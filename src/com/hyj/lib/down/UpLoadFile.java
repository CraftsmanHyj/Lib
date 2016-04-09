package com.hyj.lib.down;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * <pre>
 * HttpClient文件上传操作
 * </pre>
 * 
 * @author hyj
 * @Date 2016-4-8 下午5:31:57
 */
public class UpLoadFile {
	private Context context;
	private String url;

	public void upLoadHttpClient() {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		MultipartEntity muti = new MultipartEntity();
		File parent = Environment.getExternalStorageDirectory();
		File filAbs = new File(parent, "sky.jpg");

		FileBody fileBody = new FileBody(filAbs);
		muti.addPart("file", fileBody);
		post.setEntity(muti);
		try {
			HttpResponse response = client.execute(post);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
