package com.songbai.futurex.websocket;

public interface Connector {

    void send(String sendMsg);

    void connect();

    boolean isConnecting();

    boolean isConnected();

    void disconnect();

    boolean isDisconnecting();

    void onConnected();

    void onError(String error);

    void onMessage(String receivedMsg);

    void onDisconnected();
}
