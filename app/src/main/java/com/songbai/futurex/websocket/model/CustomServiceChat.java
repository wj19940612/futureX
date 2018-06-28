package com.songbai.futurex.websocket.model;

public class CustomServiceChat {

    public static final int USER_TO_CUSTOMER = 1;
    public static final int CUSTOMER_TO_USER = 2;

    public static final int MSG_TEXT = 1;
    public static final int MSG_PHOTO = 2;

    public static final int MSG_CHAT = 0;
    public static final int MSG_SYSTEM = 1;


    public static final int HAVE_READ = 1;
    public static final int NOT_READ = 0;

    public static final int SEND_SUCCESS = 0;
    public static final int SEND_FAILED = 1;

    private String content;

    private long createTime;

    private int cusid;

    private String deviceid;

    private int direction; //2, --消息方向（1 客户到客服 2 客服到客户）

    private int msgtype;//0, --消息类型（1 文本消息 2 图片）

    private int read; //1 --是否已读（0 未读 1 已读）

    private int status;//发送状态 0-成功 1-失败

    private String cusnick;  //客服昵称
    private String usernick; //用户昵称

    private int userid;

    private int chatType; //区分是否系统消息

    public CustomServiceChat() {
    }

    public CustomServiceChat(String content, int status,boolean isPhoto) {
        this.content = content;
        createTime = System.currentTimeMillis();
        direction = USER_TO_CUSTOMER;
        msgtype = MSG_PHOTO;
        this.status = status;
    }

    public CustomServiceChat(String content, int status) {
        this.content = content;
        createTime = System.currentTimeMillis();
        direction = USER_TO_CUSTOMER;
        msgtype = MSG_TEXT;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCusid() {
        return cusid;
    }

    public void setCusid(int cusid) {
        this.cusid = cusid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getCusnick() {
        return cusnick;
    }

    public void setCusnick(String cusnick) {
        this.cusnick = cusnick;
    }

    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public boolean isCustomerService() {
        return direction == CUSTOMER_TO_USER;
    }

    public boolean isPhoto() {
        return msgtype == MSG_PHOTO;
    }

    public boolean isSuccess(){
        return status == SEND_SUCCESS;
    }

    public boolean isSystemMsg(){
        return chatType == MSG_SYSTEM;
    }
}
