package com.songbai.futurex.model.local;

import com.songbai.futurex.model.mine.BankCardBean;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/30
 */
public class OtcBankInfoMsg {
    private int mTradeDirect;

    public List<BankCardBean> getBankCardBeans() {
        return mBankCardBeans;
    }

    public int getTradeDirect() {
        return mTradeDirect;
    }

    public void setTradeDirect(int tradeDirect) {
        mTradeDirect = tradeDirect;
    }

    public void setBankCardBeans(List<BankCardBean> bankCardBeans) {
        mBankCardBeans = bankCardBeans;
    }

    private List<BankCardBean> mBankCardBeans;

}
