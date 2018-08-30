package com.songbai.futurex.model;

/**
 * @author yangguangda
 * @date 2018/8/30
 */
public class NewOrderData {

    /**
     * waresId : 309
     * orderType : 1
     * orderId : 1035101267640049666
     * param : {"coin_amount":"69.20","sync_url":"http://ex.esongbai.abc/","coin_sign":"USDT","sign":"f1b487fa5408a36c62830678152843ee","order_time":"1535622348","pay_card_num":"5885956865563","async_url":"http://223.93.174.145:8004/otc/download/otcback/v1/otc365BackMethod.do","id_card_num":"33252819920804361X","kyc":"2","phone":"18767116422","company_order_num":"1035101267640049666","appkey":"2","id_card_type":"1","username":"羊羊羊"}
     * id : 945
     * targetUrl : http://open.otc360.top/v1/third/merchant_buy
     */

    private int waresId;
    private int orderType;
    private String orderId;
    private ParamBean param;
    private int id;
    private String targetUrl;

    public int getWaresId() {
        return waresId;
    }

    public void setWaresId(int waresId) {
        this.waresId = waresId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
