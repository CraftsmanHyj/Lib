package com.hyj.lib.tuling;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyj.lib.R;
import com.hyj.lib.http.HttpApi;
import com.hyj.lib.http.HttpFactory;
import com.hyj.lib.tools.LogUtils;
import com.hyj.lib.tuling.bean.ChatMessage;
import com.hyj.lib.tuling.bean.Result;
import com.hyj.lib.utils.ProgressExecutor;

public class TulingActivity extends Activity {
	// 图灵机器人api接入地址
	private final String URL = "http://www.tuling123.com/openapi/api";
	// 用户key
	private final String KEY = "4ab35a0afd5dd41ac21b1cdcb77039f3";

	private List<ChatMessage> lData;
	private TLAdapter adapter;
	private ListView lvTalk;
	private EditText etMsg;
	private Button btSend;

	private HttpApi http;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tuling_mian);

		myInit();
	}

	private void myInit() {
		initView();
		initDatas();
		initListener();
	}

	private void initView() {
		lData = new ArrayList<ChatMessage>();
		adapter = new TLAdapter(this, lData);
		lvTalk = (ListView) findViewById(R.id.tlLvTalk);
		lvTalk.setAdapter(adapter);

		etMsg = (EditText) findViewById(R.id.tlEtMsg);
		btSend = (Button) findViewById(R.id.tlBtSend);
	}

	private void initDatas() {
		http = HttpFactory.getHttp(this);
	}

	private void initListener() {
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				talk();
			}
		});
	}

	protected void talk() {
		final String msg = etMsg.getText().toString().trim();
		if (TextUtils.isEmpty(msg)) {
			return;
		}
		etMsg.setText("");

		ChatMessage chat = new ChatMessage();
		chat.setType(com.hyj.lib.tuling.bean.ChatMessage.Type.OUTCOMING);
		chat.setMsg(msg);
		chat.setDate(new Date());
		updateListView(chat);

		ProgressExecutor<ChatMessage> pe = new ProgressExecutor<ChatMessage>(
				null) {

			@Override
			public ChatMessage execute() throws Exception {
				Map<String, String> params = new HashMap<String, String>();
				params.put("key", KEY);
				params.put("info", msg);
				String json = http.getUrlContext(URL, params);
				LogUtils.i(json);

				Gson gson = new Gson();
				@SuppressWarnings("unused")
				Type type = new TypeToken<Vector<ChatMessage>>() {
				}.getType();

				ChatMessage chat = new ChatMessage();
				Result result = null;
				try {
					result = gson.fromJson(json, Result.class);
					chat.setMsg(result.getText());
				} catch (Exception e) {
					e.printStackTrace();
					chat.setMsg("服务器繁忙，请稍后再试");
				}

				chat.setDate(new Date());
				chat.setType(com.hyj.lib.tuling.bean.ChatMessage.Type.INCOMING);

				return chat;
			}

			@Override
			public void doResult(ChatMessage msg) {
				updateListView(msg);
			}
		};
		pe.start();

		AsyncTask<String, Void, ChatMessage> task = new AsyncTask<String, Void, ChatMessage>() {

			@Override
			protected ChatMessage doInBackground(String... params) {
				return null;
			}

			@Override
			protected void onPostExecute(ChatMessage result) {
				super.onPostExecute(result);
			}
		};
		task.execute("");
	}

	private void updateListView(ChatMessage msg) {
		lData.add(msg);
		adapter.notifyDataSetChanged();
		lvTalk.smoothScrollToPosition(lData.size() - 1);
	}
}
