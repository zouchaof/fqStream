package com.register.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseInHandle extends ChannelInboundHandlerAdapter {

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
        System.out.println(msg);
        if(msg instanceof InnerResponse){
            super.channelRead(ctx, msg);
            return;
        }
        System.out.println(JSONObject.toJSONString(msg));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InnerResponse response = (InnerResponse)msg;

//        super.channelRead(ctx, msg);

        InnerRequest request = new InnerRequest();
        request.setUrl("url");


//        super.channelRead(ctx, msg);
        ctx.writeAndFlush(request);
    }



}