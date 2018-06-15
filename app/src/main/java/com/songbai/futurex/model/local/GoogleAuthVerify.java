package com.songbai.futurex.model.local;

/**
 * @author yangguangda
 * @date 2018/6/15
 */
public class GoogleAuthVerify {

    /**
     * DRAW : 1
     * SET_DRAW_PASS : 0
     * CNY_TRADE : 0
     */

    private int DRAW;
    private int SET_DRAW_PASS;
    private int CNY_TRADE;

    public int getDRAW() {
        return DRAW;
    }

    public void setDRAW(int DRAW) {
        this.DRAW = DRAW;
    }

    public int getSET_DRAW_PASS() {
        return SET_DRAW_PASS;
    }

    public void setSET_DRAW_PASS(int SET_DRAW_PASS) {
        this.SET_DRAW_PASS = SET_DRAW_PASS;
    }

    public int getCNY_TRADE() {
        return CNY_TRADE;
    }

    public void setCNY_TRADE(int CNY_TRADE) {
        this.CNY_TRADE = CNY_TRADE;
    }
}
