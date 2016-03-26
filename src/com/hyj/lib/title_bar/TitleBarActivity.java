package com.hyj.lib.title_bar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.hyj.lib.R;
import com.hyj.lib.title_bar.TitleBar.DoActionIterface;

public class TitleBarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.titlebar);

		myInit();
	}

	private void myInit() {
		TitleBar titleBar = (TitleBar) findViewById(R.id.titleCustom);

		titleBar.setOnLeftButtonClick(new DoActionIterface() {

			@Override
			public void action() {
				Toast.makeText(TitleBarActivity.this, "左边", Toast.LENGTH_SHORT)
						.show();
			}
		});

		titleBar.setOnRightButtonClick(new DoActionIterface() {

			@Override
			public void action() {
				Toast.makeText(TitleBarActivity.this, "右边", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}
