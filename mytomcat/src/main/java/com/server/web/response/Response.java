package com.server.web.response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface Response {

	OutputStream getOutputStream();

	default String getContentType() {
		return "application/x-www-form-urlencoded; charset=UTF-8;";
	}
	
	ByteArrayOutputStream getWrite();
	
	byte[] getContentByte();
}
