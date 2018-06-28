package com.songbai.futurex.websocket.im;

public class IMParam {
    public static final String CHAT = "queue:customer:chat";

    private String queue;

    public IMParam(String queue) {
        this.queue = queue;
    }
}
