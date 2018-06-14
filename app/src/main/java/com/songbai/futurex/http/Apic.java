package com.songbai.futurex.http;

import com.sbai.httplib.ReqParams;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.FindPsdData;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.local.LoginData;
import com.songbai.futurex.model.local.RealNameAuthData;
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
     * /api/user/userSafe/realNameAuth.do
     * POST
     * 身份认证--薛松
     * 初级认证
     */
    public static Api realNameAuth(RealNameAuthData realNameAuthData) {
        return Api.post("/api/user/userSafe/realNameAuth.do",
                new ReqParams(RealNameAuthData.class, realNameAuthData));
    }

    /**
     * /api/user/userSafe/getUserAuth.do
     * GET
     * 或者用户的认证信息--薛松
     */
    public static Api getUserAuth() {
        return Api.get("/api/user/userSafe/getUserAuth.do");
    }

    /**
     * /api/otc/bank/authenticationName
     * GET
     * 获取用户认证姓名
     */
    public static Api authenticationName() {
        return Api.get("/api/otc/bank/authenticationName");
    }

    /**
     * /api/otc/bank/bindList.do
     * GET
     * 支付管理
     */
    public static Api bindList(int type) {
        return Api.get("/api/otc/bank/bindList.do",
                new ReqParams().put("type", type));
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
     * /api/entrust/coin/loadSimpleList.do
     * GET
     * 查询所有币种
     */
    public static Api coinLoadSimpleList() {
        return Api.get("/api/entrust/coin/loadSimpleList.do");
    }

    /**
     * /api/user/wallet/getDrawWalletAddrByCoinType.do
     * GET
     * 获取提现地址列表（叶海啸）
     */
    public static Api getDrawWalletAddrByCoinType(String coinType) {
        return Api.get("/api/user/wallet/getDrawWalletAddrByCoinType.do", new ReqParams().put("coinType", coinType));
    }

    /**
     * /api/user/wallet/countDrawWalletAddrByCoinType.do
     * POST
     * 获取提现地址的个数
     */
    public static Api countDrawWalletAddrByCoinType(String coinType) {
        return Api.get("/api/user/wallet/countDrawWalletAddrByCoinType.do", new ReqParams().put("coinType", coinType));
    }

    /**
     * /api/user/wallet/addDrawWalletAddrByCoinType.do
     * POST
     * 添加提现地址（叶海啸）
     */
    public static Api addDrawWalletAddrByCoinType(String coinType, String toAddr, String remark) {
        return Api.post("/api/user/wallet/addDrawWalletAddrByCoinType.do",
                new ReqParams()
                        .put("coinType", coinType)
                        .put("toAddr", toAddr)
                        .put("remark", remark));
    }

    /**
     * /api/user/wallet/removeDrawWalletAddr.do
     * POST
     * 删除提现地址（叶海啸）
     */
    public static Api removeDrawWalletAddr(int id) {
        return Api.post("/api/user/wallet/removeDrawWalletAddr.do",
                new ReqParams()
                        .put("id", id));
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
     * /user/wallet/getAccountByUser.do
     * GET
     * 账户查询（叶海啸）
     * 币币账户
     */
    public static Api getAccountByUser(String coinType) {
        return Api.get("/api/user/wallet/getAccountByUser.do",
                new ReqParams().put("coinType", coinType));
    }

    /**
     * /api/otc/account/list
     * GET
     * 法币账户
     */
    public static Api otcAccountList() {
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
     * /api/otc/account/transfer
     * POST
     * 资金划转--(v1.1)
     */
    public static Api accountTransfer(String coinType, int type, String count) {
        return Api.post("/api/otc/account/transfer",
                new ReqParams()
                        .put("coinType", coinType)
                        .put("type", type)
                        .put("count", count));
    }

    /**
     * /api/user/wallet/getAccountByUserForMuti.do
     * GET
     * 获取多个品种账户可用资金
     *
     * @param coinType usdt,btc 多个品种用逗号隔开
     */
    public static Api getAccountByUserForMuti(String coinType) {
        return Api.get("/api/user/wallet/getAccountByUserForMuti.do",
                new ReqParams()
                        .put("coinType", coinType));
    }

    /**
     * /api/user/wallet/getUserFinanceFlow.do
     * GET
     * 资产明细（叶海啸）
     */
    public static Api getUserFinanceFlow(GetUserFinanceFlowData getUserFinanceFlowData, int page, int pageSize) {
        return Api.get("/api/user/wallet/getUserFinanceFlow.do",
                new ReqParams(GetUserFinanceFlowData.class, getUserFinanceFlowData)
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * /api/user/wallet/getDepositWalletAddrByCoinType.do
     * GET
     * 获取充值地址（叶海啸）
     */
    public static Api getDepositWalletAddrByCoinType(String coinType) {
        return Api.get("/api/user/wallet/getDepositWalletAddrByCoinType.do",
                new ReqParams()
                        .put("coinType", coinType));
    }

    /**
     * /api/user/wallet/getCoinTypeDrawLimit.do
     * GET
     * 获取提现限制（叶海啸）
     */
    public static Api getCoinTypeDrawLimit(String coinType) {
        return Api.get("/api/user/wallet/getCoinTypeDrawLimit.do",
                new ReqParams()
                        .put("coinType", coinType));
    }

    /**
     * /api/user/wallet/drawCoin.do
     * POST
     * 提币（叶海啸）
     */
    public static Api drawCoin(String coinType, String toAddr, double withdrawAmount, String googleCode, String drawPassword) {
        return Api.post("/api/user/wallet/drawCoin.do",
                new ReqParams()
                        .put("coinType", coinType)
                        .put("toAddr", toAddr)
                        .put("withdrawAmount", withdrawAmount)
                        .put("googleCode", googleCode)
                        .put("drawPassword", drawPassword));
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
                        .put("drawPass", drawPass)
                        .put("affirmPass", affirmPass)
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
     * /api/user/msg/list
     * GET
     * 消息列表
     */
    public static Api msgList(int page, int size) {
        return Api.get("/api/user/msg/list",
                new ReqParams()
                        .put("page", page)
                        .put("size", size));
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
     * /api/user/userSafe/sendOld.do
     * POST
     * 发送验证码(需要原有的手机号或者短信校验使用)--薛松
     */
    public static Api sendOld(String imgCode, int smsType) {
        return Api.post("/api/user/userSafe/sendOld.do",
                new ReqParams().put("imgCode", imgCode).put("smsType", smsType));
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
        return Api.get("/api/user/user/findUserInfo.do",
                new ReqParams()
                        .put("logsize", 1));
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

    /**
     * 搜索货币对
     *
     * @param keyword
     * @return
     */
    public static Api searchCurrencyPairs(String keyword) {
        return Api.get("/api/entrust/pairs/search",
                new ReqParams()
                        .put("symbol", keyword));
    }

    /**
     * 添加自选
     *
     * @param pairs
     * @return
     */
    public static Api addOptional(String pairs) {
        return Api.post("/api/entrust/pairs/option.do",
                new ReqParams()
                        .put("pairs", pairs)
                        .put("type", 1));
    }

    /**
     * 取消自选
     *
     * @param pairs
     * @return
     */
    public static Api cancelOptional(String pairs) {
        return Api.post("/api/entrust/pairs/option.do",
                new ReqParams()
                        .put("pairs", pairs)
                        .put("type", 0));
    }


    public interface url {
    }
}
