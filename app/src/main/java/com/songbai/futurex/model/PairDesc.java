package com.songbai.futurex.model;

/**
 * Modified by john on 2018/6/20
 * <p>
 * Description: 交易对描述
 * <p>
 * APIs:
 */
public class PairDesc {

    /**
     * suffixSymbol : {"advert":0,"balancePoint":8,"category":0,"classify":"btc","ctoc":0,"dayWithdrawAmount":"600000","maxEntrustAmount":"283000000","maxWithdrawAmount":"200","minEntrustAmount":"0.01","minMergeAmount":"1","minRechargeAmount":"0","minWithdrawAmount":"0.1","sort":2,"status":1,"supply":2830000000,"symbol":"usdt","withdrawRate":0.1}
     * prefixSymbol : {"advert":0,"balancePoint":8,"category":0,"classify":"btc","ctoc":0,"dayWithdrawAmount":"200","maxEntrustAmount":"2100000","maxWithdrawAmount":"200","minEntrustAmount":"0.01","minMergeAmount":"0","minRechargeAmount":"0","minWithdrawAmount":"0.00001","sort":1,"status":1,"supply":21000000,"symbol":"btc","withdrawRate":1.0E-5}
     * pairs : {"category":1,"createTime":1526908228000,"dealRate":1,"deep":"1","deleted":0,"entrustRate":1,"fresh":0,"id":23,"maxEntrustPrice":0,"minEntrustPrice":0,"option":0,"pairs":"btc_usdt","prefixSymbol":"btc","pricePoint":2,"sort":1,"status":1,"suffixSymbol":"usdt","updateTime":1528703743000}
     */

    private SuffixSymbol suffixSymbol;
    private PrefixSymbol prefixSymbol;
    private Pairs pairs;

    public SuffixSymbol getSuffixSymbol() {
        return suffixSymbol;
    }

    public PrefixSymbol getPrefixSymbol() {
        return prefixSymbol;
    }

    public Pairs getPairs() {
        return pairs;
    }

    public static class SuffixSymbol {
        /**
         * advert : 0
         * balancePoint : 8
         * category : 0
         * classify : btc
         * ctoc : 0
         * dayWithdrawAmount : 600000
         * maxEntrustAmount : 283000000
         * maxWithdrawAmount : 200
         * minEntrustAmount : 0.01
         * minMergeAmount : 1
         * minRechargeAmount : 0
         * minWithdrawAmount : 0.1
         * sort : 2
         * status : 1
         * supply : 2830000000
         * symbol : usdt
         * withdrawRate : 0.1
         */

        private int advert;
        private int balancePoint;
        private int category;
        private String classify;
        private int ctoc;
        private String dayWithdrawAmount;
        private String maxEntrustAmount;
        private String maxWithdrawAmount;
        private String minEntrustAmount;
        private String minMergeAmount;
        private String minRechargeAmount;
        private String minWithdrawAmount;
        private int sort;
        private int status;
        private long supply;
        private String symbol;
        private double withdrawRate;
    }

    public static class PrefixSymbol {
        /**
         * advert : 0
         * balancePoint : 8
         * category : 0
         * classify : btc
         * ctoc : 0
         * dayWithdrawAmount : 200
         * maxEntrustAmount : 2100000
         * maxWithdrawAmount : 200
         * minEntrustAmount : 0.01
         * minMergeAmount : 0
         * minRechargeAmount : 0
         * minWithdrawAmount : 0.00001
         * sort : 1
         * status : 1
         * supply : 21000000
         * symbol : btc
         * withdrawRate : 1.0E-5
         */

        private int advert;
        private int balancePoint;
        private int category;
        private String classify;
        private int ctoc;
        private String dayWithdrawAmount;
        private String maxEntrustAmount;
        private String maxWithdrawAmount;
        private String minEntrustAmount;
        private String minMergeAmount;
        private String minRechargeAmount;
        private String minWithdrawAmount;
        private int sort;
        private int status;
        private int supply;
        private String symbol;
        private double withdrawRate;
    }

    public static class Pairs {
        /**
         * category : 1
         * createTime : 1526908228000
         * dealRate : 1
         * deep : 1
         * deleted : 0
         * entrustRate : 1
         * fresh : 0
         * id : 23
         * maxEntrustPrice : 0
         * minEntrustPrice : 0
         * option : 0
         * pairs : btc_usdt
         * prefixSymbol : btc
         * pricePoint : 2
         * sort : 1
         * status : 1
         * suffixSymbol : usdt
         * updateTime : 1528703743000
         */

        private int category;
        private long createTime;
        private double dealRate;
        private String deep;
        private int deleted;
        private double entrustRate;
        private double maxEntrustPrice;

        private double minEntrustPrice;
        private int option;
        private String pairs;
        private String prefixSymbol;
        private int pricePoint;
        private int sort;
        private int status;
        private String suffixSymbol;
        private long updateTime;

        public int getCategory() {
            return category;
        }

        public long getCreateTime() {
            return createTime;
        }

        public double getDealRate() {
            return dealRate;
        }

        public String getDeep() {
            return deep;
        }

        public int getDeleted() {
            return deleted;
        }

        public double getEntrustRate() {
            return entrustRate;
        }

        public double getMaxEntrustPrice() {
            return maxEntrustPrice;
        }

        public double getMinEntrustPrice() {
            return minEntrustPrice;
        }

        public int getOption() {
            return option;
        }

        public String getPairs() {
            return pairs;
        }

        public String getPrefixSymbol() {
            return prefixSymbol;
        }

        public int getPricePoint() {
            return pricePoint;
        }

        public int getSort() {
            return sort;
        }

        public int getStatus() {
            return status;
        }

        public String getSuffixSymbol() {
            return suffixSymbol;
        }

        public long getUpdateTime() {
            return updateTime;
        }
    }
}
