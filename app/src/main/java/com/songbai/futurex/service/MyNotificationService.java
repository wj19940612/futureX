package com.songbai.futurex.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.activity.auth.RegisterActivity;
import com.songbai.futurex.fragment.DealDetailFragment;
import com.songbai.futurex.fragment.HomeFragment;
import com.songbai.futurex.fragment.MarketDetailFragment;
import com.songbai.futurex.fragment.legalcurrency.LegalCurrencyOrderDetailFragment;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.LegalCurrencyOrder;
import com.songbai.futurex.model.order.Order;
import com.songbai.futurex.utils.Launcher;
import com.umeng.message.UTrack;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Random;

/**
 * Modified by Zhang on 18/8/6.
 */
public class MyNotificationService extends Service {
    private static final String TAG = MyNotificationService.class.getName();
    public static UMessage oldMessage = null;
    private static Gson sGson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String message = intent.getStringExtra("UmengMsg");
        try {
            UMessage msg = new UMessage(new JSONObject(message));
            if (oldMessage != null) {
                UTrack.getInstance(getApplicationContext()).setClearPrevMessage(true);
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(oldMessage);
            }
            showNotification(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(UMessage msg) {
        int id = new Random(System.nanoTime()).nextInt();
        oldMessage = msg;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle(msg.title)
                .setContentText(msg.text)
                .setTicker(msg.ticker)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        int contentType = Integer.valueOf(msg.extra.get("contentType"));
        if (contentType != 1) {
            return;
        }
        Log.e(TAG, "确实是实名认证的消息");
        Notification notification = mBuilder.getNotification();
        PendingIntent clickPendingIntent = getClickPendingIntent(this, msg);
        PendingIntent dismissPendingIntent = getDismissPendingIntent(this, msg);
        notification.deleteIntent = dismissPendingIntent;
        notification.contentIntent = clickPendingIntent;
        manager.notify(id, notification);
    }

    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
        Intent clickIntent = getIntent(context, msg);
        if (clickIntent == null) {
            return null;
        }

        clickIntent.setClass(context, RegisterActivity.class);


        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return clickPendingIntent;
    }

    public Intent getIntent(Context context, UMessage msg) {
        Intent clickIntent = new Intent();
        String contentType = msg.extra.get("contentType");
        if (TextUtils.isEmpty(contentType)) {
            return null;
        }
        String modelJsonStr = msg.extra.get("value");
        if (contentType.equals("Register")) {
            clickIntent.setClass(context, RegisterActivity.class);
        } else if (contentType.equals("Login")) {
            clickIntent.setClass(context, LoginActivity.class);
        } else if (contentType.equals("Exchange")) {
            if (TextUtils.isEmpty(modelJsonStr)) {
                return null;
            }

            clickIntent.setClass(context, UniqueActivity.class);
            clickIntent.putExtra("frag", MarketDetailFragment.class.getCanonicalName());
            Type type = new TypeToken<CurrencyPair>() {}.getType();
            CurrencyPair currencyPair = sGson.fromJson(modelJsonStr, type);
            if (currencyPair == null) return null;
            clickIntent.putExtra(ExtraKeys.CURRENCY_PAIR, currencyPair);
        } else if (contentType.equals("History") || contentType.equals("Orders")) {
            if (TextUtils.isEmpty(modelJsonStr)) {
                return null;
            }

            clickIntent.setClass(context, UniqueActivity.class);
            clickIntent.putExtra("frag", DealDetailFragment.class.getCanonicalName());
            Type type = new TypeToken<Order>() {}.getType();
            Order order = sGson.fromJson(modelJsonStr, type);
            if (order == null) return null;
            clickIntent.putExtra(ExtraKeys.ORDER, order);
        } else if (contentType.equals("Details")) {
            if (TextUtils.isEmpty(modelJsonStr)) {
                return null;
            }

            clickIntent.setClass(context, UniqueActivity.class);
            clickIntent.putExtra("frag", LegalCurrencyOrderDetailFragment.class.getCanonicalName());
            Type type = new TypeToken<LegalCurrencyOrder>() {}.getType();
            LegalCurrencyOrder legalCurrencyOrder = sGson.fromJson(modelJsonStr, type);
            if (legalCurrencyOrder == null) return null;
            clickIntent.putExtra(ExtraKeys.ORDER_ID, legalCurrencyOrder.getId());
            clickIntent.putExtra(ExtraKeys.TRADE_DIRECTION, legalCurrencyOrder.getDirect());
        }else if(contentType.equals("Off-shelves")){

        }

        if (clickIntent != null) {
            clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return clickIntent;
    }

//    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
//        Intent clickIntent = new Intent();
//        clickIntent.setClass(context, NotificationBroadcast.class);
//        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
//                msg.getRaw().toString());
//        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_ACTION,
//                NotificationBroadcast.ACTION_CLICK);
//        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
//                (int) (System.currentTimeMillis()),
//                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        return clickPendingIntent;
//    }

    public PendingIntent getDismissPendingIntent(Context context, UMessage msg) {
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, NotificationBroadcast.class);
        deleteIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
                msg.getRaw().toString());
        deleteIntent.putExtra(
                NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_DISMISS);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis() + 1),
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return deletePendingIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
