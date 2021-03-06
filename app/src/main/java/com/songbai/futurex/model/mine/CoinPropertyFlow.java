package com.songbai.futurex.model.mine;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class CoinPropertyFlow {
    private String id;
    private String coinType;
    private long createTime;
    @SerializedName(value = "flowType", alternate = {"flowCode"})
    private int flowType;
    private int status;
    @SerializedName(value = "value", alternate = {"commission"})
    private double value;
    private String wid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }
}
