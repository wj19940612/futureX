package com.songbai.futurex.websocket;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TopicParam {

    public static final String ALL = "topic:quota:list";
    public static final String SINGLE = "topic:quota:all:";

    private String topic;

    public TopicParam(String topic) {
        this.topic = topic;
    }
}
