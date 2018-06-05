package com.songbai.futurex.model.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.songbai.futurex.Preference;
import com.songbai.futurex.model.UserInfo;

/**
 * Modified by john on 2018/6/5
 * <p>
 * Description: 用户包装层
 */
public class LocalUser {

    private static LocalUser sLocalUser;

    private UserInfo mUserInfo;
    private String mPhone;

    public static LocalUser getUser() {
        if (sLocalUser == null) {
            sLocalUser = loadFromPreference();
        }
        return sLocalUser;
    }

    private static LocalUser loadFromPreference() {
        String userJson = Preference.get().getUserJson();
        if (!TextUtils.isEmpty(userJson)) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, LocalUser.class);
        }

        return new LocalUser();
    }

    private void saveToPreference() {
        String userJson = new Gson().toJson(this);
        Preference.get().setUserJson(userJson);
    }

    public void setUserInfo(UserInfo userInfo, String phone) {
        mUserInfo = userInfo;
        mPhone = phone;
        saveToPreference();
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        saveToPreference();
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public String getPhone() {
        return mPhone;
    }

    public boolean isLogin() {
        return mUserInfo != null;
    }

    public void logout() {
        mUserInfo = null;
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
