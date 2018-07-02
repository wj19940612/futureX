package com.songbai.futurex.model.status;

/**
 * Modified by john on 2018/6/30
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public interface OrderStatus {
    /**
     * 1 等待成交
     * 2 部分成交
     * 3 全部成交
     * 4 撤单中
     * 5 已撤单
     * 6 系统撤单
     * 7 部分成交已撤
     */

    int PENDING_DEAL = 1;
    int PART_DEAL = 2;
    int ALL_DEAL = 3;
    int REVOKING = 4;
    int REVOKED = 5;
    int SYSTEM_REVOKED = 6;
    int PART_DEAL_PART_REVOKED = 7;
}
