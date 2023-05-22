package com.register.agent.handler;

import com.alibaba.fastjson.JSONObject;
import com.register.agent.core.AgentClientMain;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.req.RegisterAgentInfo;
import com.register.agent.spring.SpringApplicationContextHolder;
import com.register.agent.util.HttpRequestUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    //客户端连接成功后，向服务器发送数据
    public void channelActive(ChannelHandlerContext ctx) {



//        ctx.channel().writeAndFlush(getAgentInfo());
//        System.out.println("向服务器发送数据成功！");

        //启动心跳检测任务
        ctx.executor().scheduleAtFixedRate(
                () -> ctx.channel().writeAndFlush(getAgentInfo()),
                0, 5, TimeUnit.SECONDS);
    }

    private RegisterAgentInfo getAgentInfo(){
        RegisterAgentInfo agentInfo = new RegisterAgentInfo();
        agentInfo.setAppName("agent");
        agentInfo.setPath("/test");
        agentInfo.setLastRegisterTime(LocalDateTime.now());
        agentInfo.setServerHost("http://localhost:81");
        return agentInfo;
    }

    //当连接断开时，重新连接
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务器断开连接，将进行重连！");
        AgentClientMain client = SpringApplicationContextHolder.getBean(AgentClientMain.class);
        client.startNettyAgent();
    }

    //异常处理
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}