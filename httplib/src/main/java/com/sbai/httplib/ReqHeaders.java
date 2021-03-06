package com.sbai.httplib;

import java.util.HashMap;

public class ReqHeaders {

    private HashMap<String, String> mHeaders;

    public ReqHeaders put(String key, Object value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }

        if (value != null) {
            mHeaders.put(key, value.toString());
        }

        return this;
    }

    public String get(String key) {
        if (mHeaders != null) {
            return mHeaders.get(key);
        }
        return null;
    }

    public HashMap<String, String> get() {
        return mHeaders;
    }
}
