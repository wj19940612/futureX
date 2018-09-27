package com.songbai.futurex.view.chart;

import com.songbai.futurex.view.RadioHeader;

/**
 * Modified by john on 09/04/2018
 * <p>
 * Description: kline 工具类，对应 {@link RadioHeader#getSelectedPosition()} 0-9 的位置，从左到右，从上到下
 * <p>
 *     0: 分时图
 *     4: 日线
 *     5: 1分图
 *     8: 周线
 *     9: 月线
 */
public class KlineUtils {

    public static String getDateFormat(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                return "HH:mm";
            case 4:
            case 8:
            case 9:
                return "MM-dd";
            default:
                return null;
        }
    }

    public static String getHeaderDateFormat(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                return "yyyy/MM/dd HH:mm";
            case 4:
            case 8:
            case 9:
                return "yyyy/MM/dd";
            default:
                return null;
        }
    }

    public static String getKlineType(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 0:
                return "1";
            case 1:
                return "15";
            case 2:
                return "60";
            case 3:
                return "240";
            case 4:
                return "day";
            case 5:
                return "1";
            case 6:
                return "5";
            case 7:
                return "30";
            case 8:
                return "week";
            case 9:
                return "month";
            default:
                return null;
        }
    }

    public static int getRefreshInterval(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 0:
                return 60 * 1000;
            case 1:
                return 15 * 60 * 1000;
            case 5:
                return 60 * 1000;
            case 6:
                return 5 * 60 * 1000;
            default:
                return -1;
        }
    }
}
