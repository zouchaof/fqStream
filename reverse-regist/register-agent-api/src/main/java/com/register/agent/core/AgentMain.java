package com.register.agent.core;

import com.register.agent.handler.AgentInHandle;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.req.RegisterAgentInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;


@Slf4j
@Component
public class AgentMain {


    @PostConstruct
    private void init(){
        new Thread(this::startNettyAgent).start();
    }


    private void startNettyAgent(){
        EventLoopGroup group = new NioEventLoopGroup();
        try{

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    //选择客户 Socket 实现类，NioSocketChannel 表示基于 NIO 的客户端实现
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 19090)
                    .handler(new ChannelInitializer<Channel>(){
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ObjectEncoder());
                            channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            channel.pipeline().addLast(new AgentInHandle());
                        }
                    })
                    .connect()
                    //Netty 中很多方法都是异步的，如 connect，这时需要使用 sync 方法等待 connect 建立连接完毕
                    .sync();

            ChannelFuture future = bootstrap.connect().sync();
            new Thread(()->{
                int i = 0;
//                while (i++ >= 0){
//                    future.channel().writeAndFlush(new Base("aa", i));
                RegisterAgentInfo agentInfo = new RegisterAgentInfo();
                agentInfo.setAppName("agent");
                agentInfo.setPath("/test");
                agentInfo.setLastRegisterTime(LocalDateTime.now());
                agentInfo.setServerPath("http://localhost:81");
                future.channel().writeAndFlush(agentInfo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                }
            }).start();


//            future.channel().writeAndFlush("test");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("error", e);
        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) {


    }


}
