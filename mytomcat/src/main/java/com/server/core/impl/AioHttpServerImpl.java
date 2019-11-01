package com.server.core.impl;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.core.HttpServer;
import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.http1.MyHttpServlet;

public class AioHttpServerImpl implements HttpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AioHttpServerImpl.class);

	private AsynchronousServerSocketChannel serverSocketChannel;

	private CountDownLatch latch;

	@Override
	public void serverShutdown() {
		latch.countDown();
	}

	/**
	 * start
	 */
	@Override
	public void serverStartUp(Integer port) {
		LOGGER.info("tomcat(aio) start...");
		try {
			this.latch = new CountDownLatch(1);
			this.serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
			serverSocketChannel.accept(this, new AcceptHandle());
			this.latch.await();
		} catch (Exception e) {
			LOGGER.error("aio start error", e);
		}
		
	}
	
	/**
	 * accept
	 * @author zouchao
	 *
	 */
	private class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, HttpServer>{

		@Override
		public void completed(AsynchronousSocketChannel result, HttpServer attachment) {
			try {
				serverSocketChannel.accept(attachment, this);
				LOGGER.info("one connect... ");
				handle(result);
			} catch (Exception e) {
				LOGGER.error("aio completed error", e);
			}
		}

		private void handle(AsynchronousSocketChannel result) {
			ByteBuffer buffer = ByteBuffer.allocate(10*1024);
			result.read(buffer, buffer, new AioReadHandle(result));
			
		}

		@Override
		public void failed(Throwable exc, HttpServer attachment) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * read
	 * @author zouchao
	 *
	 */
	private class AioReadHandle implements CompletionHandler<Integer, ByteBuffer>{

		private AsynchronousSocketChannel socketChannel;
		
		public AioReadHandle(AsynchronousSocketChannel socketChannel) {
			this.socketChannel = socketChannel;
		}

		@Override
		public void completed(Integer result, ByteBuffer buffer) {
			buffer.flip();
			Requset requset = new NioRequset(buffer.array());
			Response response = new NioResponse();
			new MyHttpServlet().service(requset, response);
			ByteBuffer outbuffer = ByteBuffer
					.wrap(((ByteArrayOutputStream) response.getOutputStream()).toByteArray());
			LOGGER.info("server return info:{}", new String(outbuffer.array()));
			socketChannel.write(outbuffer);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
