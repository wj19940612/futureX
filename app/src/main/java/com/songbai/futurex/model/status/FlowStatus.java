package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface FlowStatus {
    /**
     * 已完成
     */
    int COMPLATED = 1;
    /**
     * 冻结
     */
    int FREEZE = 2;
    /**
     * 提币驳回
     */
    int WITHDRAW_COIN_REJECT = 3;
    /**
     * 系统撤单
     */
    int SYS_WITHDRAW = 4;
}
