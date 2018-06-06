package com.songbai.futurex.model.local;

/**
 * Modified by john on 2018/6/5
 * <p>
 * Description: 登录发送数据
 */
public class LoginData {
    private String phone;
    private String email;
    private String userPass;
    private String platform;
    private String imgCode;

    public LoginData(String platform) {
        this.platform = platform;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }
}
