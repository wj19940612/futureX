package com.sbai.httplib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description: 请求参数，支持链式 put & 对象解析
 *
 */
public class ReqParams {

    private HashMap<String, String> mParams;

    public ReqParams() {
    }

    public ReqParams(Class<?> clazz, Object object) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public ReqParams put(String key, Object value) {
        if (mParams == null) {
            mParams = new HashMap<>();
        }

        if (value != null) {
            mParams.put(key, value.toString());
        }

        return this;
    }

    public HashMap<String, String> get() {
        return mParams;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (mParams != null && !mParams.isEmpty()) {
            builder.append("?");
            for (Object key : mParams.keySet()) {
                builder.append(key).append("=").append(mParams.get(key)).append("&");
            }
            if (builder.toString().endsWith("&")) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    public String replaceHolders(String api) {
        if (mParams != null) {
            Iterator<Map.Entry<String, String>> it = mParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                String holder = "{" + next.getKey() + "}";
                int indexOf = api.indexOf(holder);
                if (indexOf > -1) {
                    api = api.substring(0, indexOf)
                            .concat(next.getValue())
                            .concat(api.substring(indexOf + holder.length()));
                    it.remove();
                }
            }
        }
        return api;
    }
}
