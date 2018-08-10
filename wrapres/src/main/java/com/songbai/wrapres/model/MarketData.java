package com.songbai.wrapres.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 06/02/2018
 * <p>
 * Description:
 *
 */
public class MarketData implements Parcelable {

    /**
     * askPrice : 0.1752
     * bidPrice : 0.165
     * code : data_usdt
     * currencyMoney : usdt
     * exchangeCode : gate.io
     * highestPrice : 0.1907
     * lastPrice : 0.165
     * lastVolume : 518044.2545
     * lowestPrice : 0.159
     * name : data
     * rate : 6.35
     * status : 0
     * tradeDay : 2018-01-26
     * upDropPrice : -0.05150000000000021
     * upDropSpeed : -0.13157894736842
     * upTime : 1516950438965
     * upTimeFormat : 2018-01-26 15:07:18
     * volume : 91431.3021
     */

    private double askPrice;     //卖出价（买价）
    private double bidPrice;     //买入价（卖价）
    private String code;         //代码
    private String currencyMoney;//计价货币
    private String exchangeCode; //交易所code
    private double highestPrice; //最高价
    private double lastPrice;    // 最新 成交价
    private double lastVolume;   // 最新量（最新成交总量）
    private double lowestPrice;
    private double marketValue;  // 市值
    private String name;         // 基准货币
    private double rate;         //兑换rmb 汇率
    private int status;
    private String tradeDay;
    private double upDropPrice;  // 涨跌值
    private double upDropSpeed;  //涨跌幅
    private long upTime;
    private String upTimeFormat;
    private double volume;
    private int seq;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public String getCode() {
        return code;
    }

    public String getCurrencyMoney() {
        return currencyMoney;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public int getStatus() {
        return status;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public long getUpTime() {
        return upTime;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public double getVolume() {
        return volume;
    }

    public double getUpDropPrice() {
        return upDropPrice;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "askPrice=" + askPrice +
                ", bidPrice=" + bidPrice +
                ", code='" + code + '\'' +
                ", currencyMoney='" + currencyMoney + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", highestPrice=" + highestPrice +
                ", lastPrice=" + lastPrice +
                ", lastVolume=" + lastVolume +
                ", lowestPrice=" + lowestPrice +
                ", marketValue=" + marketValue +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                ", status=" + status +
                ", tradeDay='" + tradeDay + '\'' +
                ", upDropPrice=" + upDropPrice +
                ", upDropSpeed=" + upDropSpeed +
                ", upTime=" + upTime +
                ", upTimeFormat='" + upTimeFormat + '\'' +
                ", volume=" + volume +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.askPrice);
        dest.writeDouble(this.bidPrice);
        dest.writeString(this.code);
        dest.writeString(this.currencyMoney);
        dest.writeString(this.exchangeCode);
        dest.writeDouble(this.highestPrice);
        dest.writeDouble(this.lastPrice);
        dest.writeDouble(this.lastVolume);
        dest.writeDouble(this.lowestPrice);
        dest.writeDouble(this.marketValue);
        dest.writeString(this.name);
        dest.writeDouble(this.rate);
        dest.writeInt(this.status);
        dest.writeString(this.tradeDay);
        dest.writeDouble(this.upDropPrice);
        dest.writeDouble(this.upDropSpeed);
        dest.writeLong(this.upTime);
        dest.writeString(this.upTimeFormat);
        dest.writeDouble(this.volume);
    }

    public MarketData() {
    }

    protected MarketData(Parcel in) {
        this.askPrice = in.readDouble();
        this.bidPrice = in.readDouble();
        this.code = in.readString();
        this.currencyMoney = in.readString();
        this.exchangeCode = in.readString();
        this.highestPrice = in.readDouble();
        this.lastPrice = in.readDouble();
        this.lastVolume = in.readDouble();
        this.lowestPrice = in.readDouble();
        this.marketValue = in.readDouble();
        this.name = in.readString();
        this.rate = in.readDouble();
        this.status = in.readInt();
        this.tradeDay = in.readString();
        this.upDropPrice = in.readDouble();
        this.upDropSpeed = in.readDouble();
        this.upTime = in.readLong();
        this.upTimeFormat = in.readString();
        this.volume = in.readDouble();
    }

    public static final Creator<MarketData> CREATOR = new Creator<MarketData>() {
        @Override
        public MarketData createFromParcel(Parcel source) {
            return new MarketData(source);
        }

        @Override
        public MarketData[] newArray(int size) {
            return new MarketData[size];
        }
    };
}
