package com.server.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.server.core.HttpServer;
import com.server.web.request.BioRequset;
import com.server.web.request.Requset;
import com.server.web.response.BioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.http1.MyHttpServlet;

public class BioHttpServerImpl implements HttpServer {

	private boolean shutdown = false;

	/**
	 * 核心线程4，最大线程4，空闲线程（这里4-4=0个空闲线程）的存活时间0ms,等待队列10
	 */
	private ExecutorService executorService = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));
	
	@Override
	public void serverStartUp(Integer port) {
		try {
			acceptWait(port);
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
				Requset requset = new BioRequset(socket.getInputStream());
				Response response = new BioResponse();
				new MyHttpServlet().service(requset, response);
				socket.getOutputStream().write(
						((ByteArrayOutputStream)response.getOutputStream())
						.toByteArray());
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
	
	private void acceptWait(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("tomcat(bio) start...");
		while (!shutdown) {
			Socket socket = serverSocket.accept();
			/**
			 * 复习线程执行方式
			 * execute 系列   -- 异步执行指定的任务。 
			 * invoke 和 invokeAll 系列  -- 执行指定的任务，等待完成，返回结果。 
			 * submit 系列   -- 异步执行指定的任务并立即返回一个 Future 对象。 
			 */
			executorService.submit(new ServerThread(socket));
		}
		executorService.shutdown();
		serverSocket.close();
	}

	@Override
	public void serverShutdown() {
		this.shutdown = true;
	}

}
