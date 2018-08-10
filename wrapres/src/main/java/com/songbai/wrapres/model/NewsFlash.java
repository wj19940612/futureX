package com.songbai.wrapres.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 快讯
 */

public class NewsFlash implements Parcelable {

    /**
     * content : 韩国经济副总理兼企划财政部长官金东渊（김동연）表示将于本月31日发表政府关于虚拟货币征税和房地产持有税的立场。
     * id : 957859350960107500
     * rank : 0
     * releaseTime : 1517206449000
     * shareCount : 0
     * status : 1
     * title : 【韩国政府将于本月31日发布政府关于“虚拟货币征税和房地产持有税”的立场】
     */

    private String content;
    private long id;
    private int rank;
    private long releaseTime;
    private int shareCount;
    private int status;
    private String title;

    public boolean isImportant() {
        return rank == 1;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeLong(this.id);
        dest.writeInt(this.rank);
        dest.writeLong(this.releaseTime);
        dest.writeInt(this.shareCount);
        dest.writeInt(this.status);
        dest.writeString(this.title);
    }

    public NewsFlash() {
    }

    protected NewsFlash(Parcel in) {
        this.content = in.readString();
        this.id = in.readLong();
        this.rank = in.readInt();
        this.releaseTime = in.readLong();
        this.shareCount = in.readInt();
        this.status = in.readInt();
        this.title = in.readString();
    }

    public static final Creator<NewsFlash> CREATOR = new Creator<NewsFlash>() {
        @Override
        public NewsFlash createFromParcel(Parcel source) {
            return new NewsFlash(source);
        }

        @Override
        public NewsFlash[] newArray(int size) {
            return new NewsFlash[size];
        }
    };
}
