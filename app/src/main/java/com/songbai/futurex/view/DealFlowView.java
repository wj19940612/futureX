package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.Display;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class DealFlowView extends LinearLayout {

    private TextView mTime;
    private TextView mDirection;
    private TextView mPrice;
    private TextView mVolume;

    private float mMargin12;
    private int mHeight;

    public DealFlowView(Context context) {
        super(context);
        init();
    }

    public DealFlowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mMargin12 = Display.dp2Px(12, getResources());
        mHeight = (int) Display.dp2Px(32, getResources());

        mTime = getTextView();
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 0.6f;
        params.setMargins((int) mMargin12, 0, 0, 0);
        addView(mTime, params);

        mDirection = getTextView();
        addView(mDirection);

        mPrice = getTextView();
        mPrice.setGravity(Gravity.RIGHT);
        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        addView(mPrice, params);

        mVolume = getTextView();
        mVolume.setGravity(Gravity.RIGHT);
        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, (int) mMargin12, 0);
        params.weight = 1;
        addView(mVolume, params);

        if (isInEditMode()) {
            mTime.setText("20:08:32");
            mVolume.setText("0.0814");
            mPrice.setText("9216.30");
            mDirection.setText(R.string.buy_in);
            mDirection.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, mHeight);
    }

    private TextView getTextView() {
        TextView textView = new TextView(getContext());

        textView.setTextSize(12);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text66));

        return textView;
    }
}
