package com.register.server.core;

import com.register.server.handler.ContextInHandle;
import com.register.server.handler.RegisterInHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RegisterServerStrap {

    @Value("${register.server.port:19090}")
    private int registerServerPort;


    @PostConstruct
    private void init(){
        new Thread(this::startNettyServer).start();
    }


    private void startNettyServer(){
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(10);//接收线程池
        EventLoopGroup eventLoopGroup2 = new NioEventLoopGroup(10);//工作线程池
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup, eventLoopGroup2).channel(NioServerSocketChannel.class);
        ChannelFuture future;
        try {
            future = bootstrap.childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(new RegisterInHandle());
                    ch.pipeline().addLast(new ContextInHandle());
                }
            }).bind(registerServerPort);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty error", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
            eventLoopGroup2.shutdownGracefully();
        }
    }


}
