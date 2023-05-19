package com.register.server.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public abstract class ServerInHandleAdapter<T> extends ChannelInboundHandlerAdapter {

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

        if(checkExecClass(msg)){
            log.info("receive msg:{}", JSONObject.toJSONString(msg));
            serverRead(ctx, (T)msg);
            return;
        }
        super.channelRead(ctx, msg);
    }

    protected abstract void serverRead(ChannelHandlerContext ctx, T msg);

    private boolean checkExecClass(Object obj){

        Type type = this.getClass().getGenericSuperclass(); // 获取泛型父类类型
        ParameterizedType pType = (ParameterizedType) type;
        Class<?> c = (Class<?>) pType.getActualTypeArguments()[0];

        return obj.getClass().isAssignableFrom(c);
    }


}