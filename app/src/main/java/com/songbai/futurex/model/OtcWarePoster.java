package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/6/22
 */
public class OtcWarePoster {

    /**
     * coinSymbol : btc
     * conditionType : auth,trade
     * conditionValue : 1,1
     * counterUserId : 100420
     * createTime : 1529660598000
     * dealCount : 0.0
     * dealType : 2
     * deleted : 0
     * frozenCount : 0.0
     * id : 224
     * maxTurnover : 100000.0
     * minTurnover : 1000.0
     * payCurrency : cny
     * payIds : 113,112
     * payInfo : aliPay,bankPay
     * percent : 999.0
     * priceType : 2
     * remark : 快来买呀
     * status : 1
     * totalCount : 0.0
     * tradeCount : 800.0
     * updateTime : 1529660672000
     */

    private String coinSymbol;
    private String conditionType;
    private String conditionValue;
    private int counterUserId;
    private long createTime;
    private double dealCount;
    private int dealType;
    private int deleted;
    private double frozenCount;
    private int id;
    private double maxTurnover;
    private double minTurnover;
    private String payCurrency;
    private String payIds;
    private String payInfo;
    private double percent;
    private int priceType;
    private String remark;
    private int status;
    private double totalCount;
    private double tradeCount;
    private long updateTime;

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

    public int getCounterUserId() {
        return counterUserId;
    }

    public void setCounterUserId(int counterUserId) {
        this.counterUserId = counterUserId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getDealCount() {
        return dealCount;
    }

    public void setDealCount(double dealCount) {
        this.dealCount = dealCount;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public double getFrozenCount() {
        return frozenCount;
    }

    public void setFrozenCount(double frozenCount) {
        this.frozenCount = frozenCount;
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

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getPayIds() {
        return payIds;
    }

    public void setPayIds(String payIds) {
        this.payIds = payIds;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public double getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(double tradeCount) {
        this.tradeCount = tradeCount;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
