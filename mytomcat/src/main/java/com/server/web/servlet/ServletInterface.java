package com.server.web.servlet;

import java.io.IOException;
import java.util.Date;

import com.constant.info.HttpServerConstant;
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
		sendReturn(response);
	}
	
	//这里只处理返回文字信息
	default void sendReturn(Response response) {
		try {
			bulidHeader(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	default void bulidHeader(Response response) throws IOException {
		byte[] old = response.getContentByte();
		StringBuilder contextText = new StringBuilder();
		contextText.append(new String(old));

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
		
		response.getOutputStream().write(sb.toString().getBytes());
		response.getOutputStream().write(old);
		response.getOutputStream().close();
	}
}
