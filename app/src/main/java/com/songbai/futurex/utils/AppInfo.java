package com.songbai.futurex.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class AppInfo {
    /**
     * 获取版本名，例如 1.0.1
     *
     * @return version name
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取 manifest 里面的 meta-data
     *
     * @param context
     * @param name
     * @return
     */
    public static String getMetaData(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        String result = "";
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            result = info.metaData.get(name).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * 获取设备唯一标识符
     *
     * @param context
     * @return
     */
    public static String getDeviceHardwareId(Context context) {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String typePrefix = "DEVICE_ID_";
            String deviceId = tm.getDeviceId();

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Build.SERIAL;
                typePrefix = "SERIAL_";
            }

            if (TextUtils.isEmpty(deviceId)) {
                android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                String mac = wifi.getConnectionInfo().getMacAddress();
                deviceId = mac;
                typePrefix = "MAC_";
            }

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                typePrefix = "ANDROID_ID_";
            }

            Log.d("AppInfo", "getDeviceHardwareId: " + (typePrefix + deviceId));
            return SecurityUtil.md5Encrypt(typePrefix + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
