package com.hyj.lib.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.hyj.lib.R;
import com.hyj.lib.tools.DialogUtils;

@SuppressLint("InflateParams")
public class PopupActivity extends Activity implements OnClickListener {

	private Button bt4Bottom;// 从底部上滑显示popup

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();
		initListener();
	}

	private void initView() {
		bt4Bottom = (Button) findViewById(R.id.popupShowBottom);
	}

	private void initData() {

	}

	private void initListener() {
		bt4Bottom.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popupShowBottom:
			lightSwitch(0.3f);
			showPopup4Bottom(bt4Bottom);
			break;
		}
	}

	/**
	 * 从底部展示popupWindow
	 * 
	 * @param locationView
	 *            触发显示popup的view
	 */
	private void showPopup4Bottom(View locationView) {
		/**
		 * <pre>
		 * window.setFocusable(true)、window.setBackgroundDrawable()这两个方法必须调用
		 * 如果是想让popWindow半透明，就是上面的那个方法，
		 * 如果只是单纯的调用这个方法就这样写window.setBackgroundDrawable(new BitmapDrawable());
		 * </pre>
		 */

		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.popup_bottom, null);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		final PopupWindow window = new PopupWindow(popupView,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		window.setFocusable(true);// 设置窗口可点击

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x00000000);
		window.setBackgroundDrawable(dw);

		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.popup_bottom);
		// 设置在底部显示
		window.showAtLocation(locationView, Gravity.BOTTOM, 0, 0);

		/**
		 * <pre>
		 * 将popup显示在任意位置
		 * int[] location = new int[2];
		 * view.getLocationOnScreen(location);
		 * window.showAtLocation(view, Gravity.NO_GRAVITY, location[0],
		 * 		location[1] - locationView.getHeight());
		 * 也可以使用window.showAsDropDown(v,location[0],location[1]);方法实现
		 * </pre>
		 */

		window.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				lightSwitch(1.0f);
			}
		});

		// 实现按钮点击事件
		Button btUpdate = (Button) popupView.findViewById(R.id.popupBtnUpdate);
		Button btBug = (Button) popupView.findViewById(R.id.popupBtnBug);
		Button btExit = (Button) popupView.findViewById(R.id.popupBtnExit);
		OnClickListener click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				window.dismiss();
				String msg = ((Button) v).getText().toString();
				DialogUtils.showToastShort(PopupActivity.this, msg);
			}
		};
		btUpdate.setOnClickListener(click);
		btBug.setOnClickListener(click);
		btExit.setOnClickListener(click);
	}

	/**
	 * 内容区域明暗度设置
	 */
	private void lightSwitch(float alpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = alpha;
		getWindow().setAttributes(lp);
	}
}
