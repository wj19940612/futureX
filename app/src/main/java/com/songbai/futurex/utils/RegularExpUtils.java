package com.songbai.futurex.utils;

import java.util.regex.Pattern;

/**
 * Modified by john on 2018/6/5
 * <p>
 * Description: 正则判断工具
 * <p>
 * APIs:
 */
public class RegularExpUtils {

    /**
     * 数字，英文字符，符号下划线的单独组合
     *
     * @param password
     * @return
     */
    public static boolean isWeakPassword(String password) {
        String pattern = "^(\\d+|[a-z]+|[A-Z]+|[\\W]+)$";
        return Pattern.matches(pattern, password);
    }

    /**
     * 数字，英文字符，符号下划线的两两组合
     *
     * @param password
     * @return
     */
    public static boolean isMiddlePassword(String password) {
        String pattern = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$)([\\da-zA-Z]{8,}|[\\d\\W_]{8,}|[a-zA-Z\\W_]{8,})$";
        return Pattern.matches(pattern, password);
    }

    /**
     * 数字，英文字符，符号下划线的三重组合
     *
     * @param password
     * @return
     */
    public static boolean isStrongPassword(String password) {
        String pattern = "^(?![\\da-zA-Z]+$)(?![a-zA-Z\\W_]+$)(?![\\d\\W_]+$)([\\da-zA-Z\\W_]{8,})$";
        return Pattern.matches(pattern, password);
    }

    /**
     * 判断是否是合法邮箱
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        String pattern = "^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(pattern, email);
    }
}
