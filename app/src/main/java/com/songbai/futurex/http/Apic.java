package com.songbai.futurex.http;

import com.sbai.httplib.ReqParams;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.LoginData;
import com.songbai.futurex.model.local.RegisterData;

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
     * /user/country/country.do
     * GET
     * 查询系统支持的地区
     */
    public static Api getSupportLocal() {
        return Api.get("/api/user/country/country.do");
    }

    /**
     * /user/msg/count
     * GET
     * 消息未读数量
     */
    public static Api getMsgCount() {
        return Api.get("/api/user/msg/count");
    }

    /**
     * /user/user/updatePic.do
     * POST
     * 更新头像接口(陈作衡)
     */
    public static Api submitPortraitPath(String data) {
        return Api.post("/api/user/user/updatePic.do",
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
        return Api.post("/api/user/userSafe/updatePhone.do",
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
        return Api.post("/api/user/userFeedback/addFeedback.do",
                new ReqParams()
                        .put("content", content));
    }

    /**
     * /otc/chat/send
     * POST
     * 发送消息
     */
    public static Api chatSend(String msg, String waresOrderId, int msgType) {
        return Api.post("/api/otc/chat/send",
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
        return Api.post("/api/otc/chat/history",
                new ReqParams()
                        .put("waresOrderId", waresOrderId)
                        .put("startTime", startTime)
                        .put("size", size));
    }

    /**
     * /user/wallet/getAccountByUser.do
     * GET
     * 账户查询（叶海啸）
     */
    public static Api getAccountByUser() {
        return Api.get("/api/user/wallet/getAccountByUser.do");
    }

    /**
     * 获取（phone/email）验证码
     * <p>
     * /api/user/validate/sendMsgCode.do
     *
     * @param authCodeGet
     * @return
     */
    public static Api getAuthCode(AuthCodeGet authCodeGet) {
        return Api.post("/api/user/validate/sendMsgCode.do",
                new ReqParams(AuthCodeGet.class, authCodeGet));
    }

    /**
     * 注册
     * <p>
     * /api/user/user/register.do
     *
     * @param registerData
     * @return
     */
    public static Api register(RegisterData registerData) {
        return Api.post("/api/user/user/register.do",
                new ReqParams(RegisterData.class, registerData));
    }

    /**
     * 获取用户基本信息
     * <p>
     * /api/user/user/findUserInfo.do
     *
     * @return
     */
    public static Api getUserInfo() {
        return Api.get("/api/user/user/findUserInfo.do");
    }

    /**
     * 获取图片验证码
     *
     * @param msgType
     * @return
     */
    public static Api getAuthCodeImage(int msgType) {
        return Api.get("/api/user/validate/download/codeImg.do",
                new ReqParams()
                        .put("msgType", msgType));
    }

    /**
     * 登录
     *
     * @param loginData
     * @return
     */
    public static Api login(LoginData loginData) {
        return Api.post("/api/user/user/login.do",
                new ReqParams(LoginData.class, loginData));
    }

    public interface url {
    }
}
