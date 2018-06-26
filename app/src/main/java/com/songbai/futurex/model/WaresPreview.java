package com.songbai.futurex.model;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/25
 */
public class WaresPreview {

    /**
     * bankList : [{"bind":0,"blurCardNumber":"18*******20","blurRealName":"*1","cardNumber":"18*******20","id":113,"payType":1,"realName":"*1"}]
     * coinSymbol : usdt
     * counterUserId : 100420
     * dealType : 1
     * fixedPrice : 10.0
     * maxTurnover : 10000.0
     * minTurnover : 10.0
     * payCurrency : cny
     * priceType : 1
     * remark : 呵呵哈哈哈
     * tradeCount : 15
     */

    private String coinSymbol;
    private int counterUserId;
    private int dealType;
    private double percent;
    private double fixedPrice;
    private double maxTurnover;
    private double minTurnover;
    private String payCurrency;
    private int priceType;
    private String remark;
    private String tradeCount;
    private List<BankListBean> bankList;

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public int getCounterUserId() {
        return counterUserId;
    }

    public void setCounterUserId(int counterUserId) {
        this.counterUserId = counterUserId;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public double getMaxTurnover() {
        return maxTurnover;
    }

    public void setMaxTurnover(double maxTurnover) {
        this.maxTurnover = maxTurnover;
    }

    public double getMinTurnover() {
        return minTurnover;
    }

    public void setMinTurnover(double minTurnover) {
        this.minTurnover = minTurnover;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(String tradeCount) {
        this.tradeCount = tradeCount;
    }

    public List<BankListBean> getBankList() {
        return bankList;
    }

    public void setBankList(List<BankListBean> bankList) {
        this.bankList = bankList;
    }

    public static class BankListBean {
        /**
         * bind : 0
         * blurCardNumber : 18*******20
         * blurRealName : *1
         * cardNumber : 18*******20
         * id : 113
         * payType : 1
         * realName : *1
         */

        private int bind;
        private String blurCardNumber;
        private String blurRealName;
        private String cardNumber;
        private int id;
        private int payType;
        private String realName;

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
    }
}
