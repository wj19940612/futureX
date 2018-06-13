package com.songbai.futurex.utils;

/**
 * Modified by john on 2018/6/13
 * <p>
 * Description: 数据显示辅助类
 * <p>
 * APIs:
 */
public class NumUtils {

    /**
     * 交易量
     * @param volume
     * @return
     */
    public static String getVolume(double volume) {
        // TODO: 2018/6/13 how show
        return String.valueOf(volume);
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
