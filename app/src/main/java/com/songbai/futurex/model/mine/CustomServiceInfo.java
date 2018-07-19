package com.songbai.futurex.model.mine;

/**
 * @author yangguangda
 * @date 2018/7/18
 */
public class CustomServiceInfo {

    /**
     * createDate : 1528190265000
     * description : 真实姓名
     * id : 29
     * name : 客服小鸟
     * status : 1
     * userAccount : kefuxiaoniao
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/image/201806/0517/1528191154725023509.jpeg
     */

    private long createDate;
    private String description;
    private int id;
    private String name;
    private int status;
    private String userAccount;
    private String userPortrait;

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
