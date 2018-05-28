package com.sbai.httplib;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description: 请求中间等候 UI 的抽象类
 *
 */
public interface ReqIndeterminate {
    void onHttpUiShow(String tag);
    void onHttpUiDismiss(String tag);
}
