package com.songbai.futurex.model.local;

/**
 * Modified by john on 2018/7/5
 * <p>
 * Description: 验证短信（邮箱）验证码
 * <p>
 * APIs:
 */
public class AuthCodeCheck {
    private String data;
    private String msgCode;
    private int type;
    private String teleCode;

    public void setData(String data) {
        this.data = data;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTeleCode(String teleCode) {
        this.teleCode = teleCode;
    }
}
