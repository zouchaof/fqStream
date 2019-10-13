package com.server.web.request;


import java.io.InputStream;

import com.enums.RequsetMethod;

public class Requset {
	
	private RequsetMethod requsetMethod;
	private InputStream inputStream;
	private String url;

	public Requset() {
	}

	public RequsetMethod getRequsetMethod() {
		return requsetMethod;
	}


	public void setRequsetMethod(RequsetMethod requsetMethod) {
		this.requsetMethod = requsetMethod;
	}


	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
