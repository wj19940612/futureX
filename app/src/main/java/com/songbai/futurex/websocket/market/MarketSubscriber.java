package com.songbai.futurex.websocket.market;

import com.songbai.futurex.websocket.MessageProcessor;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Request;
import com.songbai.futurex.websocket.TopicParam;
import com.songbai.futurex.websocket.Utils;


public class MarketSubscriber implements MarketSubscribe {

    private MessageProcessor mMessageProcessor;
    private OnDataRecListener mOnDataRecListener;

    public MarketSubscriber(OnDataRecListener onDataRecListener) {
        mMessageProcessor = MessageProcessor.get();
        mOnDataRecListener = onDataRecListener;
    }

    private void subscribe(Request request) {
        mMessageProcessor.send(request);
    }

    public void resume() {
        mMessageProcessor.addOnDataRecListener(mOnDataRecListener);
    }

    public void pause() {
        mMessageProcessor.removeOnDataRecListener(mOnDataRecListener);
    }

    @Override
    public void subscribe(String pairCode) {
        TopicParam param = new TopicParam(TopicParam.SINGLE + pairCode);
        Request request = Utils.getRequest(Request.SUBSCRIBE_BUSINESS, param);
        subscribe(request);
    }

    @Override
    public void subscribeAll() {
        TopicParam param = new TopicParam(TopicParam.ALL);
        Request request = Utils.getRequest(Request.SUBSCRIBE_BUSINESS, param);
        subscribe(request);
    }

    @Override
    public void unSubscribeAll() {
        TopicParam param = new TopicParam(TopicParam.ALL);
        Request request = Utils.getRequest(Request.UNSUBSCRIBE_BUSINESS, param);
        subscribe(request);
    }

    @Override
    public void unSubscribe(String pairCode) {
        TopicParam param = new TopicParam(TopicParam.SINGLE + pairCode);
        Request request = Utils.getRequest(Request.UNSUBSCRIBE_BUSINESS, param);
        subscribe(request);
    }
}
