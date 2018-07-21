package com.songbai.futurex.websocket.msg;

import com.songbai.futurex.websocket.MessageProcessor;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Request;
import com.songbai.futurex.websocket.RequestCode;
import com.songbai.futurex.websocket.Utils;
import com.songbai.futurex.websocket.im.IMParam;

public class MsgProcessor {
    private MessageProcessor mMessageProcessor;
    private OnDataRecListener mOnDataRecListener;

    public MsgProcessor(OnDataRecListener onDataRecListener) {
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
        IMParam param = new IMParam(MsgParam.MSG);
        Request request = Utils.getRequest(RequestCode.SUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }

    public void unregisterMsg() {
        IMParam param = new IMParam(MsgParam.MSG);
        Request request = Utils.getRequest(RequestCode.UNSUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }
}
