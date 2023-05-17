package com.register.agent.req;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

@Data
public class BaseMessage implements Serializable {

    private long reqId;



}
