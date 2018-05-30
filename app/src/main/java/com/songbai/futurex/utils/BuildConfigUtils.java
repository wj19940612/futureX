package com.songbai.futurex.utils;


import com.songbai.futurex.BuildConfig;

/**
 * Created by ${wangJie} on 2018/1/22.
 */

public class BuildConfigUtils {

    private static final String FLAVOR_NAME_DEV = "dev";

    public static final String FLAVOR_NAME_ALPHA = "alpha";


    public static boolean isProductFlavor() {
        return !(BuildConfig.FLAVOR.equalsIgnoreCase(FLAVOR_NAME_DEV) ||
                BuildConfig.FLAVOR.equalsIgnoreCase(FLAVOR_NAME_ALPHA));
    }

}
