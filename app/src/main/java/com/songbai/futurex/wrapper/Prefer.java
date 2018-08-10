package com.songbai.futurex.wrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songbai.futurex.App;
import com.songbai.futurex.BuildConfig;
import com.songbai.wrapres.model.TrainingSubmit;

import java.util.ArrayList;
import java.util.List;


public class Prefer {
    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs_wrap";

    interface Key {
        String FOREGROUND = "foreground";
        String USER_JSON = "userJson";
        String PUSH_CLIENT_ID = "PUSH_CLIENT_ID";
        String SERVER_TIME = "server_time";
        String IS_FIRST_TRAIN = "IS_FIRST_TRAIN";
        String TRAINING_SUBMITS = "training_submits";
        String FIRST_LOGIN = "first_login";
        String NEWS_BIG_IMAGE = "news_big_image";
    }

    private static Prefer sInstance;

    private SharedPreferences mPrefs;

    public static Prefer get() {
        if (sInstance == null) {
            sInstance = new Prefer();
        }
        return sInstance;
    }

    private Prefer() {
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

    public void setForeground(boolean foreground) {
        apply(Key.FOREGROUND, foreground);
    }

    public void setTimestamp(String key, long timestamp) {
        apply(key, timestamp);
    }

    public void setUserJson(String userJson) {
        apply(Key.USER_JSON, userJson);
    }

    public String getUserJson() {
        return mPrefs.getString(Key.USER_JSON, null);
    }

    public String getPushClientId() {
        return mPrefs.getString(Key.PUSH_CLIENT_ID, "");
    }

    public long getTimestamp(String key) {
        return mPrefs.getLong(key, 0);
    }

    public long getServerTime() {
        return mPrefs.getLong(Key.SERVER_TIME, 0);
    }

    public boolean isFirstLogin() {
        return mPrefs.getBoolean(Key.FIRST_LOGIN, true);
    }

    public void setFirstLogin(boolean isFirstLogin) {
        apply(Key.FIRST_LOGIN, isFirstLogin);
    }

    public boolean isFirstTrain(int trainId) {
        return mPrefs.getBoolean(Key.IS_FIRST_TRAIN + trainId, true);
    }

    public void setIsFirstTrainFalse(int trainId, boolean isFirst) {
        apply(Key.IS_FIRST_TRAIN + trainId, isFirst);
    }

    public void setTrainingSubmits(String phone, List<TrainingSubmit> submits) {
        apply(phone + Key.TRAINING_SUBMITS, new Gson().toJson(submits));
    }

    public void setBigImage(String img) {
        String key = Key.NEWS_BIG_IMAGE;
        apply(key, img);
    }

    public String getBigImage() {
        String key = Key.NEWS_BIG_IMAGE;
        return mPrefs.getString(key, null);
    }

    public List<TrainingSubmit> getTrainingSubmits(String phone) {
        String json = mPrefs.getString(phone + Key.TRAINING_SUBMITS, null);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            return getTrainingSubmitsFromJson(json);
        }
    }

    @Nullable
    private List<TrainingSubmit> getTrainingSubmitsFromJson(String json) {
        List<TrainingSubmit> submits = null;
        try {
            submits = new Gson().fromJson(json,
                    new TypeToken<List<TrainingSubmit>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            submits = new ArrayList<>();
        } finally {
            return submits;
        }
    }
}
