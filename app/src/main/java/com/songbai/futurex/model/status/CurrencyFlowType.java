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
    /**
     * 周期解锁
     */
    int PERIODIC_RELEASE = 12;
    /**
     * 特殊交易
     */
    int SPECIAL_TRADE = 13;
    /**
     * 手续费返还
     */
    int CASHBACK = 14;
    /**
     * 邀请奖励
     */
    int INVT_REWARD = 15;
    /**
     * 收入分配
     */
    int DISTRIBUTED_REV = 16;
    /**
     * 挖矿解锁
     * 20没了和17重复
     */
    int RELEASED_BFB = 17;
    /**
     * 挖矿奖励
     */
    int MINERS_REWAR = 18;
    /**
     * 手续费分成
     */
    int SHARED_FEE = 19;
    /**
     * 认购交易
     */
    int SUBSCRIPTION = 21;
    /**
     * 活动发放
     */
    int EVENT_GRANT= 22;
    /**
     * 活动奖励
     */
    int EVENT_AWARD = 23;
}
