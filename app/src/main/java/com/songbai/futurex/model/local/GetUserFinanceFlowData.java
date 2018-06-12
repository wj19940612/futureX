package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class GetUserFinanceFlowData {
    String coinType;//币种
    int flowType;//类型
    String startTime;//开始时间
    String endTime;//结束时间

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
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