package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/6/9
 */
public class BankListBean {

    public static final int AREA_MAINLAND = 1;
    public static final int AREA_TW = 2;
    /**
     * bankArea : 1
     * bankName : 中国工商银行
     * createTime : 1525251422000
     * icon : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525261601611&di=bd8b0449318b156f006ef526d21658cf&imgtype=0&src=http%3A%2F%2Fwww.mxw.gov.cn%2FUpload%2F201312%2F20131205020245618.JPG
     * id : 1
     * sort : 0
     * status : 1
     * updateTime : 1525251701000
     */

    private int bankArea;
    private String bankName;
    private long createTime;
    private String icon;
    private int id;
    private int sort;
    private int status;
    private long updateTime;

    public int getBankArea() {
        return bankArea;
    }

    public void setBankArea(int bankArea) {
        this.bankArea = bankArea;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
