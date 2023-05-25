package com.register.agent.utils;

public class IdWork {

    private static Snowflake snowflake = new Snowflake();


    public static long getId(){
        return snowflake.nextId();
    }

}
