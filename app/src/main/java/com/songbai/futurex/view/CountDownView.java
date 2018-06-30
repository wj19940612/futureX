package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class CountDownView extends FrameLayout implements Runnable {
    private boolean run = false;
    private long mDiff;
    private Long mCloseTime = System.currentTimeMillis();

    private int mCountDownState = STOPPED;

    private OnStateChangeListener mOnStateChangeListener;
    private TextView mTenMin;
    private TextView mMin;
    private TextView mTenSecond;
    private TextView mSecond;

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    public interface OnStateChangeListener {
        void onStateChange(int countDownState);
    }

    @CountDownState
    public static final int COUNTING = 0;
    public static final int CLOSED = 1;
    public static final int STOPPED = 2;

    @IntDef({COUNTING, CLOSED, STOPPED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CountDownState {
    }

    public CountDownView(@NonNull Context context) {
        this(context, null);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_count_down, this, false);
        mTenMin = view.findViewById(R.id.tenMin);
        mMin = view.findViewById(R.id.min);
        mTenSecond = view.findViewById(R.id.tenSecond);
        mSecond = view.findViewById(R.id.second);
        addView(view);
    }

    public void removeOnStateChangeListener() {
        mOnStateChangeListener = null;
    }

    public void setTimes(String time) {
        mCloseTime = Long.valueOf(time);
    }

    public void setTimes(long t) {
        mCloseTime = t;
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
        mCountDownState = STOPPED;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        beginRun();
        mCountDownState = COUNTING;
    }

    @Override
    public void run() {
        removeCallbacks(this);
        if (run) {
            computeTime();
            if (mDiff <= 0) {
                mCountDownState = CLOSED;
            } else {
                long tenMin = mDiff / 1000 / (60 * 10);
                long min = mDiff / 1000 / 60 % 10;
                long tenSecond = mDiff / 1000 % 60 / 10;
                long second = mDiff / 1000 % 60 % 10;
                mTenMin.setText(String.valueOf(tenMin));
                mMin.setText(String.valueOf(min));
                mTenSecond.setText(String.valueOf(tenSecond));
                mSecond.setText(String.valueOf(second));
            }
            postDelayed(this, 1000);
            if (mOnStateChangeListener != null) {
                mOnStateChangeListener.onStateChange(mCountDownState);
            }
        } else {
            removeCallbacks(this);
        }
    }
}
