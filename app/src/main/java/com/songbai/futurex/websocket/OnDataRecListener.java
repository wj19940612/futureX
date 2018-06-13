package com.songbai.futurex.websocket;

/**
 * Modified by john on 13/03/2018
 * <p>
 * Description: 行情数据, eg. "EURUSD,1.1,2.1,3.0,3.0"
 * <p>
 *
 */
public interface OnDataRecListener {

    void onDataReceive(String data, int code);
}
