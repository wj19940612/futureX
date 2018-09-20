package com.songbai.futurex.websocket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yangguangda
 * @date 2018/9/20
 */
public class PairsPrice implements Parcelable {

    /**
     * TWD : 30.6
     * USD : 1
     * CNY : 6.81
     */

    private double TWD;
    private double USD;
    private double CNY;

    public double getTWD() {
        return TWD;
    }

    public void setTWD(double TWD) {
        this.TWD = TWD;
    }

    public double getUSD() {
        return USD;
    }

    public void setUSD(double USD) {
        this.USD = USD;
    }

    public double getCNY() {
        return CNY;
    }

    public void setCNY(double CNY) {
        this.CNY = CNY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.TWD);
        dest.writeDouble(this.USD);
        dest.writeDouble(this.CNY);
    }

    public PairsPrice() {
    }

    protected PairsPrice(Parcel in) {
        this.TWD = in.readDouble();
        this.USD = in.readDouble();
        this.CNY = in.readDouble();
    }

    public static final Parcelable.Creator<PairsPrice> CREATOR = new Parcelable.Creator<PairsPrice>() {
        @Override
        public PairsPrice createFromParcel(Parcel source) {
            return new PairsPrice(source);
        }

        @Override
        public PairsPrice[] newArray(int size) {
            return new PairsPrice[size];
        }
    };
}
