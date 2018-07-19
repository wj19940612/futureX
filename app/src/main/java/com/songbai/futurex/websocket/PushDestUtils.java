package com.songbai.futurex.websocket;

import android.text.TextUtils;

import com.songbai.futurex.websocket.im.IMParam;
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

    public static boolean isOtcChat(int id, String dest) {
        return (OtcParam.OTC_CHAT + id).equals(dest);
    }

    public static boolean isCustomerChat(String dest) {
        return IMParam.CHAT.equals(dest);
    }

    public static boolean isServiceOnline(String dest) {
        return IMParam.CUSOONLINE.equals(dest);
    }

    public static boolean isServiceOffline(String dest) {
        return IMParam.CUSOOFFLINE.equals(dest);
    }
}
