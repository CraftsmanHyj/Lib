package com.hyj.lib.jigsaw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.hyj.lib.R;
import com.hyj.lib.jigsaw.GameJigsawLayout.OnGameJigsawListener;
import com.hyj.lib.tools.DialogUtils;
import com.hyj.lib.tools.DialogUtils.DialogAction;

public class JigsawActivity extends Activity {
	private GameJigsawLayout game;
	private TextView tvLevel;
	private TextView tvTime;
	private TextView tvPreview;
	private TextView tvRestart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jigsaw_main);

		myInit();
	}

	private void myInit() {
		initView();
		iniListener();
	}

	private void initView() {
		tvLevel = (TextView) findViewById(R.id.jigsawLevel);
		tvTime = (TextView) findViewById(R.id.jigsawTime);
		tvPreview = (TextView) findViewById(R.id.jigsawPreview);
		tvRestart = (TextView) findViewById(R.id.jigsawRestart);

		game = (GameJigsawLayout) findViewById(R.id.jigsaw);
	}

	@Override
	protected void onResume() {
		super.onResume();
		game.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		game.pause();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void iniListener() {
		tvRestart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				restartGame("是否重新开始此关卡");
			}
		});

		tvPreview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					game.preview(true);
					break;

				case MotionEvent.ACTION_UP:
					game.preview(false);
					break;
				}
				return true;
			}
		});

		game.setOnGameJigsawListener(new OnGameJigsawListener() {

			@Override
			public void timeChanged(int currentTime) {
				tvTime.setText(currentTime + "");
			}

			@Override
			public void nextLevel(final int nextLevel) {
				DialogUtils.showConfirmDialog(JigsawActivity.this, "提示",
						"恭喜过关，是否进行下一级？", new DialogAction() {

							@Override
							public void action() {
								game.nextLevel();
								tvLevel.setText(nextLevel + "");
							}
						});
			}

			@Override
			public void gameOver() {
				restartGame("游戏结束，是否重新开始");
			}
		});
	}

	private void restartGame(String msg) {
		DialogUtils.showConfirmDialog(JigsawActivity.this, "提示", msg,
				new DialogAction() {
					@Override
					public void action() {
						game.restart();
					}
				});
	}
}
