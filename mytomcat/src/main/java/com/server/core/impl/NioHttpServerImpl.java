package com.server.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.core.HttpServer;
import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.http1.MyHttpServlet;

public class NioHttpServerImpl implements HttpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(NioHttpServerImpl.class);
	
	private boolean shutdown = false;

	/**
	 * 核心线程4，最大线程4，空闲线程（这里4-4=0个空闲线程）的存活时间0ms,等待队列10
	 */
	private ExecutorService executorService = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));
	
	@Override
	public void serverStartUp(Integer port) {
		try {
			initSelector(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ReadInfoThread implements Runnable{

		private SelectionKey selectionKey;
		
		public ReadInfoThread(SelectionKey key) {
			this.selectionKey = key;
		}

		@Override
		public void run() {
			SocketChannel channel = null;
			try {
				// 创建一个缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				channel = (SocketChannel) selectionKey.channel();
				// 把通道的数据填入缓冲区
				channel.read(buffer);
				Requset requset = new NioRequset(buffer);
				Response response = new NioResponse();
				new MyHttpServlet().service(requset, response);
				ByteBuffer outbuffer = ByteBuffer.wrap(
						((ByteArrayOutputStream)response.getOutputStream())
						.toByteArray());
				LOGGER.info("server return info:{}", new String(outbuffer.array()));
				channel.write(outbuffer);
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(channel != null) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
	private void initSelector(int port) throws IOException {
		LOGGER.info("tomcat(nio) start...");
		//NIO的处理是基于Channel控制的，所以有一个Selector就是负责管理所有的Channel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//需要为其设置一个非阻塞的状态机制
		serverSocketChannel.configureBlocking(false);
		//服务器上需要提供有一个网络的监听端口
		serverSocketChannel.bind(new InetSocketAddress(port));
		//需要设置一个Selector，作为一个选择器的出现，目的是管理所有的Channel
		Selector selector = Selector.open();
		//将当前的Channel注册到Selector之中
		/**
		 * 	NIO selectionKey定义的4种事件 -->
				SelectionKey.OP_ACCEPT —— 接收连接继续事件，表示服务器监听到了客户连接，服务器可以接收这个连接了
				SelectionKey.OP_CONNECT —— 连接就绪事件，表示客户与服务器的连接已经建立成功
				SelectionKey.OP_READ —— 读就绪事件，表示通道中已经有了可读的数据，可以执行读操作了（通道目前有数据，可以进行读操作了）
				SelectionKey.OP_WRITE —— 写就绪事件，表示已经可以向通道写数据了（通道目前可以用于写操作）
		 */
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);// 连接时处理
		while (!shutdown) {
			//NIO采用的是轮询模式，每当发现有用户连接的时候就需要启动一个线程（线程池管理）
			int keySelect = 0; // 接收轮询状态
			while((keySelect = selector.select()) > 0 ) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if(!key.isValid()) {
						continue;
					}
					if(key.isAcceptable()) {//为连接模式（阻塞状态），等待连接
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);// 注册读事件
					}else if(key.isReadable()) {
						executorService.submit(new ReadInfoThread(key));
					}
				}
			}
			
		}
		executorService.shutdown();
		serverSocketChannel.close();
	}

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}

}
