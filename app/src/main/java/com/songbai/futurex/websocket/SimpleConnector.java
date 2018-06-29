package com.songbai.futurex.websocket;

import android.util.Log;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified by john on 15/03/2018
 * <p>
 * Description: websocket 连接器
 * <p>
 * APIs:
 */
public final class SimpleConnector implements Connector {

    private static final String TAG = "WebSocket";

    public interface OnConnectListener {
        void onConnected();

        void onDisconnected();

        void onMessage(String receivedMsg);
    }

    private WebSocket mWebSocket;
    private boolean mConnecting;
    private List<OnConnectListener> mOnConnectListenerList;
    private OnConnectListener mOnConnectListener;
    private String mUri;

    private static SimpleConnector sSimpleConnector;

    public static SimpleConnector get() {
        if (sSimpleConnector == null) {
            sSimpleConnector = new SimpleConnector();
        }
        return sSimpleConnector;
    }

    private SimpleConnector() {
        mConnecting = false;
        mOnConnectListenerList = new ArrayList<>();
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    @Override
    public void send(String sendMsg) {
        if (isConnected()) {
            mWebSocket.send(sendMsg);
        }
    }

    public void setOnConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListener = onConnectListener;
    }

    public void addConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListenerList.add(onConnectListener);
    }

    public void removeConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListenerList.remove(onConnectListener);
    }

    @Override
    public void connect() {
        if (isConnected() || isConnecting()) return;

        Log.d(TAG, "connect: " + mUri);
        AsyncHttpClient.getDefaultInstance().websocket(mUri, null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                mConnecting = false;

                mWebSocket = webSocket;

                if (isConnected()) {

                    onConnected();

                    initWebSocket();

                    Log.d(TAG, "onCompleted: connected!");
                } else {
                    onError(ex.getMessage());
                }
            }
        });
    }

    private String convert(ByteBuffer byteBuffer) {
        try {
            byte[] bytes = new byte[byteBuffer.capacity()];
            byteBuffer.get(bytes, 0, bytes.length);
            String uncompressStr = Utils.uncompress(bytes, "UTF-8");
            return URLDecoder.decode(uncompressStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initWebSocket() {
        mWebSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                ByteBuffer byteBuffer = bb.getAll();
                String message = convert(byteBuffer);
                onMessage(message);
                bb.recycle();
            }
        });
        mWebSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    onError(ex.getMessage());
                }
                Log.d(TAG, "onCompleted: ClosedCallback");
                onDisconnected();
            }
        });
        mWebSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    onError(ex.getMessage());
                }
                Log.d(TAG, "onCompleted: EndCallback");
                onDisconnected();
            }
        });
        mWebSocket.setPingCallback(new WebSocket.PingCallback() {
            @Override
            public void onPingReceived(String s) {
                mWebSocket.pong(s);
            }
        });
    }

    @Override
    public boolean isConnecting() {
        return mConnecting;
    }

    @Override
    public boolean isConnected() {
        return mWebSocket != null && mWebSocket.isOpen();
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            mWebSocket.close();
        }
    }

    @Override
    public boolean isDisconnecting() {
        return false;
    }

    @Override
    public void onConnected() {
        for (OnConnectListener listener : mOnConnectListenerList) {
            listener.onConnected();
        }
        if (mOnConnectListener != null) {
            mOnConnectListener.onConnected();
        }
    }

    @Override
    public void onError(String error) {
        Log.d(TAG, "onError: " + error);
    }

    @Override
    public void onMessage(String receivedMsg) {
        for (OnConnectListener listener : mOnConnectListenerList) {
            listener.onMessage(receivedMsg);
        }
        if (mOnConnectListener != null) {
            mOnConnectListener.onMessage(receivedMsg);
        }
    }

    @Override
    public void onDisconnected() {
        for (OnConnectListener listener : mOnConnectListenerList) {
            listener.onDisconnected();
        }
        if (mOnConnectListener != null) {
            mOnConnectListener.onDisconnected();
        }
    }
}
