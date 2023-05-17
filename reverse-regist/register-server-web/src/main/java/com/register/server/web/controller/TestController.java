package com.register.server.web.controller;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.req.RegisterAgentInfo;
import com.register.server.core.RegisterAgentFactory;
import com.register.server.listen.ResponseListen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping
public class TestController {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(9999));

    private boolean b = false;


    @RequestMapping("timer")
    @ResponseBody
    public String timer(){
        log.info("timer start...");
        b = true;
        if(executor.getTaskCount() > 0){
            return "has begin";
        }
        executor.execute(() -> {
            while (b){
                log.info("executor log print start...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return "success2";
    }

    @RequestMapping("timerStop")
    @ResponseBody
    public String timerStop() {
        log.info("timer stop...");
        b = false;
        return "timer stop";
    }

    @RequestMapping("invoke")
    @ResponseBody
    public String invoke() {
        log.info("invoke start...");



        RegisterAgentInfo agentInfo = RegisterAgentFactory.getExecAgentInfo("agent");
//        agentInfo.getCtx().writeAndFlush("test");
        InnerRequest request = new InnerRequest();
        request.setReqId(1);
        request.setUrl(agentInfo.getServerPath() + "timerStop");
        agentInfo.getCtx().writeAndFlush(request);

//        agentInfo.getCtx().executor().submit(new Runnable() {
//            @Override
//            public void run() {
//                // 执行具体的写操作
//                agentInfo.getCtx().writeAndFlush(request);
//            }
//        });
//


        InnerResponse response = ResponseListen.getResponse(request);

        return response.getContent();
//        return "1";
    }


}
