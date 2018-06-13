package com.songbai.futurex.model.local;

import com.songbai.futurex.model.mine.CoinInfo;

/**
 * @author yangguangda
 * @date 2018/6/11
 */
public class CoinAddressCountWrapper {
    CoinInfo mCoinInfo;
    int count;

    public CoinInfo getCoinInfo() {
        return mCoinInfo;
    }

    public CoinAddressCountWrapper setCoinInfo(CoinInfo coinInfo) {
        mCoinInfo = coinInfo;
        return this;
    }

    public int getCount() {
        return count;
    }

    public CoinAddressCountWrapper setCount(int count) {
        this.count = count;
        return this;
    }
}
