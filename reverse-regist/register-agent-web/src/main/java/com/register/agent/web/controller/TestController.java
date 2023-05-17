package com.register.agent.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
