package com.songbai.futurex.model.order;

/**
 * Modified by john on 2018/7/10
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class DealDetail {

    /**
     * alreadyCount : 0.04000000
     * changeAmount : 268.81720000
     * coinSymbol : usdt
     * createTime : 1531138184000
     * dealCount : 0.04000000
     * dealPrice : 6720.43
     * dealTime : 1531138184000
     * direction : 2
     * entrustId : 1939079
     * entrustPrice : 6716.54
     * entrustSrc : 1
     * entrustType : 1
     * id : 897415
     * pairs : btc_usdt
     * poundage : 4.03225800
     * status : 3
     * userId : 101586
     */

    private String alreadyCount;
    private String changeAmount;
    private String coinSymbol;
    private long createTime;
    private double dealCount;
    private double dealPrice;
    private long dealTime;
    private int direction;
    private int entrustId;
    private double entrustPrice;
    private int entrustSrc;
    private int entrustType;
    private int id;
    private String pairs;
    private double poundage;
    private int status;
    private int userId;

    public String getAlreadyCount() {
        return alreadyCount;
    }

    public void setAlreadyCount(String alreadyCount) {
        this.alreadyCount = alreadyCount;
    }

    public String getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(String changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getDealCount() {
        return dealCount;
    }

    public double getDealPrice() {
        return dealPrice;
    }

    public long getDealTime() {
        return dealTime;
    }

    public void setDealTime(long dealTime) {
        this.dealTime = dealTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(int entrustId) {
        this.entrustId = entrustId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPairs() {
        return pairs;
    }

    public void setPairs(String pairs) {
        this.pairs = pairs;
    }

    public double getPoundage() {
        return poundage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
