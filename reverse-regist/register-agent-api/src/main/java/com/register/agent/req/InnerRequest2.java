package com.register.agent.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class InnerRequest2 implements Serializable {

    private long reqId;

    private Integer reqStatus;

    private String url;

    private String method;

    private Map<String, Object> headMap;

    private Map<String, Object> paramsMap;



}
