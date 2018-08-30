package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/8/27
 */
public class NewOTCPrice {

    /**
     * buyPrice : 6.92
     * buyMinCount : 9.99
     * buyMaxPrice : 50000.0
     * sellMinPrice : 69.1
     * buyMaxCount : 7225.43
     * sellPrice : 6.9
     * sellMinCount : 10.0
     * sellMaxPrice : 50000.0
     * buyMinPrice : 69.1
     * sellMaxCount : 7235.89
     */

    private double buyPrice;
    private double sellPrice;
    private double buyMinCount;
    private double buyMaxCount;
    private double buyMinPrice;
    private double buyMaxPrice;
    private double sellMinPrice;
    private double sellMaxPrice;
    private double sellMinCount;
    private double sellMaxCount;

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getBuyMinCount() {
        return buyMinCount;
    }

    public void setBuyMinCount(double buyMinCount) {
        this.buyMinCount = buyMinCount;
    }

    public double getBuyMaxCount() {
        return buyMaxCount;
    }

    public void setBuyMaxCount(double buyMaxCount) {
        this.buyMaxCount = buyMaxCount;
    }

    public double getBuyMinPrice() {
        return buyMinPrice;
    }

    public void setBuyMinPrice(double buyMinPrice) {
        this.buyMinPrice = buyMinPrice;
    }

    public double getBuyMaxPrice() {
        return buyMaxPrice;
    }

    public void setBuyMaxPrice(double buyMaxPrice) {
        this.buyMaxPrice = buyMaxPrice;
    }

    public double getSellMinPrice() {
        return sellMinPrice;
    }

    public void setSellMinPrice(double sellMinPrice) {
        this.sellMinPrice = sellMinPrice;
    }

    public double getSellMaxPrice() {
        return sellMaxPrice;
    }

    public void setSellMaxPrice(double sellMaxPrice) {
        this.sellMaxPrice = sellMaxPrice;
    }

    public double getSellMinCount() {
        return sellMinCount;
    }

    public void setSellMinCount(double sellMinCount) {
        this.sellMinCount = sellMinCount;
    }

    public double getSellMaxCount() {
        return sellMaxCount;
    }

    public void setSellMaxCount(double sellMaxCount) {
        this.sellMaxCount = sellMaxCount;
    }
}
