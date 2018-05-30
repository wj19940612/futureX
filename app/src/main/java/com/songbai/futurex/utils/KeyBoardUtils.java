package com.songbai.futurex.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
}
