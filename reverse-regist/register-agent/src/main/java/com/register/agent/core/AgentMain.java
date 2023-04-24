package com.register.agent.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AgentMain {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        try{

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 9090)
//                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpReqHandle());
                        }
                    });

            ChannelFuture future = bootstrap.connect().sync();
            new Thread(()->{
                int i = 0;
                while (i++ >= 0){
                    future.channel().writeAndFlush("test" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


//            future.channel().writeAndFlush("test");
//            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("error", e);
        } finally {
//            group.shutdownGracefully();
        }
    }


}
