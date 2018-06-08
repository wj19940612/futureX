package com.songbai.futurex.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.songbai.futurex.Preference;

import java.util.Locale;

/**
 * @author yangguangda
 * @date 2018/6/8
 */
public class LanguageUtils {

    /**
     * 获取用户设置的locale
     *
     * @param context context
     * @return locale
     */
    public static Locale getuserlocale(Context context) {
        String localJson = Preference.get().getLocalJson();
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
    public static boolean updateLocale(Context context, Locale newUserLocale) {
        if (needUpdateLocale(context, newUserLocale)) {
            Configuration configuration = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(newUserLocale);
            } else {
                configuration.locale = newUserLocale;
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, displayMetrics);
            saveUserLocale(newUserLocale);
            return true;
        }
        return false;
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
}
