package com.songbai.wrapres.chart;

import android.support.annotation.NonNull;

/**
 * Modified by john on 24/02/2018
 * <p>
 * Description: 分时图数据结构
 * <p>
 * APIs:
 */
public class TrendData implements Comparable<TrendData> {

    private float closePrice;
    private String time;
    private double nowVolume;
    private long timeStamp;

    public float getClosePrice() {
        return closePrice;
    }

    public String getTime() {
        return time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public double getNowVolume() {
        return nowVolume;
    }

    @Override
    public int compareTo(@NonNull TrendData o) {
        return (int) (this.timeStamp - o.getTimeStamp());
    }
}
