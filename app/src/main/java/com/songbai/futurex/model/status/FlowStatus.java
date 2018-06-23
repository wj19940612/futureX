package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/6/14
 */
public interface FlowStatus {
    /**
     * 已完成
     */
    int SUCCESS = 1;
    /**
     * 冻结
     */
    int FREEZE = 2;
    /**
     * 提币驳回
     */
    int DRAW_REJECT = 3;
    /**
     * 系统撤单
     */
    int ENTRUS_RETURN = 4;
    /**
     * 冻结扣除
     */
    int FREEZE_DEDUCT = 5;
    /**
     * 系统撤单
     */
    int ENTRUSE_RETURN_SYS = 6;
    /**
     * 冻结返还
     */
    int FREEZE_RETURN = 7;
}
