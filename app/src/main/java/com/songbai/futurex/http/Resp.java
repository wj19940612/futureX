package com.songbai.futurex.http;


public class Resp<T> {
    // 验证码请求过多 需要图片验证码
    public static final int CODE_IMAGE_AUTH_CODE_REQUIRED = 1123;
    //未绑定微信
    public static final int CODE_NO_BIND_WE_CHAT = 1115;
    //账号异常
    public static final int CODE_ACCOUNT_EXCEPTION = 1114;

    //收藏文章已经被下架
    public static final int CODE_ARTICLE_ALREADY_SOLD_OUT = 1126;
    // 咨询不存在
    public static final int CODE_MSG_NOT_FIND = 1302;


    private int code;
    private String msg;
    private int page;
    private int pageSize;
    private int resultCount;
    private int total;

    private T data;


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getResultCount() {
        return resultCount;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isTokenExpired() {
        return code == 503;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
