package com.songbai.wrapres.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 单个训练的完整数据结构
 */
public class Training implements Parcelable {

    public static final int PLAY_TYPE_REMOVE = 1;
    public static final int PLAY_TYPE_MATCH_STAR = 2;
    public static final int PLAY_TYPE_JUDGEMENT = 3;
    public static final int PLAY_TYPE_SORT = 4;

    public static final int TYPE_THEORY = 1;
    public static final int TYPE_TECHNOLOGY = 2;
    public static final int TYPE_FUNDAMENTAL = 3;
    public static final int TYPE_COMPREHENSIVE = 4;

    /**
     * createTime : 2017-08-02 13:58:03
     * finishCount : 0
     * id : 5
     * imageUrl : http://baidu.com
     * image2Url:
     * knowledgeUrl : http://baidu.com
     * level : 1
     * modifyTime : 2017-08-08 17:25:08
     * recommend : 1
     * remark : test remark
     * status : 1
     * time : 1000
     * title : test
     * type : 2
     * playType :
     */

    private String createTime;
    private int finishCount; // 所有用户总完成次数
    private int id;
    private String imageUrl;
    private String image2Url; // 发现用的图片地址
    private String knowledgeUrl;
    private int level;
    private String modifyTime;
    private int recommend;
    private String remark;
    private int status;
    private int time;
    private String title;
    private int type;
    private int playType; // 1.消消乐 2.星星匹配 3.涨跌判断 4.排序

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKnowledgeUrl() {
        return knowledgeUrl;
    }

    public void setKnowledgeUrl(String knowledgeUrl) {
        this.knowledgeUrl = knowledgeUrl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage2Url() {
        return image2Url;
    }

    public void setImage2Url(String image2Url) {
        this.image2Url = image2Url;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    @Override
    public String toString() {
        return "Training{" +
                "createTime='" + createTime + '\'' +
                ", finishCount=" + finishCount +
                ", id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", image2Url='" + image2Url + '\'' +
                ", knowledgeUrl='" + knowledgeUrl + '\'' +
                ", level=" + level +
                ", modifyTime='" + modifyTime + '\'' +
                ", recommend=" + recommend +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", playType=" + playType +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createTime);
        dest.writeInt(this.finishCount);
        dest.writeInt(this.id);
        dest.writeString(this.imageUrl);
        dest.writeString(this.image2Url);
        dest.writeString(this.knowledgeUrl);
        dest.writeInt(this.level);
        dest.writeString(this.modifyTime);
        dest.writeInt(this.recommend);
        dest.writeString(this.remark);
        dest.writeInt(this.status);
        dest.writeInt(this.time);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.playType);
    }

    public Training() {
    }

    protected Training(Parcel in) {
        this.createTime = in.readString();
        this.finishCount = in.readInt();
        this.id = in.readInt();
        this.imageUrl = in.readString();
        this.image2Url = in.readString();
        this.knowledgeUrl = in.readString();
        this.level = in.readInt();
        this.modifyTime = in.readString();
        this.recommend = in.readInt();
        this.remark = in.readString();
        this.status = in.readInt();
        this.time = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.playType = in.readInt();
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel source) {
            return new Training(source);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
}
