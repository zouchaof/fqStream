package com.register.agent.core;

import com.register.agent.handler.AgentInHandle;
import com.register.agent.req.RegisterAgentInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class AgentClientMain {

    @Value("${register.server.host:localhost}")
    private String host;

    @Value("${register.server.port:19090}")
    private int port;

    private Channel channel;

    private Bootstrap bootstrap;

    @PostConstruct
    private void init(){
        new Thread(this::startNettyAgent).start();
    }

    public void startNettyAgent(){
        EventLoopGroup group = new NioEventLoopGroup(10);
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                //选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .handler(new ChannelInitializer<Channel>(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ObjectEncoder());
                        channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        channel.pipeline().addLast(new AgentInHandle());
                    }
                });
            doConnect();
    }


    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect();
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                log.warn("连接远程服务器成功， host:{}, port:{}", host, port);
                channel = futureListener.channel();
            } else {
                log.warn("连接失败，将进行断线重连...host:{}, port:{}", host, port);
                futureListener.channel().eventLoop().schedule(this::doConnect, 10, TimeUnit.SECONDS);
            }
        });
    }




}
