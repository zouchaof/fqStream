package com.server.core.impl;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.core.HttpServer;
import com.server.web.request.NioRequset;
import com.server.web.request.Requset;
import com.server.web.response.NioResponse;
import com.server.web.response.Response;
import com.server.web.servlet.http1.MyHttpServlet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class NettyHttpServerImpl implements HttpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerImpl.class);
	
	@Override
	public void serverStartUp(Integer port) {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup(10);//接收线程池
		EventLoopGroup eventLoopGroup2 = new NioEventLoopGroup(10);//工作线程池
		ServerBootstrap bootstrap = new ServerBootstrap();

//		部分配置link {https://www.w3cschool.cn/essential_netty_in_action/	}
		bootstrap.group(eventLoopGroup, eventLoopGroup2).channel(NioServerSocketChannel.class);
		ChannelFuture future;
		try {
			future = bootstrap.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
//					ch.pipeline().addLast(new HttpResponseEncoder());
//					ch.pipeline().addLast(new HttpRequestDecoder()) ;    // 请求解码
//					ch.pipeline().addLast(new HttpServerHandle());
					ch.pipeline().addLast(new HttpServerHandle2());
				}
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true).bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			LOGGER.error("error", e);
		} finally {
			eventLoopGroup.shutdownGracefully();
			eventLoopGroup2.shutdownGracefully();
        }
	}

	@Override
	public void serverShutdown() {
		// TODO Auto-generated method stub
		
	}

	class HttpServerHandle extends ChannelInboundHandlerAdapter{
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if(msg instanceof HttpRequest) {
				HttpRequest request = (HttpRequest) msg;
				System.out.println("【Netty-HTTP服务器端】uri = " + request.uri() + "、Method = " + request.method() + "、Headers = " + request.headers());
	            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, Unpooled.copiedBuffer("test", CharsetUtil.UTF_8)))
	            	.addListener(ChannelFutureListener.CLOSE);
			}
		}
		
	}
	class HttpServerHandle2 extends ChannelInboundHandlerAdapter{
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf p = (ByteBuf)msg;
			Requset requset = new NioRequset(p.toString(CharsetUtil.UTF_8).getBytes());
			Response response = new NioResponse();
			new MyHttpServlet().service(requset, response);
			ByteBuffer outbuffer = ByteBuffer
					.wrap(((ByteArrayOutputStream) response.getOutputStream()).toByteArray());
			LOGGER.info("server return info:{}", new String(outbuffer.array()));
			ctx.writeAndFlush(Unpooled.copiedBuffer(new String(outbuffer.array()), CharsetUtil.UTF_8))
				.addListener(ChannelFutureListener.CLOSE);
		}
		
	}
	
}
