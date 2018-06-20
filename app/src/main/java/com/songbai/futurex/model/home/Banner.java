package com.songbai.futurex.model.home;

import java.util.List;

/**
 * Modified by john on 09/03/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Banner {

    /**
     * content : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/image/201805/2115/1526889352238036587.png
     * createTime : 1526889375402
     * id : 5b027b9fa499a80698312893
     * includeVersion :
     * index : 1111
     * jumpContent : <p>321321321321321</p>
     * jumpType : 2
     * lang : zh_CN
     * operator : admin
     * scopes : [3,4]
     * showEndTime : 4670409600000
     * showStartTime : 1514736000000
     * status : 1
     * subTitle : 11
     * title : 11
     * updateTime : 1527579920096
     */

    private String content;
    private long createTime;
    private String id;
    private String includeVersion;
    private int index;
    private String jumpContent;
    private int jumpType;
    private String lang;
    private String operator;
    private long showEndTime;
    private long showStartTime;
    private int status;
    private String subTitle;
    private String title;
    private long updateTime;
    private List<Integer> scopes;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncludeVersion() {
        return includeVersion;
    }

    public void setIncludeVersion(String includeVersion) {
        this.includeVersion = includeVersion;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getJumpContent() {
        return jumpContent;
    }

    public void setJumpContent(String jumpContent) {
        this.jumpContent = jumpContent;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getShowEndTime() {
        return showEndTime;
    }

    public void setShowEndTime(long showEndTime) {
        this.showEndTime = showEndTime;
    }

    public long getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(long showStartTime) {
        this.showStartTime = showStartTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<Integer> getScopes() {
        return scopes;
    }

    public void setScopes(List<Integer> scopes) {
        this.scopes = scopes;
    }
}
