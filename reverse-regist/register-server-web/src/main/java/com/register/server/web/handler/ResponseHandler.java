package com.register.server.web.handler;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ResponseHandler {


    private static final Map<Long, SkInnerRequest> listenMap = new ConcurrentHashMap<>();
    private static int reqTimeout = 30;
    private static ResponseHandler handler;

    private static void addListen(InnerRequest request){
        if(handler == null){
            handler = new ResponseHandler();
        }
        SkInnerRequest skInnerRequest = handler.new SkInnerRequest();
        skInnerRequest.setRequest(request);
        listenMap.put(request.getReqId(), skInnerRequest);
        try {
            skInnerRequest.getLatch().await(reqTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("add listen error", e);
        }
    }


    public static void handleResponse(InnerResponse response){
        synchronized (String.valueOf(response.getReqId()).intern()){
            SkInnerRequest skRequest = listenMap.get(response.getReqId());
            if(skRequest == null){
                return;
            }
            skRequest.setResponse(response);
            skRequest.getLatch().countDown();
        }
    }


    public static InnerResponse getResponse(InnerRequest request){
        addListen(request);
        synchronized (String.valueOf(request.getReqId()).intern()){
            SkInnerRequest skRequest = listenMap.get(request.getReqId());
            if(skRequest == null){
                return null;
            }
            InnerResponse response = skRequest.getResponse();
            listenMap.remove(request.getReqId());
            return response;
        }
    }

    @Data
    private class SkInnerRequest{
        private InnerRequest request;
        private InnerResponse response;
        private CountDownLatch latch = new CountDownLatch(1);
    }


}
