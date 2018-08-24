package com.songbai.futurex.model.status;

/**
 * @author yangguangda
 * @date 2018/8/16
 */
public interface IoStatus {
    /**
     * 审核中
     */
    int AUDITING = 1;
    /**
     * 审核失败
     */
    int REJECTED = 2;
    /**
     * 确认中
     */
    int CONFIRMING = 3;
    /**
     * 成功
     */
    int SUCCEEDED = 4;
    /**
     * 确认失败
     */
    int FAILED = 5;
}
