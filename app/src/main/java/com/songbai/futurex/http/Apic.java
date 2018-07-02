package com.songbai.futurex.http;

import com.sbai.httplib.ReqParams;
import com.songbai.futurex.App;
import com.songbai.futurex.model.local.AuthCodeGet;
import com.songbai.futurex.model.local.AuthSendOld;
import com.songbai.futurex.model.local.BankBindData;
import com.songbai.futurex.model.local.FindPsdData;
import com.songbai.futurex.model.local.GetOtcWaresHome;
import com.songbai.futurex.model.local.GetUserFinanceFlowData;
import com.songbai.futurex.model.local.LoginData;
import com.songbai.futurex.model.local.MakeOrder;
import com.songbai.futurex.model.local.RealNameAuthData;
import com.songbai.futurex.model.local.RegisterData;
import com.songbai.futurex.model.local.WaresModel;
import com.songbai.futurex.utils.AppInfo;
import com.songbai.futurex.websocket.model.CustomServiceChat;

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
     * /api/user/appVersion/queryForceVersion.do
     * GET
     * 查询当前平台版本信息
     */
    public static Api queryForceVersion() {
        return Api.get("/api/user/appVersion/queryForceVersion.do",
                new ReqParams()
                        .put("platform", 2));
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
     * /api/otc/bank/untie
     * POST
     * 解绑银行卡
     */
    public static Api bindUntie(int id, String withDrawPass) {
        return Api.post("/api/otc/bank/untie",
                new ReqParams()
                        .put("id", id)
                        .put("withDrawPass", withDrawPass));
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
     * /api/otc/bank/updateBankAccount
     * POST
     * 修改微信/支付宝账户
     */
    public static Api updateBankAccount(String type, String account, String name, String withDrawPass) {
        return Api.post("/api/otc/bank/updateBankAccount",
                new ReqParams()
                        .put("type", type)
                        .put("account", account)
                        .put("name", name)
                        .put("withDrawPass", withDrawPass));
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
     * /api/user/userSafe/bindGoogleKey.do
     * POST
     * 绑定谷歌验证--薛松
     */
    public static Api bindGoogleKey(String googleCode, String drawPass, String googleKey) {
        return Api.post("/api/user/userSafe/bindGoogleKey.do",
                new ReqParams()
                        .put("googleCode", googleCode)
                        .put("drawPass", drawPass)
                        .put("googleKey", googleKey));
    }

    /**
     * /api/user/userSafe/setAuthVerify.do
     * POST
     * 设置google验证码使用场景--薛松(新增谷歌验证码一定要输入(邵文星))
     */
    public static Api setAuthVerify(String authCode, String googleCode) {
        return Api.post("/api/user/userSafe/setAuthVerify.do",
                new ReqParams()
                        .put("authCode", authCode)
                        .put("googleCode", googleCode));
    }

    /**
     * /api/user/user/toBePromoter.do
     * GET
     * 申请成为推广员(陈作衡)
     */
    public static Api toBePromoter() {
        return Api.get("/api/user/user/toBePromoter.do");
    }

    /**
     * /api/user/user/getCurrentPromoterMsg.do
     * GET
     * 获取推广员信息
     */
    public static Api getCurrentPromoterMsg() {
        return Api.get("/api/user/user/getCurrentPromoterMsg.do");
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
     * /api/user/msg/readAll
     * POST
     * 标记全部已读
     */
    public static Api msgReadAll() {
        return Api.get("/api/user/msg/list");
    }

    /**
     * /api/user/msg/read
     * POST
     * 标记已读
     */
    public static Api msgRead(int msgId) {
        return Api.get("/api/user/msg/list",
                new ReqParams()
                        .put("msgId", msgId));
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
     * /api/user/banner/findBannerList
     * GET
     * 查询banner列表
     */
    public static Api findBannerList(String locale) {
        return Api.get("/api/user/banner/findBannerList.do",
                new ReqParams()
                        .put("locale", locale));
    }

    /**
     * /api/user/news/findNewsList.do
     * GET
     * 查询资讯列表
     */
    public static Api findNewsList(int type, String lang, int offset, int size) {
        return Api.get("/api/user/news/findNewsList.do",
                new ReqParams()
                        .put("type", type)
                        .put("lang", lang)
                        .put("offset", offset)
                        .put("size", size));
    }

    /**
     * /api/entrust/pairs/list
     * GET
     * 首页交易对列表
     */
    public static Api entrustPairsList(int page, int pageSize, String suffixSymbol) {
        return Api.get("/api/entrust/pairs/list",
                new ReqParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("suffixSymbol", suffixSymbol));
    }

    /**
     * /api/entrust/selfPairs/indexRiseList
     * GET
     * 首页涨幅榜排名
     */
    public static Api indexRiseList() {
        return Api.get("/api/entrust/selfPairs/indexRiseList");
    }

    /**
     * /api/otc/order/buy
     * POST
     * 下单--购买
     */
    public static Api otcOrderBuy(int id, String cost, String coinCount) {
        return Api.post("/api/otc/order/buy",
                new ReqParams()
                        .put("id", id)
                        .put("cost", cost)
                        .put("coinCount", coinCount));
    }

    /**
     * /api/otc/order/sell
     * POST
     * 下单--出售
     */
    public static Api otcOrderSell(int id, String coinCount, String drawPass) {
        return Api.post("/api/otc/order/sell",
                new ReqParams()
                        .put("id", id)
                        .put("coinCount", coinCount)
                        .put("drawPass", drawPass));
    }

    /**
     * /api/otc/wares/home
     * GET
     * (改)首页列表/个人广告页--(v1.1)
     */
    public static Api otcWaresHome(GetOtcWaresHome getOtcWaresHome) {
        return Api.get("/api/otc/wares/home",
                new ReqParams(GetOtcWaresHome.class, getOtcWaresHome));
    }

    /**
     * /api/otc/wares/list
     * GET
     * 广告管理--(v1.2)
     */
    public static Api otcWaresList(int page, int pageSize) {
        return Api.get("/api/otc/wares/list",
                new ReqParams()
                        .put("page", page)
                        .put("pageSize", pageSize));
    }

    /**
     * /api/otc/wares/updateStatus
     * POST
     * 上架/下架
     */
    public static Api otcWaresUpdateStatus(int id, int status) {
        return Api.post("/api/otc/wares/updateStatus",
                new ReqParams()
                        .put("id", id)
                        .put("status", status));
    }

    /**
     * /api/otc/wares/delete
     * POST
     * 删除广告
     */
    public static Api otcWaresDelete(int id) {
        return Api.post("/api/otc/wares/delete",
                new ReqParams()
                        .put("id", id));
    }

    /**
     * /api/otc/wares/add
     * POST
     * 发布广告--(v1.1)(v1.2)
     */
    public static Api otcWaresAdd(WaresModel otcWaresAdd) {
        return Api.post("/api/otc/wares/add",
                new ReqParams(WaresModel.class, otcWaresAdd));
    }

    /**
     * /api/otc/wares/update
     * POST
     * 编辑广告
     */
    public static Api otcWaresUpdate(WaresModel otcWaresAdd) {
        return Api.post("/api/otc/wares/update",
                new ReqParams(WaresModel.class, otcWaresAdd));
    }

    /**
     * /api/otc/wares/get
     * GET
     * (新)预览广告
     */
    public static Api otcWaresGet(int id) {
        return Api.get("/api/otc/wares/get",
                new ReqParams().put("id", id));
    }

    /**
     * http://ex.esongbai.abc/api/entrust/coin/legalCoin
     * GET
     * 查询可以用法币购买的币种
     */
    public static Api getLegalCoin() {
        return Api.get("/api/entrust/coin/legalCoin");
    }

    /**
     * /api/user/country/currency.do
     * GET
     * 查询法币 币种
     */
    public static Api getCountryCurrency() {
        return Api.get("/api/user/country/currency.do");
    }

    /**
     * /api/otc/quota/price
     * GET
     * 发布广告--最低出售价
     */
    public static Api quotaPrice(String coin, String payCurrency, int type) {
        return Api.get("/api/otc/quota/price",
                new ReqParams()
                        .put("coin", coin)
                        .put("payCurrency", payCurrency)
                        .put("type", type));
    }

    /**
     * /api/otc/account/balance
     * GET
     * 查询法币余额--(v1.1)
     */
    public static Api accountBalance(String coinType) {
        return Api.get("/api/otc/account/balance",
                new ReqParams()
                        .put("coinType", coinType));
    }

    /**
     * /api/otc/order/list
     * GET
     * 订单管理
     */
    public static Api legalCurrencyOrderList(int page, int pageSize, String status) {
        return Api.get("/api/otc/order/list",
                new ReqParams()
                        .put("page", page)
                        .put("pageSize", pageSize)
                        .put("status", status));
    }

    /**
     * /api/otc/order/detail.do
     * GET
     * 订单详情--(v1.1)
     */
    public static Api otcOrderDetail(int id, int direct) {
        return Api.get("/api/otc/order/detail.do",
                new ReqParams()
                        .put("id", id)
                        .put("direct", direct));
    }

    /**
     * /api/otc/order/cancel
     * POST
     * 取消订单
     */
    public static Api otcOrderCancel(int id) {
        return Api.post("/api/otc/order/cancel",
                new ReqParams()
                        .put("id", id));
    }

    /**
     * /api/otc/order/confirm
     * POST
     * 订单状态修改
     */
    public static Api otcOrderConfirm(int id, int status, String drawPass, String googleCode) {
        return Api.post("/api/otc/order/confirm",
                new ReqParams()
                        .put("id", id)
                        .put("status", status)
                        .put("drawPass", drawPass)
                        .put("googleCode", googleCode));
    }

    /**
     * /api/otc/wares/mine
     * GET
     * (改)个人广告主页-个人信息(V1.2)
     */
    public static Api otcWaresMine(String waresId, int orderId, int orientation) {
        return Api.get("/api/otc/wares/mine",
                new ReqParams()
                        .put("waresId", waresId)
                        .put("orderId", orderId)
                        .put("orientation", orientation));
    }

    /**
     * /api/otc/order/payInfo
     * GET
     * 订单支付信息--(v1.1)
     */
    public static Api orderPayInfo(int id) {
        return Api.get("/api/otc/order/payInfo",
                new ReqParams()
                        .put("id", id));
    }

    /**
     * /api/otc/chat/history
     * GET
     * 历史消息
     */
    public static Api otcChatHistory(int waresOrderId, String startTime, int size) {
        return Api.get("/api/otc/chat/history",
                new ReqParams()
                        .put("waresOrderId", waresOrderId)
                        .put("startTime", startTime)
                        .put("size", size));
    }

    /**
     * /api/otc/chat/send
     * POST
     * 发送消息 消息类型 1文字 2图片
     */
    public static Api otcChatSend(String msg, int waresOrderId, int msgType) {
        return Api.post("/api/otc/chat/send",
                new ReqParams()
                        .put("msg", msg)
                        .put("waresOrderId", waresOrderId)
                        .put("msgType", msgType));
    }

    /**
     * /api/otc/chat/user
     * POST
     * 获取用户信息
     */
    public static Api otcChatUser(int waresOrderId) {
        return Api.get("/api/otc/chat/user",
                new ReqParams()
                        .put("waresOrderId", waresOrderId));
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
    public static Api sendOld(AuthSendOld authSendOld) {
        return Api.post("/api/user/userSafe/sendOld.do",
                new ReqParams(AuthSendOld.class, authSendOld));
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
     * <p>
     * /api/user/validate/download/codeImg.do
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
     * <p>
     * /api/user/user/login.do
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
     * <p>
     * /api/user/user/forgetUserPass.do
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
     * <p>
     * /api/entrust/pairs/pairsSimpleList.do
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
     * <p>
     * /api/entrust/pairs/pairsSimpleList.do
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
     * <p>
     * /api/gateway/tcp/websocket.do
     *
     * @return
     */
    public static Api getSocketConfig() {
        return Api.get("/api/gateway/tcp/websocket.do");
    }

    /**
     * 获取服务器系统时间
     * <p>
     * /user/user/getSystemTime.do
     *
     * @return
     */
    public static Api getSystemTime() {
        return Api.get("/api/user/user/getSystemTime.do");
    }

    /**
     * 搜索货币对
     * <p>
     * /api/entrust/pairs/search
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
     * <p>
     * /api/entrust/pairs/option.do
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
     * <p>
     * /api/entrust/pairs/option.do
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

    /**
     * 获取 k 线数据
     * <p>
     * /api/quota/quota/{code}/k
     *
     * @param code
     * @param type
     * @param endTime
     * @return
     */
    public static Api getKlineData(String code, String type, String endTime) {
        return Api.get("/api/quota/quota/{code}/k",
                new ReqParams()
                        .put("code", code)
                        .put("type", type)
                        .put("endTime", endTime)
                        .put("limit", 200));
    }

    /**
     * 获取 分时图数据
     * <p>
     * /api/quota/quota/{code}/trend
     *
     * @param code
     * @param endTime
     * @return
     */
    public static Api getTrendData(String code, String endTime) {
        return Api.get("/api/quota/quota/{code}/k",
                new ReqParams()
                        .put("code", code)
                        .put("type", 1)
                        .put("endTime", endTime)
                        .put("limit", 200));
    }

    /**
     * 交易对每个币种的详情
     * <p>
     * /api/entrust/pairs/pairsDescription
     *
     * @param pair
     * @return
     */
    public static Api getPairDescription(String pair) {
        return Api.get("/api/entrust/pairs/pairsDescription",
                new ReqParams()
                        .put("pairs", pair));
    }

    /**
     * 下单
     * <p>
     * /api/entrust/entrust/order
     *
     * @param makeOrder
     * @return
     */
    public static Api makeOrder(MakeOrder makeOrder) {
        return Api.post("/api/entrust/entrust/order",
                new ReqParams(MakeOrder.class, makeOrder));
    }

    /**
     * 请求委托订单列表
     *
     * @param page
     * @param type
     * @param endDate
     * @param prefixSymbol
     * @param suffixSymbol
     * @return
     */
    public static Api getEntrustOrderList(int page, int type, String endDate, String prefixSymbol, String suffixSymbol) {
        return Api.get("/api/entrust/entrust/mine", new ReqParams()
                .put("pageSize", DEFAULT_PAGE_SIZE)
                .put("page", page)
                .put("current", type)
                .put("endDate", endDate)
                .put("suffixSymbol", suffixSymbol)
                .put("prefixSymbol", prefixSymbol));
    }

    /**
     * 撤单
     *
     * @param orderId
     * @return
     */
    public static Api revokeOrder(String orderId) {
        return Api.post("/api/entrust/entrust/cancel/{id}",
                new ReqParams()
                .put("id", orderId));
    }

    /**
     * 获取客服状态
     *
     * @return
     */
    public static Api getCustomerStatus() {
        return Api.post("/api/user/chat/online.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())));
//        return Api.post("/api/user/chat/online.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())));
    }

    /**
     * 发起客服聊天
     *
     * @return
     */
    public static Api chat() {
        return Api.post("/api/user/chat/connect.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())));
    }

    /**
     * 查询客服聊天历史数据
     * pageDir 0-从时间位置向前查询 1-从时间位置向后查询
     *
     * @return
     */
    public static Api requestChatHistory() {
        return Api.post("/api/user/chat/page.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())).put("startTime", System.currentTimeMillis()).put("pageDir", 0).put("pageSize", 200));
    }

    /**
     * 发送给客服消息
     *
     * @return
     */
    public static Api sendTextChat(String msg) {
        return Api.post("/api/user/chat/send.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())).put("msgtype", CustomServiceChat.MSG_TEXT).put("content", msg));
    }

    /**
     * 发送给客服图片
     *
     * @return
     */
    public static Api sendPhotoChat(String photoAddress) {
        return Api.post("/api/user/chat/send.do", new ReqParams().put("deviceid", AppInfo.getDeviceHardwareId(App.getAppContext())).put("msgtype", CustomServiceChat.MSG_PHOTO).put("content", photoAddress));
    }

    /**
     * @return
     */
    public static Api requestPlatformIntroduce(String code) {
        return Api.get("/api/user/article/getAgreement.do", new ReqParams().put("code", code));
    }

    public interface url {
    }

}
