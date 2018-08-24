package com.songbai.futurex.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.Preference;

import java.util.Locale;

/**
 * @author yangguangda
 * @date 2018/6/8
 */
public class LanguageUtils {

    private static String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";

    /**
     * 获取用户设置的locale
     *
     * @param context context
     * @return locale
     */
    public static Locale getUserLocale(Context context) {
        String localJson = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString("locale_json", null);
        if (TextUtils.isEmpty(localJson)) {
            return getCurrentLocale(context);
        }
        return json2Locale(localJson);
    }

    /**
     * 获取当前的locale
     *
     * @param context context
     * @return locale
     */
    public static Locale getCurrentLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * 保存用户设置的locale
     *
     * @param userLocale locale
     */
    public static void saveUserLocale(Locale userLocale) {
        Preference.get().setLocalJson(locale2Json(userLocale));
    }

    /**
     * locale转成json
     *
     * @param userLocale userlocale
     * @return json string
     */
    private static String locale2Json(Locale userLocale) {
        Gson gson = new Gson();
        return gson.toJson(userLocale);
    }

    /**
     * json转成locale
     *
     * @param localeJson localejson
     * @return locale
     */
    private static Locale json2Locale(String localeJson) {
        Gson gson = new Gson();
        return gson.fromJson(localeJson, Locale.class);
    }

    /**
     * 更新locale
     *
     * @param context       context
     * @param newUserLocale new user locale
     */
    public static synchronized boolean updateLocale(Context context, Locale newUserLocale) {
        if (needUpdateLocale(context, newUserLocale)) {
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(newUserLocale);
            } else {
                configuration.locale = newUserLocale;
            }
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, displayMetrics);
            if (needUpdateLocale(context, newUserLocale)) {
                return false;
            }
            saveUserLocale(newUserLocale);
            return true;
        }
        return false;
    }

    public static Context attachBaseContext(Context context, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * 判断需不需要更新
     *
     * @param context       context
     * @param newUserLocale new user locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Context context, Locale newUserLocale) {
        return newUserLocale != null && !getCurrentLocale(context).equals(newUserLocale);
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getUserLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }
}
