package com.songbai.futurex.websocket.otc;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class OtcParam {

    public static final String USER_STATUS = "topic:user:status";
    public static final String OTC_CHAT = "queue:otc:chat:";

    private String topic;

    public OtcParam(String topic) {
        this.topic = topic;
    }
}
