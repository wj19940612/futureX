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
    private String dealCount;
    private String dealPrice;
    private long dealTime;
    private int direction;
    private int entrustId;
    private String entrustPrice;
    private int entrustSrc;
    private int entrustType;
    private int id;
    private String pairs;
    private String poundage;
    private int status;
    private int userId;

    public String getAlreadyCount() {
        return alreadyCount;
    }

    public String getChangeAmount() {
        return changeAmount;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getDealTime() {
        return dealTime;
    }

    public int getDirection() {
        return direction;
    }

    public int getEntrustId() {
        return entrustId;
    }

    public int getId() {
        return id;
    }

    public String getPairs() {
        return pairs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDealCount() {
        return dealCount;
    }

    public String getDealPrice() {
        return dealPrice;
    }

    public String getEntrustPrice() {
        return entrustPrice;
    }

    public int getEntrustSrc() {
        return entrustSrc;
    }

    public int getEntrustType() {
        return entrustType;
    }

    public String getPoundage() {
        return poundage;
    }
}
