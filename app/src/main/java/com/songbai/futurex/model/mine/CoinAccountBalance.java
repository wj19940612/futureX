package com.songbai.futurex.model.mine;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/7/12
 */
public class CoinAccountBalance {

    /**
     * account : [{"ableCoin":"0.00000000","coinType":"btc","estimateBtc":"0.00000000","freezeCoin":"0.00000000","isCanDraw":1,"legal":1,"pairs":["btc_usdt"],"recharge":1}]
     * balance : 0.00000000
     */

    private String balance;
    private List<AccountBean> account;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public List<AccountBean> getAccount() {
        return account;
    }

    public void setAccount(List<AccountBean> account) {
        this.account = account;
    }
}
