package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/9/17
 */
public class PreTradeBean {

    /**
     * waresId : 469
     * orderAmount : 1.0
     * coinSymbol : usdt
     * sellerName : 0#427_001
     * amout : 0.2
     * orderRate : 0.0
     * orderPrice : 1.0
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180515095522105.jpg
     * fixedPrice : 5.0
     * waresType : 0
     * coinCount : 0.2
     * ratio : 0.0
     */

    private int waresId;
    private double orderAmount;
    private String coinSymbol;
    private String sellerName;
    private double amout;
    private double orderRate;
    private double orderPrice;
    private String portrait;
    private double fixedPrice;
    private int waresType;
    private double coinCount;
    private double ratio;

    public int getWaresId() {
        return waresId;
    }

    public void setWaresId(int waresId) {
        this.waresId = waresId;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getAmout() {
        return amout;
    }

    public void setAmout(double amout) {
        this.amout = amout;
    }

    public double getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(double orderRate) {
        this.orderRate = orderRate;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public double getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public int getWaresType() {
        return waresType;
    }

    public void setWaresType(int waresType) {
        this.waresType = waresType;
    }

    public double getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(double coinCount) {
        this.coinCount = coinCount;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
