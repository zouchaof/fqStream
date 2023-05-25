package com.register.agent.handler;

import com.register.agent.core.AgentClientMain;
import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.req.RegisterAgentInfo;
import com.register.agent.spring.SpringApplicationContextHolder;
import com.register.agent.util.HttpRequestUtil;
import com.register.agent.utils.IdWork;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AgentInHandle extends ChannelInboundHandlerAdapter {

    private static final long AGENT_CLIENT_ID = IdWork.getId();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(99999));

    private static int a = 0;
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
        InnerRequest request = (InnerRequest)msg;
        InnerResponse response = new InnerResponse();
        response.setReqId(request.getReqId());
        //读数据绑定单线程，所以真实解析数据返回使用多线程提升性能
        executor.execute(() -> {
            execRequest(request, response);
            //保证数据不错乱，提交netty线程任务来写数据
            ctx.executor().execute(()->{
                ctx.writeAndFlush(response);
            });
        });
    }

    private void execRequest(InnerRequest request, InnerResponse response) {
        //第一版，直接http转
        response.setContent(HttpRequestUtil.invokeRequest(request));
    }

    //客户端连接成功后，向服务器发送数据
    public void channelActive(ChannelHandlerContext ctx) {
        //启动心跳检测任务
        ctx.executor().scheduleAtFixedRate(
                () -> ctx.channel().writeAndFlush(getAgentInfo()),
                0, 30, TimeUnit.SECONDS);
    }

    private RegisterAgentInfo getAgentInfo(){
        RegisterAgentInfo agentInfo = new RegisterAgentInfo();
        agentInfo.setReqId(AGENT_CLIENT_ID);
        agentInfo.setAppName("agent");
        agentInfo.setPath("/agent");
        agentInfo.setLastRegisterTime(LocalDateTime.now());
        agentInfo.setServerHost("http://localhost:81");
        return agentInfo;
    }

    //当连接断开时，重新连接
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("与服务器断开连接，将进行重连...");
        AgentClientMain client = SpringApplicationContextHolder.getBean(AgentClientMain.class);
        client.startNettyAgent();
    }

    //异常处理
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}