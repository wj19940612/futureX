package com.songbai.futurex.websocket.notification;

import android.util.Log;
import android.widget.LinearLayout;

import com.songbai.futurex.websocket.MessageProcessor;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Request;
import com.songbai.futurex.websocket.RequestCode;
import com.songbai.futurex.websocket.Utils;

public class NotificationProcessor {

    private MessageProcessor mMessageProcessor;
    private OnDataRecListener mOnDataRecListener;

    public NotificationProcessor(OnDataRecListener mOnDataRecListener) {
        this.mMessageProcessor = MessageProcessor.get();
        this.mOnDataRecListener = mOnDataRecListener;
    }

    public void resume() {
        mMessageProcessor.addOnDataRecListener(mOnDataRecListener);
    }

    public void pause() {
        mMessageProcessor.removeOnDataRecListener(mOnDataRecListener);
    }

    public void registerMsg() {
        NotificationParam param = new NotificationParam(NotificationParam.NOTIFICATION);
        Request request = Utils.getRequest(RequestCode.SUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }

    public void unRegisterMsg() {
        NotificationParam param = new NotificationParam(NotificationParam.NOTIFICATION);
        Request request = Utils.getRequest(RequestCode.UNSUBSCRIBE_BUSINESS, param);
        mMessageProcessor.send(request);
    }
}
