package com.server.core.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.info.HostInfo;
import com.server.core.HttpServer;

public class AioHttpServerImpl implements HttpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AioHttpServerImpl.class);
	
	private boolean shutdown = false;

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}

	@Override
	public void serverStartUp(Integer port) {
		LOGGER.info("tomcat(aio) start...");
		try {
			AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
			serverSocketChannel.accept(this, new AcceptHandle());
		} catch (IOException e) {
			LOGGER.error("aio start error", e);
		}
		
	}
	
	private class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, HttpServer>{

		@Override
		public void completed(AsynchronousSocketChannel result, HttpServer attachment) {
			try {
				result.setOption(StandardSocketOptions.TCP_NODELAY, true);
			} catch (IOException e) {
				LOGGER.error("aio completed error", e);
			}
		}

		@Override
		public void failed(Throwable exc, HttpServer attachment) {
			// TODO Auto-generated method stub
			
		}

		
	}
	
}
