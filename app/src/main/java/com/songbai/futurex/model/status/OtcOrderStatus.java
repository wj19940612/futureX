package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/29
 */
public interface OtcOrderStatus {
    int ORDER_DIRECT_BUY = 1;//买入
    int ORDER_DIRECT_SELL = 2;//卖出
    int ORDER_CANCLED = 0;// 订单被取消
    int ORDER_UNPAIED = 1;// 下单成功、等待买家付款，
    int ORDER_PAIED = 2;// 买家已经付款、等待买家确认收款、
    int ORDER_COMPLATED = 3;//买家确认收款、等待转币 ，已经成交 ，
}
