package com.server.web.servlet.http1;

import java.io.IOException;

import com.server.web.request.Requset;
import com.server.web.response.Response;
import com.server.web.servlet.ServletInterface;

public class MyHttpServlet implements ServletInterface{

	@Override
	public void doGet(Requset requset, Response response) {
		doPost(requset, response);
	}

	@Override
	public void doPost(Requset requset, Response response) {
		try {
			if("/favicon.ico".equals(requset.getUrl())) {
				response.getWrite().write("<html>404</html>".getBytes());
			}else {
				response.getWrite().write("my tomcat server test return!".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
