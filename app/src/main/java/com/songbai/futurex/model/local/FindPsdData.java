package com.songbai.futurex.model.local;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Modified by john on 2018/6/6
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class FindPsdData implements Parcelable {
    private String email;
    private String phone;
    private String userPass;
    private String msgCode;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public boolean isFindPhonePassword() {
        return !TextUtils.isEmpty(phone);
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.userPass);
        dest.writeString(this.msgCode);
    }

    public FindPsdData() {
    }

    protected FindPsdData(Parcel in) {
        this.email = in.readString();
        this.phone = in.readString();
        this.userPass = in.readString();
        this.msgCode = in.readString();
    }

    public static final Parcelable.Creator<FindPsdData> CREATOR = new Parcelable.Creator<FindPsdData>() {
        @Override
        public FindPsdData createFromParcel(Parcel source) {
            return new FindPsdData(source);
        }

        @Override
        public FindPsdData[] newArray(int size) {
            return new FindPsdData[size];
        }
    };
}
