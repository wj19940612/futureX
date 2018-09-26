package com.songbai.futurex.model;

import com.songbai.futurex.websocket.model.PairsPrice;

/**
 * @author yangguangda
 * @date 2018/9/20
 */
public class PairsMoney {

    /**
     * btc : {"TWD":195725.61,"USD":6396.26,"CNY":43558.54}
     * eth : {"TWD":6399.99,"USD":209.15,"CNY":1424.31}
     * usdt : {"TWD":30.6,"USD":1,"CNY":6.81}
     */

    private PairsPrice btc;
    private PairsPrice eth;
    private PairsPrice usdt;

    public PairsPrice getBtc() {
        return btc;
    }

    public void setBtc(PairsPrice btc) {
        this.btc = btc;
    }

    public PairsPrice getEth() {
        return eth;
    }

    public void setEth(PairsPrice eth) {
        this.eth = eth;
    }

    public PairsPrice getUsdt() {
        return usdt;
    }

    public void setUsdt(PairsPrice usdt) {
        this.usdt = usdt;
    }
}
