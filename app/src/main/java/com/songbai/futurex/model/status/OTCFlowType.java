package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface OTCFlowType {
    /**
     * 币币账户转入
     */
    int COIN_ACCOUNT_IN = 3;
    /**
     * 法币账户转出
     */
    int LEGAL_CURRENCY_ACCOUNT_OUT = 4;
    /**
     * OTC交易转入
     */
    int OTC_TRADE_IN = 5;
    /**
     * OTC交易转出
     */
    int OTC_TRADE_OUT = 6;
}
