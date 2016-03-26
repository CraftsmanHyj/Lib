package com.hyj.lib.a;

import com.hyj.lib.tools.LogUtils;

public class HelloChild extends HelloParent {
	HelloY y = new HelloY("Child");
	static {
		LogUtils.i("child static block");
	}

	public HelloChild() {
		LogUtils.i("child construct");
	}

}
