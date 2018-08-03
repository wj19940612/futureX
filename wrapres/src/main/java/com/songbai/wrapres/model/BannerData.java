package com.songbai.wrapres.model;

/**
 * Modified by john on 09/03/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BannerData {

    private String summary;
    private String content;
    private String cover;   //封面
    private long createTime;
    private String id;
    private int index;
    private String operator;
    private int status;
    private String style;//html,h5, functionModule功能模块,originalPage原生页面
    private String title;
    private int type;
    private long updateTime;
    private int recommend;//是否前台推荐
    private int scope;//适用范围
    private int showType;//banner展示类型
    private int showcase;//banner展示位
    private String subTitle;//副标题
    private int userCount;//参与人数
    private String montageData;//活动关键字
    private int jumpType;//原生页面要跳到哪个原生页面
    private String jumpContent;
    private String jumpId;//原生页面跳过去带Id传过去

    public String getCover() {
        return cover;
    }
}
