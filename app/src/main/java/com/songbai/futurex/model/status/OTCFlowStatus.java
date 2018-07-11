package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface OTCFlowStatus {
    /**
     * 已完成
     */
    int SUCCESS = 1;
    /**
     * 冻结
     */
    int FREEZE = 2;
    /**
     * 冻结扣除
     */
    int FREEZE_DEDUCT = 3;
    /**
     * 取消交易解冻
     */
    int CANCEL_TRADE_FREEZE = 4;
    /**
     * 系统取消交易解冻
     */
    int SYS_CANCEL_TRADE_FREEZE = 5;
    /**
     * 广告下架返还
     */
    int POSTER_OFF_SHELF_RETURN = 6;
}
