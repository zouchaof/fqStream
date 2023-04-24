package com.register.agent.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpReqHandle extends ChannelInboundHandlerAdapter {
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if(msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            System.out.println("【Netty-HTTP服务器端】uri = " + request.uri() + "、Method = " + request.method() + "、Headers = " + request.headers());
//            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, Unpooled.copiedBuffer("test", CharsetUtil.UTF_8)))
//                    .addListener(ChannelFutureListener.CLOSE);
//        }
//
//        System.out.println(JSONObject.toJSONString(msg));
//
//    }


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