package com.songbai.futurex.model.home;

/**
 * @author yangguangda
 * @date 2018/6/20
 */
public class PairRiseListBean {

    /**
     * lastPrice : 15.73
     * lastVolume : 0.0
     * pairs : qtum_usdt
     * prefixSymbol : qtum
     * suffixSymbol : usdt
     * upDropPrice : 0.0
     * upDropSpeed : 0.0
     */

    private double lastPrice;
    private double lastVolume;
    private String pairs;
    private String prefixSymbol;
    private String suffixSymbol;
    private double upDropPrice;
    private double upDropSpeed;

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(double lastVolume) {
        this.lastVolume = lastVolume;
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
}
