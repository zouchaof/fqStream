package com.server.web.response;

import java.io.OutputStream;

public abstract class Response {

	private OutputStream outputStream;

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
}
