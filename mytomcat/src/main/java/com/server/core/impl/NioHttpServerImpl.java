package com.server.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

import com.constant.info.HostInfo;
import com.server.core.HttpServer;
import com.server.web.request.BioRequset;
import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.BioResponse;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.ServletInterface;
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

	class ServerThread implements Runnable{

		private Socket socket;
		public ServerThread(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			try {
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();
				Requset requset;
				Response response;
				if ("bio".equals(System.getProperty("useIo"))) {
					requset = new BioRequset(inputStream);
					response = new BioResponse(outputStream);
				} else if ("nio".equals(System.getProperty("useIo"))) {
					requset = new NioRequset(inputStream);
					response = new NioResponse(outputStream);
				} else {
					requset = new BioRequset(inputStream);
					response = new BioResponse(outputStream);
				}
				ServletInterface servletInterface;
				if ("http2".equals(System.getProperty("httpType"))) {
					throw new RuntimeException("暂不支持http2服务器");
				} else {
					servletInterface = new MyHttpServlet();
				}
				servletInterface.service(requset, response);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
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
						this.acceptWait(selector, key);
					}else if(key.isReadable()) {
						this.readInfo(key);
					}
				}
			}
			
		}
		executorService.shutdown();
		serverSocketChannel.close();
	}

	private void readInfo(SelectionKey key) throws IOException {
		// 创建一个缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SocketChannel channel = (SocketChannel) key.channel();
		// 我们把通道的数据填入缓冲区
		channel.read(buffer);
		String request = new String(buffer.array()).trim();
		System.out.println("客户端的请求内容" + request);
		// 把我们的html内容返回给客户端

		String outString = "HTTP/1.1 200 OK\n" + "Content-Type:text/html; charset=UTF-8\n\n" + "<html>\n" + "<head>\n"
				+ "<title>first page</title>\n" + "</head>\n" + "<body>\n" + "hello fomcat\n" + "</body>\n" + "</html>";

		ByteBuffer outbuffer = ByteBuffer.wrap(outString.getBytes());
		channel.write(outbuffer);
		channel.close();

	}

	private void acceptWait(Selector selector, SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);// 注册读事件
	}

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}

}
