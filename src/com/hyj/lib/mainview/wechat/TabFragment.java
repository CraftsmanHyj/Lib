package com.hyj.lib.mainview.wechat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * fragment模板代码
 * 
 * @author async
 * 
 */
public class TabFragment extends Fragment {
	public static final String TITLE = "title";

	private String sTitle = "Default";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		if (bundle != null) {
			sTitle = bundle.getString(TITLE);
		}

		TextView tv = new TextView(getActivity());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(30);
		tv.setText(sTitle);

		return tv;
	}
}
