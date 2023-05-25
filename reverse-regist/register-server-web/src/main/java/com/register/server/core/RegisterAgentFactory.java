package com.register.server.core;

import com.alibaba.fastjson.JSONObject;
import com.register.agent.req.RegisterAgentInfo;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RegisterAgentFactory {

    private static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2020,1,1,0,0);
    /**
     * 执行节点各种策略待定
     */
    private static Map<String, List<RegisterAgentInfo>> registerAgentListMap = new ConcurrentHashMap<>();

    public static Set<String> getAppNameSet(){
        return registerAgentListMap.keySet();
    }

    public static void registerAgent(ChannelHandlerContext ctx, RegisterAgentInfo registerAgentInfo){

        log.info("注册客户端信息：{}", JSONObject.toJSONString(registerAgentInfo));
        if(registerAgentInfo == null || StringUtils.isEmpty(registerAgentInfo.getAppName())){
            return;
        }
        registerAgentInfo.setCtx(ctx);

        String appName = registerAgentInfo.getAppName();
        if(registerAgentListMap.containsKey(appName)){
            List<RegisterAgentInfo> registerAgentInfoList = registerAgentListMap.get(appName);
            boolean hasRegister = false;
            for(RegisterAgentInfo info : registerAgentInfoList){
                if(info.getReqId() == registerAgentInfo.getReqId()){
                    info.setLastRegisterTime(registerAgentInfo.getLastRegisterTime());
                    hasRegister = true;
                }
            }
            if(!hasRegister){
                registerAgentListMap.get(appName).add(registerAgentInfo);
            }
        }else{
            List<RegisterAgentInfo> registerAgentInfoList = new ArrayList<>();
            registerAgentInfo.setLastUseTime(DEFAULT_TIME);
            registerAgentInfoList.add(registerAgentInfo);
            registerAgentListMap.put(appName, registerAgentInfoList);
        }
    }


    public static RegisterAgentInfo getExecAgentInfo(String appName){
        if(!registerAgentListMap.containsKey(appName)){
            return null;
        }

        LocalDateTime  halfHourAgo = LocalDateTime.now().minusMinutes(3);
        List<RegisterAgentInfo> registerAgentInfoList = registerAgentListMap.get(appName);
        registerAgentInfoList.removeIf(item -> halfHourAgo.isAfter(item.getLastRegisterTime()));
        return registerAgentInfoList.stream().reduce((one, next) -> {
                    if(one.getLastUseTime().isBefore(next.getLastUseTime())){
                        return one;
                    }
                    return next;
                }).get();
    }


}
