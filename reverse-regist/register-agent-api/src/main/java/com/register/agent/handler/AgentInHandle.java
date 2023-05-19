package com.register.agent.handler;

import com.alibaba.fastjson.JSONObject;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.util.HttpRequestUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentInHandle extends ChannelInboundHandlerAdapter {

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
        if("test".equals(msg)){
            System.out.println(msg);
            ctx.writeAndFlush("response");
            return;
        }
//        System.out.println(JSONObject.toJSONString(msg));
        InnerRequest request = (InnerRequest)msg;
        InnerResponse response = new InnerResponse();
        response.setReqId(request.getReqId());
        //第一版，直接http转
        execHttp(request, response);
        ctx.writeAndFlush(response);
    }

    private void execHttp(InnerRequest request, InnerResponse response) {
        response.setContent(HttpRequestUtil.invokeRequest(request));
    }


}