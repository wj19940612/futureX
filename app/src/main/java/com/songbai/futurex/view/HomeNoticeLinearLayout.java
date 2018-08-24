package com.songbai.futurex.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author yangguangda
 * @date 2018/8/21
 */
public class HomeNoticeLinearLayout extends LinearLayout {
    private OnNoticeTouchListener mOnNoticeTouchListener;

    public interface OnNoticeTouchListener {
        void onTouch(boolean touch);
    }

    public void setOnNoticeTouchListener(OnNoticeTouchListener onNoticeTouchListener) {
        mOnNoticeTouchListener = onNoticeTouchListener;
    }

    public HomeNoticeLinearLayout(Context context) {
        super(context);
    }

    public HomeNoticeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeNoticeLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeNoticeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnNoticeTouchListener != null) {
                    mOnNoticeTouchListener.onTouch(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (mOnNoticeTouchListener != null) {
                    mOnNoticeTouchListener.onTouch(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
