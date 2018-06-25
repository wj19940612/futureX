package com.songbai.futurex.websocket.model;

/**
 * Modified by john on 2018/6/22
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class DealData implements TradeDir {

    private String instrumentId;
    private double lastPrice;
    private double lastVolume;
    private long upTime;
    private String tradeDay;
    private String upTimeFormat;
    private int direction;

    public String getInstrumentId() {
        return instrumentId;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public long getUpTime() {
        return upTime;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public int getDirection() {
        return direction;
    }
}
