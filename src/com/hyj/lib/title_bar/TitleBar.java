package com.hyj.lib.title_bar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyj.lib.R;

public class TitleBar extends RelativeLayout {
	private int defaultColor = Color.BLACK;

	private Button leftButton, rightButton;
	private TextView tvTitle;

	private int leftTextColor;
	private Drawable leftBackground;
	private String leftText;

	private int rightTextColor;
	private Drawable rightBackground;
	private String rightText;

	private float titleTextSize;
	private int titleTextColor;
	private String titleText;

	private DoActionIterface leftAction;
	private DoActionIterface rightAction;

	/**
	 * 设置左边button点击事件
	 * 
	 * @param leftAction
	 */
	public void setOnLeftButtonClick(DoActionIterface leftAction) {
		this.leftAction = leftAction;
	}

	/**
	 * 设置右边button点击事件
	 * 
	 * @param rightAction
	 */
	public void setOnRightButtonClick(DoActionIterface rightAction) {
		this.rightAction = rightAction;
	}

	public TitleBar(Context context) {
		this(context, null);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		myInit(context, attrs);
	}

	@SuppressLint("NewApi")
	private void myInit(Context context, AttributeSet attrs) {

		initAttrs(context, attrs);
		initView(context);
		initListener(context);

	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context
				.obtainStyledAttributes(attrs, R.styleable.title);

		leftTextColor = ta.getColor(R.styleable.title_leftTextColor,
				defaultColor);
		leftBackground = ta.getDrawable(R.styleable.title_leftBackground);
		leftText = ta.getString(R.styleable.title_leftText);

		rightTextColor = ta.getColor(R.styleable.title_rightTextColor,
				defaultColor);
		rightBackground = ta.getDrawable(R.styleable.title_rightBackground);
		rightText = ta.getString(R.styleable.title_rightText);

		titleTextSize = ta.getDimension(R.styleable.title_titleTextSize, 14);
		titleTextColor = ta
				.getColor(R.styleable.title_titleColor, defaultColor);
		titleText = ta.getString(R.styleable.title_titleText);

		ta.recycle();// 释放资源
	}

	@SuppressLint("NewApi")
	private void initView(Context context) {
		leftButton = new Button(context);
		rightButton = new Button(context);
		tvTitle = new TextView(context);

		leftButton.setText(leftText);
		leftButton.setTextColor(leftTextColor);
		leftButton.setBackground(leftBackground);

		rightButton.setText(rightText);
		rightButton.setTextColor(rightTextColor);
		rightButton.setBackground(rightBackground);

		tvTitle.setText(titleText);
		tvTitle.setTextColor(titleTextColor);
		tvTitle.setTextSize(titleTextSize);
		tvTitle.setGravity(Gravity.CENTER);

		setBackgroundColor(0XFFF59563);// 设置整个布局的背景颜色

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		addView(leftButton, params);// 将view添加到父布局中

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		addView(rightButton, params);// 将rightbutton添加到父布局中

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		addView(tvTitle, params);// 将title天骄到父布局中
	}

	private void initListener(Context context) {
		leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (leftAction != null) {
					leftAction.action();
				}
			}
		});

		rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (rightAction != null) {
					rightAction.action();
				}
			}
		});
	}

	/**
	 * 按钮点击事件
	 * 
	 * @author async
	 * 
	 */
	public interface DoActionIterface {
		public void action();
	}
}
