package com.register.agent.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpServerHandle extends ChannelInboundHandlerAdapter {

    public void channelRead2(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println(JSONObject.toJSONString(msg));
//        if(msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            System.out.println("【Netty-HTTP服务器端】uri = " + request.uri() + "、Method = " + request.method() + "、Headers = " + request.headers());
//            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, Unpooled.copiedBuffer("test", CharsetUtil.UTF_8)))
//                    .addListener(ChannelFutureListener.CLOSE);
//        }else{
//        }
//        ctx.writeAndFlush("res2").addListener(ChannelFutureListener.CLOSE);


    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(JSONObject.toJSONString(msg));
        super.channelRead(ctx, msg);
    }



}