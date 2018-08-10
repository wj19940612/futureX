package com.songbai.wrapres.model;

/**
 * Modified by john on 26/04/2018
 * <p>
 * Description: 我的训练记录的数据结构，如果当前这个训练我并没有做，那记录那个对象就为空（说明没有记录），只有训练对象
 * <p>
 * APIs:
 */
public class MyTrainingRecord {

    private TrainingRecord record;
    private Training train;

    public TrainingRecord getRecord() {
        return record;
    }

    public Training getTrain() {
        return train;
    }
}
