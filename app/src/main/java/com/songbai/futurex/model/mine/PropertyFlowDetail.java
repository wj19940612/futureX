package com.songbai.futurex.model.mine;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class PropertyFlowDetail {
    private int id;
    private String coinType;
    private long createTime;
    @SerializedName(value = "flowType", alternate = {"flowCode"})
    private int flowType;
    private int status;
    @SerializedName(value = "valueStr", alternate = {"value"})
    private String valueStr;
    private String toAddr;
    private String fee;
    private int confirmNum;
    private int confirm;
    private int ioStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(int confirmNum) {
        this.confirmNum = confirmNum;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public int getIoStatus() {
        return ioStatus;
    }

    public void setIoStatus(int ioStatus) {
        this.ioStatus = ioStatus;
    }
}
