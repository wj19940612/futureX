package com.songbai.futurex.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.sbai.httplib.ReqIndeterminate;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.utils.SecurityUtil;
import com.songbai.futurex.utils.TimerHandler;
import com.songbai.futurex.view.RequestProgress;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.websocket.MessageProcessor;

import java.security.NoSuchAlgorithmException;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description: 基础 Activity
 */
public class BaseActivity extends StatusBarActivity implements ReqIndeterminate, TimerHandler.TimerCallback {

    public static final String ACTION_TOKEN_EXPIRED = "com.sbai.fin.token_expired";
    public static final String ACTION_LOGIN_SUCCESS = "com.sbai.fin.login_success";

    public static final String EX_TOKEN_EXPIRED_MESSAGE = "ex_token_expired_message";

    protected String TAG;

    private TimerHandler mTimerHandler;
    private RequestProgress mRequestProgress;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_TOKEN_EXPIRED.equalsIgnoreCase(intent.getAction())) {
                //LocalUser.getUser().logout();
//                SimpleConnector.get().disconnect();
//                SimpleConnector.get().connect();
//                Launcher.with(getActivity(), LoginActivity.class).execute();
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
                //Api.cancel(TAG);
            }
        });
        //MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        SysTime.getSysTime().sync();
        MessageProcessor.get().connect();
    }

    private void scrollToTop(View view) {
        if (view instanceof AbsListView) {
            ((AbsListView) view).smoothScrollToPositionFromTop(0, 0);
        } else if (view instanceof RecyclerView) {
            ((RecyclerView) view).smoothScrollToPosition(0);
        } else if (view instanceof ScrollView) {
            ((ScrollView) view).smoothScrollTo(0, 0);
        }
    }

    /**
     * 点击 anchor 向上滚动 AbsListView / RecyclerView / ScrollView
     *
     * @param anchor
     * @param view
     */
    protected void scrollToTop(View anchor, final View view) {
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop(view);
            }
        });
    }

    /**
     * 友盟统计埋点
     *
     * @param eventKey
     */
    protected void umengEventCount(String eventKey) {
        //MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //MobclickAgent.onPageStart(TAG);
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd(TAG);
        //MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(ACTION_TOKEN_EXPIRED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Api.cancel(TAG);

        SmartDialog.dismiss(this);

        mRequestProgress.dismissAll();

        stopScheduleJob();
    }

    protected FragmentActivity getActivity() {
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

    protected void startScheduleJob(int millisecond, long delayMillis) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }
        mTimerHandler.sendEmptyMessageDelayed(millisecond, delayMillis);
    }

    protected void startScheduleJob(int millisecond) {
        startScheduleJob(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.resetCount();
        }
    }

    @Override
    public void onTimeUp(int count) {
    }

    protected String md5Encrypt(String value) {
        try {
            return SecurityUtil.md5Encrypt(value);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return value;
        }
    }
}
