package com.songbai.futurex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.sbai.httplib.ReqLogger;
import com.songbai.futurex.activity.CrashInfoActivity;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.service.PushIntentService;
import com.songbai.futurex.service.PushService;
import com.songbai.futurex.utils.LanguageUtils;

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

        //init getui sdk
        PushManager.getInstance().initialize(this, PushService.class);
        //init getui service
        PushManager.getInstance().registerPushIntentService(this, PushIntentService.class);
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
