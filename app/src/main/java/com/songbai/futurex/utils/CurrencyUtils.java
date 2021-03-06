package com.songbai.futurex.utils;

import android.text.TextUtils;

import java.math.RoundingMode;

/**
 * Modified by john on 2018/6/13
 * <p>
 * Description: 数据显示辅助类
 * <p>
 * APIs:
 */
public class CurrencyUtils {

    public static String formatPairName(String pairName) {
        if (!TextUtils.isEmpty(pairName) && pairName.indexOf('_') > -1) {
            String[] split = pairName.split("_", 2);
            return split[0].toUpperCase() + "/" + split[1].toUpperCase();
        }
        return pairName.toUpperCase();
    }

    /**
     * 交易量
     * @param volume
     * @return
     */
    public static String getVolume(double volume) {
        // TODO: 2018/6/13 how show
        return String.valueOf(volume);
    }

    public static String getVolume(double volume, int scale) {
        return FinanceUtil.formatWithScale(volume, scale);
    }

    /**
     * 24小时交易量格式化，正无穷舍入
     *
     * @param volume
     * @return
     */
    public static String get24HourVolume(double volume) {
        return FinanceUtil.formatWithScale(volume, 0, RoundingMode.CEILING);
    }

    /**
     * 价格
     * @param price
     * @return
     */
    public static String getPrice(double price) {
        // TODO: 2018/6/13 how show
        return String.valueOf(price);
    }

    /**
     * 价格
     *
     * @param price
     * @param scale
     * @return
     */
    public static String getPrice(double price, int scale) {
        return FinanceUtil.formatWithScale(price, scale);
    }

    /**
     * 金额
     *
     * @param amt
     * @return
     */
    public static String getAmt(double amt) {
        return FinanceUtil.formatWithScale(amt, 8);
    }

    /**
     * 交易金额
     *
     * @param amt
     * @param scale
     * @return
     */
    public static String getAmt(double amt, int scale) {
        return FinanceUtil.formatWithScale(amt, scale);
    }

    /**
     * 浮点数据
     *
     * @param number
     * @return
     */
    public static double getDouble(String number) {
        if (TextUtils.isEmpty(number)) {
            return 0;
        }

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 涨跌幅
     * @param percent
     * @return
     */
    public static String getPrefixPercent(double percent) {
        if (percent < 0) {
            return FinanceUtil.formatToPercentage(percent);
        } else {
            return "+" + FinanceUtil.formatToPercentage(percent);
        }
    }
}
