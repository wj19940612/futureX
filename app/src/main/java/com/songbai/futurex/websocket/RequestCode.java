package com.songbai.futurex.websocket;

/**
 * Modified by john on 2018/6/12
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public interface RequestCode {
    int HEART = 1000;
    int REGISTER = 1100;
    int UNREGISTER = 1101;
    int MSG_OFFLINE = 1102; // 离线消息
    int MSG_ACK = 1103; // 消息确认
    int SUBSCRIBE_BUSINESS = 1120; // 订阅业务消息
    int UNSUBSCRIBE_BUSINESS = 1121; // 取消业务消息
    int FORWARD_MSG = 1131; // 转发消息
    int CHECK_MSG_ONLINE = 1141; // 查看在线状态
    int REQUEST = 1200; // 业务请求
}
