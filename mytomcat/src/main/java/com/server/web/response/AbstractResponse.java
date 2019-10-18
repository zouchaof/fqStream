package com.server.web.response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;


public abstract class AbstractResponse implements Response {

	private OutputStream outputStream = new ByteArrayOutputStream();

	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public PrintWriter getPrintWrite() {
		return new PrintWriter(byteArrayOutputStream);
	}
	
	public byte[] getContentByte() {byteArrayOutputStream.reset();
		return byteArrayOutputStream.toByteArray();
	}
}
