package com.register.server.utils;

public class IdWork {

    private static Snowflake snowflake = new Snowflake();


    public static long getId(){
        return snowflake.nextId();
    }

}
