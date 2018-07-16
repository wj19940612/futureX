package com.songbai.futurex.model.order;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 2018/6/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Order implements OrderStatus, Parcelable {

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
    private String dealCount;
    private String dealPrice;
    private int direction;
    private String entrustCount;
    private String entrustPrice;
    private int entrustType;
    private String feeCoin;
    private String id;
    private String orderNumber;
    private int orderStatus;
    private long orderTime;
    private String pairs;
    private String poundage;
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

    public String getDealCount() {
        return dealCount;
    }

    public double getDealCountDouble() {
        try {
            return Double.parseDouble(dealCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getDealPrice() {
        return dealPrice;
    }

    public double getDealPriceDouble() {
        try {
            return Double.parseDouble(dealPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getDirection() {
        return direction;
    }

    public String getEntrustCount() {
        return entrustCount;
    }

    public String getEntrustPrice() {
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

    public long getOrderTime() {
        return orderTime;
    }

    public String getPairs() {
        return pairs;
    }

    public String getPoundage() {
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

    public boolean hasDealDetail() {
        return status == ALL_DEAL || status == PART_DEAL || status == PART_DEAL_PART_REVOKED;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.amount);
        dest.writeLong(this.cancelTime);
        dest.writeLong(this.createTime);
        dest.writeString(this.dealCount);
        dest.writeString(this.dealPrice);
        dest.writeInt(this.direction);
        dest.writeString(this.entrustCount);
        dest.writeString(this.entrustPrice);
        dest.writeInt(this.entrustType);
        dest.writeString(this.feeCoin);
        dest.writeString(this.id);
        dest.writeString(this.orderNumber);
        dest.writeInt(this.orderStatus);
        dest.writeLong(this.orderTime);
        dest.writeString(this.pairs);
        dest.writeString(this.poundage);
        dest.writeInt(this.status);
        dest.writeLong(this.updateTime);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.amount = in.readString();
        this.cancelTime = in.readLong();
        this.createTime = in.readLong();
        this.dealCount = in.readString();
        this.dealPrice = in.readString();
        this.direction = in.readInt();
        this.entrustCount = in.readString();
        this.entrustPrice = in.readString();
        this.entrustType = in.readInt();
        this.feeCoin = in.readString();
        this.id = in.readString();
        this.orderNumber = in.readString();
        this.orderStatus = in.readInt();
        this.orderTime = in.readLong();
        this.pairs = in.readString();
        this.poundage = in.readString();
        this.status = in.readInt();
        this.updateTime = in.readLong();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
