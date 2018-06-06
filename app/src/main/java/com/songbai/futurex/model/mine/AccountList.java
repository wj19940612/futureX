package com.songbai.futurex.model.mine;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/6
 */
public class AccountList {
    /**
     * balance : 9,994.00013176
     * account : [{"coinType":"usdt","estimate":"0.00013176","freeze":"0.0000000000","usable":"1.0000000000"},{"coinType":"btc","estimate":"9,994.00000000","freeze":"0.00000000","usable":"9,994.00000000"},{"coinType":"eth","estimate":"0.00000000","freeze":"0.00000000","usable":"0.00000000"}]
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

    public static class AccountBean {
        /**
         * coinType : usdt
         * estimate : 0.00013176
         * freeze : 0.0000000000
         * usable : 1.0000000000
         */

        private String coinType;
        private String estimate;
        private String freeze;
        private String usable;

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getEstimate() {
            return estimate;
        }

        public void setEstimate(String estimate) {
            this.estimate = estimate;
        }

        public String getFreeze() {
            return freeze;
        }

        public void setFreeze(String freeze) {
            this.freeze = freeze;
        }

        public String getUsable() {
            return usable;
        }

        public void setUsable(String usable) {
            this.usable = usable;
        }
    }
}
