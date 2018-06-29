package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/22
 */
public class WaresModel {
    private int dealType;//交易类型:1买入,2卖出
    private String coinSymbol;//币种
    private int priceType;//价格 1,固定价格 ，2 浮动价格
    private String fixedPrice;//固定价格
    private String percent;//溢价百分比
    private double minTurnover;//最小成交额
    private double maxTurnover;//最大成交额
    private String payIds;//银行卡id
    private String payInfo;//广告的支付信息,逗号分隔 aliPay,wxPay,bankPay查看
    private String conditionType;//条件限制集合 auth认证  trade 交易次数
    private String conditionValue;//条件值的集合 与必须上面 对应
    private String payCurrency;//支付的货币符号 是
    private String remark;//备注
    private double tradeCount;//交易数量
    private String areaCode;//(v1.2)电话区号
    private String telephone;//z(v1.2)
    private String id;//广告ID

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(String fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public double getMinTurnover() {
        return minTurnover;
    }

    public void setMinTurnover(double minTurnover) {
        this.minTurnover = minTurnover;
    }

    public double getMaxTurnover() {
        return maxTurnover;
    }

    public void setMaxTurnover(double maxTurnover) {
        this.maxTurnover = maxTurnover;
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

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(double tradeCount) {
        this.tradeCount = tradeCount;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
