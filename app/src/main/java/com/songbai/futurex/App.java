package com.songbai.futurex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.sbai.httplib.ReqLogger;
import com.songbai.futurex.activity.CrashInfoActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.service.UmengNotificationService;
import com.songbai.futurex.utils.LanguageUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import java.util.Locale;

/**
 * Modified by john on 23/01/2018
 * <p>
 * Description:
 */
public class App extends Application {

    private static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        if (sContext != null) {
            sContext = this;
        }
        super.attachBaseContext(LanguageUtils.attachBaseContext(base, LanguageUtils.getUserLocale(base)));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        setLanguage();
        Api.init(sContext.getCacheDir(), sContext.getFilesDir());
        Api.setLogger(new ReqLogger() {
            @Override
            public void onTag(String log) {
                Log.d("VHttp", log);
            }
        });
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, BuildConfig.PUSH_SECRET);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {//注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
//        processCaughtException();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onLanguageChange();
    }

    private void onLanguageChange() {
        setLanguage();
    }

    private void setLanguage() {
        Locale locale = LanguageUtils.getUserLocale(getAppContext());
        LanguageUtils.updateLocale(getAppContext(), locale);
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
        PlatformConfig.setWeixin("wxc9e47f8de7a045c6", "d7184597d4c0c4e537cbbecef61cf548");
    }
}
