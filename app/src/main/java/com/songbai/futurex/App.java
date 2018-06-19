package com.songbai.futurex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sbai.httplib.ReqLogger;
import com.songbai.futurex.activity.CrashInfoActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.utils.LanguageUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.util.Locale;

/**
 * Modified by john on 23/01/2018
 * <p>
 * Description:
 */
public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        Api.init(sContext.getCacheDir(), sContext.getFilesDir());
        Api.setLogger(new ReqLogger() {
            @Override
            public void onTag(String log) {
                Log.d("VHttp", log);
            }
        });
        UMConfigure.setLogEnabled(true);
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
        UMConfigure.init(this, "5b16535cb27b0a5f1e000017", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "a1eddf08c530b6109ffed1b516e3934e");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("wtf", "onSuccess: " + deviceToken);
                Log.i("wtf", "device token: " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("wtf", "onSuccess: " + s + "======" + s1);
                Log.i("wtf", "device token: " + s1);
            }
        });
        processCaughtException();
        setLanguage();
    }

    private void setLanguage() {
        Locale locale = LanguageUtils.getCurrentLocale(this);
        LanguageUtils.updateLocale(this, locale);
    }

    private void processCaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
//                if (BuildConfigUtils.isProductFlavor()) {
//                    submitErrorInfoToServers(e);
//                } else {
                openCrashInfoPage(e);
//                }
                System.exit(1);
            }
        });
    }

    private void openCrashInfoPage(Throwable e) {
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), CrashInfoActivity.class);
        intent.putExtra(ExtraKeys.CRASH_INFO, e);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void submitErrorInfoToServers(Throwable e) {
        // TODO: 2018/1/22 上传至服务器

        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static Context getAppContext() {
        return sContext;
    }

    static {
        // 注意：测试用 appId & secret
//        PlatformConfig.setWeixin("wx7576ec9bb65aea1a", "d640a05c70ec272f56557bd7e9c15dc4");
//        PlatformConfig.setQQZone("1106465763", "qYrMZDFn2dn5KQhP");
    }
}
