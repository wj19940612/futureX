package com.songbai.futurex.wrapper;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sbai.httplib.CookieManger;

public class LocalWrapUser {
    private static LocalWrapUser sLocalUser;

    private WrapUserInfo mUserInfo;
    private String mPhone;
    private String mToken;

    public static LocalWrapUser getUser() {
        if (sLocalUser == null) {
            sLocalUser = loadFromPreference();
        }
        return sLocalUser;
    }

    private static LocalWrapUser loadFromPreference() {
        String userJson = Prefer.get().getUserJson();
        if (!TextUtils.isEmpty(userJson)) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, LocalWrapUser.class);
        }

        return new LocalWrapUser();
    }

    private void saveToPreference() {
        String userJson = new Gson().toJson(this);
        Prefer.get().setUserJson(userJson);
    }

    public void setUserInfo(WrapUserInfo userInfo, String phone) {
        mUserInfo = userInfo;
        mPhone = phone;
        saveToPreference();
    }

    public void login() {
        mToken = CookieManger.getInstance().getNameValuePair("token");
    }

    public void setUserInfo(WrapUserInfo userInfo) {
        mUserInfo = userInfo;
        saveToPreference();
    }

    public WrapUserInfo getUserInfo() {
        return mUserInfo;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isLogin() {
        return mUserInfo != null && !TextUtils.isEmpty(mToken);
    }

    public void logout() {
        mUserInfo = null;
        mToken = null;
        saveToPreference();
    }

    @Override
    public String toString() {
        return "LocalUser{" +
                "mUserInfo=" + mUserInfo +
                ", mPhone='" + mPhone + '\'' +
                '}';
    }
}
