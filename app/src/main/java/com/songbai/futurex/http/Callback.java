package com.songbai.futurex.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.httplib.NullResponseError;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.App;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.utils.ToastUtil;

/**
 * <p>
 * 使用 Toast 实现 onFailure() 里面错误信息的展示.
 * <p>
 * 实现 onSuccess() 中对 http 200 的返回的几种数据类型进行预处理:
 * <ul>
 * <li>Null 类型，非正常情况下返回的类型，无法处理，Toast 错误提示</li>
 * <li>{@link Resp} 类型，通用业务业务数据类型：涉及 User's 登录超时处理；
 * Resp.code 为 200 回调 {@link Callback#onRespSuccess(Object)}, 否则 {@link Callback#onRespFailure(Resp)}</li>
 * <li>String 类型，业务中有时会需要纯 String 返回数据，需要提前进行 User's 登录超时处理</li>
 * <li>其他类型，直接返回</li>
 * </ul>
 *
 * @param <T>
 */

public abstract class Callback<T> extends ReqCallback<T> {

    @Override
    public void onSuccess(T t) {
        if (t == null) {
            onFailure(new ReqError(new NullResponseError("Server return null")));
        }

        if (t instanceof Resp) {
            processResp((Resp) t);
        } else if (t instanceof String) {
            processString((String) t);
        } else {
            onRespSuccess(t);
        }
    }

    private void processString(String s) {
        if (s.indexOf("code") != -1) {
            try {
                Resp resp = new Gson().fromJson(s, Resp.class);
                if (resp.isTokenExpired()) {
                    processTokenExpiredError(resp);
                } else {
                    onRespSuccess((T) s);
                }
            } catch (JsonSyntaxException e) {
                onRespSuccess((T) s);
            }
        } else {
            onRespSuccess((T) s);
        }
    }

    private void processResp(Resp resp) {
        if (resp.isTokenExpired()) {
            processTokenExpiredError(resp);
        } else if (resp.isSuccess()) {
            onRespSuccess((T) resp);
        } else {
            onRespFailure(resp);
        }
    }

    private void processTokenExpiredError(Resp resp) {
        sendTokenExpiredBroadcast(resp.getMsg());
        onFailure(null);
    }

    private void sendTokenExpiredBroadcast(String msg) {
        Intent intent = new Intent(BaseActivity.ACTION_TOKEN_EXPIRED);
        intent.putExtra(BaseActivity.EX_TOKEN_EXPIRED_MESSAGE, msg);
        LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);
    }

    @Override
    public void onFailure(ReqError reqError) {
        if (reqError == null) return;

        int toastResId = R.string.http_error_network;
        if (reqError.getError() instanceof NullResponseError) {
            toastResId = R.string.http_error_null;

        } else if (reqError.getType() == ReqError.ERROR_TIMEOUT) {
            toastResId = R.string.http_error_timeout;
        } else if (reqError.getType() == ReqError.ERROR_PARSE) {
            toastResId = R.string.http_error_parse;
        } else if (reqError.getType() == ReqError.ERROR_NETWORK) {
            toastResId = R.string.http_error_network;
        } else if (reqError.getType() == ReqError.ERROR_SERVER) {
            toastResId = R.string.http_error_server;
        }

        if (toastError()) {
            ToastUtil.show(toastResId);
        }
    }

    protected abstract void onRespSuccess(T resp);

    protected void onRespFailure(Resp failedResp) {
        if (toastError()) {
            ToastUtil.show(failedResp.getMsg());
        }
    }

    protected boolean toastError() {
        return true;
    }
}
