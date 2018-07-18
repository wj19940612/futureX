package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface CurrencyFlowType {
    /**
     * 充币
     */
    int DEPOSITE = 1;
    /**
     * 提现
     */
    int DRAW = -1;
    /**
     * 委托买入
     */
    int ENTRUST_BUY = 2;
    /**
     * 委托卖出
     */
    int ENTRUST_SELL = 3;
    /**
     * OTC交易转出
     */
    int OTC_TRADE_OUT = 4;
    /**
     * 提币手续费
     */
    int DRAW_FEE = 5;
    /**
     * 交易手续费
     */
    int TRADE_FEE = 6;
    /**
     * 推广账户转入
     */
    int PROMOTER_TO = 7;
    /**
     * OTC交易转入
     */
    int OTC_TRADE_IN = 8;
    /**
     * 代理账户转入
     */
    int AGENCY_TO = 9;
    /**
     * 法币账户转入
     */
    int LEGAL_ACCOUNT_IN = 10;
    /**
     * 币币账户转出
     */
    int COIN_ACCOUNT_OUT = 11;
}
