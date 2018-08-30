package com.songbai.futurex.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.songbai.futurex.Preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * @author yangguangda
 * @date 2018/8/23
 */
public class NotchUtil {
    public static void setDisplayCutMode(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }

    //在使用LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES的时候，状态栏会显示为白色，这和主内容区域颜色冲突,
    //所以我们要开启沉浸式布局模式，即真正的全屏模式,以实现状态和主体内容背景一致
    public static void openFullScreenModel(Activity mAc) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            mAc.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = mAc.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            mAc.getWindow().setAttributes(lp);
            View decorView = mAc.getWindow().getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            systemUiVisibility |= flags;
            mAc.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }

    public static boolean hasNotchInScreen(Activity context) {
        boolean ret = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            View decorView = context.getWindow().getDecorView();
            if (decorView != null) {
                WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
                if (rootWindowInsets != null) {
                    DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                    if (displayCutout != null) {
                        List<Rect> boundingRects = displayCutout.getBoundingRects();
                        if (boundingRects != null && boundingRects.size() > 0) {
                            ret = true;
                        }
                    }
                }
            }
        } else {
            ret = hasNotchInHuawei(context) || hasNotchInOppo(context) || hasNotchInVivo(context) || hasNotchInMIUI();
        }
        return ret;
    }

    public static int[] getStandardNotchSize(Activity context) {
        int[] size = new int[]{0, 0};
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            View decorView = context.getWindow().getDecorView();
            if (decorView != null) {
                WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
                if (rootWindowInsets != null) {
                    DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                    if (displayCutout != null) {
                        List<Rect> boundingRects = displayCutout.getBoundingRects();
                        if (boundingRects.size() > 0) {
                            Rect rect = boundingRects.get(0);
                            size[0] = rect.right - rect.left;
                            size[1] = rect.bottom - rect.top;
                        }
                    }
                }
            }
        }
        return size;
    }

    public static int[] getNotchSize(Activity context) {
        String romType = getRomType();
        if (SYS_EMUI.equals(romType)) {
            return getHuaweiNotchSize(context);
        } else if (SYS_MIUI.equals(romType)) {
            return getMIUINotchSize(context);
        } else if (SYS_OPPO.equals(romType)) {
            return getOppoNotchSize();
        } else if (SYS_VIVO.equals(romType)) {
            return getStandardNotchSize(context);
        } else {
            return getStandardNotchSize(context);
        }
    }

    public static boolean hasNotchInHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInHuawei");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static int[] getHuaweiNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getHuaweiNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "getHuaweiNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "getHuaweiNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "getHuaweiNotchSize Exception");
        } finally {
            return ret;
        }
    }

    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static boolean hasNotchInMIUI() {
        // https://dev.mi.com/console/doc/detail?pId=1293
        String s = SystemProperties.get("ro.miui.notch");
        return "1".equals(s);
    }

    public static int[] getMIUINotchSize(Activity context) {
        if (getMIUIVersion() >= 10) {
            // https://dev.mi.com/console/doc/detail?pId=1293
            // MIUI 10 新增了获取刘海宽和高的方法，需升级至8.6.26开发版及以上版本。
            int[] ret = new int[]{0, 0};
            int widthId = context.getResources().getIdentifier("notch_width", "dimen", "android");
            if (widthId > 0) {
                int width = context.getResources().getDimensionPixelSize(widthId);
                ret[0] = width;
            }
            int heightId = context.getResources().getIdentifier("notch_height", "dimen", "android");
            if (heightId > 0) {
                int height = context.getResources().getDimensionPixelSize(heightId);
                ret[1] = height;
            }
            return ret;
        } else {
            return getStandardNotchSize(context);
        }
    }

    public static int getMIUIVersion() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int[] getOppoNotchSize() {
        String mProperty = SystemProperties.get("ro.oppo.screen.heteromorphism");
        return new int[]{0, 0};
    }

    public static class SystemProperties {
        public static String get(String key) {
            String value = "";
            Class<?> cls = null;
            try {
                cls = Class.forName("android.os.SystemProperties");
                Method hideMethod = cls.getMethod("get",
                        String.class);
                Object object = cls.newInstance();
                value = (String) hideMethod.invoke(object, key);
            } catch (ClassNotFoundException e) {
                Log.e("error", "get error() ", e);
            } catch (NoSuchMethodException e) {
                Log.e("error", "get error() ", e);
            } catch (InstantiationException e) {
                Log.e("error", "get error() ", e);
            } catch (IllegalAccessException e) {
                Log.e("error", "get error() ", e);
            } catch (IllegalArgumentException e) {
                Log.e("error", "get error() ", e);
            } catch (InvocationTargetException e) {
                Log.e("error", "get error() ", e);
            }
            return value;
        }
    }

    /**
     * 是否有凹槽
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;
    /**
     * 是否有圆角
     */
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;

    public static boolean hasNotchInVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static final String SYS_EMUI = "sys_emui";
    public static final String SYS_MIUI = "sys_miui";
    public static final String SYS_OPPO = "sys_oppo";
    public static final String SYS_VIVO = "sys_vivo";
    public static final String SYS_FLYME = "sys_flyme";
    public static final String SYS_OTHER = "sys_other";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_OPPO_VERSION = "ro.build.version.opporom";
    private static final String KEY_VIVO_VERSION = "ro.vivo.os.version";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    public static String getRomType() {
        String sysType = Preference.get().getSysModel();
        if (TextUtils.isEmpty(sysType)) {
            try {
                sysType = SYS_OTHER;
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                    sysType = SYS_MIUI;//小米
                } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                        || prop.getProperty(KEY_EMUI_VERSION, null) != null
                        || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                    sysType = SYS_EMUI;//华为
                } else if (prop.getProperty(KEY_OPPO_VERSION, null) != null) {
                    sysType = SYS_OPPO;//OPPO
                } else if (prop.getProperty(KEY_VIVO_VERSION, null) != null) {
                    sysType = SYS_VIVO;//VIVO
                } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                    sysType = SYS_FLYME;//魅族
                }
            } catch (IOException e) {
                e.printStackTrace();
                return sysType;
            }
        }
        return sysType;
    }

    public static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
