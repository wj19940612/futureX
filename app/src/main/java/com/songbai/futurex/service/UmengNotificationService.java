package com.songbai.futurex.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.umeng.message.UmengMessageService;

import org.android.agoo.common.AgooConstants;

/**
 * Created by Zhang on 16/10/26.
 */
public class UmengNotificationService extends UmengMessageService {

    //保活service
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!Helper.isServiceRunning(UmengNotificationService.this,SocketPushService.class.getName())){
                startNotificationService();
            }
            sendEmptyMessageDelayed(0, 10000);
        }
    };

    private void startNotificationService(){
        Intent intent = new Intent(UmengNotificationService.this,SocketPushService.class);
        startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Intent intent1 = new Intent();
        intent1.setClass(context, MyNotificationService.class);
        intent1.putExtra("UmengMsg", message);
        context.startService(intent1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
