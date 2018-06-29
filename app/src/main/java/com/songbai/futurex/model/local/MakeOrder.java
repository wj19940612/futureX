package com.songbai.futurex.model.local;

/**
 * Modified by john on 2018/6/29
 * <p>
 * Description: 下单对象
 * <p>
 * APIs:
 */
public class MakeOrder {
    private static final int ANDROID_APP = 1;

    public static final int DIR_BUY = 1;
    public static final int DIR_SELL = 2;

    public static final int LIMIT_TRADE = 1;
    public static final int MARKET_TRADE = 2;

    private String signId;
    private int direction;
    private double entrustCount;
    private String pairs;
    private int entrustType;
    private double entrustPrice;
    private int source;

    public MakeOrder() {
        this.signId = String.valueOf(SysTime.getSysTime().getSystemTimestamp());
        this.source = ANDROID_APP;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setEntrustCount(double entrustCount) {
        this.entrustCount = entrustCount;
    }

    public void setPairs(String pairs) {
        this.pairs = pairs;
    }

    public void setEntrustType(int entrustType) {
        this.entrustType = entrustType;
    }

    public void setEntrustPrice(double entrustPrice) {
        this.entrustPrice = entrustPrice;
    }
}
