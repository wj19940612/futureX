package com.songbai.futurex.model.home;

/**
 * @author yangguangda
 * @date 2018/7/30
 */
public class BFBInfo {

    /**
     * frontProduce : 0.00000000
     * nowProduce : 0.00000000
     * volume : 126337.924012773922
     */

    private String frontProduce;
    private String nowProduce;
    private String volume;
    private String frontMillionBfbConvertBtc;
    private int display;

    public String getFrontProduce() {
        return frontProduce;
    }

    public void setFrontProduce(String frontProduce) {
        this.frontProduce = frontProduce;
    }

    public String getNowProduce() {
        return nowProduce;
    }

    public void setNowProduce(String nowProduce) {
        this.nowProduce = nowProduce;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getFrontMillionBfbConvertBtc() {
        return frontMillionBfbConvertBtc;
    }

    public void setFrontMillionBfbConvertBtc(String frontMillionBfbConvertBtc) {
        this.frontMillionBfbConvertBtc = frontMillionBfbConvertBtc;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }
}
