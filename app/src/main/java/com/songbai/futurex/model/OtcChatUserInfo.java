package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/30
 */
public class OtcChatUserInfo {

    /**
     * certificationLevel : 0
     * id : 100479
     * userName : User134485
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/bitfuture/common_head_portrait.png
     */

    private int certificationLevel;
    private int id;
    private String userName;
    private String userPortrait;

    public int getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
