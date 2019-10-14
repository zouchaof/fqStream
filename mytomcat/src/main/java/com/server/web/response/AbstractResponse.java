package com.server.web.response;

import java.io.OutputStream;
import java.util.Date;

import com.constant.info.HttpServerConstant;

public abstract class AbstractResponse implements Response {

	private OutputStream outputStream;

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

}
