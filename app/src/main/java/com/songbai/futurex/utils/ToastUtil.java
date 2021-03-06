package com.songbai.futurex.utils;

import android.widget.Toast;

import com.songbai.futurex.App;


public class ToastUtil {

    private static Toast sToast;
    private static long sFirstTime;
    private static long sSecondTime;
    private static String sMessage;

    public static void show(int messageId) {
        show(App.getAppContext().getString(messageId));
    }

    public static void show(String message) {
        if (sToast == null) {
            sToast = Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT);
            sToast.show();
            sFirstTime = System.currentTimeMillis();
        } else {
            sSecondTime = System.currentTimeMillis();
            if (message.equals(sMessage)) {
                if (sSecondTime - sFirstTime > Toast.LENGTH_SHORT) {
                    sToast.show();
                }
            } else {
                sMessage = message;
                sToast.setText(message);
                sToast.show();
            }
        }
        sFirstTime = sSecondTime;
    }
}