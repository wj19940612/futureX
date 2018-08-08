package com.songbai.futurex.model;

public class JumpContent {
    private String sendAction;
    private String sendValue;

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

    public String getSendValue() {
        return sendValue;
    }

    public void setSendValue(String sendValue) {
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
