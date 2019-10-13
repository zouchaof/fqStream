package com.server.web.response;

import java.io.IOException;
import java.io.OutputStream;

public class BioResponse extends AbstractResponse {

	public BioResponse(OutputStream outputStream) {
		setOutputStream(outputStream);
	}

	@Override
	public void sendReturn() {
		String returnMessage = "my tomcat test return~";
		OutputStream outputStream = null;
		try {
			outputStream = getOutputStream();
			outputStream.write(bulidHeader(returnMessage).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
