package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

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

    private double balance;
    private List<AccountBean> account;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<AccountBean> getAccount() {
        return account;
    }

    public void setAccount(List<AccountBean> account) {
        this.account = account;
    }

    public AccountList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.balance);
        dest.writeTypedList(this.account);
    }

    protected AccountList(Parcel in) {
        this.balance = in.readDouble();
        this.account = in.createTypedArrayList(AccountBean.CREATOR);
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
