package com.songbai.futurex.model.local;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 2018/6/5
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class RegisterData implements Parcelable {

    public static final String PLATFORM_ANDROID = "1";

    private String email;
    private String phone;
    private String userPass;
    private String platform;
    private String msgCode;
    private String promoterCode;
    private String telteCode;

    public RegisterData(String platform) {
        this.platform = platform;
    }

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

    public void setPromoterCode(String promoterCode) {
        this.promoterCode = promoterCode;
    }

    public void setTelteCode(String telteCode) {
        this.telteCode = telteCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
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
        dest.writeString(this.platform);
        dest.writeString(this.msgCode);
        dest.writeString(this.promoterCode);
        dest.writeString(this.telteCode);
    }

    protected RegisterData(Parcel in) {
        this.email = in.readString();
        this.phone = in.readString();
        this.userPass = in.readString();
        this.platform = in.readString();
        this.msgCode = in.readString();
        this.promoterCode = in.readString();
        this.telteCode = in.readString();
    }

    public static final Parcelable.Creator<RegisterData> CREATOR = new Parcelable.Creator<RegisterData>() {
        @Override
        public RegisterData createFromParcel(Parcel source) {
            return new RegisterData(source);
        }

        @Override
        public RegisterData[] newArray(int size) {
            return new RegisterData[size];
        }
    };
}
