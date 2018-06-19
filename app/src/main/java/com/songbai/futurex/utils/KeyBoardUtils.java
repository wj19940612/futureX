package com.songbai.futurex.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.songbai.futurex.App;

/**
 * 键盘控制类.
 */
public class KeyBoardUtils {

    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void closeOrOpenKeyBoard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void openKeyBoard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v     View
     * @param event MotionEvent
     * @return boolean
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            return isOutside(event, v);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public static boolean isOutside(MotionEvent ev, View v) {
        int[] l = {0, 0};
        v.getLocationInWindow(l);
        int left = l[0],
                top = l[1],
                bottom = top + v.getHeight(),
                right = left + v.getWidth();
        return !(ev.getX() > left && ev.getX() < right
                && ev.getY() > top && ev.getY() < bottom);
    }
}
