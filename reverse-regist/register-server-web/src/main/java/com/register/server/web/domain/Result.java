package com.register.server.web.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Result<T> implements Serializable {

    private String code = "0";

    private String msg;

    private int count;

    private T data;

    public static Result ok(){
        return new Result<>();
    }

    public static Result error(String msg){
        Result result = new Result<>();
        result.setCode("-1");
        result.setMsg(msg);
        return result;
    }

    public static <T> Result ok(T data){
        Result result = new Result<>();
        result.setData(data);
        return result;
    }

    public static <T> Result okPage(Integer count, T data) {
        Result result = new Result<>();
        result.setCount(count);
        result.setData(data);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
