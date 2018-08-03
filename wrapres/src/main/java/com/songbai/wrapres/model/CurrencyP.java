package com.songbai.wrapres.model;

public class CurrencyP {

    /**
     * code : ada_btc
     * createTime : 1517552222000
     * currencyMoney : btc
     * deleted : 0
     * exchangeName : gate.io
     * id : 245
     * name : ADA
     * seq : 133
     * status : 1
     * transactionMoney : ada
     * updateTime : 1517552222000
     */

    private String code;
    private long createTime;
    private String currencyMoney;
    private int deleted;
    private String exchangeName;
    private String id;
    private String name;
    private int seq;
    private int status;
    private String transactionMoney;
    private long updateTime;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCurrencyMoney() {
        return currencyMoney;
    }

    public void setCurrencyMoney(String currencyMoney) {
        this.currencyMoney = currencyMoney;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionMoney() {
        return transactionMoney;
    }

    public void setTransactionMoney(String transactionMoney) {
        this.transactionMoney = transactionMoney;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
