package com.songbai.futurex.model.mine;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public class SysMessage {
    public static final int READ = 1;
    public static final int UNREAD = 0;

    /**
     * agencyName : 平台
     * agencyRelationCode : 0#
     * content : <p><span style="font-size: 16px; line-height: 150%; color: rgb(89, 89, 89);">&nbsp;</span></p><p style="text-align: justify;"><span style="color: rgb(89, 89, 89);"><span style="color: rgb(89, 89, 89); font-size: 16px; line-height: 150%;">&nbsp;</span><span style="color: rgb(89, 89, 89); font-size: 16px; line-height: 150%; font-family: Calibri;"> A new round of scientific and technological revolution, together with industrial transformation, is now sweeping the world. New techs such as big data, cloud computing, IOT, AI, and blockchain are continuously emerging. As a new engine of economic growth, digital economy is profoundly changing the way people produce and live. Consequently, cryptocurrency trading has become very hot. As a global leading cryptocurrency trading platform, Bitfuture reminds all the users that:</span></span></p><p style="text-align: justify;"><span style="font-size: 16px; line-height: 150%; font-family: Calibri; color: rgb(89, 89, 89);">&nbsp; Cryptocurrency is not issued by monetary authorities, and has no legal compensation or coerciveness. Since the price of cryptocurrency fluctuates sharply, you are strongly advised to invest carefully at your own risk.</span></p><p style="text-align: justify;"><span style="font-size: 16px; line-height: 150%; font-family: Calibri; color: rgb(89, 89, 89);">&nbsp; Statement by any user on the internet (including QQ group, WeChat group, Weibo, we-media, and so on) stands on their own position, and should not constitute an investment advice to you. All your investment decisions should be based on your own thought.</span></p><p style="text-align: justify;"><span style="font-size: 16px; line-height: 150%; font-family: Calibri; color: rgb(89, 89, 89);">&nbsp; As a neutral provider of cryptocurrency trading platform, Bitfuture does not participate in cryptocurrency trading, nor does it bear the losses of users caused by their trades.</span></p><p style="text-align:right"><span style="font-size: 16px; line-height: 150%; font-family: Calibri; color: rgb(89, 89, 89);">Bitfuture</span></p><p style="text-align:right"><span style="font-size: 16px; line-height: 150%; font-family: Calibri; color: rgb(89, 89, 89);">April 7, 2018</span></p><p><br/></p>
     * createTime : 1527661593452
     * format : 1
     * id : 5b0e441919719b0b855ea811
     * includeAgency : [0,1]
     * index : 1
     * lang : en
     * operator : admin
     * showEndTime : 4670409600000
     * showStartTime : 1523094262000
     * status : 1
     * style :
     * title : Risk Warning of Cryptocurrency
     * type : 1
     * updateTime : 1527759869480
     * uuid : 97ab8679709247079728ca616745447a
     */

    private String classify;
    private int dataId;
    private String msg;

    private String agencyName;
    private String agencyRelationCode;
    private String content;
    private long createTime;
    private int format;
    private String id;
    private int index;
    private String lang;
    private String operator;
    private long showEndTime;
    private long showStartTime;
    private int status;
    private String style;
    private String title;
    private int type;
    private long updateTime;
    private String uuid;
    private List<Integer> includeAgency;

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyRelationCode() {
        return agencyRelationCode;
    }

    public void setAgencyRelationCode(String agencyRelationCode) {
        this.agencyRelationCode = agencyRelationCode;
    }

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

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Integer> getIncludeAgency() {
        return includeAgency;
    }

    public void setIncludeAgency(List<Integer> includeAgency) {
        this.includeAgency = includeAgency;
    }
}
