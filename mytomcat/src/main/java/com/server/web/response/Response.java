package com.server.web.response;

import java.io.OutputStream;
import java.io.PrintWriter;

public interface Response {

	OutputStream getOutputStream();

	default String getContentType() {
		return "application/x-www-form-urlencoded; charset=UTF-8;";
	}
	
	PrintWriter getPrintWrite();
	
	byte[] getContentByte();
}
