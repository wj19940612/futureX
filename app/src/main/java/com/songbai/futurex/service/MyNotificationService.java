package com.songbai.futurex.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.OtcOrderCompletedActivity;
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
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.JumpIntentUtil;
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
    private static final String PHONE_BRAND_SAMSUNG = "samsung";

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
            showNotification(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(UMessage msg) {
        PendingIntent intent = getClickPendingIntent(this, msg);
        if (intent == null) {
            return;
        }
        String channelId = getString(R.string.app_name);
        boolean b = !TextUtils.isEmpty(msg.title);
        String notificationTitle;
        if (b) {
            notificationTitle = msg.title;
        } else {
            notificationTitle = channelId;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(msg.text);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        String brand = Build.BRAND;

        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (!TextUtils.isEmpty(brand) && brand.equalsIgnoreCase(PHONE_BRAND_SAMSUNG)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(bitmap);
        }
        builder.setContentIntent(intent);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(R.string.app_name, builder.build());
        int notificationId = (int) System.currentTimeMillis();
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = createNotificationChannel(channelId, notificationManager);
            }
            notificationManager.notify(notificationId, builder.build());
        }
    }

    @SuppressLint("InlinedApi")
    private NotificationChannel createNotificationChannel(String channelId, NotificationManager notificationManager) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);   //开启指示灯，如果设备有的话。
        notificationChannel.enableVibration(true); //开启震动
        notificationChannel.setLightColor(Color.RED); // 设置指示灯颜色
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知
        notificationChannel.setShowBadge(true);  //设置是否显示角标
        notificationChannel.setBypassDnd(true);  // 设置绕过免打扰模式
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400}); //设置震动频率
        notificationChannel.setDescription(channelId);
        notificationManager.createNotificationChannel(notificationChannel);
        return notificationChannel;
    }

    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
        Intent clickIntent = JumpIntentUtil.getJumpIntent(context, msg);
        if (clickIntent == null) {
            return null;
        }

        PendingIntent clickPendingIntent = PendingIntent.getActivity(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return clickPendingIntent;
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
