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
        EventLoopGroup group = new NioEventLoopGroup();
//        try{

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
//                    .connect();
                    //Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
//                    .sync();

//            ChannelFuture future = bootstrap.connect().sync();
//            new Thread(()->{
//                int i = 0;
////                while (i++ >= 0){
////                    future.channel().writeAndFlush(new Base("aa", i));
//                RegisterAgentInfo agentInfo = new RegisterAgentInfo();
//                agentInfo.setAppName("agent");
//                agentInfo.setPath("/test");
//                agentInfo.setLastRegisterTime(LocalDateTime.now());
//                agentInfo.setServerHost("http://localhost:81");
//                future.channel().writeAndFlush(agentInfo);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                }
//            }).start();

            doConnect();

//            future.channel().writeAndFlush("test");
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            log.error("error", e);
//        } finally {
//            group.shutdownGracefully();
//        }
    }


    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect();
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                System.out.println("连接Netty服务器成功！");
                channel = futureListener.channel();
            } else {
                System.out.println("连接失败，将进行短线重连！");
                futureListener.channel().eventLoop().schedule(this::doConnect, 10, TimeUnit.SECONDS);
            }
        });
    }




}
