package com.songbai.futurex.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified by john on 2018/8/14
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class PermissionFragment extends DialogFragment {

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
                activity.requestPermissions(permissions, mRequestCode);
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
}
