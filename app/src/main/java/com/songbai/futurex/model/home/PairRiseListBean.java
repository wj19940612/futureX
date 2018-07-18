package com.songbai.futurex.model.home;

/**
 * @author yangguangda
 * @date 2018/6/20
 */
public class PairRiseListBean {

    /**
     * lastPrice : 16.731
     * pairs : etc_usdt
     * prefixSymbol : etc
     * pricePoint : 3
     * suffixSymbol : usdt
     * upDropPrice : 0.587
     * upDropSpeed : 0.0364
     * volume : 179260.98457887
     */

    private double lastPrice;
    private String pairs;
    private String prefixSymbol;
    private int pricePoint;
    private String suffixSymbol;
    private double upDropPrice;
    private double upDropSpeed;
    private double volume;

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getPairs() {
        return pairs;
    }

    public void setPairs(String pairs) {
        this.pairs = pairs;
    }

    public String getPrefixSymbol() {
        return prefixSymbol;
    }

    public void setPrefixSymbol(String prefixSymbol) {
        this.prefixSymbol = prefixSymbol;
    }

    public int getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(int pricePoint) {
        this.pricePoint = pricePoint;
    }

    public String getSuffixSymbol() {
        return suffixSymbol;
    }

    public void setSuffixSymbol(String suffixSymbol) {
        this.suffixSymbol = suffixSymbol;
    }

    public double getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(double upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
