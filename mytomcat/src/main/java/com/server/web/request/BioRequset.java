package com.server.web.request;

import java.io.IOException;
import java.io.InputStream;

import com.enums.RequsetMethod;

public class BioRequset extends AbstractRequset {

	public BioRequset(InputStream inputStream) {
		setInputStream(inputStream);
		parseInputStream();
	}

	private void parseInputStream() {
		StringBuffer stringBuffer = new StringBuffer();
		int i;
		int size = 1024 * 20;
		byte[] buffer = new byte[size];
		InputStream inputStream = getInputStream();
		do {
			try {
				i = inputStream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				i = -1;
			}
			stringBuffer.append(new String(buffer));
		}while(i == size);
		// 打印读取的socket中的内容
		System.out.println(stringBuffer.toString());
		setUrl(parseUrL(stringBuffer.toString()));
		setRequsetMethod(parseUMethod(stringBuffer.toString()));
	}

	private RequsetMethod parseUMethod(String requestString) {
		int index = requestString.indexOf(' ');
		if (index != -1) {
			return RequsetMethod.getRequsetMethodByname(requestString.substring(0, index));
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
