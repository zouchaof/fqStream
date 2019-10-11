package com.server.start;

import com.all.util.PropertiesUtil;
import com.constant.info.HostInfo;
import com.server.core.HttpServer;
import com.server.core.impl.HttpServerCoreImpl;

public class BootStrap {
	
	public static void main(String[] args) {
		System.setProperties(PropertiesUtil.getConfig("config.properties"));
		HttpServer server = new HttpServerCoreImpl();
		server.serverStartUp(HostInfo.PORT);
	}
	
}
