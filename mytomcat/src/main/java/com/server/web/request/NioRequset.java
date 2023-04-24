package com.server.web.request;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class NioRequset extends AbstractRequset {


	public NioRequset(byte[] bytes) {
		setInputStream(new ByteArrayInputStream(bytes));
		parseRequestStr(new String(bytes));
	}

	
	
}
