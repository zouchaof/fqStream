package com.register.server.core;

import com.register.agent.core.HttpServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerMain {

    public static void main(String[] args) {
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
					ch.pipeline().addLast(new HttpServerHandle());
//                    ch.pipeline().addLast(new HttpServerHandle2());
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true).bind(9090).sync();

            new Thread(()->{
                int i = 0;
                while (i++ >= 0){
                    future.channel().writeAndFlush("res" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
//            LOGGER.error("error", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
            eventLoopGroup2.shutdownGracefully();
        }
    }


}
