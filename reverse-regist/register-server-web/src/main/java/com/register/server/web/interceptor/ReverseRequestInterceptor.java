package com.register.server.web.interceptor;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.InnerResponse;
import com.register.server.web.handler.RequestHandler;
import com.register.server.web.handler.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ReverseRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            InnerRequest innerRequest = RequestHandler.invokeRequest(request);
            InnerResponse innerResponse = ResponseHandler.getResponse(innerRequest);
            String result = innerResponse == null ? "{\"code\":\"-1\",\"msg\":\"异常结果\"}" : innerResponse.getContent();
            response.getWriter().write(result);
        }catch (Exception e){
            log.error("invoke error", e);
            response.getWriter().write("{\"code\":\"-1\",\"msg\":\"异常结果\"}");
        }
        return false;
    }
}
