package com.songbai.futurex.model.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sbai.httplib.CookieManger;
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
    private String mEmail;
    private String mLastAct;
    private String mToken;

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

    private void saveToPreferenceSYNC() {
        String userJson = new Gson().toJson(this);
        Preference.get().setUserJsonSYNC(userJson);
    }

    public void login() {
        mToken = CookieManger.getInstance().getNameValuePair("token");
    }

    public void setUserInfo(UserInfo userInfo, String phone, String email) {
        mUserInfo = userInfo;
        mPhone = phone;
        mEmail = email;
        mLastAct = chooseLastAccount(mPhone, mEmail);

        saveToPreference();
    }

    private String chooseLastAccount(String phone, String email) {
        return TextUtils.isEmpty(phone) ? email : phone;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;

        saveToPreference();
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public String getLastAct() {
        return mLastAct;
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

    public void logoutSYNC() {
        mUserInfo = null;
        mToken = null;
        saveToPreferenceSYNC();
    }

    @Override
    public String toString() {
        return "LocalUser{" +
                "mUserInfo=" + mUserInfo +
                ", mPhone='" + mPhone + '\'' +
                '}';
    }
}
