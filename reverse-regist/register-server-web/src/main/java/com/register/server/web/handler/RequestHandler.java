package com.register.server.web.handler;

import com.register.agent.req.InnerRequest;
import com.register.agent.req.RegisterAgentInfo;
import com.register.server.core.RegisterAgentFactory;
import com.register.agent.utils.IdWork;
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


    public static InnerRequest invokeRequest(HttpServletRequest request, Map<String, Object> appInfo) {

        if(appInfo == null){
            throw new RuntimeException("没有配置转发路径");
        }
        //获取处理节点
        RegisterAgentInfo agentInfo = RegisterAgentFactory.getExecAgentInfo((String) appInfo.get("APP_NAME"));
        if(agentInfo == null){
            throw new RuntimeException("暂无注册节点");
        }
        //转换为内部请求参数
        InnerRequest innerRequest = getInnerRequest(request, agentInfo, appInfo);
        //发起请求,多线程直接写数据会混乱，用线程池写
        agentInfo.getCtx().executor().submit(() -> {
            // 执行具体的写操作
            agentInfo.getCtx().writeAndFlush(innerRequest);
        });

        return innerRequest;

    }

    private static InnerRequest getInnerRequest(HttpServletRequest request, RegisterAgentInfo agentInfo, Map<String, Object> appInfo) {
        InnerRequest innerRequest = new InnerRequest();
        innerRequest.setReqId(IdWork.getId());

        String serverPath = (String) appInfo.get("SERVER_PATH");
        if(StringUtils.isBlank(serverPath)){
            serverPath = agentInfo.getServerHost();
        }
        innerRequest.setUrl(serverPath + request.getRequestURI());
        innerRequest.setMethod(request.getMethod());
        innerRequest.setHeadMap(getHeadMap(request));
        innerRequest.setParamsMap(getParamMap(request));
        try {
            innerRequest.setJsonParam(IOUtils.toString(request.getInputStream(), "utf-8"));
        } catch (IOException e) {
            log.error("获取请求参数异常", e);
        }
        return innerRequest;
    }

    private static Map<String, String> getHeadMap(HttpServletRequest request){
        return Collections.list(request.getHeaderNames())
                .stream().collect(Collectors.toMap(name -> name, request::getHeader));
    }

    private static Map<String, String> getParamMap(HttpServletRequest request){
        return Collections.list(request.getParameterNames())
                .stream().collect(Collectors.toMap(name -> name, request::getParameter));
    }

}
