package com.songbai.futurex.utils;


import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_NOT_HOUR = "MM月dd日 ";
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy年MM月dd日 HH:mm";

    public static final String FORMAT_SPECIAL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_SPECIAL_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_SPECIAL_SLASH_NO_HOUR = "yyyy/MM/dd";
    public static final String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";

    public static final String FORMAT_MINUTE_SECOND = "mm:ss";
    public static final String FORMAT_DATE_ARENA = "yyyy.MM.dd";
    public static final String FORMAT_HOUR_MINUTE_DATE = "HH:mm MM/dd";

    public static final String FORMAT_UTZ_STANDARD = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
    public static final String FORMAT_HOUR_MINUTE_SECOND_DATE_YEAR = "HH:mm:ss MM/dd/yyyy";
    public static final String FORMAT_SPECIAL_SLASH_ALL = "yyyy/MM/dd HH:mm:ss";

    public static String format(long time, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(toFormat);
        return dateFormat.format(new Date(time));
    }

    public static String format(String time, String fromFormat, String toFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(toFormat);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isToday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isToday(date, todayDate);
    }

    public static boolean isToday(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTomorrow(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isTomorrow(date, todayDate);
    }

    public static boolean isTomorrow(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return isToday(calendar.getTime(), todayCalendar.getTime());
    }

    public static boolean isYesterday(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isYesterday(date, todayDate);
    }

    private static boolean isYesterday(Date date, Date todayDate) {
        Calendar toadyCalendar = Calendar.getInstance();
        toadyCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        toadyCalendar.add(Calendar.DAY_OF_YEAR, -1);
        return isToday(calendar.getTime(), toadyCalendar.getTime());
    }

    public static boolean isNextWeek(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isNextWeek(date, todayDate);
    }

    public static boolean isNextWeek(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        todayCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        return calendar.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isInThisMonth(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isInThisMonth(date, todayDate);
    }

    public static boolean isInThisMonth(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
    }

    public static boolean isInThisDay(long time, long today) {
        Date date = new Date(time);
        Date todayDate = new Date(today);
        return isInThisDay(date, todayDate);
    }

    public static boolean isInThisDay(Date date, Date todayDate) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE) == todayCalendar.get(Calendar.DATE)
                && calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
    }

    public static boolean isInThisYear(long time) {
        Date date = new Date(time);
        return isInThisYear(date);
    }

    public static boolean isInThisYear(String time, String fromFormat) {
        if (time.length() != fromFormat.length()) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        try {
            Date date = dateFormat.parse(time);
            return isInThisYear(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInThisYear(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }

    public static String addOneMinute(String date, String format) {
        if (date.length() != format.length()) {
            return date;
        }
        SimpleDateFormat parser = new SimpleDateFormat(format);
        try {
            long originDate = parser.parse(date).getTime();
            long finalDate = originDate + 60 * 1000; // 1 min
            return parser.format(new Date(finalDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getDayOfMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthOfYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String format(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    /**
     * 将日期格式转化为时间
     *
     * @param time
     * @return
     */
    public static long getDate(String time) {
        return getDate(time, DEFAULT_FORMAT);
    }

    /**
     * 将日期格式转化为时间
     *
     * @param time
     * @return
     */
    public static long getDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    /**
     * 格式化时间  如果是当天 则显示18:20
     * 如果是昨天 则 昨天 18:20
     * 其他的   12月12日 12:20
     * 不是今年  则 2015年12月18日
     *
     * @param createTime
     * @return
     */
    public static String getFormatTime(long createTime) {
        long systemTime = System.currentTimeMillis();
        if (DateUtil.isInThisYear(createTime)) {
            if (DateUtil.isToday(createTime, systemTime)) {
                return DateUtil.format(createTime, "HH:mm:ss");
            } else if (DateUtil.isYesterday(createTime, systemTime)) {
                return DateUtil.format(createTime, "昨日  " + "HH:mm");
            } else {
                return DateUtil.format(createTime, DateUtil.FORMAT_NOT_HOUR);
            }
        } else {
            return DateUtil.format(createTime, DateUtil.FORMAT_YEAR_MONTH_DAY);
        }
    }

    /**
     * <<<<<<< HEAD
     * 返回格式 7月15日
     *
     * @param createTime
     * @return
     */

    public static String getStudyFormatTime(long createTime) {
        return DateUtil.format(createTime, DateUtil.FORMAT_NOT_HOUR);
    }

    /**
     * 当前是否白天
     *
     * @return
     */
    public static boolean isDayTime() {
        long systemTime = System.currentTimeMillis();
        int time = Integer.valueOf(DateUtil.format(systemTime, "HHmm"));
        return time >= 600 && time <= 1800;
    }

    public static String formatNoticeTime(long time) {
        long timeMillis = System.currentTimeMillis();
        if (DateUtil.isInThisDay(time, timeMillis)) {
            return DateUtil.format(time, FORMAT_HOUR_MINUTE_SECOND);
        }
        return DateUtil.format(time, FORMAT_SPECIAL_SLASH_NO_HOUR);
    }

    /**
     * 计算传入的时间与当前时间的相差天数
     *
     * @param date 服务器所传递的时间
     * @return
     */
    public static String compareTimeDifference(String date) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long overTime = getDate(date, DateUtil.FORMAT_SPECIAL) / 1000;
        long time = overTime - curTime;

        if (time < 60 && time >= 0) {
            return "1分钟后";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟后";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时后";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天后";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月后";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年后";
        } else {
            return "0";
        }
    }

    /**
     * 比较两个时间相差的天数
     *
     * @param timestamp
     * @return
     */
    public static int compareDateDifference(long timestamp) {
        long systemTime = System.currentTimeMillis();
        if (timestamp >= systemTime) {
            return 0;
        }
        long days = (systemTime - timestamp) / (1000 * 60 * 60 * 24);
        return (int) days;
    }

    public static String getTodayStartTime(long timestamp) {
        return format(timestamp, "yyyy-MM-dd") + " 00:00:00";
    }

    public static String getTodayEndTime(long timestamp) {
        return format(timestamp, "yyyy-MM-dd") + " 24:00:00";
    }

    /**
     * 比较两个时间相差的分钟 返回格式为11:40
     */
    public static String compareTime(long timestamp) {
        String resultHour;
        String resultMin;
        long systemTime = System.currentTimeMillis();
        if (timestamp == 0 || timestamp < systemTime) {
            return "00:00";
        }
        long minutes = (timestamp - systemTime) / (1000 * 60);
        long hours = minutes / 60;
        long minute = minutes % 60;
        if (hours >= 24) {
            return "24:00";
        }
        if (hours < 10) {
            resultHour = "0" + String.valueOf(hours);
        } else {
            resultHour = String.valueOf(hours);
        }
        if (minute < 10) {
            resultMin = "0" + String.valueOf(minute);
        } else {
            resultMin = String.valueOf(minute);
        }
        return resultHour + ":" + resultMin;
    }


    /**
     * string 类型转换为 long 类型  strTime 的时间格式和 formatType 的时间格式必须相同
     *
     * @param time
     * @param fromFormat 时间格式
     * @return
     * @throws ParseException
     */
    public static long convertString2Long(String time, String fromFormat) {
        Date date = convertString2Date(time, fromFormat); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            return date.getTime();
        }
    }


    /**
     * string 类型转换为 date 类型
     *
     * @param time
     * @param fromFormat 要转换的格式 yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * @return
     * @throws ParseException
     */
    public static Date convertString2Date(String time, String fromFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(fromFormat);
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //xxxx年第几季度
    public static String getYearQuarter(String date) {
        return getYearQuarter(getDate(date, "yyyy-MM-dd"));
    }

    public static String getYearQuarter(long date) {
        Calendar.getInstance().setTime(new Date(date));
        int i = Calendar.getInstance().get(Calendar.MONTH);
        String year = format(date, "yyyy");
        String month = format(date, "yyyy MM");
        month = month.substring(month.length() - 2, month.length());
        if (!TextUtils.isEmpty(month) && month.startsWith("0")) {
            month = month.substring(0, month.length());
        }
        int monthTime = Integer.valueOf(month);
        return year + "年第" + getQuarter(monthTime) + "季度  " + "(截止日期: " + format(date, FORMAT_SPECIAL_SLASH_NO_HOUR + ")");
    }

    public static String getQuarter(int time) {
        String monthTime = String.valueOf(time);
        switch (time) {
            case 1:
            case 2:
            case 3:
                return "—";
            case 4:
            case 5:
            case 6:
                return "二";
            case 7:
            case 8:
            case 9:
                return "三";
            case 10:
            case 11:
            case 12:
                return "四";
        }
        return "";
    }

    //return xx:xx
    public static String getCountdownTime(int total, int spend) {
        String timeStr = null;
        int last = total - spend;
        int minute = 0;
        int second = 0;
        if (last <= 0) {
            return "00:00";
        } else {
            minute = last / 60;
            if (minute < 60) {
                second = last % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 15分钟
     *
     * @param seconds
     * @return
     */
    public static String getMinutes(long seconds) {
        return seconds / 60 + "分钟";
    }

    /**
     * 1分钟内：显示xx秒
     * 60s等整数：显示1分钟
     * 超过60后：显示xx分xx秒
     */

    public static String formatTime(long second) {
        if (second < 60) {
            return second + "秒";
        } else if (second % 60 == 0) {
            return second / 60 + "分钟";
        } else {
            return second / 60 + "分" + second % 60 + "秒";
        }
    }

    /**
     * @param timestamp
     * @return 1-上午 2-下午 3-晚上
     */
    public static int getDayAndNight(long timestamp) {
        Date date = new Date(timestamp);
        int hour = date.getHours();
        if (hour == 12) {
            if (date.getMinutes() > 0) {
                return 2;
            } else {
                return 1;
            }
        }

        if (hour == 18) {
            if (date.getMinutes() > 0) {
                return 3;
            } else {
                return 2;
            }
        }
        if (hour > 0 && hour < 12) {
            return 1;
        } else if (hour > 12 && hour < 18) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 判断 time1 是否比 time2 晚了 milliseconds, 即 time1 - time2 <= milliseconds && time1 - time2 >= 0
     *
     * @param time1
     * @param time2
     * @param milliseconds
     * @return
     */
    public static boolean isLessThanTimeInterval(long time1, long time2, long milliseconds) {
        long diff = time1 - time2;
        return diff >= 0 && diff <= milliseconds;
    }

}
