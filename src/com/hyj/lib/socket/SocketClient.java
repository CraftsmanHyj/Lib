package com.hyj.lib.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SocketClient {

	public static void main(String[] args) {
		SocketClient client = new SocketClient();
		client.start();
	}

	private void start() {
		BufferedReader inputReader;
		// 设置读取Control中的消息
		inputReader = new BufferedReader(new InputStreamReader(System.in));
		String inputContent;
		try {
			while (!(inputContent = inputReader.readLine()).equals("bye")) {
				System.out.println(inputContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
