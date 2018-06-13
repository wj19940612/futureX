package com.songbai.futurex.http;

import com.sbai.httplib.ReqParams;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.FindPsdData;
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
     * /api/user/country/lang.do
     * GET
     * 查询系统所有的语言
     */
    public static Api getSupportLang() {
        return Api.get("/api/user/country/lang.do");
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
     * /api/user/user/findUserInfo.do
     * POST
     * 用户基本信息
     */
    public static Api findUserInfo() {
        return Api.get("/api/user/user/findUserInfo.do");
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
     * /api/user/user/updateUser.do
     * POST
     * 更新用户昵称接口
     */
    public static Api updateNickName(String userName) {
        return Api.post("/api/user/user/updateUser.do", new ReqParams().put("userName", userName));
    }

    /**
     * /api/user/userSafe/bindEmail.do
     * POST
     * 绑定邮箱--薛松
     */
    public static Api bindEmail(String email, String emailMsgCode, String phoneMsgCode) {
        return Api.post("/api/user/userSafe/bindEmail.do",
                new ReqParams()
                        .put("email", email)
                        .put("emailMsgCode", emailMsgCode)
                        .put("phoneMsgCode", phoneMsgCode));
    }

    /**
     * /api/otc/bank/bind
     * POST
     * 绑定卡号--(v1.1)
     */
    public static Api bankBind(BankBindData bankBindData) {
        return Api.post("/api/otc/bank/bind", new ReqParams(BankBindData.class, bankBindData));
    }

    /**
     * /api/otc/bank/list
     * GET
     * 银行列表
     */
    public static Api bankList() {
        return Api.get("/api/otc/bank/list");
    }

    /**
     * /api/user/wallet/getDrawWalletAddrByCoinType.do
     * GET
     * 获取提现地址列表（叶海啸）
     */
    public static Api getDrawWalletAddrByCoinType() {
        return Api.get("/api/user/wallet/getDrawWalletAddrByCoinType.do");
    }

    /**
     * /api/user/user/findCommissionOfSubordinate
     * GET
     * 最近/全部 邀请用户
     */
    public static Api findCommissionOfSubordinate() {
        return Api.get("/api/user/user/findCommissionOfSubordinate");
    }

    /**
     * /api/otc/account/list
     * GET
     * 法币账户
     */
    public static Api accountList() {
        return Api.get("/api/otc/account/list");
    }

    /**
     * /api/user/user/account
     * GET
     * 推广员账户
     */
    public static Api userAccount() {
        return Api.get("/api/user/user/account");
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
     * /api/user/userSafe/isDrawPass.do
     * POST
     * 是否设置资金密码--薛松
     */
    public static Api isDrawPass() {
        return Api.get("/api/user/userSafe/isDrawPass.do");
    }

    /**
     * /api/user/userSafe/setDrawPass.do
     * POST
     * 设置、资金密码--薛松
     */
    public static Api setDrawPass(String drawPass, String affirmPass, String msgCode, String type, String googleCode) {
        return Api.post("/api/user/userSafe/setDrawPass.do",
                new ReqParams()
                        .put("phone", drawPass)
                        .put("phoneMsgCode", affirmPass)
                        .put("msgCode", msgCode)
                        .put("type", type)
                        .put("googleCode", googleCode));
    }

    /**
     * /api/user/userSafe/needGoogle.do
     * POST
     * 是否需要google--薛松
     */
    public static Api needGoogle(String type) {
        return Api.post("/api/user/userSafe/needGoogle.do", new ReqParams().put("type", type));
    }

    /**
     * /api/user/user/updateLoginPass.do
     * POST
     * 修改登录密码
     */
    public static Api updateLoginPass(String newUserPass, String oldUserPass) {
        return Api.post("/api/user/user/updateLoginPass.do",
                new ReqParams()
                        .put("newUserPass", newUserPass)
                        .put("oldUserPass", oldUserPass));
    }

    /**
     * /api/user/userSafe/createGoogleKey.do
     * POST
     * 生成google验证码密钥--薛松
     */
    public static Api createGoogleKey() {
        return Api.post("/api/user/userSafe/createGoogleKey.do");
    }

    /**
     * /api/user/upload/image.do
     * POST
     * 上传图片-单个图片
     */

    public static Api uploadImage(String picture) {
        return Api.post("/api/user/upload/image.do",
                new ReqParams()
                        .put("picture", picture));
    }

    /**
     * /api/user/upload/images.do
     * POST
     * 上传图片-多个图片
     */
    public static Api uploadImages(String picture) {
        return Api.post("/api/user/upload/images.do",
                new ReqParams()
                        .put("picture", picture));
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

    /**
     * 设置新的登录密码
     *
     * @param findPsdData
     * @return
     */
    public static Api updateLoginPsd(FindPsdData findPsdData) {
        return Api.post("/api/user/user/forgetUserPass.do",
                new ReqParams(FindPsdData.class, findPsdData));
    }

    /**
     * 获取自选列表
     *
     * @return
     */
    public static Api getOptionalList() {
        return Api.get("/api/entrust/pairs/pairsSimpleList.do",
                new ReqParams()
                        .put("type", 1));
    }

    /**
     * 根据计价货币获取货币对
     *
     * @param suffixSymbol
     * @return
     */
    public static Api getCurrencyPairList(String suffixSymbol) {
        return Api.get("/api/entrust/pairs/pairsSimpleList.do",
                new ReqParams()
                        .put("type", 0)
                        .put("suffixSymbol", suffixSymbol));
    }

    /**
     * 获取 socket 配置，host & port
     *
     * @return
     */
    public static Api getSocketConfig() {
        return Api.get("/api/gateway/tcp/websocket.do");
    }

    /**
     * 获取服务器系统时间
     *
     * @return
     */
    public static Api getSystemTime() {
        return Api.get("/user/user/getSystemTime.do");
    }


    public interface url {
    }
}
