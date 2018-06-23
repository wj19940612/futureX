package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/21
 */
public class GetOtcWaresHome {
    int page;
    int pageSize;
    String coinType;
    String orderId;
    String sortName;
    String sort;
    int type;
    String payCurrency;
    int orientation;
    String waresId;
    String payType;
    String cost;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getWaresId() {
        return waresId;
    }

    public void setWaresId(String waresId) {
        this.waresId = waresId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
