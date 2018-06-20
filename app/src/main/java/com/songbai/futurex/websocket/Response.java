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
    private String msgId; // 推送的消息id Long 类型 如果存在msgId 说明这个消息会重发 客户端需要做重发的过滤（同时发送收到的确认消息）暂无
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
