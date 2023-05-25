package com.register.server.web.controller;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.agent.req.RegisterAgentInfo;
import com.register.server.core.RegisterAgentFactory;
import com.register.server.web.handler.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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


    @GetMapping("/hello2")
    public String hello2(Model model) {
//        ModelAndView mv = new ModelAndView("hello");
//        mv.addObject("title", "Hello");
//        mv.addObject("message", "Hello, World!");
        model.addAttribute("title", "Hello");
        model.addAttribute("message", "Hello, World!");

        return "hello";
    }



}
