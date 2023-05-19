package com.register.server.netty.handler;

import com.register.agent.req.InnerResponse;
import com.register.server.web.handler.ResponseHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextInHandle extends ServerInHandleAdapter<InnerResponse> {

    @Override
    protected void serverRead(ChannelHandlerContext ctx, InnerResponse msg) {
        ResponseHandler.handleResponse(msg);
    }

}