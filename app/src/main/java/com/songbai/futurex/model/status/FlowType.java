package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface FlowType {
    /**
     * 充币
     */
    int RECHARGE = 1;
    /**
     * 提现
     */
    int DRAW_CASH = -1;
    /**
     * 委托买入
     */
    int BUY_ORDER = 2;
    /**
     * 委托卖出
     */
    int SELL_ORDER = 3;
    /**
     * OTC交易转出
     */
    int OTC_TRANSFER_OUT = 4;
    /**
     * 提币手续费
     */
    int DRAW_COIN_FEE = 5;
    /**
     * 交易手续费
     */
    int DEAL_FEE = 6;
    /**
     * 推广账户转入
     */
    int PROMOTER_ACCOUNT_TRANSFER_INTO = 7;
}
