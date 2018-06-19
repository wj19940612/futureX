package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class CoinAddressCount implements Parcelable {

    /**
     * coinType : btc
     * count : 2
     */

    private String coinType;
    private int count;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coinType);
        dest.writeInt(this.count);
    }

    public CoinAddressCount() {
    }

    protected CoinAddressCount(Parcel in) {
        this.coinType = in.readString();
        this.count = in.readInt();
    }

    public static final Parcelable.Creator<CoinAddressCount> CREATOR = new Parcelable.Creator<CoinAddressCount>() {
        @Override
        public CoinAddressCount createFromParcel(Parcel source) {
            return new CoinAddressCount(source);
        }

        @Override
        public CoinAddressCount[] newArray(int size) {
            return new CoinAddressCount[size];
        }
    };
}
