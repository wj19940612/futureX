package com.songbai.futurex.model;

import android.support.annotation.NonNull;

public class KTrend implements Comparable<KTrend> {
    private double closePrice;
    private double nowVolume;
    private String time;
    private long timeStamp;

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getNowVolume() {
        return nowVolume;
    }

    public void setNowVolume(double nowVolume) {
        this.nowVolume = nowVolume;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(@NonNull KTrend kTrend) {
        if (this.closePrice - kTrend.closePrice == 0) {
            return 0;
        } else if (this.closePrice - kTrend.closePrice > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
