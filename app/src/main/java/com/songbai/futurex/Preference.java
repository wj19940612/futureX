package com.songbai.futurex;

import android.content.Context;
import android.content.SharedPreferences;


public class Preference {
    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";


    interface Key {
        String USER_JSON = "userJson";
        String ACCOUNT_TYPE = "account_type";
        String LOCALE_JSON = "locale_json";
        String SERVER_TIME = "server_time";
    }

    private static Preference sInstance;

    private SharedPreferences mPrefs;

    public static Preference get() {
        if (sInstance == null) {
            sInstance = new Preference();
        }
        return sInstance;
    }

    private Preference() {
        mPrefs = App.getAppContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }

    private void apply(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    private void apply(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    private void apply(String key, long value) {
        getEditor().putLong(key, value).apply();
    }

    private void apply(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    private void apply(String key, float value) {
        getEditor().putFloat(key, value).apply();
    }

    public void setUserJson(String userJson) {
        apply(Key.USER_JSON, userJson);
    }

    public String getUserJson() {
        return mPrefs.getString(Key.USER_JSON, null);
    }

    public void setLocalJson(String localJson) {
        apply(Key.LOCALE_JSON, localJson);
    }

    public String getLocalJson() {
        return mPrefs.getString(Key.LOCALE_JSON, null);
    }

    public int getUserAccountType(String phone) {
        return mPrefs.getInt(phone + Key.ACCOUNT_TYPE, 0);
    }

    public void setUserAccountType(String phone, int accountType) {
        getEditor().putInt(phone + Key.ACCOUNT_TYPE, accountType);
    }

    public void setTimestamp(String key, long timestamp) {
        apply(key, timestamp);
    }

    public long getTimestamp(String key) {
        return mPrefs.getLong(key, 0);
    }


    public void setServerTime(long serverTime) {
        apply(Key.SERVER_TIME, serverTime);
    }

    public long getServerTime() {
        return mPrefs.getLong(Key.SERVER_TIME, 0);
    }
}
