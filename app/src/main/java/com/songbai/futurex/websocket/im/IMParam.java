package com.songbai.futurex.websocket.im;

public class IMParam {
    public static final String CHAT = "queue:customer:chat";
    public static final String CUSOOFFLINE = "queue:customer:chat:cusoffline";
    public static final String CUSOONLINE = "topic:customer:chat:cusonline";

    private String queue;

    public IMParam(String queue) {
        this.queue = queue;
    }
}
