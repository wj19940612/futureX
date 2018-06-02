package com.songbai.futurex.http;

import com.sbai.httplib.ReqParams;

/**
 * Modified by john on 23/01/2018
 * <p>
 * Description: 统一管理 api 请求
 * <p>
 */
public class Apic {

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static Api getAreaCodes() {
        return Api.get("/api/user/country/country.d");
    }

    /**
     * /user/msg/count
     * GET
     * 消息未读数量
     */
    public static Api getMsgCount() {
        return Api.get("/user/msg/count");
    }

    /**
     * /user/user/updatePic.do
     * POST
     * 更新头像接口(陈作衡)
     */
    public static Api submitPortraitPath(String data) {
        return Api.post("/user/user/updatePic.do",
                new ReqParams()
                        .put("pic", data));
    }

    /**
     *
     */
    // TODO: 2018/5/29 修改昵称
    public static Api submitNickName(String nickName) {
        return Api.get("");
    }

    /**
     * /user/userSafe/updatePhone.do
     * POST
     * 绑定、修改手机号--薛松
     */
    public static Api updatePhone(String phoneNum, String phoneMsgCode, String msgCode, String type) {
        return Api.post("/user/userSafe/updatePhone.do",
                new ReqParams()
                        .put("phone", phoneNum)
                        .put("phoneMsgCode", phoneMsgCode)
                        .put("msgCode", msgCode)
                        .put("type", type));
    }

    /**
     * /user/userFeedback/addFeedback.do
     * POST
     * 添加用户反馈2.3.0(陈作衡)
     */
    public static Api addFeedback(String content, String contactInfo, String feedbackPic) {
        return Api.post("/user/userFeedback/addFeedback.do",
                new ReqParams()
                        .put("content", content));
    }

    /**
     * /otc/chat/send
     * POST
     * 发送消息
     */
    public static Api chatSend(String msg, String waresOrderId, int msgType) {
        return Api.post("/otc/chat/send",
                new ReqParams()
                        .put("msg", msg)
                        .put("waresOrderId", waresOrderId)
                        .put("msgType", msgType));
    }

    /**
     * /otc/chat/history
     * GET
     * 历史消息
     */
    public static Api chatHistory(String waresOrderId, String startTime, int size) {
        return Api.post("/otc/chat/history",
                new ReqParams()
                        .put("waresOrderId", waresOrderId)
                        .put("startTime", startTime)
                        .put("size", size));
    }

    public interface url {
    }
}
