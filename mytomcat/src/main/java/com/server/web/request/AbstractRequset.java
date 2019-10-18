package com.server.web.request;


import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enums.RequsetMethod;

public class AbstractRequset implements Requset{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequset.class);
	
	private RequsetMethod requsetMethod;
	private InputStream inputStream;
	private String url;

	public AbstractRequset() {
	}

	@Override
	public RequsetMethod getRequsetMethod() {
		return requsetMethod;
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

	protected void parseRequestStr(String requestStr) {
		LOGGER.info("request info: {}", requestStr);
		requsetMethod = parseUMethod(requestStr);
		url = parseUrL(requestStr);
	}
	
	private RequsetMethod parseUMethod(String requestString) {
		int index = requestString.indexOf(' ');
		if (index != -1) {
			return RequsetMethod.getRequsetMethodByname(requestString.substring(0, index));
		}
		return null;

	}

	private String parseUrL(String requestString) {
		int index1, index2;
		// 查看socket获取的请求头是否有值
		index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1) {
				return requestString.substring(index1 + 1, index2);
			}
		}
		return null;
	}

}
