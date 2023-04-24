package com.server.web.response;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface Response {

	OutputStream getOutputStream();

	String getContentType();
	
	ByteArrayOutputStream getWrite();
	
	byte[] getContentByte();
}
