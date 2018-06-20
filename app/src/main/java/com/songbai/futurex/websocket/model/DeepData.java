package com.songbai.futurex.websocket.model;

/**
 * Modified by john on 2018/6/20
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class DeepData {

    /**
     * price : 6613.94
     * count : 0.00249589
     * totalCount : 0
     */

    private double price;
    private double count;
    private double totalCount;

    public double getPrice() {
        return price;
    }

    public double getCount() {
        return count;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }
}
