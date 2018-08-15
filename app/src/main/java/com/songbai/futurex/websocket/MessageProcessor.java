package com.songbai.futurex.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.songbai.futurex.App;
import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.utils.AppInfo;
import com.songbai.futurex.utils.Network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Modified by john on 15/03/2018
 * <p>
 * Description: 消息集中处理器
 * <p>
 * APIs:
 */
public class MessageProcessor implements SimpleConnector.OnConnectListener {

    private static final String TAG = "WebSocket";
    private static final String WS_PREFIX = BuildConfig.IS_PROD ? "wss://" : "ws://";

    private SimpleConnector mConnector;
    private Queue<Request> mWaitingReqList;
    private Gson mGson;
    private Handler mHandler;
    private List<OnDataRecListener> mOnDataRecListeners;
    private boolean mAddressRequesting;

    private static MessageProcessor sMessageProcessor;

    public static MessageProcessor get() {
        if (sMessageProcessor == null) {
            sMessageProcessor = new MessageProcessor();
        }
        return sMessageProcessor;
    }

    private MessageProcessor() {
        mConnector = SimpleConnector.get();
        mConnector.setOnConnectListener(this);

        mWaitingReqList = new LinkedList<>();
        mGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        mHandler = new Handler(Looper.getMainLooper());
        mOnDataRecListeners = new ArrayList<>();
    }

    public void connect() {
        if (isConnected() || isConnecting()) return;

        Apic.getSocketConfig().callback(new Callback<WsConfig>() {
            @Override
            protected void onRespSuccess(WsConfig resp) {
                connect(resp);
            }

            @Override
            public void onStart() {
                super.onStart();
                mAddressRequesting = true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mAddressRequesting = false;
            }
        }).fireFreely();
    }

    private void connect(WsConfig data) {
        List<RemoteHost> hosts = data.http;
        if (BuildConfig.IS_PROD) {
            hosts = data.https;
        }
        if (!hosts.isEmpty()) {
            int index = new Random().nextInt(hosts.size());
            String host = hosts.get(index).host;
            String port = hosts.get(index).port;
            mConnector.setUri(WS_PREFIX + host + ":" + port);
            mConnector.connect();
        }
    }

    private boolean isConnecting() {
        return mAddressRequesting || mConnector.isConnecting();
    }

    private boolean isConnected() {
        return mConnector.isConnected();
    }

    public void addOnDataRecListener(OnDataRecListener onDataRecListener) {
        if (onDataRecListener != null) {
            mOnDataRecListeners.add(onDataRecListener);
        }
    }

    public void removeOnDataRecListener(OnDataRecListener onDataRecListener) {
        if (onDataRecListener != null) {
            mOnDataRecListeners.remove(onDataRecListener);
        }
    }

    public void send(Request request) {
        if (!Network.isNetworkAvailable()) {
            Response resp = new Response(Response.NETWORK_ERROR);
            onDataReceived(mGson.toJson(resp), resp.getCode(), resp.getDest());
            return;
        }

        if (isConnected()) {
            Log.d(TAG, "execute: " + request.toJson(mGson));
            mConnector.send(request.toJson(mGson));
        } else {
            mWaitingReqList.offer(request);
            connect();
        }
    }

    @Override
    public void onConnected() {
        register();
    }

    public void register() {
        String tokens = LocalUser.getUser().getToken();
        RegisterInfo registerInfo = new RegisterInfo(tokens);
        registerInfo.setDevice(AppInfo.getDeviceHardwareId(App.getAppContext()));
        registerInfo.setChannel(AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
        Request<RegisterInfo> request = Utils.getRequest(Request.REGISTER, registerInfo);

        Log.d(TAG, "register: " + registerInfo);

        send(request);
    }

    public void unRegister(){
        String tokens = LocalUser.getUser().getToken();
        RegisterInfo registerInfo = new RegisterInfo(tokens);
        registerInfo.setDevice(AppInfo.getDeviceHardwareId(App.getAppContext()));
        registerInfo.setChannel(AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
        Request<RegisterInfo> request = Utils.getRequest(Request.UNREGISTER, registerInfo);

        Log.d(TAG, "unRegister: " + registerInfo);

        send(request);
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onMessage(final String receivedMsg) {
        Log.d(TAG, "onMessage: " + receivedMsg);

        final Resp resp = mGson.fromJson(receivedMsg, Resp.class);
        if (resp.code == Response.HEART) {
            mConnector.send(new Request(Request.HEART).toJson(mGson));
            return;
        }

        if (resp.code == Response.REGISTER_SUCCESS) {
            Log.d(TAG, "register success: ");
            executePendingList();
            return;
        }

        if (resp.code == Response.REGISTER_FAILURE) {
            register(); // re-register
            return;
        }

        if (resp.code == Response.SUBSCRIBE_BUSINESS_SUCCESS) {
            Log.d(TAG, "onMessage: SUBSCRIBE_BUSINESS_SUCCESS");
            return;
        }

        if (resp.code == Response.UNSUBSCRIBE_BUSINESS_SUCCESS) {
            Log.d(TAG, "onMessage: UNSUBSCRIBE_BUSINESS_SUCCESS");
            return;
        }

        if (resp.code == Response.PUSH) {
            onDataReceived(receivedMsg, resp.code, resp.dest);
            return;
        }
    }

    public static class Resp {
        int code;
        long timestamp;
        String dest;
        // 后期做请求管理和推送过滤会用到
    }

    private void onDataReceived(final String msg, final int code, final String dest) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDataRecListener onDataRecListener : mOnDataRecListeners) {
                    onDataRecListener.onDataReceive(msg, code, dest);
                }
            }
        });
    }

    private void executePendingList() {
        while (!mWaitingReqList.isEmpty()) {
            Request request = mWaitingReqList.poll();
            send(request);
        }
    }

    private static class WsConfig {
        public List<RemoteHost> http;
        public List<RemoteHost> https;

        @Override
        public String toString() {
            return "WsConfig{" +
                    "http=" + http +
                    ", https=" + https +
                    '}';
        }
    }

    private static class RemoteHost {
        public String host;
        public String port;

        @Override
        public String toString() {
            return "RemoteHost{" +
                    "host='" + host + '\'' +
                    ", port='" + port + '\'' +
                    '}';
        }
    }
}
