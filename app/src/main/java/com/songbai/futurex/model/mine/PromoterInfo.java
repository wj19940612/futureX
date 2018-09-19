package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/15
 */
public class PromoterInfo {

    /**
     * code : 7w2xu
     * totalCom : 0.00000000
     * myUsersCount : 0
     * coinType : btc
     */

    private String code;
    private String totalCom;
    private int myUsersCount;
    private String coinType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTotalCom() {
        return totalCom;
    }

    public void setTotalCom(String totalCom) {
        this.totalCom = totalCom;
    }

    public int getMyUsersCount() {
        return myUsersCount;
    }

    public void setMyUsersCount(int myUsersCount) {
        this.myUsersCount = myUsersCount;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }
}
