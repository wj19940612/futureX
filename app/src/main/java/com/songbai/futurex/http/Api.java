package com.songbai.futurex.http;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.sbai.httplib.CookieManger;
import com.sbai.httplib.GsonRequest;
import com.sbai.httplib.MultipartRequest;
import com.sbai.httplib.ReqCallback;
import com.sbai.httplib.ReqError;
import com.sbai.httplib.ReqHeaders;
import com.sbai.httplib.ReqIndeterminate;
import com.sbai.httplib.ReqLogger;
import com.sbai.httplib.ReqParams;
import com.sbai.httplib.RequestManager;
import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.utils.BuildConfigUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Modified by john on 23/01/2018
 * <p>
 * Description: 请求 Api 基础类
 * <p>
 */
public class Api extends RequestManager {

    private static Set<String> sCurrentUrls = new HashSet<>();

    private int mMethod;
    private String mId;
    private String mTag;
    private String mApi;
    private String mJsonBody;
    private ReqCallback<?> mCallback;
    private ReqParams mReqParams;
    private ReqIndeterminate mIndeterminate;
    private int mTimeout;
    private String mHost;

    private File mFile;
    private String mFileName;

    private Api() {
        mMethod = GET;
    }

    public static Api get(String api) {
        Api reqApi = new Api();
        reqApi.mApi = api;
        return reqApi;
    }

    public static Api get(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }

    public static Api put(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mMethod = PUT;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }


    public static Api post(String api) {
        return post(api, null);
    }

    public static Api post(String api, ReqParams reqParams) {
        Api reqApi = new Api();
        reqApi.mMethod = POST;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        return reqApi;
    }

    public static Api post(String api, ReqParams reqParams, String jsonBody) {
        Api reqApi = new Api();
        reqApi.mMethod = POST;
        reqApi.mApi = api;
        reqApi.mReqParams = reqParams;
        reqApi.mJsonBody = jsonBody;
        return reqApi;
    }

    public static Api post(String uri, ReqParams apiParams, String filePartName, File file) {
        Api api = new Api();
        api.mMethod = POST;
        api.mApi = uri;
        api.mReqParams = apiParams;
        api.mFile = file;
        api.mFileName = filePartName;
        return api;
    }

    public Api id(String id) {
        mId = id;
        return this;
    }

    public Api tag(String tag) {
        mTag = tag;
        return this;
    }

    public Api callback(ReqCallback<?> callback) {
        mCallback = callback;
        return this;
    }

    public Api host(String host) {
        mHost = host;
        return this;
    }

    public Api indeterminate(ReqIndeterminate indeterminate) {
        mIndeterminate = indeterminate;
        return this;
    }

    public Api timeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public void fire() {
        String url = getUrl();
        synchronized (sCurrentUrls) {
            if (sCurrentUrls.add(mTag + "#" + url)) {
                createReqThenEnqueue(url);
            }
        }
    }

    public void fireFreely() {
        String url = getUrl();
        createReqThenEnqueue(url);
    }

    private void createReqThenEnqueue(String url) {
        ReqHeaders headers = new ReqHeaders();
        setupHeaders(headers);

        // setup Callback
        if (mCallback != null) {
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
            mCallback.setId(mId);
            mCallback.setTag(mTag);
            mCallback.setIndeterminate(mIndeterminate);
        } else { // with a default callback for handle request finish event
            mCallback = new ReqCallback<Object>() {
                @Override
                public void onSuccess(Object o) {
                }

                @Override
                public void onFailure(ReqError reqError) {
                }
            };
            mCallback.setUrl(url);
            mCallback.setOnFinishedListener(new RequestFinishedListener());
        }

        // new request
        Request request;
        Type type = mCallback.getGenericType();
        if (mFile != null && !TextUtils.isEmpty(mFileName)) {
            request = new MultipartRequest(mMethod, url, headers, mFileName, mFile, mReqParams, type, mCallback);
        } else {
            request = new GsonRequest(mMethod, url, headers, mReqParams, mJsonBody, type, mCallback);
        }
        request.setTag(mTag);

        if (mTimeout != 0) {
            request.setRetryPolicy(new DefaultRetryPolicy(mTimeout, 1, 1));
        }

        // start and enqueue
        mCallback.onStart();
        enqueue(request);
    }

    private void setupHeaders(ReqHeaders headers) {
        String cookies = CookieManger.getInstance().getCookies();
        if (!TextUtils.isEmpty(cookies)) {
            headers.put("Cookie", cookies);
        }
//        headers.put("lemi-version", AppInfo.getVersionName(App.getAppContext()))
//                .put("lemi-device", AppInfo.getDeviceHardwareId(App.getAppContext()))
//                .put("lemi-channel", "android:" + AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
    }

    private static class RequestFinishedListener implements ReqCallback.onFinishedListener {

        public void onFinished(String tag, String url) {
            if (sCurrentUrls != null) {
                sCurrentUrls.remove(tag + "#" + url);
            }
        }
    }

    private String getUrl() {
        if (mReqParams != null) {
            mApi = mReqParams.replaceHolders(mApi);
        }

        String host = TextUtils.isEmpty(mHost) ? getFixedHost() : mHost;
        String url = new StringBuilder(host).append(mApi).toString();
        if (mMethod == GET && mReqParams != null) {
            url += mReqParams.toString();
            mReqParams = null;
        }

        return url;
    }

    public static String getFixedHost() {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                || BuildConfig.FLAVOR.equalsIgnoreCase(BuildConfigUtils.FLAVOR_NAME_ALPHA)) {
            return "http://" + BuildConfig.HOST;
        }
        return "http://" + BuildConfig.HOST;
    }

    public static void cancel(String tag) {
        RequestManager.cancel(tag);
        Iterator<String> iterator = sCurrentUrls.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (str.startsWith(tag + "#")) {
                iterator.remove();

                ReqLogger logger = getLogger();
                if (logger != null) {
                    logger.onTag("req of " + tag + " is not finish, cancel (" + str + ")");
                }
            }
        }
    }
}
