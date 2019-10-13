package com.server.web.response;

import java.io.OutputStream;
import java.util.Date;

import com.constant.info.HttpServerConstant;

public abstract class AbstractResponse implements Response {

	private OutputStream outputStream;

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	protected String bulidHeader(String context) {
		StringBuilder contextText = new StringBuilder();
		contextText.append(context);

		StringBuilder sb = new StringBuilder();
		/* 通用头域begin */
		sb.append("HTTP/1.1").append(HttpServerConstant.SPACE).append("200").append(HttpServerConstant.SPACE)
				.append("OK").append(HttpServerConstant.ENTER);
		sb.append("Server:myServer").append(HttpServerConstant.SPACE).append("0.0.1v").append(HttpServerConstant.ENTER);
		sb.append("Date:Sat," + HttpServerConstant.SPACE).append(new Date()).append(HttpServerConstant.ENTER);
		sb.append("Content-Type:text/html;charset=UTF-8").append(HttpServerConstant.ENTER);
		sb.append("Content-Length:").append(contextText.toString().getBytes().length).append(HttpServerConstant.ENTER);
		/* 通用头域end */
		sb.append(HttpServerConstant.ENTER);// 空一行
		sb.append(contextText);// 正文部分
		return sb.toString();
	}

}
