package com.songbai.futurex.model.local;

import com.songbai.futurex.Preference;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback4Resp;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.utils.TimeRecorder;

import java.util.Date;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description: 系统时间对象
 * <p>
 * APIs:
 */
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
        if (Math.abs(TimeRecorder.getElapsedTimeInMinute(RECORD_KEY)) < 10) return;

        Apic.getSystemTime()
                .callback(new Callback4Resp<Resp<Long>, Long>() {
                    @Override
                    protected void onRespData(Long data) {
                        mSystemTime = data.longValue();
                        Preference.get().setServerTime(mSystemTime);
                        TimeRecorder.record(RECORD_KEY);
                    }

                    @Override
                    protected boolean toastError() {
                        return false;
                    }
                }).fire();
    }

    public long getSystemTimestamp() {
        if (mSystemTime == 0) {
            mSystemTime = Preference.get().getServerTime();
            if (mSystemTime == 0) {
                return new Date().getTime();
            }
        }
        return mSystemTime + TimeRecorder.getElapsedTimeInMillis(RECORD_KEY);
    }
}
