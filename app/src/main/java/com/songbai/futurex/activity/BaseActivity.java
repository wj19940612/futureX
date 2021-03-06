package com.songbai.futurex.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.sbai.httplib.ReqIndeterminate;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.local.SysTime;
import com.songbai.futurex.utils.LanguageUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.SecurityUtil;
import com.songbai.futurex.utils.TimerHandler;
import com.songbai.futurex.view.RequestProgress;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.websocket.MessageProcessor;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description: 基础 Activity
 */
public class BaseActivity extends StatusBarActivity implements ReqIndeterminate, TimerHandler.TimerCallback {

    public static final String ACTION_TOKEN_EXPIRED = "com.sbai.fin.token_expired";
    public static final String EX_TOKEN_EXPIRED_MESSAGE = "ex_token_expired_message";

    public static final int REQ_CODE_LOGIN = 808;

    protected String TAG;

    private TimerHandler mTimerHandler;
    private RequestProgress mRequestProgress;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_TOKEN_EXPIRED.equalsIgnoreCase(intent.getAction())) {
                LocalUser.getUser().logout();
//                SimpleConnector.get().disconnect();
//                SimpleConnector.get().connect();

                showUserInvalidDialog();
            }
        }
    };

    private void showUserInvalidDialog() {
        SmartDialog.solo(getActivity(), R.string.user_invalid)
                .setCancelableOnTouchOutside(false)
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog) {
                        dialog.dismiss();
                        reopenMainPage();
                    }
                }).setPositive(R.string.ok, new SmartDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
                Launcher.with(getActivity(), LoginActivity.class).execute(REQ_CODE_LOGIN);
            }
        }).show();
    }

    private void reopenMainPage() {
        Launcher.with(getActivity(), MainActivity.class)
                .execute();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtils.attachBaseContext(base, LanguageUtils.getUserLocale(base)));
    }

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

        // umeng 统计场景初始化
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // Push 初始化
        PushAgent.getInstance(this).onAppStart();

        // 时间同步
        SysTime.getSysTime().sync();

        // 触发消息连接，页面切换就触发，已连接就不会触发连接
        MessageProcessor.get().connect();

        setLanguage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode != RESULT_OK) {
            reopenMainPage();
        }
        if (requestCode == REQ_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            recreate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setLanguage();
        super.onConfigurationChanged(newConfig);
    }

    private void setLanguage() {
        Locale locale = LanguageUtils.getUserLocale(this);
        LanguageUtils.updateLocale(this, locale);
    }

    /**
     * 点击 anchor 向上滚动 AbsListView / RecyclerView / ScrollView
     *
     * @param view
     */
    private void scrollToTop(View view) {
        if (view instanceof AbsListView) {
            ((AbsListView) view).smoothScrollToPositionFromTop(0, 0);
        } else if (view instanceof RecyclerView) {
            ((RecyclerView) view).smoothScrollToPosition(0);
        } else if (view instanceof ScrollView) {
            ((ScrollView) view).smoothScrollTo(0, 0);
        }
    }

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
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(ACTION_TOKEN_EXPIRED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
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
