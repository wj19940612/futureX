package com.songbai.futurex.websocket.im;

import com.songbai.futurex.websocket.MessageProcessor;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Request;
import com.songbai.futurex.websocket.Utils;

public class IMProcessor {
    private MessageProcessor mMessageProcessor;
    private OnDataRecListener mOnDataRecListener;

    public IMProcessor(OnDataRecListener onDataRecListener) {
        mMessageProcessor = MessageProcessor.get();
        mOnDataRecListener = onDataRecListener;
    }

    public void resume() {
        mMessageProcessor.addOnDataRecListener(mOnDataRecListener);
    }

    public void pause() {
        mMessageProcessor.removeOnDataRecListener(mOnDataRecListener);
    }

    public void registerMsg() {
        IMParam param = new IMParam(IMParam.CHAT);
        Request request = Utils.getRequest(param);
        mMessageProcessor.send(request);
    }

    public void registerOffline() {
        IMParam param = new IMParam(IMParam.CUSOOFFLINE);
        Request request = Utils.getRequest(param);
        mMessageProcessor.send(request);
    }

    public void registerOnline() {
        IMParam param = new IMParam(IMParam.CUSOONLINE);
        Request request = Utils.getRequest(param);
        mMessageProcessor.send(request);
    }
}
