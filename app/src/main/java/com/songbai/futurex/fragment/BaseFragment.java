package com.songbai.futurex.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.sbai.httplib.ReqIndeterminate;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.utils.NotchUtil;
import com.songbai.futurex.utils.SecurityUtil;
import com.songbai.futurex.utils.TimerHandler;
import com.umeng.analytics.MobclickAgent;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment implements ReqIndeterminate, TimerHandler.TimerCallback {

    private TimerHandler mTimerHandler;

    protected String TAG;

    private PermissionCallback mPermissionCallback;
    private int mRequestCode;

    public interface PermissionCallback {
        void onPermissionGranted(int requestCode);

        void onPermissionDenied(int requestCode);
    }

    protected boolean hasSelfPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (String permission : permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }
    protected void requestPermission(Activity activity, int requestCode, String[] permissions) {
        requestPermission(activity, requestCode, permissions, null);
    }

    protected void requestPermission(Activity activity, int requestCode, String[] permissions, PermissionCallback callback) {
        mPermissionCallback = callback;
        mRequestCode = requestCode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6.0
            List<String> deniedPermissions = findDeniedPermissions(activity, permissions);
            if (deniedPermissions.size() > 0) {
                requestPermissions(permissions, mRequestCode);
            }
        } else {
            mPermissionCallback.onPermissionGranted(mRequestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private List<String> findDeniedPermissions(Activity activity, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String value : permissions) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(value);
            }
        }
        return deniedPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (mPermissionCallback == null) return;

        if (deniedPermissions.size() > 0) {
            mPermissionCallback.onPermissionDenied(mRequestCode);
        } else {
            mPermissionCallback.onPermissionGranted(mRequestCode);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleJob();
        Api.cancel(TAG);
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

    protected void scrollToTop(View anchor, final View view) {
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop(view);
            }
        });
    }

    protected void startScheduleJobNext(int millisecond) {
        startScheduleJobRightNow(millisecond, millisecond);
    }

    protected void startScheduleJobRightNow(int millisecond) {
        startScheduleJobRightNow(millisecond, 0);
    }

    protected void startScheduleJobRightNow(int millisecond, long delayMillis) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }
        mTimerHandler.sendEmptyMessageDelayed(millisecond, delayMillis);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.resetCount();
        }
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

    protected void addTopPaddingWithStatusBar(View view) {
        int paddingTop = getStatusBarHeight();
        view.setPadding(0, paddingTop, 0, 0);
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    public void addStatusBarHeightPaddingTop(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int extraHeight = getStatusBarHeight();
            if (NotchUtil.hasNotchInScreen(getActivity())) {
                int[] notchSize = NotchUtil.getNotchSize(getActivity());
                int height = notchSize[1];
                if (height > extraHeight) {
                    extraHeight = height;
                }
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + extraHeight,
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
