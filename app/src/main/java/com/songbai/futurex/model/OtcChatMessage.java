package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/29
 */
public class OtcChatMessage {
    public static final int SEND_SUCCESS = 0;
    public static final int SEND_FAILED = 1;
    public static final int DERECTION_RECEIVE = 1;
    public static final int DIRECTION_SEND = 2;
    public static final int MSG_TEXT = 1;
    public static final int MSG_PHOTO = 2;
    /**
     * createTime : 1530275076998
     * direction : 2
     * id : 1012673187009388545
     * message : 聊天
     * msgType : 1
     * read : 0
     * receiveUid : 101611
     * sendUid : 100476
     * waresId : 256
     * waresOrderId : 545
     */

    private long createTime;
    private int direction;
    private String id;
    private String message;
    private int msgType;
    private int read;
    private int receiveUid;
    private int sendUid;
    private int waresId;
    private int waresOrderId;
    private int chatMsgStatus;

    public OtcChatMessage(int msgType, int direction, String message, int receiveUid, int sendUid, int chatMsgStatus) {
        createTime = System.currentTimeMillis();
        this.msgType = msgType;
        this.direction = direction;
        this.message = message;
        this.receiveUid = receiveUid;
        this.sendUid = sendUid;
        this.chatMsgStatus = chatMsgStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getReceiveUid() {
        return receiveUid;
    }

    public void setReceiveUid(int receiveUid) {
        this.receiveUid = receiveUid;
    }

    public int getSendUid() {
        return sendUid;
    }

    public void setSendUid(int sendUid) {
        this.sendUid = sendUid;
    }

    public int getWaresId() {
        return waresId;
    }

    public void setWaresId(int waresId) {
        this.waresId = waresId;
    }

    public int getWaresOrderId() {
        return waresOrderId;
    }

    public void setWaresOrderId(int waresOrderId) {
        this.waresOrderId = waresOrderId;
    }

    public boolean isSuccess() {
        return chatMsgStatus == SEND_SUCCESS;
    }

}
