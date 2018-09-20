package com.songbai.futurex;

import android.content.Context;
import android.content.SharedPreferences;


public class Preference {
    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";

    interface Key {
        String USER_JSON = "userJson";
        String LOCALE_JSON = "locale_json";
        String SERVER_TIME = "server_time";
        String SEARCH_RECORDS = "search_record";
        String DEFAULT_TRADE_PAIR = "default_trade_pair";
        String REFRESH_OPTIONAL = "refresh_optional";
        String REFRESH_POSTER = "refresh_poster";
        String REFRESH_LANGUAGE = "refresh_language";
        String LANGUAGE_STR = "language_str";
        String SYS_MODEL = "sys_model";
        String PRICING_METHOD = "pricing_method";
        String FAST_TRADE = "fast_trade";
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

    public void setSearchRecordsForUserOrDeviceId(String userOrDeviceId, String json) {
        apply(Key.SEARCH_RECORDS + userOrDeviceId, json);
    }

    public String getSearchRecordsByUserOrDeviceId(String userOrDeviceId) {
        return mPrefs.getString(Key.SEARCH_RECORDS + userOrDeviceId, null);
    }

    public String getDefaultTradePair() {
        return mPrefs.getString(Key.DEFAULT_TRADE_PAIR, null);
    }

    public void setDefaultTradePair(String tradePair) {
        apply(Key.DEFAULT_TRADE_PAIR, tradePair);
    }

    public void setOptionalListRefresh(boolean refresh) {
        apply(Key.REFRESH_OPTIONAL, refresh);
    }

    public boolean getOptionalListRefresh() {
        return mPrefs.getBoolean(Key.REFRESH_OPTIONAL, false);
    }

    public void setPosterListRefresh(boolean refresh) {
        apply(Key.REFRESH_POSTER, refresh);
    }

    public boolean getPosterListRefresh() {
        return mPrefs.getBoolean(Key.REFRESH_POSTER, false);
    }

    public void setCurrentLangageStr(String languageStr) {
        apply(Key.LANGUAGE_STR, languageStr);
    }

    public String getCurrentLangageStr() {
        return mPrefs.getString(Key.LANGUAGE_STR, "");
    }

    public void setRefreshLanguage(boolean refresh) {
        apply(Key.REFRESH_LANGUAGE, refresh);
    }

    public boolean getRefreshLanguage() {
        return mPrefs.getBoolean(Key.REFRESH_LANGUAGE, false);
    }

    public void setSysModel(String sysModel) {
        apply(Key.SYS_MODEL, sysModel);
    }

    public String getSysModel() {
        return mPrefs.getString(Key.SYS_MODEL, "");
    }

    public void setPricingMethod(String pricingMethod) {
        apply(Key.PRICING_METHOD, pricingMethod);
    }

    public String getPricingMethod() {
        return mPrefs.getString(Key.PRICING_METHOD, "cny");
    }

    public boolean isQuickExchange() {
        return mPrefs.getBoolean(Key.FAST_TRADE, false);
    }

    public void setQuickExchange(boolean fastTrade) {
        apply(Key.FAST_TRADE, fastTrade);
    }
}
