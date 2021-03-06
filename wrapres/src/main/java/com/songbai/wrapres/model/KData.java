package com.songbai.wrapres.model;

import android.os.Parcel;
import android.os.Parcelable;


public class KData implements Parcelable {

    public static final int TYPE_LONG = 1;
    public static final int TYPE_SHORT = 0;

    /**
     * id : 893313204497801217
     * isOption : true
     * k : {"closePrice":3250.42,"day":"2017-08-03","instrumentID":"1A0001","maxPrice":0,"minPrice":0,"openPrice":0,"time":1501743599950,"timeStamp":1501743599950}
     * remark : dasdsad
     * type : 0
     */

    private String id;
    private boolean isOption; //是否当天买卖
    private KlineViewData k;
    private String remark;
    private int type; // 1 看涨 0看跌

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOption() {
        return isOption;
    }

    public void setOption(boolean option) {
        isOption = option;
    }

    public KlineViewData getK() {
        return k;
    }

    public void setK(KlineViewData k) {
        this.k = k;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByte(this.isOption ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.k, flags);
        dest.writeString(this.remark);
        dest.writeInt(this.type);
    }

    public KData() {
    }

    protected KData(Parcel in) {
        this.id = in.readString();
        this.isOption = in.readByte() != 0;
        this.k = in.readParcelable(KlineViewData.class.getClassLoader());
        this.remark = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<KData> CREATOR = new Creator<KData>() {
        @Override
        public KData createFromParcel(Parcel source) {
            return new KData(source);
        }

        @Override
        public KData[] newArray(int size) {
            return new KData[size];
        }
    };
}
