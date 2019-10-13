package com.server.web.request;


import java.io.InputStream;

import com.enums.RequsetMethod;

public class AbstractRequset implements Requset{
	
	private RequsetMethod requsetMethod;
	private InputStream inputStream;
	private String url;

	public AbstractRequset() {
	}

	@Override
	public RequsetMethod getRequsetMethod() {
		return requsetMethod;
	}


	public void setRequsetMethod(RequsetMethod requsetMethod) {
		this.requsetMethod = requsetMethod;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
