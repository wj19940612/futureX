package com.songbai.futurex.model.local;

import android.text.TextUtils;

/**
 * @author yangguangda
 * @date 2018/6/12
 */
public class GetUserFinanceFlowData {
    String coinType;//币种
    String flowType;//类型
    String type;//类型
    String flowCode;//类型
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
        if (!TextUtils.isEmpty(flowType)) {
            return flowType;
        }
        if (!TextUtils.isEmpty(type)) {
            return type;
        }
        if (!TextUtils.isEmpty(flowCode)) {
            return flowCode;
        }
        return "";
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
        this.type = flowType;
        this.flowCode = flowType;
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