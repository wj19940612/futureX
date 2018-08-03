package com.songbai.futurex.wrapper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sbai.httplib.ReqIndeterminate;
import com.songbai.futurex.activity.StatusBarActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.TimerHandler;
import com.songbai.futurex.view.RequestProgress;
import com.songbai.wrapres.SmartDialog;

public class WrapBaseActivity extends StatusBarActivity implements ReqIndeterminate, TimerHandler.TimerCallback {

    public static final String ACTION_TOKEN_EXPIRED = "com.sbai.fin.token_expired";
    public static final String EX_TOKEN_EXPIRED_MESSAGE = "ex_token_expired_message";

    protected String TAG;

    private TimerHandler mTimerHandler;
    private RequestProgress mRequestProgress;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_TOKEN_EXPIRED.equalsIgnoreCase(intent.getAction())) {
                LocalWrapUser.getUser().logout();
                Launcher.with(getActivity(), WrapLoginActivity.class).execute();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mRequestProgress = new RequestProgress(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Api.cancel(TAG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Api.cancel(TAG);

        SmartDialog.dismiss(this);

        mRequestProgress.dismissAll();

        stopScheduleJob();
    }

    protected void startScheduleJobNext(int millisecond) {
        startScheduleJobRightNow(millisecond, millisecond);
    }

    protected void startScheduleJobRightNow(int millisecond, long delayMillis) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }
        mTimerHandler.sendEmptyMessageDelayed(millisecond, delayMillis);
    }

    protected void startScheduleJobRightNow(int millisecond) {
        startScheduleJobRightNow(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.resetCount();
        }
    }

    protected Activity getActivity() {
        return this;
    }

    @Override
    public void onHttpUiShow(String tag) {
        if (mRequestProgress != null) {
            mRequestProgress.show(this);
        }
    }

    @Override
    public void onHttpUiDismiss(String tag) {
        if (mRequestProgress != null) {
            mRequestProgress.dismiss();
        }
    }

    @Override
    public void onTimeUp(int count) {

    }
}
