package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yangguangda
 * @date 2018/6/27
 */
public class BankCardBean implements Parcelable {
    public static final int ALIPAY_WECHATPAY_BIND = 1;

    public static final int PAYTYPE_ALIPAY = 1;
    public static final int PAYTYPE_WX = 2;
    public static final int PAYTYPE_BANK = 3;
    /**
     * bankArea : 1
     * bankBranch : 滨江
     * bankName : 中国工商银行
     * bind : 1
     * blurRealName : **
     * cardNumber : *****6467
     * createTime : 1528709360000
     * id : 112
     * payType : 3
     * realName : **
     * status : 1
     * updateTime : 1528709360000
     * userId : 100420
     */

    private int bankArea;
    private String bankBranch;
    private String bankName;
    private int bind;
    private String blurRealName;
    private String cardNumber;
    private long createTime;
    private int id;
    private int payType;
    private String realName;
    private String payPic;
    private int status;
    private long updateTime;
    private int userId;

    public int getBankArea() {
        return bankArea;
    }

    public void setBankArea(int bankArea) {
        this.bankArea = bankArea;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public String getBlurRealName() {
        return blurRealName;
    }

    public void setBlurRealName(String blurRealName) {
        this.blurRealName = blurRealName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPayPic() {
        return payPic;
    }

    public void setPayPic(String payPic) {
        this.payPic = payPic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BankCardBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bankArea);
        dest.writeString(this.bankBranch);
        dest.writeString(this.bankName);
        dest.writeInt(this.bind);
        dest.writeString(this.blurRealName);
        dest.writeString(this.cardNumber);
        dest.writeLong(this.createTime);
        dest.writeInt(this.id);
        dest.writeInt(this.payType);
        dest.writeString(this.realName);
        dest.writeString(this.payPic);
        dest.writeInt(this.status);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.userId);
    }

    protected BankCardBean(Parcel in) {
        this.bankArea = in.readInt();
        this.bankBranch = in.readString();
        this.bankName = in.readString();
        this.bind = in.readInt();
        this.blurRealName = in.readString();
        this.cardNumber = in.readString();
        this.createTime = in.readLong();
        this.id = in.readInt();
        this.payType = in.readInt();
        this.realName = in.readString();
        this.payPic = in.readString();
        this.status = in.readInt();
        this.updateTime = in.readLong();
        this.userId = in.readInt();
    }

    public static final Parcelable.Creator<BankCardBean> CREATOR = new Parcelable.Creator<BankCardBean>() {
        @Override
        public BankCardBean createFromParcel(Parcel source) {
            return new BankCardBean(source);
        }

        @Override
        public BankCardBean[] newArray(int size) {
            return new BankCardBean[size];
        }
    };
}
