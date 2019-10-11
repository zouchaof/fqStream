package com.server.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.server.core.HttpServer;
import com.server.web.request.BioRequset;
import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.BioResponse;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.ServletInterface;
import com.server.web.servlet.http1.MyHttp1Servlet;

public class HttpServerCoreImpl implements HttpServer{

	private boolean shutdown = false;
	
	@Override
	public void serverStartUp(Integer port) {
		try {
			acceptWait(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void acceptWait(Integer port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		while(!shutdown) {
			Socket socket = serverSocket.accept();
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			Requset requset;
			Response response;
			if("bio".equals(System.getProperty("useIo"))) {
				requset = new BioRequset(inputStream);
				response = new BioResponse(outputStream);
			}else if("nio".equals(System.getProperty("useIo"))) {
				requset = new NioRequset(inputStream);
				response = new NioResponse(outputStream);
			}else {
				requset = new BioRequset(inputStream);
				response = new BioResponse(outputStream);
			}
			ServletInterface servletInterface;
			if("http2".equals(System.getProperty("httpType"))) {
				throw new RuntimeException("暂不支持http2服务器");
			}else {
				servletInterface = new MyHttp1Servlet();
			}
			servletInterface.service(requset, response);
			socket.close();
		}
		serverSocket.close();
	}

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}
	
}
