package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class SysMessage {
    public static final int READ = 1;
    public static final int UNREAD = 0;

    private String classify;
    private long createTime;
    private String id;
    private String msg;
    private int status;
    private int type;
    private long updateTime;

    private String title;
    private String style;
    private long showStartTime;

//    private String content;
//    private long createTime;
//    private int format;
//    private String id;
//    private int index;
//    private String lang;
//    private String operator;
//    private long showEndTime;

//    private int status;
//    private String style;

//    private int type;
//    private long updateTime;

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public long getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(long showStartTime) {
        this.showStartTime = showStartTime;
    }
}
