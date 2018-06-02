package com.songbai.futurex.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.songbai.futurex.R;
import com.songbai.futurex.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 继承于 {@link Callback} 的工具回调，只用于处理业务数据结构为 {@link Resp} 且 Resp.code 为 200 时候的数据
 * <p>
 * 提供了 {@link Callback4Resp#onInterceptData(String)} 方法为 Resp.Data 为 String 时候进行拦截预处理
 *
 * @param <T> Type of Resp
 * @param <D> Type of Resp.Data
 */
public abstract class Callback4Resp<T, D> extends Callback<T> {

    @Override
    protected final void onRespSuccess(T t) {
        if (t instanceof Resp) {
            processRespData(((Resp) t).getData());
        }
    }

    private void processRespData(Object data) {
        if (data == null) return;

        if (data instanceof String) {
            data = onInterceptData((String) data);
            try {
                Object o = new Gson().fromJson((String) data, getDataType());
                onRespData((D) o);
            } catch (JsonSyntaxException e) {
                ToastUtil.show(R.string.data_parse_error);
            }
        } else {
            onRespData((D) data);
        }
    }

    /**
     * Called only when T.data is String type
     *
     * @param data
     * @return
     */
    protected String onInterceptData(String data) {
        return data;
    }

    protected abstract void onRespData(D data);

    public Type getDataType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[1]);
    }
}
