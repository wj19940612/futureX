package com.songbai.futurex.websocket;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.songbai.futurex.R;
import com.songbai.futurex.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Modified by john on 28/03/2018
 * <p>
 * Description: websocket 数据解析器
 * <p>
 */
public abstract class DataParser<T> {

    private String mData;
    private Gson mGson;
    private boolean mToastError;

    public DataParser(String data) {
        mData = data;
        mGson = new Gson();
        mToastError = true;
    }

    public DataParser(String data, boolean toastError) {
        mData = data;
        mGson = new Gson();
        mToastError = toastError;
    }

    public void parse() {
        Type type = getGenericType();
        try {
            T result = mGson.fromJson(mData, type);
            processResult(result);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.d("WebSocket", "DataParser:JsonSyntaxException(" + type.toString() + "): " + e.toString());
        } catch (JsonParseException e) {
            e.printStackTrace();
            Log.d("WebSocket", "DataParser:JsonParseException(" + type.toString() + "): " + e.toString());
        }
    }

    private void processResult(T result) {
        if (!(result instanceof Response)) return;

        Response resp = (Response) result;
        if (resp.getCode() == Response.NETWORK_ERROR) {
            onError(resp.getCode(), resp.getMessage());
            return;
        }

        onSuccess(result);
    }

    public abstract void onSuccess(T t);

    public void onError(int code, String message) {
        if (mToastError) {
            if (code == Response.NETWORK_ERROR) {
                ToastUtil.show(R.string.http_error_network);
            } else if (!TextUtils.isEmpty(message)) {
                ToastUtil.show(message);
            }
        }
    }

    public Type getGenericType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }
}
