package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/7/12
 */
public class AccountBean implements Parcelable {
    public static final int CANT_RECHAREGE = 0;
    public static final int CAN_RECHARGE = 1;
    public static final int CANT_DRAW = 0;
    public static final int CAN_DRAW = 1;
    public static final int IS_LEGAL = 1;

    /**
     * ableCoin : 999999999
     * coinType : etc
     * estimateBtc : 0
     * freezeCoin : 0
     * isCanDraw : 1 //0：不可提币 1可提
     * pairs : ["etc_usdt","etc_eth"]
     * recharge : 1 //0：不可充值币1：可充币
     * legal : 1 //0：否1：是
     */
    @SerializedName(value = "ableCoin", alternate = {"usable"})
    private double ableCoin;
    private String coinType;
    @SerializedName(value = "estimateBtc", alternate = {"estimate"})
    private double estimateBtc;
    @SerializedName(value = "freezeCoin", alternate = {"freeze"})
    private double freezeCoin;
    private int isCanDraw;
    private int recharge;
    private int legal;
    private List<String> pairs;

    public double getAbleCoin() {
        return ableCoin;
    }

    public void setAbleCoin(double ableCoin) {
        this.ableCoin = ableCoin;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public double getEstimateBtc() {
        return estimateBtc;
    }

    public void setEstimateBtc(double estimateBtc) {
        this.estimateBtc = estimateBtc;
    }

    public double getFreezeCoin() {
        return freezeCoin;
    }

    public void setFreezeCoin(double freezeCoin) {
        this.freezeCoin = freezeCoin;
    }

    public int getIsCanDraw() {
        return isCanDraw;
    }

    public void setIsCanDraw(int isCanDraw) {
        this.isCanDraw = isCanDraw;
    }

    public int getRecharge() {
        return recharge;
    }

    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }

    public int getLegal() {
        return legal;
    }

    public void setLegal(int legal) {
        this.legal = legal;
    }

    public List<String> getPairs() {
        return pairs;
    }

    public void setPairs(List<String> pairs) {
        this.pairs = pairs;
    }

    public AccountBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.ableCoin);
        dest.writeString(this.coinType);
        dest.writeDouble(this.estimateBtc);
        dest.writeDouble(this.freezeCoin);
        dest.writeInt(this.isCanDraw);
        dest.writeInt(this.recharge);
        dest.writeInt(this.legal);
        dest.writeStringList(this.pairs);
    }

    protected AccountBean(Parcel in) {
        this.ableCoin = in.readDouble();
        this.coinType = in.readString();
        this.estimateBtc = in.readDouble();
        this.freezeCoin = in.readDouble();
        this.isCanDraw = in.readInt();
        this.recharge = in.readInt();
        this.legal = in.readInt();
        this.pairs = in.createStringArrayList();
    }

    public static final Parcelable.Creator<AccountBean> CREATOR = new Parcelable.Creator<AccountBean>() {
        @Override
        public AccountBean createFromParcel(Parcel source) {
            return new AccountBean(source);
        }

        @Override
        public AccountBean[] newArray(int size) {
            return new AccountBean[size];
        }
    };
}
