package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class WaresUserInfo {

    /**
     * authStatus : 2
     * bindEmail : 1
     * bindPhone : 1
     * budgetCoin : 0
     * countDeal : 7
     * doneRate : 0.3889
     * operate : 1
     * registerTime : 1525405784000
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180628192035476/100392i1530184835476.jpeg?x-oss-process=image/resize,m_fill,h_200,w_200
     * username : mxmx
     */

    private int authStatus;
    private int bindEmail;
    private int bindPhone;
    private int budgetCoin;
    private int countDeal;
    private double doneRate;
    private int operate;
    private long registerTime;
    private String userPortrait;
    private String username;

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public int getBindEmail() {
        return bindEmail;
    }

    public void setBindEmail(int bindEmail) {
        this.bindEmail = bindEmail;
    }

    public int getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(int bindPhone) {
        this.bindPhone = bindPhone;
    }

    public int getBudgetCoin() {
        return budgetCoin;
    }

    public void setBudgetCoin(int budgetCoin) {
        this.budgetCoin = budgetCoin;
    }

    public int getCountDeal() {
        return countDeal;
    }

    public void setCountDeal(int countDeal) {
        this.countDeal = countDeal;
    }

    public double getDoneRate() {
        return doneRate;
    }

    public void setDoneRate(double doneRate) {
        this.doneRate = doneRate;
    }

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
