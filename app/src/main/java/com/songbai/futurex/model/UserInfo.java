package com.songbai.futurex.model;

import java.util.List;

/**
 * Modified by john on 2018/6/5
 * <p>
 * Description: 用户信息
 */
public class UserInfo {

    /**
     * authenticationStatus : 1
     * certificationLevel : 0
     * googleAuth : 0
     * googleAuthString: "{\"DRAW\":1,\"SET_DRAW_PASS\":0,\"CNY_TRADE\":0}",
     * lastLoginTime : 1524794314000
     * loginIp : 127.0.0.1
     * promoter : 0
     * realName : 测*
     * registrationIp : 127.0.0.1
     * safeGrade : 1
     * safeSetting : 1
     * teleCode : 86
     * userName : 用户80765
     * userPhone : 188****9221
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/news/20180206/lm1517907436609.jpg
     */

    private int authenticationStatus; // 认证状态:0未完成任何认证,1初级认证,2高级认证成功,3高级认证审核中,4高级认证失败
    private int certificationLevel;
    private int googleAuth; // 是否设置谷歌验证 yes or no
    private long lastLoginTime; //最后登录时间
    private String googleAuthString; //谷歌验证开启情况
    private int id;
    private String loginIp;
    private int payment;
    private int promoter; // 是否是推广员 yes or no
    private String realName; // 真实姓名
    private String registrationIp;
    private int safeGrade; // 安全等级
    private int safeSetting; // 安全密码是否设置，yes or no
    private String teleCode; // 国际长途电话区号
    private String userName;  // 用户名
    private String userPhone; // 手机号
    private String userEmail;
    private String userPortrait; // 用户头像
    private List<Log> listLog;

    public static class Log {
        private String browserAgent;
        private String createTime;
        private String ip;
        private String userId;

        public String getUserId() {
            return userId;
        }
    }

    public String getUserId() {
        if (listLog != null && listLog.size() > 0) {
            return listLog.get(0).getUserId();
        }
        return null;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(int authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public int getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public int getGoogleAuth() {
        return googleAuth;
    }

    public void setGoogleAuth(int googleAuth) {
        this.googleAuth = googleAuth;
    }

    public String getGoogleAuthString() {
        return googleAuthString;
    }

    public void setGoogleAuthString(String googleAuthString) {
        this.googleAuthString = googleAuthString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public int getPromoter() {
        return promoter;
    }

    public void setPromoter(int promoter) {
        this.promoter = promoter;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }

    public void setRegistrationIp(String registrationIp) {
        this.registrationIp = registrationIp;
    }

    public int getSafeGrade() {
        return safeGrade;
    }

    public void setSafeGrade(int safeGrade) {
        this.safeGrade = safeGrade;
    }

    public int getSafeSetting() {
        return safeSetting;
    }

    public void setSafeSetting(int safeSetting) {
        this.safeSetting = safeSetting;
    }

    public String getTeleCode() {
        return teleCode;
    }

    public void setTeleCode(String teleCode) {
        this.teleCode = teleCode;
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
