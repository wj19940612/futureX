package com.songbai.futurex.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @author YangGuangda
 * @date 2017/8/7
 */

public class TimerTextView extends AppCompatTextView implements Runnable {
    /**
     * 是否启动了
     */
    private boolean run = false;
    private long mDiff;
    private Long mCloseTime = System.currentTimeMillis();
    private CountDownState mCountDownState = CountDownState.stopped;
    private OnStateChangeListener mOnStateChangeListener;
    private SetTextCallback mSetTextCallback;

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    public void setSetTextCallback(SetTextCallback setTextCallback) {
        mSetTextCallback = setTextCallback;
    }

    public enum CountDownState {
        counting, closed, stopped
    }

    public TimerTextView(Context context) {
        this(context, null);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTimes(String time) {
        mCloseTime = Long.valueOf(time);
    }

    public void setTimes(long t) {
        mCloseTime = t;
    }

    public interface OnStateChangeListener {
        void onStateChange(CountDownState countDownState);
    }

    public interface SetTextCallback {
        String getText(long diff);
    }

    public void removeOnStateChangeListener() {
        mOnStateChangeListener = null;
    }

    public void removeSetTextCallback() {
        mSetTextCallback = null;
    }

    /**
     * 倒计时计算
     */
    private void computeTime() {
        mDiff = mCloseTime - System.currentTimeMillis();
    }

    /**
     * 判断是否正在倒计时
     */
    public boolean isRun() {
        return run;
    }

    /**
     * 准备倒计时
     */
    public void beginRun() {
        this.run = true;
        synchronized (this) {
            run();
        }
    }

    /**
     * 停止倒计时
     */
    public void stopRun() {
        this.run = false;
        removeCallbacks(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRun();
        mCountDownState = CountDownState.stopped;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        beginRun();
        mCountDownState = CountDownState.counting;
    }

    @Override
    public void run() {
        //标示已经启动
        removeCallbacks(this);
        if (run) {
            computeTime();
            if (mDiff < 1000) {
                mDiff = 0;
                removeCallbacks(this);
                run = false;
                mCountDownState = CountDownState.closed;
            } else {
                if (mSetTextCallback != null) {
                    this.setText(mSetTextCallback.getText(mDiff));
                }
            }
            postDelayed(this, 1000);
            if (mOnStateChangeListener != null) {
                mOnStateChangeListener.onStateChange(mCountDownState);
            }
        } else {
            removeCallbacks(this);
            removeSetTextCallback();
            removeOnStateChangeListener();
        }
    }
}
