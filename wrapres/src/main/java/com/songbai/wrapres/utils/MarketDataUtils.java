package com.songbai.wrapres.utils;

import java.math.RoundingMode;

/**
 * Modified by john on 11/02/2018
 * <p>
 * Description: 数字货币行情数据处理工具
 * <p>
 * APIs:
 */
public class MarketDataUtils {

    private static final String SIGN_DOLLAR = "$";
    private static final String SIGN_RMB = "￥";

    /**
     * 格式化成交量数据，大于等于 10000 的时候显示“XXX.X万” 保留1位小数，小于10000 的时候显示具体成交量，保留1位小数
     *
     * @param volume
     * @return
     */
    public static String formatVolume(double volume) {
        return FinanceUtil.unitize(volume, 1);
    }

    /**
     * 格式化价格（美元）数据，大于等于10时，保留2位小数，小于10时保留4位小数
     *
     * @param dollar
     * @return
     */
    public static String formatDollar(double dollar) {
        if (dollar >= 10) {
            return FinanceUtil.formatWithScale(dollar, 2, RoundingMode.DOWN);
        } else {
            return FinanceUtil.formatWithScale(dollar, 4, RoundingMode.DOWN);
        }
    }

    /**
     * 格式化价格（美元）数据，带 $ 符号
     *
     * @param dollar
     * @return
     */
    public static String formatDollarWithSign(double dollar) {
        return SIGN_DOLLAR + formatDollar(dollar);
    }

    /**
     * 格式化价格（人民币）数据
     *
     * @param rmb
     * @return
     */
    public static String formatRmb(double rmb) {
        return FinanceUtil.formatWithScale(rmb, 2, RoundingMode.DOWN);
    }

    /**
     * 格式化价格（人民币）数据，带 ￥ 符号
     *
     * @param rmb
     * @return
     */
    public static String formatRmbWithSign(double rmb) {
        return SIGN_RMB + formatRmb(rmb);
    }

    /**
     * 百分化数据带前缀与符号（+/-/%）
     *
     * @param number
     * @return
     */
    public static String percentWithPrefix(double number) {
        if (number < 0) {
            return FinanceUtil.formatToPercentage(number, 2);
        }
        return "+" + FinanceUtil.formatToPercentage(number, 2);
    }

    /**
     * 格式化价格（美元）数据，带 +/- 前缀
     *
     * @param dollar
     * @return
     */
    public static String formatDollarWithPrefix(double dollar) {
        if (dollar < 0) {
            return formatDollar(dollar);
        }
        return "+" + formatDollar(dollar);
    }

    /**
     * 格式化市值保留整数，千分符号分割
     *
     * @param marketValue
     * @return
     */
    public static String formatMarketValue(double marketValue) {
        return FinanceUtil.formatWithThousandsSeparator(marketValue, 0, RoundingMode.DOWN);
    }
}
