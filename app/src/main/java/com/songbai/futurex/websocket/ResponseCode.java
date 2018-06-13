package com.songbai.futurex.websocket;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public interface ResponseCode {
    int HEART = 2000;

    int UNKNOWN_REQUEST = 2001; // 未知指令
    int JSON_ERROR = 2002;
    int UNKNOWN_ERROR = 2003;

    int UNREGISTER = 2010;
    int UNLOGIN = 2011;

    int REGISTER_SUCCESS = 2100;
    int REGISTER_FAILURE = 2101;

    int MSG_OFFLINE_SUCCESS = 2103;
    int MSG_ACK_SUCCESS = 2104; // 暂时没有，客户端回复消息确认码

    int REQUEST_SUCCESS = 2200;

    int REQUEST_PARESE_ERROR = 2201; // 解析错误
    int REQUEST_GONE = 2202; // 命令无法找到
    int PARAMS_ERROR = 2210; // 参数异常

    int SUBSCRIBE_BUSINESS_SUCCESS = 2120; // 订阅业务消息成功
    int UNSUBSCRIBE_BUSINESS_SUCCESS = 2121; // 取消业务消息成功
    int FORWORD_MSG_SUCCESS = 2131; // 转发消息成功
    int CHECK_ONLINE_STATUS_SUCCESS = 2141; // 查看在线状态成功

    int PUSH = 3000;

    int NETWORK_ERROR = -2000; // local network error
}
