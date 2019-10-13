package com.server.web.servlet;

import com.enums.RequsetMethod;
import com.server.web.request.Requset;
import com.server.web.response.Response;

public interface ServletInterface {
	
	void doGet(Requset requset, Response response);
	
	void doPost(Requset requset, Response response);

	default void service(Requset requset, Response response) {
		if(requset.getRequsetMethod() == RequsetMethod.GET) {
			doGet(requset, response);
		}else if(requset.getRequsetMethod() == RequsetMethod.POST) {
			doPost(requset, response);
		}
	}
	
}
