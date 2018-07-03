package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class GetUserFinanceFlowData {
    String coinType;//币种
    String flowType;//类型
    String status;//结束时间
    String startTime;//开始时间
    String endTime;//结束时间

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}