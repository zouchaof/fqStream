package com.register.server.web.handler;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.RegisterAgentInfo;
import com.register.server.core.RegisterAgentFactory;
import com.register.server.utils.IdWork;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RequestHandler {


    public static InnerRequest invokeRequest(HttpServletRequest request) {

        //获取处理节点
        RegisterAgentInfo agentInfo = RegisterAgentFactory.getExecAgentInfo(getContextPathFromUrl(request.getRequestURI()));
        if(agentInfo == null){
            throw new RuntimeException("暂无注册节点");
        }
        //转换为内部请求参数
        InnerRequest innerRequest = getInnerRequest(request, agentInfo);
        //发起请求,多线程直接写数据会混乱，用线程池写
        agentInfo.getCtx().executor().submit(() -> {
            // 执行具体的写操作
            agentInfo.getCtx().writeAndFlush(innerRequest);
        });

        return innerRequest;

    }


    private static String getContextPathFromUrl(String url){
        if(StringUtils.isBlank(url) || url.length() < 1 || !url.startsWith("/")){
            return "";
        }
        String url2 = url.substring(1);
        if(url2.contains("/")){
            return StringUtils.substringBefore(url2, "/");
        }
        return url2;
    }


    private static InnerRequest getInnerRequest(HttpServletRequest request, RegisterAgentInfo agentInfo) {
        InnerRequest innerRequest = new InnerRequest();
        innerRequest.setReqId(IdWork.getId());

        innerRequest.setUrl(agentInfo.getServerHost() + request.getRequestURI());
        innerRequest.setMethod(request.getMethod());
        innerRequest.setHeadMap(getHeadMap(request));
        innerRequest.setParamsMap(getParamMap(request));
        try {
            innerRequest.setJsonParam(IOUtils.toString(request.getInputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return innerRequest;
    }

    private static Map<String, String> getHeadMap(HttpServletRequest request){
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(name -> name, request::getHeader));
    }

    private static Map<String, String> getParamMap(HttpServletRequest request){
        return Collections.list(request.getParameterNames())
                .stream()
                .collect(Collectors.toMap(name -> name, request::getParameter));
    }

}
