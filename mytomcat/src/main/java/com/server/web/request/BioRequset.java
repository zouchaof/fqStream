package com.server.web.request;

import java.io.IOException;
import java.io.InputStream;

import com.enums.RequsetMethod;

public class BioRequset extends Requset {

	public BioRequset(InputStream inputStream) {
		setInputStream(inputStream);
		parseInputStream();
	}

	private void parseInputStream() {
		StringBuffer stringBuffer = new StringBuffer();
		int i;
		byte[] buffer = new byte[1024 * 2];
		try {
			i = getInputStream().read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		for (int j = 0; j < i; j++) {
			stringBuffer.append((char) buffer[j]);
		}
		// 打印读取的socket中的内容
		System.out.print(stringBuffer.toString());
		setUrl(parseUrL(stringBuffer.toString()));
		setRequsetMethod(parseUMethod(stringBuffer.toString()));
	}

	private RequsetMethod parseUMethod(String requestString) {
		int index = requestString.indexOf(' ');
		if (index != -1) {
//	            return requestString.substring(0,index);
			System.out.println("method:" + requestString.substring(0, index));
		}
		return null;

	}

	private String parseUrL(String requestString) {
		int index1, index2;
		// 查看socket获取的请求头是否有值
		index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1) {
				return requestString.substring(index1 + 1, index2);
			}
		}
		return null;
	}

}
