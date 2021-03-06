package com.songbai.wrapres;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Modified by john on 2018/7/13
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class ObservableScrollView extends ScrollView {

    public interface OnScrollChangedListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public void setScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, x, y, oldX, oldY);
        }
    }
}
