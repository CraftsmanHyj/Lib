package com.hyj.lib.a;

import com.hyj.lib.tools.LogUtils;

public class HelloParent {
	HelloY y = new HelloY("Parent");
	static {
		LogUtils.i("parent static block");
	}

	public HelloParent() {
		LogUtils.i("parent construck");
	}
}

class HelloY {
	String str;

	public HelloY(String str) {
		LogUtils.i("this is Y " + str);
	}
}