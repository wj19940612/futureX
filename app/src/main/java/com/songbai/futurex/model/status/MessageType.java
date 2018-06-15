package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/15
 */
public interface MessageType {
    /**
     * 0 充值地址变化
     * <p>
     * 您的充值地址发生了变化， 赶紧去看看
     */
    int PAY_ADDR_CHANGE = 0;

    /**
     * 1 otc 买家下单
     */
    int OTC_BUY_ORDER = 1;

    /**
     * 2 otc 卖家下单
     * <p>
     * 您的广告已有卖家下单，赶紧去看看吧>>
     */
    int OTC_SELL_ORDER = 2;

    /**
     * 3 otc 买家支付
     * <p>
     * 您的法币交易订单，买家已付款，请尽快做确认>>
     */
    int OTC_BUY_PAY = 3;

    /**
     * 4 otc 卖家确认收款并转币
     * <p>
     * 您的法币交易订单，卖家已确认收款，并转币，请查收>>
     */
    int OTC_SELL_PAY = 4;

    /**
     * 5 OTC 交易聊天消息
     * <p>
     * 您的订单已经回复,赶紧去看看吧>>
     */
    int OTC_ORDER_MSG = 5;

    /**
     * 6 高级认证失败
     * <p>
     * 您提交的认证信息审核失败，点击查看详情
     */
    int USER_AUTH_FAIL = 6;
}
