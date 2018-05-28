package com.sbai.httplib;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description:
 * <p>
 * Http 请求基础回调：四个主要的回调方法
 * <p>
 * <ul>
 * <li>onStart() when request start</li>
 * <li>onFinish() when request finish</li>
 * <li>onSuccess() when request success, code == 200</li>
 * <li>onFailure() when request failure, code != 200</li>
 * </ul>
 */
public abstract class ReqCallback<T> implements Response.Listener<T>, Response.ErrorListener {

    public interface onFinishedListener {
        void onFinished(String tag, String url);
    }

    private String mUrl;
    private onFinishedListener mOnFinishedListener;
    private String mTag;
    private ReqIndeterminate mIndeterminate;
    private String mId;

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setOnFinishedListener(onFinishedListener onFinishedListener) {
        mOnFinishedListener = onFinishedListener;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setIndeterminate(ReqIndeterminate Indeterminate) {
        mIndeterminate = Indeterminate;
    }

    /**
     * Call when request start
     */
    public void onStart() {
        if (mIndeterminate != null) {
            mIndeterminate.onHttpUiShow(mTag);
        }
    }

    /**
     * Call when request finish
     */
    public void onFinish() {
        if (mOnFinishedListener != null) {
            mOnFinishedListener.onFinished(mTag, mUrl);
        }

        if (mIndeterminate != null) {
            mIndeterminate.onHttpUiDismiss(mTag);
        }
    }

    /**
     * Call when request success, code == 200
     *
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * Call when request failure, code != 200
     *
     * @param reqError
     */
    public abstract void onFailure(ReqError reqError);

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        onFinish();
        onFailure(createApiError(volleyError));
    }

    // Wrap volley based error
    private ReqError createApiError(VolleyError volleyError) {
        ReqError reqError = new ReqError(volleyError);
        if (volleyError instanceof TimeoutError) {
            reqError.setType(ReqError.ERROR_TIMEOUT);
        } else if (volleyError instanceof ParseError) {
            reqError.setType(ReqError.ERROR_PARSE);
        } else if (volleyError instanceof NetworkError) {
            reqError.setType(ReqError.ERROR_NETWORK);
        } else if (volleyError instanceof ServerError) {
            reqError.setType(ReqError.ERROR_SERVER);
        }

        ReqLogger logger = RequestManager.getLogger();
        if (logger != null) {
            logger.onTag("onFailure(): " + getUrl() + " -> error: " + volleyError);
        }

        return reqError;
    }

    @Override
    public void onResponse(T t) {
        onFinish();
        onSuccess(t);

        ReqLogger logger = RequestManager.getLogger();
        if (logger != null) {
            logger.onTag("onSuccess(): " + getUrl() + " -> resp: " + t.toString());
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
