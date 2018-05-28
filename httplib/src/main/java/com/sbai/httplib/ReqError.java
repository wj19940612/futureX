package com.sbai.httplib;

/**
 * Modified by john on 13/11/2017
 * <p>
 * Description: http error wrap class
 */
public class ReqError<E> {

    public static final int ERROR_TIMEOUT = 0;
    public static final int ERROR_NETWORK = 1;
    public static final int ERROR_PARSE = 2;
    public static final int ERROR_SERVER = 3;

    private E error;
    private int type;

    public ReqError(E error) {
        this.error = error;
    }

    public E getError() {
        return error;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return error.toString();
    }
}
