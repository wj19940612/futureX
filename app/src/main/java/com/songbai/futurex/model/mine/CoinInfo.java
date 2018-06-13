package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class CoinInfo implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.balancePoint);
        dest.writeLong(this.createTime);
        dest.writeInt(this.id);
        dest.writeLong(this.supply);
        dest.writeString(this.symbol);
        dest.writeLong(this.updateTime);
    }

    public CoinInfo() {
    }

    protected CoinInfo(Parcel in) {
        this.balancePoint = in.readInt();
        this.createTime = in.readLong();
        this.id = in.readInt();
        this.supply = in.readLong();
        this.symbol = in.readString();
        this.updateTime = in.readLong();
    }

    public static final Parcelable.Creator<CoinInfo> CREATOR = new Parcelable.Creator<CoinInfo>() {
        @Override
        public CoinInfo createFromParcel(Parcel source) {
            return new CoinInfo(source);
        }

        @Override
        public CoinInfo[] newArray(int size) {
            return new CoinInfo[size];
        }
    };
}
