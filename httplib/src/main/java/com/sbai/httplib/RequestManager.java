package com.sbai.httplib;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description:
 * <p>
 * http 请求基础管理类
 */
public class RequestManager {

    protected static final int GET = Request.Method.GET;
    protected static final int POST = Request.Method.POST;
    protected static final int PUT = Request.Method.PUT;

    private static class Volley {

        private static final String DEFAULT_CACHE_DIR = "inv_volley";

        public static RequestQueue newRequestQueue(File cacheDirectory) {
            File cacheDir = new File(cacheDirectory, DEFAULT_CACHE_DIR);
            Network network = null;
            try {
                TLSSocketFactory factory = new TLSSocketFactory();
                network = new BasicNetwork(new CookieHurlStack(null, factory));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
            queue.start();
            return queue;
        }
    }

    public static void init(File cacheDir, File cookieCacheDir) {
        sRequestQueue = Volley.newRequestQueue(cacheDir);
        CookieManger.getInstance().init(cookieCacheDir);
    }

    public static void init(File cacheDir) {
        sRequestQueue = Volley.newRequestQueue(cacheDir);
    }

    public static void setLogger(ReqLogger logger) {
        sLogger = logger;
    }

    protected static void enqueue(Request<?> request) {
        if (sRequestQueue != null) {
            sRequestQueue.add(request);
            if (sLogger != null) {
                sLogger.onTag("enqueue(): " + request.toString());
            }
        } else {
            throw new NullPointerException("Request queue isn't initialized.");
        }
    }

    public static void cancel(String tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    protected static ReqLogger getLogger() {
        return sLogger;
    }

    private static RequestQueue sRequestQueue;
    private static ReqLogger sLogger;
}
