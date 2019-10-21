package com.server.web.response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


public abstract class AbstractResponse implements Response {

	private OutputStream outputStream = new ByteArrayOutputStream();

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	@Override
	public ByteArrayOutputStream getWrite() {
		return byteArrayOutputStream;
	}

	public byte[] getContentByte() {
		return byteArrayOutputStream.toByteArray();
	}
}
