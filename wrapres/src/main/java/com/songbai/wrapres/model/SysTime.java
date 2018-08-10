package com.songbai.wrapres.model;


import java.util.Date;

public class SysTime {

    private static final String RECORD_KEY = "SysTime";

    private static SysTime sSysTime;

    public static SysTime getSysTime() {
        if (sSysTime == null) {
            sSysTime = new SysTime();
        }
        return sSysTime;
    }

    private long mSystemTime;

    public void sync() {
        //if (Math.abs(TimeRecorder.getElapsedTimeInMinute(RECORD_KEY)) < 10) return;

        //TODO 获取时间
//        Apic.syncSystemTime()
//                .host(Apic.BC_NEWS_HOST)
//                .timeout(5 * 1000)
//                .callback(new Callback<Resp<Long>>() {
//                    @Override
//                    protected void onRespSuccess(Resp<Long> resp) {
//                    }
//                }).fire();
    }

    public long getSystemTimestamp() {
        return new Date().getTime();
    }
}
