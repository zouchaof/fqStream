package com.register.agent.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Data
public class InnerRequest extends BaseMessage {

    private String url;

    private String method;

    private Map<String, String> headMap;

    private Map<String, String> paramsMap;

    private String jsonParam;

}
