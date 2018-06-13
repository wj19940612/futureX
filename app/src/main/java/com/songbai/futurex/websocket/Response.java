package com.songbai.futurex.websocket;

/**
 * Modified by john on 16/03/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Response<T> implements ResponseCode {

    private int code;
    private T content;
    private String dest;
    private long timestamp;
    private String uuid;
    private String msgId;
    private String message;

    public Response(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public T getContent() {
        return content;
    }

    public String getDest() {
        return dest;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getMessage() {
        return message;
    }
}
