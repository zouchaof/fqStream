package com.server.web.request;

import java.io.IOException;
import java.io.InputStream;

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
		parseRequestStr(stringBuffer.toString());
	}


}
