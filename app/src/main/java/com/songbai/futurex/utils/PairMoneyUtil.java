package com.songbai.futurex.utils;

import com.songbai.futurex.Preference;
import com.songbai.futurex.model.PairsMoney;
import com.songbai.futurex.model.PairsPrice;

/**
 * @author yangguangda
 * @date 2018/9/20
 */
public class PairMoneyUtil {
    public static double getCoinPrice(String coinType, PairsMoney pairsMoney) {
        switch (coinType) {
            case "btc":
                return getPrice(pairsMoney.getBtc());
            case "eth":
                return getPrice(pairsMoney.getUsdt());
            case "usdt":
                return getPrice(pairsMoney.getUsdt());
            default:
        }
        return 0;
    }

    private static double getPrice(PairsPrice btc) {
        String pricingMethod = Preference.get().getPricingMethod();
        switch (pricingMethod) {
            case "cny":
                return btc.getCNY();
            case "usd":
                return btc.getUSD();
            case "twd":
                return btc.getTWD();
            default:
        }
        return 0;
    }
}
