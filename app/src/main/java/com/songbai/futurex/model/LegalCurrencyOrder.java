package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class LegalCurrencyOrder {

    /**
     * changeName : mxmx
     * changePortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180627115037357/100392i1530071437358.jpg?x-oss-process=image/resize,m_fill,h_200,w_200
     * coinSymbol : btc
     * direct : 2
     * id : 531
     * orderAmount : 42848.59229
     * orderCount : 0.1
     * orderId : 1011058837290840065
     * orderPrice : 428485.9229
     * orderTime : 1529890186000
     * payCurrency : cny
     * percent : 999.0
     * priceType : 2
     * status : 0
     */

    private String changeName;
    private String changePortrait;
    private String coinSymbol;
    private long countDown;
    private int direct;
    private double fixedPrice;
    private String id;
    private double orderAmount;
    private double orderCount;
    private String orderId;
    private double orderPrice;
    private long orderTime;
    private String payCurrency;
    private double percent;
    private int priceType;
    private int status;

    public String getChangeName() {
        return changeName;
    }

    public void setChangeName(String changeName) {
        this.changeName = changeName;
    }

    public String getChangePortrait() {
        return changePortrait;
    }

    public void setChangePortrait(String changePortrait) {
        this.changePortrait = changePortrait;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public long getCountDown() {
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public double getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
