package com.register.agent.req;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterAgentInfo extends BaseMessage {

    private String path;

    private String appName;

    private String serverPath;

    private LocalDateTime lastRegisterTime;

    private LocalDateTime lastUseTime;

    private ChannelHandlerContext ctx;

}
