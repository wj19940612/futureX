package com.songbai.futurex.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.httplib.NullResponseError;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.utils.ToastUtil;

/**
 * <p>Implement onFailure() with error toast. Handle token expired in onSuccess()<p/>
 * <p>
 * Two main callbacks to handle our custom reponse: Resp
 * <ul>
 * <li>onRespSuccess() when Resp.code == 200</li>
 * <li>onRespFailure() when Resp.code != 200</li>
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
            processRespResult(t);
        } else if (t instanceof String) {
            processStringResult(t);
        } else {
            onReceiveResponse(t);
        }
    }

    private void processStringResult(T t) {
        if (((String) t).indexOf("code") != -1) {
            try {
                Resp resp = new Gson().fromJson((String) t, Resp.class);
                if (resp.isTokenExpired()) {
                    processTokenExpiredError(resp);
                }
            } catch (JsonSyntaxException e) {
                onReceiveResponse(t);
            }
        } else {
            onReceiveResponse(t);
        }
    }

    private void processRespResult(T t) {
        Resp resp = (Resp) t;
        if (resp.isTokenExpired()) {
            processTokenExpiredError(resp);
        } else {
            onReceiveResponse(t);
        }
    }

    private void processTokenExpiredError(Resp resp) {
        sendTokenExpiredBroadcast(resp.getMsg());
        onFailure(null);
    }

    private void sendTokenExpiredBroadcast(String msg) {
//        Intent intent = new Intent(BaseActivity.ACTION_TOKEN_EXPIRED);
//        intent.putExtra(BaseActivity.EX_TOKEN_EXPIRED_MESSAGE, msg);
//        LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);
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

    private void onReceiveResponse(T t) {
        if (t instanceof Resp) {
            Resp resp = (Resp) t;
            if (resp.isSuccess()) {
                onRespSuccess(t);
            } else {
                onRespFailure(resp);
                onFailure(null);
            }
        } else {
            onRespSuccess(t);
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
