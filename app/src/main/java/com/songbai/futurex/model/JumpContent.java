package com.songbai.futurex.model;

import com.google.gson.JsonObject;

public class JumpContent {
    private String sendAction;
    private JsonObject sendValue;

    private int userId;

    private String title;

    private String text;

    private int contentType;

    private String uuid;

    public String getSendAction() {
        return sendAction;
    }

    public void setSendAction(String sendAction) {
        this.sendAction = sendAction;
    }

    public JsonObject getSendValue() {
        return sendValue;
    }

    public void setSendValue(JsonObject sendValue) {
        this.sendValue = sendValue;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
