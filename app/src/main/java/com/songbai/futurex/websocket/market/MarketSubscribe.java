package com.songbai.futurex.websocket.market;

public interface MarketSubscribe {

    void subscribe(String pairCode);

    void subscribeAll();

    void unSubscribeAll();

    void unSubscribe(String pairCode);
}
