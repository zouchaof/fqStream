package com.server.web.servlet.http1;

import com.server.web.request.Requset;
import com.server.web.response.Response;
import com.server.web.servlet.ServletInterface;

public class MyHttp1Servlet implements ServletInterface{

	@Override
	public void doGet(Requset requset, Response response) {
		doPost(requset, response);
	}

	@Override
	public void doPost(Requset requset, Response response) {
		
	}

}
