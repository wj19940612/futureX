package com.songbai.futurex.model;

import com.songbai.futurex.model.mine.BankCardBean;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class OtcOrderDetail {

    /**
     * bank : [{"bind":0,"blurCardNumber":"18*******20","blurRealName":"*1","cardNumber":"18*******20","payType":1,"realName":"*1"}]
     * order : {"buyerId":100392,"buyerName":"mxmx","buyerPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180627115037357/100392i1530071437358.jpg?x-oss-process=image/resize,m_fill,h_200,w_200","coinSymbol":"btc","conditionType":"auth","conditionValue":"2","delay":0,"fixedPrice":112,"orderAmount":1111,"orderCount":9.919642857143,"orderId":"1012153639990292481","orderPrice":112,"orderTime":1530151207000,"payCurrency":"twd","priceType":1,"quotaPrice":112,"sellerId":100420,"status":0}
     */

    private OrderBean order;
    private List<BankCardBean> bank;

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public List<BankCardBean> getBank() {
        return bank;
    }

    public void setBank(List<BankCardBean> bank) {
        this.bank = bank;
    }

    public static class OrderBean {
        /**
         * buyerId : 100392
         * buyerName : mxmx
         * buyerPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180627115037357/100392i1530071437358.jpg?x-oss-process=image/resize,m_fill,h_200,w_200
         * coinSymbol : btc
         * conditionType : auth
         * conditionValue : 2
         * delay : 0
         * fixedPrice : 112.0
         * orderAmount : 1111.0
         * orderCount : 9.919642857143
         * orderId : 1012153639990292481
         * orderPrice : 112.0
         * orderTime : 1530151207000
         * orderType : 1
         * payCurrency : twd
         * priceType : 1
         * quotaPrice : 112.0
         * sellerId : 100420
         * status : 0
         */

        private int buyerId;
        private String buyerName;
        private String buyerPortrait;
        private String coinSymbol;
        private String conditionType;
        private String conditionValue;
        private int countDown;
        private int delay;
        private double fixedPrice;
        private double orderAmount;
        private double orderCount;
        private String orderId;
        private double orderPrice;
        private long orderTime;
        private int orderType;
        private String payCurrency;
        private int priceType;
        private double quotaPrice;
        private int sellerId;
        private int status;

        public int getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(int buyerId) {
            this.buyerId = buyerId;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getBuyerPortrait() {
            return buyerPortrait;
        }

        public void setBuyerPortrait(String buyerPortrait) {
            this.buyerPortrait = buyerPortrait;
        }

        public String getCoinSymbol() {
            return coinSymbol;
        }

        public void setCoinSymbol(String coinSymbol) {
            this.coinSymbol = coinSymbol;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }

        public String getConditionValue() {
            return conditionValue;
        }

        public void setConditionValue(String conditionValue) {
            this.conditionValue = conditionValue;
        }

        public int getCountDown() {
            return countDown;
        }

        public void setCountDown(int countDown) {
            this.countDown = countDown;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public double getFixedPrice() {
            return fixedPrice;
        }

        public void setFixedPrice(double fixedPrice) {
            this.fixedPrice = fixedPrice;
        }

        public double getOrderAmount() {
            return orderAmount;
        }

        public void setOrderAmount(double orderAmount) {
            this.orderAmount = orderAmount;
        }

        public double getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(double orderCount) {
            this.orderCount = orderCount;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public double getOrderPrice() {
            return orderPrice;
        }

        public void setOrderPrice(double orderPrice) {
            this.orderPrice = orderPrice;
        }

        public long getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(long orderTime) {
            this.orderTime = orderTime;
        }

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
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

        public double getQuotaPrice() {
            return quotaPrice;
        }

        public void setQuotaPrice(double quotaPrice) {
            this.quotaPrice = quotaPrice;
        }

        public int getSellerId() {
            return sellerId;
        }

        public void setSellerId(int sellerId) {
            this.sellerId = sellerId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
