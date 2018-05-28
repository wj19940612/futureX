package com.sbai.httplib;

import com.android.volley.VolleyError;

/**
 * Modified by john on 18/01/2018
 * <p>
 * Description:
 * <p>
 * 自定义的空 response 异常，此时 code == 200
 */
public class NullResponseError extends VolleyError {

    public NullResponseError(String exceptionMessage) {
        super(exceptionMessage);
    }
}
