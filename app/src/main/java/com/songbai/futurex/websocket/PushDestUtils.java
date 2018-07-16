package com.songbai.futurex.websocket;

import android.text.TextUtils;

import com.songbai.futurex.websocket.otc.OtcParam;

/**
 * Modified by john on 2018/7/16
 * <p>
 * Description: 区分 socket push 类型工具类
 * <p>
 * APIs:
 */
public class PushDestUtils {

    public static boolean isAllMarket(String dest) {
        return TopicParam.ALL.equals(dest);
    }

    public static boolean isSoloMarket(String dest) {
        return !TextUtils.isEmpty(dest) ? dest.contains(TopicParam.SINGLE) : false;
    }

    public static boolean isEntrustOrder(String dest) {
        return "queue:entrust".equals(dest);
    }

    public static boolean isOtcChat(String dest) {
        return OtcParam.OTC_CHAT.equals(dest);
    }
}
