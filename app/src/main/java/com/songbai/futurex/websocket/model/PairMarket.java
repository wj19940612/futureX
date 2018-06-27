package com.songbai.futurex.websocket.model;

import java.util.List;

/**
 * Modified by john on 2018/6/20
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class PairMarket {

    private MarketData quota;

    private Deep deep;

    private List<DealData> detail;

    public List<DealData> getDetail() {
        return detail;
    }

    public MarketData getQuota() {
        return quota;
    }

    public Deep getDeep() {
        return deep;
    }

    public static class Deep {
        private List<DeepData> buyDeep;
        private List<DeepData> sellDeep;
        private String paris;

        public List<DeepData> getBuyDeep() {
            return buyDeep;
        }

        public List<DeepData> getSellDeep() {
            return sellDeep;
        }

        public String getParis() {
            return paris;
        }
    }

    public boolean isVaild() {
        return quota != null && deep != null && detail != null;
    }
}
