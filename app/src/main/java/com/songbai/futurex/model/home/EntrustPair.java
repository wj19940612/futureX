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
         * category : 2
         * highestPrice : 6552.88
         * id : 23
         * lastPrice : 6538.96
         * lastVolume : 0.10763086
         * lowestPrice : 6415.55
         * option : 0
         * pairs : btc_usdt
         * prefixSymbol : btc
         * pricePoint : 2
         * sort : 1
         * suffixSymbol : usdt
         * upDropPrice : 24.98
         * upDropSpeed : 0.0038
         * volume : 893.58863101
         */

        private int category;
        private double highestPrice;
        private int id;
        private double lastPrice;
        private double lastVolume;
        private double lowestPrice;
        private int option;
        private String pairs;
        private String prefixSymbol;
        private int pricePoint;
        private int sort;
        private String suffixSymbol;
        private double upDropPrice;
        private double upDropSpeed;
        private double volume;

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public double getHighestPrice() {
            return highestPrice;
        }

        public void setHighestPrice(double highestPrice) {
            this.highestPrice = highestPrice;
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

        public double getLowestPrice() {
            return lowestPrice;
        }

        public void setLowestPrice(double lowestPrice) {
            this.lowestPrice = lowestPrice;
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

        public int getPricePoint() {
            return pricePoint;
        }

        public void setPricePoint(int pricePoint) {
            this.pricePoint = pricePoint;
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

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }
    }
}
