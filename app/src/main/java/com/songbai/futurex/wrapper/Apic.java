package com.songbai.futurex.wrapper;

import com.sbai.httplib.ReqParams;
import com.songbai.futurex.App;
import com.songbai.futurex.http.Api;
import com.songbai.futurex.utils.AppInfo;

/**
 * Modified by john on 2018/6/22
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class Apic {
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String BC_NEWS_HOST = "http://news.ailemi.com";
    public static final String LEMI_HOST = "https://lemi.ailemi.com";

    public static Api syncSystemTime() {
        return Api.get("/user/user/getSystemTime.do").host(BC_NEWS_HOST);
    }


    public static String getServiceQQ(String serviceQQ) {
//        if (qqType == ChannelServiceInfo.QQ_TYPE_NORMAL) {
//            return "mqqwpa://im/chat?chat_type=wpa&uin=" + serviceQQ + "&version=1";
//        }
//        return "mqqwpa://im/chat?chat_type=crm&uin=" + serviceQQ + "&version=1";
        return "mqqwpa://im/chat?chat_type=wpa&uin=" + serviceQQ + "&version=1";
    }

    public static Api requestHost() {
        return Api.get("").host("http://bitexcn.oss-cn-shanghai.aliyuncs.com/hosts/cn.txt");
    }

    /**
     * 修改头像
     *
     * @param picPath String 图片网址
     * @return
     */
    public static Api updateUserHeadImagePath(String picPath) {
        return Api.post("/user/user/updatePicLand.do",
                new ReqParams()
                        .put("picLand", picPath))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * 更新用户信息
     *
     * @param age
     * @param land
     * @param userSex
     * @return
     */
    public static Api updateUserInfo(Integer age, String land, Integer userSex) {
        return Api.post("/user/user/updateUser.do", new ReqParams()
                .put("age", age)
                .put("land", land)
                .put("userSex", userSex)
                .put("longitude", "")
                .put("latitude", ""))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    public static Api getBannerList() {
        return Api.get("/api/news-user/banner/findBannerList.do")
                .host(BC_NEWS_HOST);
    }

    public static Api getCurrencyPairList() {
        return Api.get("/api/news-quota/variety/list",
                new ReqParams()
                        .put("page", 0)
                        .put("size", 200)
                        .put("exchangeCode", "gate.io"))
                .host(BC_NEWS_HOST);
    }

    public static Api getMarkListData() {
        return Api.get("/api/news-quota/quota/list",
                new ReqParams()
                        .put("exchangeCode", "gate.io"))
                .host(BC_NEWS_HOST);
    }

    public static Api requestOperationSetting(String type) {
        return Api.get("/api/news-user/dictionary/json.do",
                new ReqParams()
                        .put("type", type))
                .host(BC_NEWS_HOST);
    }

    /**
     * 查询用户反馈数据
     */
    public static Api getFeedbackList(int page) {
        return Api.get("/api/news-user/feedback/page",
                new ReqParams()
                        .put("page", page)
                        .put("size", com.songbai.futurex.http.Apic.DEFAULT_PAGE_SIZE)
                        .put("deviceId", ""))
                .host(BC_NEWS_HOST);
    }

    /**
     * 提交用户反馈数据
     */
    public static Api sendFeedback(String content, int contentType) {
        return Api.post("/api/news-user/feedback/add",
                new ReqParams()
                        .put("content", content)
                        .put("contentType", contentType)
                        .put("deviceId", ""))
                .host(BC_NEWS_HOST);
    }

    public static Api logout() {
        return Api.post("/user/out/logout.do")
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * GET
     * 用户--用户详情--薛松
     */
    public static Api requestUserInfo() {
        return Api.get("/user/user/findUserInfo.do")
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

//    public static Api reqUseWxInfo() {
//        return Api.post("/api/news-user/user/use/wx")
//                .host(BC_NEWS_HOST);
//    }

//    public static Api requestWeChatLogin(String openId) {
//        return Api.post("/api/news-user/login/wechat/{openId}",
//                new ReqParams()
//                        .put("openId", openId)
//                        .put("deviceId", "")
//                        .put("platform", 0)
//                        .put("source", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL")))
//                .host(BC_NEWS_HOST);
//    }

    /**
     * /api/news-user/login/quick/{phone}/{msgCode}
     * <p>用户--手机验证码快速登录--薛松</p>
     *
     * @param authCode 短信验证码
     * @param phone    手机
     *                 deviceId 设备id
     *                 platform 平台 0-安卓 1-ios
     * @return
     */
    public static Api requestAuthCodeLogin(String phone, String authCode) {
        return Api.post("/user/registerLogin/quickLogin.do",
                new ReqParams()
                        .put("phone", phone)
                        .put("msgCode", authCode)
                        .put("deviceId", "")
                        .put("platform", 0)
                        .put("source", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL")))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /api/news-user/login/quick/{phone}/{msgCode}
     * <p>用户--手机验证码快速登录--薛松</p>
     * <p>快捷登入(for 微信)</p>
     *
     * @param authCode 短信验证码
     * @param phone    手机
     *                 deviceId 设备id
     *                 platform 平台 0-安卓 1-ios
     * @return
     */
    public static Api requestAuthCodeLogin(String phone, String authCode, String openId, String name, String iconUrl, int sex) {
        return Api.post("/api/news-user/login/quick/{phone}/{msgCode}",
                new ReqParams()
                        .put("phone", phone)
                        .put("msgCode", authCode)
                        .put("deviceId", "")
                        .put("platform", 0)
                        .put("channel", AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"))
                        .put("openId", openId)
                        .put("name", name)
                        .put("iconUrl", iconUrl)
                        .put("sex", sex))
                .host(BC_NEWS_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /api/news-user/login/msg/{phone}
     * <p>用户--发送短信验证码--薛松</p>
     *
     * @param phone
     * @return
     */
    public static Api getAuthCode(String phone) {
        return getAuthCode(phone, null);
    }


    /**
     * /api/news-user/login/msg/{phone}
     * <p>用户--发送短信验证码--薛松</p>
     *
     * @param phone
     * @param imgCode
     * @return
     */
    public static Api getAuthCode(String phone, String imgCode) {
        return Api.post("/user/registerLogin/sendMsgCode.do",
                new ReqParams()
                        .put("phone", phone)
                        .put("imgCode", imgCode))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /api/news-user/login/get/image/{phone}
     * <p>用户--获取图片验证码--薛松</p>
     *
     * @param phone
     * @return
     */
    public static String getImageAuthCode(String phone) {
        return LEMI_HOST + "/user/registerLogin/getRegImage.do?userPhone=" + phone;
    }

    /**
     * PUT
     * 用户--修改用户信息--薛松
     *
     * @return
     */
    public static Api submitUserIntroduce(String introduction) {
        return Api.post("/api/news-user/user/update",
                new ReqParams()
                        .put("introduction", introduction))
                .host(BC_NEWS_HOST);
    }

    public static Api submitNickName(String nickName) {
        return Api.post("/user/user/updateUserName.do",
                new ReqParams()
                        .put("userName", nickName))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /api/news-info/info/information.do
     * <p>快讯列表-----齐慕伟</p>
     *
     * @param time
     * @param status 0 请求小于时间戳的数据, 1 请求大于时间戳的数据
     * @return
     */
    public static Api getNewsFlash(long time, int status) {
        return Api.get("/api/news-info/info/information.do",
                new ReqParams()
                        .put("time", time)
                        .put("status", status))
                .host(BC_NEWS_HOST);
    }

    /**
     * 获取训练列表
     *
     * @return
     */
    public static Api getTrainingList() {
        return Api.get("/train/train/list.do",
                new ReqParams()
                        .put("page", 0)
                        .put("pageSize", 100))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * 获取训练详情
     *
     * @param trainId
     * @return
     */
    public static Api getTrainingDetail(int trainId) {
        return Api.get("/train/train/detail.do",
                new ReqParams()
                        .put("trainId", trainId))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * 获取 训练的内容
     *
     * @param trainId
     * @return
     */
    public static Api getTrainingContent(int trainId) {
        return Api.get("/train/train/start.do",
                new ReqParams()
                        .put("trainId", trainId))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * 获取我的训练记录
     *
     * @param trainId
     * @return
     */
    public static Api getMyTrainingRecord(int trainId) {
        return Api.get("/train/train/userTrain.do",
                new ReqParams()
                        .put("trainId", trainId))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * 提交 训练结果
     *
     * @param result
     * @return
     */
    public static Api submitTrainingResult(String result) {
        return Api.post("/train/train/finish.do",
                new ReqParams()
                        .put("data", result))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /user/vest/getVestMsg.do
     * GET
     * 获取马甲包信息接口2.2.0(陈作衡)
     *
     * @return
     */
    public static Api requestAppType() {
        return Api.get("/api/user/appVersion/vest.do",
                new ReqParams()
                        .put("identify", "cn")
                        .put("version", AppInfo.getVersionName(App.getAppContext()))
                        .put("platform", 1));
    }

    /**
     * /user/upload/image.do
     * POST
     * 上传-图片上传（yhj）
     *
     * @param base64ImageString
     * @return
     */
    public static Api uploadImage(String base64ImageString) {
        return Api.post("/user/upload/image.do",
                new ReqParams()
                        .put("picture", base64ImageString))
                .host(LEMI_HOST)
                .header("Cookie", LocalWrapUser.getUser().getToken());
    }

    /**
     * /api/news-quota/quota/{code}/trend
     * <p>获取分时图数据</p>
     *
     * @param code
     * @param exchangeCode
     * @param endTime
     */
    public static Api reqTrendData(String code, String exchangeCode, String endTime) {
        return Api.get("/api/news-quota/quota/{code}/trend",
                new ReqParams()
                        .put("code", code)
                        .put("exchangeCode", exchangeCode)
                        .put("endTime", endTime))
                .host(BC_NEWS_HOST);
    }

    /**
     * /api/news-quota/quota/{code}/k
     * <p>获取 k 线数据</p>
     *
     * @param code
     * @param exchangeCode
     * @param klineType
     * @return
     */
    public static Api reqKlineMarket(String code, String exchangeCode, String klineType, String endTime) {
        return Api.get("/api/news-quota/quota/{code}/k",
                new ReqParams()
                        .put("code", code)
                        .put("exchangeCode", exchangeCode)
                        .put("type", klineType)
                        .put("endTime", endTime)
                        .put("limit", 100))
                .host(BC_NEWS_HOST);
    }

    /**
     * /api/news-quota/quota/{code}
     * <p>请求单个数字货币的行情</p>
     *
     * @param code
     * @param exchangeCode
     * @return
     */
    public static Api reqSingleMarket(String code, String exchangeCode) {
        return Api.get("/api/news-quota/quota/{code}",
                new ReqParams()
                        .put("code", code)
                        .put("exchangeCode", exchangeCode))
                .host(BC_NEWS_HOST);
    }


    public interface url {
        String SHARE_NEWS = Api.getFixedHost() + "/news/share/index.html?id=%s";

        String QR_CODE = Api.getFixedHost() + "/qc.png";

        //关于我们界面链接
        String WEB_URI_ABOUT_PAGE = Api.getFixedHost() + "/news/banner/about.html?version=%s";
        //用户协议
        String WEB_URI_AGREEMENT = Api.getFixedHost() + "/news/banner/agreement.html?code=1";
    }

}
