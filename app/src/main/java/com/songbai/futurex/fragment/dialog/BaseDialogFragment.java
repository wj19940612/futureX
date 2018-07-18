package com.songbai.futurex.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.httplib.ReqIndeterminate;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.utils.TimerHandler;

/**
 * Modified by $nishuideyu$ on 2018/5/17
 * <p>
 * Description:
 * </p>
 */
public class BaseDialogFragment extends DialogFragment implements ReqIndeterminate, TimerHandler.TimerCallback {

    protected String TAG;
    private TimerHandler mTimerHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getDialogTheme());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(getWindowGravity());
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * getWidthRatio()), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    protected float getWidthRatio() {
        return 1f;
    }

    protected int getWindowGravity() {
        return Gravity.CENTER;
    }

    protected int getDialogTheme() {
        return R.style.BaseDialog;
    }

    public void show(FragmentManager manager) {
        try {
            this.show(manager, this.getClass().getSimpleName());
        } catch (IllegalStateException e) {
            Log.d(TAG, "show: " + e.toString());
        }
    }

    public void showAllowingStateLoss(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, this.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onTimeUp(int count) {

    }

    @Override
    public void onHttpUiShow(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onHttpUiShow(tag);
        }
    }

    @Override
    public void onHttpUiDismiss(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onHttpUiDismiss(tag);
        }
    }

    protected void startScheduleJob(int millisecond) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }

        mTimerHandler.sendEmptyMessageDelayed(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.resetCount();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleJob();
        Api.cancel(TAG);
    }
}
