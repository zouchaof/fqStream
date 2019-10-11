package com.server.web.servlet;

import com.enums.RequsetEnumType;
import com.server.web.request.Requset;
import com.server.web.response.Response;

public interface ServletInterface {
	
	void doGet(Requset requset, Response response);
	
	void doPost(Requset requset, Response response);

	default void service(Requset requset, Response response) {
		if(requset.getRequsetType() == RequsetEnumType.GET) {
			doGet(requset, response);
		}else if(requset.getRequsetType() == RequsetEnumType.POST) {
			doPost(requset, response);
		}
	}
	
}
