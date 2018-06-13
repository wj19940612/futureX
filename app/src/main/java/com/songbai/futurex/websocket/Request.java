package com.songbai.futurex.websocket;

import com.google.gson.Gson;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Request<T> implements RequestCode {

    private int code;
    private String uuid;
    private String timestamp;
    private String url;
    private String method;
    private T parameter;

    public Request(int code) {
        this.code = code;
    }

    public Request(int code, T parameter) {
        this.code = code;
        this.parameter = parameter;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}