package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/8/30
 */
public class OTC365Data {

    /**
     * param : {"coin_amount":"19.000000000000","sync_url":"http://ex.esongbai.abc/","coin_sign":"USDT","sign":"1f7bf89084229ddbedad206aa8d84cce","order_time":"1535526632","pay_card_num":"646466467","async_url":"http://223.93.174.145:8004/otc/download/otcback/v1/otc365BackMethod.do","id_card_num":"11","kyc":"2","phone":"18767116420","company_order_num":"1034699803855716354","appkey":"2","id_card_type":"2","username":"11"}
     * targetUrl : http://open.otc360.top/v1/third/merchant_buy
     */

    private ParamBean param;
    private String targetUrl;

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
