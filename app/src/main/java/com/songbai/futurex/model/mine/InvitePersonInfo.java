package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/8/16
 */
public class InvitePersonInfo {

    /**
     * commission : 0.0003091690
     * dealCount : 2
     * registerTime : 1532508946000
     * userId : 101689
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/bitfuture/common_head_portrait.png
     * username : User086697
     */

    private String coinType;
    private String commission;
    private int dealCount;
    private long registerTime;
    private int userId;
    private String userPortrait;
    private String username;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public int getDealCount() {
        return dealCount;
    }

    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
