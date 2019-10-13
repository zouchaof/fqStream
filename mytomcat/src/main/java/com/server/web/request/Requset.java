package com.server.web.request;

import java.io.InputStream;

import com.enums.RequsetMethod;

public interface Requset {
	
	RequsetMethod getRequsetMethod();
	
	String getUrl();
	
	InputStream getInputStream();
}
