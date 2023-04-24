package com.register.agent.core;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

//public class MyClientBootstrap extends ClientBootstrap {
//    @Override
//    public void connect(SocketAddress localAddress, SocketAddress remoteAddress) throws Exception {
//        ChannelFuture future = bootstrap.connect(localAddress);
//        future.addListener((ChannelFutureListener) f -> {
//            if (f.isSuccess()) {
//                System.out.println("Connected to server");
//            } else {
//                System.out.println("Failed to connect to server");
//            }
//        });
//    }
//}
