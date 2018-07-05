package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/7/5
 */
public interface AuthenticationStatus {
    int AUTHENTICATION_NONE = 0;
    int AUTHENTICATION_PRIMARY = 1;
    int AUTHENTICATION_SENIOR = 2;
    int AUTHENTICATION_SENIOR_GOING = 3;
    int AUTHENTICATION_SENIOR_FAIL = 4;
}
