package com.songbai.futurex.model.home;

import java.util.List;

/**
 * @author yangguangda
 * @date 2018/6/20
 */
public class EntrustPair {
    private List<LatelyBean> lately;

    public List<LatelyBean> getLately() {
        return lately;
    }

    public void setLately(List<LatelyBean> lately) {
        this.lately = lately;
    }

    public static class LatelyBean {
        /**
         * category : 1
         * id : 23
         * lastPrice : 6714.4
         * lastVolume : 0.0
         * option : 0
         * pairs : btc_usdt
         * prefixSymbol : btc
         * sort : 1
         * suffixSymbol : usdt
         * upDropPrice : 0.0
         * upDropSpeed : 0.0
         */

        private int category;
        private int id;
        private double lastPrice;
        private double lastVolume;
        private int option;
        private String pairs;
        private String prefixSymbol;
        private int sort;
        private String suffixSymbol;
        private double upDropPrice;
        private double upDropSpeed;

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLastPrice() {
            return lastPrice;
        }

        public void setLastPrice(double lastPrice) {
            this.lastPrice = lastPrice;
        }

        public double getLastVolume() {
            return lastVolume;
        }

        public void setLastVolume(double lastVolume) {
            this.lastVolume = lastVolume;
        }

        public int getOption() {
            return option;
        }

        public void setOption(int option) {
            this.option = option;
        }

        public String getPairs() {
            return pairs;
        }

        public void setPairs(String pairs) {
            this.pairs = pairs;
        }

        public String getPrefixSymbol() {
            return prefixSymbol;
        }

        public void setPrefixSymbol(String prefixSymbol) {
            this.prefixSymbol = prefixSymbol;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getSuffixSymbol() {
            return suffixSymbol;
        }

        public void setSuffixSymbol(String suffixSymbol) {
            this.suffixSymbol = suffixSymbol;
        }

        public double getUpDropPrice() {
            return upDropPrice;
        }

        public void setUpDropPrice(double upDropPrice) {
            this.upDropPrice = upDropPrice;
        }

        public double getUpDropSpeed() {
            return upDropSpeed;
        }

        public void setUpDropSpeed(double upDropSpeed) {
            this.upDropSpeed = upDropSpeed;
        }
    }
}
