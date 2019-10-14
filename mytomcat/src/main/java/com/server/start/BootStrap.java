package com.server.start;

import java.io.IOException;
import java.util.Properties;

import com.all.util.PropertiesUtil;
import com.constant.info.HostInfo;
import com.server.core.HttpServer;
import com.server.core.impl.HttpServerCoreImpl;

public class BootStrap {
	
	public static void main(String[] args) throws IOException {
		setConfig();
		HttpServer server = new HttpServerCoreImpl();
		server.serverStartUp(HostInfo.PORT);
	}

	private static void setConfig() {
		Properties p = PropertiesUtil.getConfig("config.properties");
		for(Object s : p.keySet()) {
			System.setProperty(String.valueOf(s), String.valueOf(p.get(s)));
		}
	}
	
}
