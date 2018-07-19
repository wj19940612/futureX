package com.songbai.futurex.websocket.msg;

public class MsgParam {
    public static final String MSG = "queue:msg";

    private String queue;

    public MsgParam(String queue) {
        this.queue = queue;
    }
}
