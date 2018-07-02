package com.songbai.futurex.model;

import com.songbai.futurex.model.status.OrderStatus;

/**
 * Modified by john on 2018/6/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Order implements OrderStatus {
    public static final int TYPE_HIS_ENTRUST = 0;
    public static final int TYPE_CUR_ENTRUST = 1;
    public static final int TYPE_HIS_DONE = 2;

    public static final int DIR_BUY = 1;
    public static final int DIR_SELL = 2;

    public static final int LIMIT_TRADE = 1;
    public static final int MARKET_TRADE = 2;

    /**
     * amount : 59.06980000
     * cancelTime : 1530270001000
     * createTime : 1530264474000
     * dealCount : 0.00000000
     * dealPrice : 0.00
     * direction : 1
     * entrustCount : 0.01000000
     * entrustPrice : 5906.98
     * entrustType : 1
     * feeCoin : btc
     * id : 1938948
     * orderNumber : 1012628717190590466
     * orderStatus : 6
     * orderTime : 1530264474000
     * pairs : btc_usdt
     * poundage : 0.00000000
     * status : 6
     * updateTime : 1530270062000
     */

    private String amount;
    private long cancelTime;
    private long createTime;
    private double dealCount;
    private double dealPrice;
    private int direction;
    private double entrustCount;
    private double entrustPrice;
    private int entrustType;
    private String feeCoin;
    private String id;
    private String orderNumber;
    private int orderStatus;
    private long orderTime;
    private String pairs;
    private double poundage;
    private int status;
    private long updateTime;

    public String getAmount() {
        return amount;
    }

    public long getCancelTime() {
        return cancelTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public double getDealCount() {
        return dealCount;
    }

    public double getDealPrice() {
        return dealPrice;
    }

    public int getDirection() {
        return direction;
    }

    public double getEntrustCount() {
        return entrustCount;
    }

    public double getEntrustPrice() {
        return entrustPrice;
    }

    public int getEntrustType() {
        return entrustType;
    }

    public String getFeeCoin() {
        return feeCoin;
    }

    public String getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public String getPairs() {
        return pairs;
    }

    public double getPoundage() {
        return poundage;
    }

    public int getStatus() {
        return status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public String getPrefix() {
        if (pairs.indexOf('_') > -1) {
            return pairs.substring(0, pairs.indexOf('_'));
        }
        return "";
    }

    public String getSuffix() {
        if (pairs.indexOf('_') > -1) {
            return pairs.substring(pairs.indexOf('_') + 1, pairs.length());
        }
        return "";
    }

    @Override
    public String toString() {
        return "Order{" +
                "amount='" + amount + '\'' +
                ", cancelTime=" + cancelTime +
                ", createTime=" + createTime +
                ", dealCount=" + dealCount +
                ", dealPrice=" + dealPrice +
                ", direction=" + direction +
                ", entrustCount=" + entrustCount +
                ", entrustPrice=" + entrustPrice +
                ", entrustType=" + entrustType +
                ", feeCoin='" + feeCoin + '\'' +
                ", id='" + id + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderTime=" + orderTime +
                ", pairs='" + pairs + '\'' +
                ", poundage=" + poundage +
                ", status=" + status +
                ", updateTime=" + updateTime +
                '}';
    }
}
