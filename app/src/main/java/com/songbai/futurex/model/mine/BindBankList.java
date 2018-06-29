package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class BindBankList implements Parcelable {

    /**
     * aliPay : {"bind":1,"blurCardNumber":"18*******20","blurRealName":"*1","cardNumber":"18*******20","createTime":1528711832000,"id":113,"payType":1,"realName":"*1","status":1,"updateTime":1528711832000,"userId":100420}
     * bankCard : [{"bankArea":1,"bankBranch":"滨江","bankName":"中国工商银行","bind":1,"blurRealName":"**","cardNumber":"*****6467","createTime":1528709360000,"id":112,"payType":3,"realName":"**","status":1,"updateTime":1528709360000,"userId":100420}]
     * wechat : {"bind":1,"blurCardNumber":"*1493994*","blurRealName":"*1","cardNumber":"*1493994*","createTime":1528711977000,"id":114,"payType":2,"realName":"*1","status":1,"updateTime":1528711977000,"userId":100420}
     */

    private BankCardBean aliPay;
    private BankCardBean wechat;
    private List<BankCardBean> bankCard;

    public BankCardBean getAliPay() {
        return aliPay;
    }

    public void setAliPay(BankCardBean aliPay) {
        this.aliPay = aliPay;
    }

    public BankCardBean getWechat() {
        return wechat;
    }

    public void setWechat(BankCardBean wechat) {
        this.wechat = wechat;
    }

    public List<BankCardBean> getBankCard() {
        return bankCard;
    }

    public void setBankCard(List<BankCardBean> bankCard) {
        this.bankCard = bankCard;
    }

    public BindBankList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.aliPay, flags);
        dest.writeParcelable(this.wechat, flags);
        dest.writeTypedList(this.bankCard);
    }

    protected BindBankList(Parcel in) {
        this.aliPay = in.readParcelable(BankCardBean.class.getClassLoader());
        this.wechat = in.readParcelable(BankCardBean.class.getClassLoader());
        this.bankCard = in.createTypedArrayList(BankCardBean.CREATOR);
    }

    public static final Parcelable.Creator<BindBankList> CREATOR = new Parcelable.Creator<BindBankList>() {
        @Override
        public BindBankList createFromParcel(Parcel source) {
            return new BindBankList(source);
        }

        @Override
        public BindBankList[] newArray(int size) {
            return new BindBankList[size];
        }
    };
}
