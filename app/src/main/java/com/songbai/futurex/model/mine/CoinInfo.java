package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class CoinInfo {

    /**
     * balancePoint : 8
     * createTime : 1526908261000
     * id : 11
     * supply : 2830000000
     * symbol : usdt
     * updateTime : 1527167762000
     */

    private int balancePoint;
    private long createTime;
    private int id;
    private long supply;
    private String symbol;
    private long updateTime;

    public int getBalancePoint() {
        return balancePoint;
    }

    public void setBalancePoint(int balancePoint) {
        this.balancePoint = balancePoint;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSupply() {
        return supply;
    }

    public void setSupply(long supply) {
        this.supply = supply;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
