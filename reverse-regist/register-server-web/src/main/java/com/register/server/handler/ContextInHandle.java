package com.register.server.handler;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.server.listen.ResponseListen;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextInHandle extends ServerInHandleAdapter<InnerResponse> {

    @Override
    protected void serverRead(ChannelHandlerContext ctx, InnerResponse msg) {
        ResponseListen.handleResponse(msg);
    }

}