package com.server.web.request;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class NioRequset extends AbstractRequset {


	public NioRequset(ByteBuffer buffer) {
		setInputStream(new ByteArrayInputStream(buffer.array()));
		parseRequestStr(new String(buffer.array()));
	}

	
	
}
