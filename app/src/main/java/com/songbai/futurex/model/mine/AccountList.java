package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/6
 */
public class AccountList implements Parcelable {

    /**
     * balance : 1.3334794261721573E9
     * account : [{"ableCoin":999999999,"coinType":"etc","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":["etc_usdt","etc_eth"],"recharge":1},{"ableCoin":999999999,"coinType":"btc","estimateBtc":999999999,"freezeCoin":0,"isCanDraw":1,"pairs":["btc_usdt"],"recharge":1},{"ableCoin":999999999,"coinType":"usdt","estimateBtc":146094.17215737264,"freezeCoin":0,"isCanDraw":1,"pairs":["btc_usdt","eth_usdt","ltc_usdt","neo_usdt","bch_usdt","qtum_usdt","etc_usdt","eos_usdt"],"recharge":1},{"ableCoin":999999999,"coinType":"eth","estimateBtc":333333333,"freezeCoin":0,"isCanDraw":1,"pairs":["eth_btc","eth_usdt"],"recharge":1},{"ableCoin":999999999,"coinType":"ltc","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":["ltc_usdt","ltc_eth","ltc_btc"],"recharge":1},{"ableCoin":999999999,"coinType":"bch","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":["bch_usdt","bch_btc","bch_eth"],"recharge":1},{"ableCoin":999999999,"coinType":"qtum","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":["qtum_usdt","qtum_btc","qtum_eth"],"recharge":1},{"ableCoin":999999999,"coinType":"bcx","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":[],"recharge":1},{"ableCoin":999999999,"coinType":"neo","estimateBtc":0,"freezeCoin":0,"isCanDraw":1,"pairs":["neo_usdt","neo_btc","neo_eth"],"recharge":1},{"ableCoin":999999999,"coinType":"eos","estimateBtc":0,"freezeCoin":0,"isCanDraw":0,"pairs":["eos_usdt"],"recharge":0}]
     */

    private String balance;
    private List<AccountBean> account;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public List<AccountBean> getAccount() {
        return account;
    }

    public void setAccount(List<AccountBean> account) {
        this.account = account;
    }

    public static class AccountBean implements Parcelable {
        public static final int CANT_RECHAREGE = 0;
        public static final int CAN_RECHAREGE = 1;
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
        private String ableCoin;
        private String coinType;
        @SerializedName(value = "estimateBtc", alternate = {"estimate"})
        private String estimateBtc;
        @SerializedName(value = "freezeCoin", alternate = {"freeze"})
        private String freezeCoin;
        private int isCanDraw;
        private int recharge;
        private int legal;
        private List<String> pairs;

        public String getAbleCoin() {
            return ableCoin;
        }

        public void setAbleCoin(String ableCoin) {
            this.ableCoin = ableCoin;
        }

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getEstimateBtc() {
            return estimateBtc;
        }

        public void setEstimateBtc(String estimateBtc) {
            this.estimateBtc = estimateBtc;
        }

        public String getFreezeCoin() {
            return freezeCoin;
        }

        public void setFreezeCoin(String freezeCoin) {
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
            dest.writeString(this.ableCoin);
            dest.writeString(this.coinType);
            dest.writeString(this.estimateBtc);
            dest.writeString(this.freezeCoin);
            dest.writeInt(this.isCanDraw);
            dest.writeInt(this.recharge);
            dest.writeInt(this.legal);
            dest.writeStringList(this.pairs);
        }

        protected AccountBean(Parcel in) {
            this.ableCoin = in.readString();
            this.coinType = in.readString();
            this.estimateBtc = in.readString();
            this.freezeCoin = in.readString();
            this.isCanDraw = in.readInt();
            this.recharge = in.readInt();
            this.legal = in.readInt();
            this.pairs = in.createStringArrayList();
        }

        public static final Creator<AccountBean> CREATOR = new Creator<AccountBean>() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.balance);
        dest.writeList(this.account);
    }

    public AccountList() {
    }

    protected AccountList(Parcel in) {
        this.balance = in.readString();
        this.account = new ArrayList<AccountBean>();
        in.readList(this.account, AccountBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AccountList> CREATOR = new Parcelable.Creator<AccountList>() {
        @Override
        public AccountList createFromParcel(Parcel source) {
            return new AccountList(source);
        }

        @Override
        public AccountList[] newArray(int size) {
            return new AccountList[size];
        }
    };
}
