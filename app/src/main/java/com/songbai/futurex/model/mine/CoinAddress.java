package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class CoinAddress {

    /**
     * coinType : usdt
     * toAddr : djjsjsjjsjsj4j
     * remark : 地址
     * id : 32
     */

    private String coinType;
    private String toAddr;
    private String remark;
    private int id;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
