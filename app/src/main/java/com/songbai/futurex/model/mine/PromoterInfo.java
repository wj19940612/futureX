package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/15
 */
public class PromoterInfo {

    /**
     * code : 7w57u
     * coinType : btc
     * myUsersCount : 0
     * totalCom : 0.00000000
     */

    private String code;
    private String coinType;
    private int myUsersCount;
    private String totalCom;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getMyUsersCount() {
        return myUsersCount;
    }

    public void setMyUsersCount(int myUsersCount) {
        this.myUsersCount = myUsersCount;
    }

    public String getTotalCom() {
        return totalCom;
    }

    public void setTotalCom(String totalCom) {
        this.totalCom = totalCom;
    }
}
