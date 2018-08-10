package com.songbai.futurex.websocket.notification;

public class NotificationParam {

    public static final String NOTIFICATION = "queue:umeng:msg";

    private String queue;

    public NotificationParam(String queue) {
        this.queue = queue;
    }
}
