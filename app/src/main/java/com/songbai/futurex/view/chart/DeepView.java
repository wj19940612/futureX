package com.songbai.futurex.view.chart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.Display;
import com.songbai.futurex.utils.CurrencyUtils;
import com.songbai.futurex.websocket.model.DeepData;

import java.util.List;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description: 深度图
 * <p>
 * APIs:
 */
public class DeepView extends LinearLayout {

    private LinearLayout mPriceLine;
    private TextView mMinPrice;
    private TextView mLastPrice;
    private TextView mMaxPrice;

    private int mPriceScale;

    private DeepV mDeepV;
    private int mHeight;

    public DeepView(Context context) {
        super(context);
        init();
    }

    public DeepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mHeight = (int) Display.dp2Px(140, getResources());

        mDeepV = new DeepV(getContext());
        addView(mDeepV, LayoutParams.MATCH_PARENT, mHeight);

        mPriceLine = new LinearLayout(getContext());
        mPriceLine.setOrientation(HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int marginTop = (int) Display.dp2Px(6, getResources());
        int marginBottom = (int) Display.dp2Px(20, getResources());
        params.setMargins(0, marginTop, 0, marginBottom);
        addView(mPriceLine, params);

        int margin12 = (int) Display.dp2Px(12, getResources());
        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.setMargins(margin12, 0, 0, 0);
        params.weight = 1;
        mMinPrice = getTextView();
        mPriceLine.addView(mMinPrice, params);

        mLastPrice = getTextView();
        mPriceLine.addView(mLastPrice);

        mMaxPrice = getTextView();
        mMaxPrice.setGravity(Gravity.RIGHT);
        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, margin12, 0);
        params.weight = 1;
        mPriceLine.addView(mMaxPrice, params);

        if (isInEditMode()) {
            mMinPrice.setText("8348.76");
            mLastPrice.setText("8348.76");
            mMaxPrice.setText("8348.76");
        }
    }

    private TextView getTextView() {
        TextView textView = new TextView(getContext());

        textView.setTextSize(10);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text99));

        return textView;
    }

    public void setDeepList(List<DeepData> buyDeepList, List<DeepData> sellDeepList) {
        mDeepV.setDeepList(buyDeepList, sellDeepList);
        mMinPrice.setText(formatPrice(buyDeepList.get(buyDeepList.size() - 1).getPrice()));
        mMaxPrice.setText(formatPrice(sellDeepList.get(sellDeepList.size() - 1).getPrice()));
    }

    public void setLastPrice(double lastPrice) {
        mLastPrice.setText(formatPrice(lastPrice));
    }

    private String formatPrice(double price) {
        return CurrencyUtils.getPrice(price, mPriceScale);
    }

    public void setPriceScale(int pricePoint) {
        mPriceScale = pricePoint;
    }

    public void reset() {
        mMinPrice.setText("--");
        mMaxPrice.setText("--");
        mLastPrice.setText("--");
        mDeepV.setDeepList(null, null);
    }
}
