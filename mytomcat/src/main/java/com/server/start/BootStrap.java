package com.server.start;

import java.io.IOException;
import java.util.Properties;

import com.all.util.PropertiesUtil;
import com.constant.info.HostInfo;
import com.server.core.HttpServer;
import com.server.core.impl.AioHttpServerImpl;
import com.server.core.impl.BioHttpServerImpl;
import com.server.core.impl.NioHttpServerImpl;

public class BootStrap {
	
	public static void main(String[] args) throws IOException {
		setConfig();
		HttpServer server;
		if ("bio".equals(System.getProperty("useIo"))) {
			server = new BioHttpServerImpl();
		}else if ("nio".equals(System.getProperty("useIo"))) {
			server = new NioHttpServerImpl();
		}else if ("aio".equals(System.getProperty("useIo"))) {
			server = new AioHttpServerImpl();
		}else {
			return;
		}
//		server.serverStartUp(HostInfo.PORT);
		new Thread(new StartServeThread(server)).start();//用线程启动，测试关闭功能
//		try {
//			Thread.sleep(1000*5);
//			server.serverShutdown();//测试关闭
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
	}
	
	private static void setConfig() {
		Properties p = PropertiesUtil.getConfig("config.properties");
		for(Object s : p.keySet()) {
			System.setProperty(String.valueOf(s), String.valueOf(p.get(s)));
		}
	}
	
}
class StartServeThread implements Runnable{
	
	private HttpServer server;
	
	public StartServeThread(HttpServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		server.serverStartUp(HostInfo.PORT);
	}
}