package com.songbai.futurex.websocket.otc;

import com.songbai.futurex.websocket.MessageProcessor;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Request;
import com.songbai.futurex.websocket.RequestCode;
import com.songbai.futurex.websocket.Utils;

public class OtcProcessor {
    private MessageProcessor mMessageProcessor;
    private OnDataRecListener mOnDataRecListener;

    public OtcProcessor(OnDataRecListener onDataRecListener) {
        mMessageProcessor = MessageProcessor.get();
        mOnDataRecListener = onDataRecListener;
    }

    public void resume() {
        mMessageProcessor.addOnDataRecListener(mOnDataRecListener);
    }

    public void pause() {
        mMessageProcessor.removeOnDataRecListener(mOnDataRecListener);
    }

    public void registerMsg(int id) {
        OtcParam param = new OtcParam(OtcParam.OTC_CHAT + id);
        Request request = Utils.getRequest(RequestCode.SUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }

    public void registerEntrust() {
        OtcParam param = new OtcParam(OtcParam.OTC_ENTRUST);
        Request request = Utils.getRequest(RequestCode.SUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }
}
