package com.songbai.futurex.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class BindBankList implements Parcelable {

    public static final int ALIPAY_WECHATPAY_BIND = 1;

    /**
     * aliPay : {"bind":1,"blurCardNumber":"18*******20","blurRealName":"*1","cardNumber":"18*******20","createTime":1528711832000,"id":113,"payType":1,"realName":"*1","status":1,"updateTime":1528711832000,"userId":100420}
     * bankCard : [{"bankArea":1,"bankBranch":"滨江","bankName":"中国工商银行","bind":1,"blurRealName":"**","cardNumber":"*****6467","createTime":1528709360000,"id":112,"payType":3,"realName":"**","status":1,"updateTime":1528709360000,"userId":100420}]
     * wechat : {"bind":1,"blurCardNumber":"*1493994*","blurRealName":"*1","cardNumber":"*1493994*","createTime":1528711977000,"id":114,"payType":2,"realName":"*1","status":1,"updateTime":1528711977000,"userId":100420}
     */

    private AliPayBean aliPay;
    private WechatBean wechat;
    private List<BankCardBean> bankCard;

    public AliPayBean getAliPay() {
        return aliPay;
    }

    public void setAliPay(AliPayBean aliPay) {
        this.aliPay = aliPay;
    }

    public WechatBean getWechat() {
        return wechat;
    }

    public void setWechat(WechatBean wechat) {
        this.wechat = wechat;
    }

    public List<BankCardBean> getBankCard() {
        return bankCard;
    }

    public void setBankCard(List<BankCardBean> bankCard) {
        this.bankCard = bankCard;
    }

    public static class AliPayBean implements Parcelable {
        /**
         * bind : 1
         * blurCardNumber : 18*******20
         * blurRealName : *1
         * cardNumber : 18*******20
         * createTime : 1528711832000
         * id : 113
         * payType : 1
         * realName : *1
         * status : 1
         * updateTime : 1528711832000
         * userId : 100420
         */

        private int bind;
        private String blurCardNumber;
        private String blurRealName;
        private String cardNumber;
        private long createTime;
        private int id;
        private int payType;
        private String realName;
        private int status;
        private long updateTime;
        private int userId;

        public int getBind() {
            return bind;
        }

        public void setBind(int bind) {
            this.bind = bind;
        }

        public String getBlurCardNumber() {
            return blurCardNumber;
        }

        public void setBlurCardNumber(String blurCardNumber) {
            this.blurCardNumber = blurCardNumber;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.bind);
            dest.writeString(this.blurCardNumber);
            dest.writeString(this.blurRealName);
            dest.writeString(this.cardNumber);
            dest.writeLong(this.createTime);
            dest.writeInt(this.id);
            dest.writeInt(this.payType);
            dest.writeString(this.realName);
            dest.writeInt(this.status);
            dest.writeLong(this.updateTime);
            dest.writeInt(this.userId);
        }

        public AliPayBean() {
        }

        protected AliPayBean(Parcel in) {
            this.bind = in.readInt();
            this.blurCardNumber = in.readString();
            this.blurRealName = in.readString();
            this.cardNumber = in.readString();
            this.createTime = in.readLong();
            this.id = in.readInt();
            this.payType = in.readInt();
            this.realName = in.readString();
            this.status = in.readInt();
            this.updateTime = in.readLong();
            this.userId = in.readInt();
        }

        public static final Creator<AliPayBean> CREATOR = new Creator<AliPayBean>() {
            @Override
            public AliPayBean createFromParcel(Parcel source) {
                return new AliPayBean(source);
            }

            @Override
            public AliPayBean[] newArray(int size) {
                return new AliPayBean[size];
            }
        };
    }

    public static class WechatBean implements Parcelable {
        /**
         * bind : 1
         * blurCardNumber : *1493994*
         * blurRealName : *1
         * cardNumber : *1493994*
         * createTime : 1528711977000
         * id : 114
         * payType : 2
         * realName : *1
         * status : 1
         * updateTime : 1528711977000
         * userId : 100420
         */

        private int bind;
        private String blurCardNumber;
        private String blurRealName;
        private String cardNumber;
        private long createTime;
        private int id;
        private int payType;
        private String realName;
        private int status;
        private long updateTime;
        private int userId;

        public int getBind() {
            return bind;
        }

        public void setBind(int bind) {
            this.bind = bind;
        }

        public String getBlurCardNumber() {
            return blurCardNumber;
        }

        public void setBlurCardNumber(String blurCardNumber) {
            this.blurCardNumber = blurCardNumber;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.bind);
            dest.writeString(this.blurCardNumber);
            dest.writeString(this.blurRealName);
            dest.writeString(this.cardNumber);
            dest.writeLong(this.createTime);
            dest.writeInt(this.id);
            dest.writeInt(this.payType);
            dest.writeString(this.realName);
            dest.writeInt(this.status);
            dest.writeLong(this.updateTime);
            dest.writeInt(this.userId);
        }

        public WechatBean() {
        }

        protected WechatBean(Parcel in) {
            this.bind = in.readInt();
            this.blurCardNumber = in.readString();
            this.blurRealName = in.readString();
            this.cardNumber = in.readString();
            this.createTime = in.readLong();
            this.id = in.readInt();
            this.payType = in.readInt();
            this.realName = in.readString();
            this.status = in.readInt();
            this.updateTime = in.readLong();
            this.userId = in.readInt();
        }

        public static final Creator<WechatBean> CREATOR = new Creator<WechatBean>() {
            @Override
            public WechatBean createFromParcel(Parcel source) {
                return new WechatBean(source);
            }

            @Override
            public WechatBean[] newArray(int size) {
                return new WechatBean[size];
            }
        };
    }

    public static class BankCardBean implements Parcelable {
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
            dest.writeInt(this.status);
            dest.writeLong(this.updateTime);
            dest.writeInt(this.userId);
        }

        public BankCardBean() {
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
            this.status = in.readInt();
            this.updateTime = in.readLong();
            this.userId = in.readInt();
        }

        public static final Creator<BankCardBean> CREATOR = new Creator<BankCardBean>() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.aliPay, flags);
        dest.writeParcelable(this.wechat, flags);
        dest.writeList(this.bankCard);
    }

    public BindBankList() {
    }

    protected BindBankList(Parcel in) {
        this.aliPay = in.readParcelable(AliPayBean.class.getClassLoader());
        this.wechat = in.readParcelable(WechatBean.class.getClassLoader());
        this.bankCard = new ArrayList<BankCardBean>();
        in.readList(this.bankCard, BankCardBean.class.getClassLoader());
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
