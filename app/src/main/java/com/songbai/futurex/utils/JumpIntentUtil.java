package com.songbai.futurex.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.activity.CustomServiceActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.OtcOrderCompletedActivity;
import com.songbai.futurex.activity.OtcTradeChatActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.activity.auth.RegisterActivity;
import com.songbai.futurex.activity.mine.InviteActivity;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.activity.mine.PersonalDataActivity;
import com.songbai.futurex.activity.mine.PropertyFlowActivity;
import com.songbai.futurex.activity.mine.TradeOrdersActivity;
import com.songbai.futurex.fragment.DealDetailFragment;
import com.songbai.futurex.fragment.MarketDetailFragment;
import com.songbai.futurex.fragment.legalcurrency.LegalCurrencyOrderDetailFragment;
import com.songbai.futurex.fragment.mine.MessageCenterActivity;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.JumpContent;
import com.songbai.futurex.model.JumpWeb;
import com.songbai.futurex.model.LegalCurrencyOrder;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.home.Banner;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.umeng.message.entity.UMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhang
 * 处理跳转协议的逻辑
 */
public class JumpIntentUtil {

    public static final String SEND_ACTION = "sendAction";
    public static final String SEND_VALUE = "sendValue";
    public static final String UUID = "uuid";

    private static Gson sGson = new Gson();
    private static List<String> sUUidCache = new ArrayList<>();

    public static Intent getJumpIntent(Context context, Banner banner) {
        String jumpContentStr = banner.getJumpContent();
        if (TextUtils.isEmpty(jumpContentStr)) {
            return null;
        }

        Type type = new TypeToken<JumpContent>() {
        }.getType();

        JumpContent jumpContent = sGson.fromJson(jumpContentStr, type);
        if (jumpContent == null || TextUtils.isEmpty(jumpContent.getSendAction())) {
            return null;
        }

        return getJumpIntent(context, jumpContent.getSendAction(), sGson.toJson(jumpContent.getSendValue()));
    }


    public static Intent getJumpIntent(Context context, JumpContent jumpContent) {
        if (jumpContent == null || TextUtils.isEmpty(jumpContent.getUuid()) || sUUidCache.contains(jumpContent.getUuid())) {
            return null;
        }

        sUUidCache.add(jumpContent.getUuid());
        return getJumpIntent(context, jumpContent.getSendAction(), sGson.toJson(jumpContent.getSendValue()));
    }

    public static Intent getJumpIntent(Context context, UMessage msg) {
        if (msg.extra == null || msg.extra.isEmpty()) return null;

        String uuid = msg.extra.get(UUID);
//        Log.e("zzz", "extra:" + msg.extra.get(SEND_VALUE));
        if (TextUtils.isEmpty(uuid) || sUUidCache.contains(uuid)) {
            return null;
        }

        sUUidCache.add(uuid);

        String contentType = msg.extra.get(SEND_ACTION);
        if (TextUtils.isEmpty(contentType)) {
            return null;
        }
        String value = msg.extra.get(SEND_VALUE);

        return getJumpIntent(context, contentType, value);
    }


