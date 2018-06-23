package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/21
 */
public class LegalCurrencyTrade {

    /**
     * authStatus : 1
     * bindEmail : 0
     * bindPhone : 0
     * budgetCoin : 0
     * changeCount : 1.00000000
     * coinSymbol : btc
     * countDeal : 7
     * dealType : 1
     * doneRate : 0.4375
     * fixedPrice : 43318.7
     * id : 219
     * maxTurnover : 9999.0
     * minTurnover : 1.0
     * operate : 1
     * payCurrency : CNY
     * payInfo : wxPay,bankPay,aliPay
     * priceType : 2
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180620175832676/100392i1529488712677.png?x-oss-process=image/resize,m_fill,h_200,w_200
     * username : mx
     */

    private int authStatus;
    private int bindEmail;
    private int bindPhone;
    private int budgetCoin;
    private String changeCount;
    private String coinSymbol;
    private int countDeal;
    private int dealType;
    private double doneRate;
    private double fixedPrice;
    private int id;
    private double maxTurnover;
    private double minTurnover;
    private int operate;
    private String payCurrency;
    private String payInfo;
    private int priceType;
    private String userPortrait;
    private String username;

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public int getBindEmail() {
        return bindEmail;
    }

    public void setBindEmail(int bindEmail) {
        this.bindEmail = bindEmail;
    }

    public int getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(int bindPhone) {
        this.bindPhone = bindPhone;
    }

    public int getBudgetCoin() {
        return budgetCoin;
    }

    public void setBudgetCoin(int budgetCoin) {
        this.budgetCoin = budgetCoin;
    }

    public String getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(String changeCount) {
        this.changeCount = changeCount;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public int getCountDeal() {
        return countDeal;
    }

    public void setCountDeal(int countDeal) {
        this.countDeal = countDeal;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public double getDoneRate() {
        return doneRate;
    }

    public void setDoneRate(double doneRate) {
        this.doneRate = doneRate;
    }

    public double getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
