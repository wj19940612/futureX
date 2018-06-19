package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/19
 */
public class AuthSendOld {
    public static final int TYPE_DEFAULT = 0; //自己选择
    public static final int TYPE_SMS = 1; //手机验证码
    public static final int TYPE_MAIL = 2;//邮箱验证码
    String imgCode;
    int smsType;
    int sendType;

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }

    public int getSmsType() {
        return smsType;
    }

    public void setSmsType(int smsType) {
        this.smsType = smsType;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
}
