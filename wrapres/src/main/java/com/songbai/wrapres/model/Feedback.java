package com.songbai.wrapres.model;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class Feedback {

    public static int CONTENT_TYPE_TEXT = 0;
    public static int CONTENT_TYPE_PICTURE = 1;


    /**
     * replyName : 1
     * content : 的撒多
     * contentType : 1
     * createDate : 1494235551000
     * id : 258
     * status : 0
     * type : 0
     * userName : 用户2408
     * userPhone : 13458962548
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494233728845.png
     */

    private String replyName;
    private String replyUserPortrait;
    private String content;
    private int contentType;
    private long createTime;
    private int id;
    private int status;
    private int direction;
    private String userName;
    private String userPhone;
    private String userPortrait;
    private String portrait;

    public long getCreateTime() {
        return createTime;
    }

    public String getReplyUserPortrait() {
        return replyUserPortrait;
    }

    public void setReplyUserPortrait(String replyUserPortrait) {
        this.replyUserPortrait = replyUserPortrait;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return direction;
    }

    public void setType(int type) {
        this.direction = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
