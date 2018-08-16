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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.songbai.futurex.R;
import com.songbai.futurex.model.JumpContent;
import com.songbai.futurex.utils.JumpIntentUtil;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.PushDestUtils;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.notification.NotificationProcessor;
import com.umeng.message.entity.UMessage;

/**
 * Created by Zhang on 18/8/6.
 */

public class SocketPushService extends Service {

    private NotificationProcessor mNotificationProcessor;
    private static final String PHONE_BRAND_SAMSUNG = "samsung";

    public static final String NEWUSER = "newuser";


    @Override
    public void onCreate() {
        super.onCreate();
        initPushListener();
        mNotificationProcessor.registerMsg();
        mNotificationProcessor.resume();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        boolean isNewUser = intent.getBooleanExtra(NEWUSER, false);
        if (isNewUser && mNotificationProcessor != null) {
            mNotificationProcessor.registerMsg();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationProcessor.unRegisterMsg();
        mNotificationProcessor.pause();
    }

    private void initPushListener() {
        mNotificationProcessor = new NotificationProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                if (PushDestUtils.isNotification(dest)) {
                    notifyMsg(data);
                }
            }
        });
    }

    private void notifyMsg(String message) {
        Log.e("zzz", "message:" + message);
        new DataParser<Response<JumpContent>>(message) {
            @Override
            public void onSuccess(Response<JumpContent> jumpContentResponse) {
                Intent intent = JumpIntentUtil.getJumpIntent(SocketPushService.this, jumpContentResponse.getContent());
                if (intent != null) {
                    notifyToUI(intent, jumpContentResponse.getContent());
                }
            }
        }.parse();

    }

    private void notifyToUI(Intent intent, JumpContent jumpContent) {
        if (jumpContent == null) return;

        PendingIntent pendingIntent = getClickPendingIntent(this, intent);

        String channelId = getString(R.string.app_name);
        boolean b = !TextUtils.isEmpty(jumpContent.getTitle());
        String notificationTitle;
        if (b) {
            notificationTitle = jumpContent.getTitle();
        } else {
            notificationTitle = channelId;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(jumpContent.getText());
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        String brand = Build.BRAND;

        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (!TextUtils.isEmpty(brand) && brand.equalsIgnoreCase(PHONE_BRAND_SAMSUNG)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(bitmap);
        }
        builder.setContentIntent(pendingIntent);
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

    public PendingIntent getClickPendingIntent(Context context, Intent clickIntent) {
        if (clickIntent == null) {
            return null;
        }

        PendingIntent clickPendingIntent = PendingIntent.getActivity(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return clickPendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
