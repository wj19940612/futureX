package com.songbai.futurex.model;

import com.songbai.futurex.model.mine.BankCardBean;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class OtcOrderDetail {

    /**
     * bank : [{"bind":0,"blurCardNumber":"18*******20","blurRealName":"*1","cardNumber":"18*******20","payType":1,"realName":"*1"}]
     * order : {"buyerId":100392,"buyerName":"mxmx","buyerPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20180627115037357/100392i1530071437358.jpg?x-oss-process=image/resize,m_fill,h_200,w_200","coinSymbol":"btc","conditionType":"auth","conditionValue":"2","delay":0,"fixedPrice":112,"orderAmount":1111,"orderCount":9.919642857143,"orderId":"1012153639990292481","orderPrice":112,"orderTime":1530151207000,"payCurrency":"twd","priceType":1,"quotaPrice":112,"sellerId":100420,"status":0}
     */

    private OrderBean order;
    private List<BankCardBean> bank;

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public List<BankCardBean> getBank() {
        return bank;
    }

    public void setBank(List<BankCardBean> bank) {
        this.bank = bank;
    }
}
