package com.server.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.http1.MyHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.info.HostInfo;
import com.server.core.HttpServer;

public class AioHttpServerImpl implements HttpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AioHttpServerImpl.class);

	private AsynchronousServerSocketChannel serverSocketChannel;

	private boolean shutdown = false;

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}

	@Override
	public void serverStartUp(Integer port) {
		LOGGER.info("tomcat(aio) start...");
		try {
			serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
			serverSocketChannel.accept(this, new AcceptHandle());
			Thread.sleep(100000);
		} catch (Exception e) {
			LOGGER.error("aio start error", e);
		}
		
	}
	
	private class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, HttpServer>{

		@Override
		public void completed(AsynchronousSocketChannel result, HttpServer attachment) {
			try {
				result.setOption(StandardSocketOptions.TCP_NODELAY, true);
				serverSocketChannel.accept(attachment, this);
				handle(result);
			} catch (IOException e) {
				LOGGER.error("aio completed error", e);
			}
		}

		private void handle(AsynchronousSocketChannel result) {
			ByteBuffer buffer = ByteBuffer.allocate(10*1024);
			result.read(buffer);
			Requset requset = new NioRequset(buffer.array());
			Response response = new NioResponse();
			new MyHttpServlet().service(requset, response);
			ByteBuffer outbuffer = ByteBuffer
					.wrap(((ByteArrayOutputStream) response.getOutputStream()).toByteArray());
			LOGGER.info("server return info:{}", new String(outbuffer.array()));
			result.write(outbuffer);
		}

		@Override
		public void failed(Throwable exc, HttpServer attachment) {
			// TODO Auto-generated method stub
			
		}

		
	}
	
	
	
}