    public static Intent getJumpIntent(Context context, String action, String value) {
        Intent clickIntent = new Intent();
        String contentType = action;
        if (TextUtils.isEmpty(contentType)) {
            return null;
        }

        String modelJsonStr = value;

        if (contentType.equals("Register")) {
            clickIntent.setClass(context, RegisterActivity.class);
        } else if (contentType.equals("Login")) {
            clickIntent.setClass(context, LoginActivity.class);
        } else if (contentType.equals("Exchange")) {
            clickIntent = putExtraExchange(context, clickIntent, modelJsonStr);
        } else if (contentType.equals("History")) {
            clickIntent = putExtraHistory(context, clickIntent, modelJsonStr);
        }else if(contentType.equals("Orders")){
            clickIntent = putExtraOrder(context, clickIntent, modelJsonStr);
        }
        else if (contentType.equals("Details")) {
            clickIntent = putExtraDetail(context, clickIntent, modelJsonStr);
        } else if (contentType.equals("Off-shelves")) {
            clickIntent.setClass(context, MainActivity.class);
            clickIntent.putExtra(ExtraKeys.PAGE_INDEX, MainActivity.PAGE_LEGAL_CURRENCY);
            clickIntent.putExtra(ExtraKeys.LEGAL_CURRENCY_PAGE_INDEX, 2);
        } else if (contentType.equals("Chat")) {
            clickIntent = putExtraChat(context, clickIntent, modelJsonStr);
        } else if (contentType.equals("Assets")) {
            clickIntent.setClass(context, MyPropertyActivity.class);
        } else if (contentType.equals("Messages")) {
            clickIntent.setClass(context, MessageCenterActivity.class);
        } else if (contentType.equals("Customer service")) {
            clickIntent.setClass(context, CustomServiceActivity.class);
        } else if (contentType.equals("webview")) {
            putExtraH5(context, clickIntent, modelJsonStr);
        } else if (contentType.equals("Promote")) {
            clickIntent.setClass(context, InviteActivity.class);
        } else if (contentType.equals("UserInfo")) {
            clickIntent.setClass(context, PersonalDataActivity.class);
        } else if (contentType.equals("AssetsDetails")) {
            clickIntent.setClass(context, PropertyFlowActivity.class);
            clickIntent.putExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, true);
            clickIntent.putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, 0);
        } else if (contentType.equals("Bills")) {
            clickIntent.setClass(context, PropertyFlowActivity.class);
            clickIntent.putExtra(ExtraKeys.PROPERTY_FLOW_FILTER_TYPE_ALL, true);
            clickIntent.putExtra(ExtraKeys.PROPERTY_FLOW_ACCOUNT_TYPE, 0);
        }

        if (clickIntent != null) {
            clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return clickIntent;
    }

    /*
    新的交易对详情
     */
    private static Intent putExtraExchange(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }

        clickIntent.setClass(context, UniqueActivity.class);
        clickIntent.putExtra("frag", MarketDetailFragment.class.getCanonicalName());
        Type type = new TypeToken<CurrencyPair>() {
        }.getType();
        CurrencyPair currencyPair = sGson.fromJson(modelJsonStr, type);
        if (currencyPair == null) return null;

        clickIntent.putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair);
        return clickIntent;
    }

    /*
    Orders 币币委托历史
     */
    private static Intent putExtraHistory(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }

        clickIntent.setClass(context, UniqueActivity.class);
        clickIntent.putExtra("frag", DealDetailFragment.class.getCanonicalName());
        Type type = new TypeToken<Order>() {
        }.getType();
        Order order = sGson.fromJson(modelJsonStr, type);
        if (order == null) return null;
        clickIntent.putExtra(ExtraKeys.ORDER, order);
        return clickIntent;
    }

    /*
    Orders 币币委托
     */
    private static Intent putExtraOrder(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }

        clickIntent.setClass(context, TradeOrdersActivity.class);
        clickIntent.putExtra(ExtraKeys.ENTER_TAB,TradeOrdersActivity.TAB_HISTORY);
        Type type = new TypeToken<Order>() {
        }.getType();
        Order order = sGson.fromJson(modelJsonStr, type);
        if (order == null) return null;
        clickIntent.putExtra(ExtraKeys.ORDER, order);
        return clickIntent;
    }

    /*
   Details 法币交易详情 下单-待付款-成交-申诉
    */
    private static Intent putExtraDetail(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }
        Type type = new TypeToken<LegalCurrencyOrder>() {
        }.getType();
        LegalCurrencyOrder legalCurrencyOrder = sGson.fromJson(modelJsonStr, type);
        if (legalCurrencyOrder == null) return null;

        if (legalCurrencyOrder.getStatus() == OTCOrderStatus.ORDER_COMPLATED || legalCurrencyOrder.getStatus() == OTCOrderStatus.ORDER_CANCLED) {
            clickIntent.setClass(context, OtcOrderCompletedActivity.class);
        } else if (legalCurrencyOrder.getStatus() == OTCOrderStatus.ORDER_PAIED || legalCurrencyOrder.getStatus() == OTCOrderStatus.ORDER_UNPAIED) {
            clickIntent.setClass(context, UniqueActivity.class);
            clickIntent.putExtra("frag", LegalCurrencyOrderDetailFragment.class.getCanonicalName());
        }

        clickIntent.putExtra(ExtraKeys.ORDER_ID, legalCurrencyOrder.getId());
        clickIntent.putExtra(ExtraKeys.TRADE_DIRECTION, legalCurrencyOrder.getDirect());
        return clickIntent;
    }

    /*
    法币聊天界面
     */
    private static Intent putExtraChat(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }
        Type type = new TypeToken<LegalCurrencyOrder>() {
        }.getType();
        LegalCurrencyOrder legalCurrencyOrder = sGson.fromJson(modelJsonStr, type);
        if (legalCurrencyOrder == null) return null;

        clickIntent.setClass(context, OtcTradeChatActivity.class);

        clickIntent.putExtra(ExtraKeys.ORDER_ID, legalCurrencyOrder.getId());
        clickIntent.putExtra(ExtraKeys.TRADE_DIRECTION, legalCurrencyOrder.getDirect());
        return clickIntent;
    }

    public static Intent putExtraH5(Context context, Intent clickIntent, String modelJsonStr) {
        if (TextUtils.isEmpty(modelJsonStr)) {
            return null;
        }
        Type type = new TypeToken<JumpWeb>() {
        }.getType();
        JumpWeb jumpWeb = sGson.fromJson(modelJsonStr, type);
        if (jumpWeb == null) return null;

        clickIntent.setClass(context, WebActivity.class);
        clickIntent.putExtra(WebActivity.EX_URL, jumpWeb.getUrl());
        clickIntent.putExtra(WebActivity.EX_TITLE, jumpWeb.getTitle());
        return clickIntent;
    }
}
