package com.songbai.wrapres;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.wrapres.utils.DateUtil;
import com.songbai.wrapres.utils.Display;
import com.songbai.wrapres.utils.MarketDataUtils;

/**
 * Modified by john on 12/02/2018
 * <p>
 * Description: 用于展示 k 线十字聚焦时候的数据详情
 * <p>
 */
public class KlineDataPlane extends LinearLayout {

    private TextView mDate;
    private TextView mOpenPrice;
    private TextView mHighestPrice;
    private TextView mLowestPrice;
    private TextView mClosePrice;

    public KlineDataPlane(Context context) {
        super(context);
        init();
    }

    public KlineDataPlane(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mDate = createTextView(12, R.color.unluckyText);
        addView(mDate);

        LinearLayout pricesArea = new LinearLayout(getContext());
        pricesArea.setOrientation(HORIZONTAL);
        mOpenPrice = createTextView(12, R.color.assistText);
        mHighestPrice = createTextView(12, R.color.assistText);
        mLowestPrice = createTextView(12, R.color.assistText);
        mClosePrice = createTextView(12, R.color.assistText);

        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        pricesArea.addView(mOpenPrice, params);

        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        pricesArea.addView(mHighestPrice, params);

        params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        pricesArea.addView(mLowestPrice, params);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pricesArea.addView(mClosePrice, params);

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) Display.dp2Px(2, getResources()), 0, 0);
        addView(pricesArea, params);

//        mOpenPrice.setText("开:$0.0000");
//        mHighestPrice.setText("高:$0.0000");
//        mLowestPrice.setText("低:$0.0000");
//        mClosePrice.setText("收:$0.0000");
    }

    private TextView createTextView(int textSize, int textColorRes) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(textSize);
        textView.setTextColor(ContextCompat.getColor(getContext(), textColorRes));
        return textView;
    }

    public void setPrices(double open, double highest, double lowest, double close) {
        mOpenPrice.setText(getContext().getString(R.string.open_, MarketDataUtils.formatDollarWithSign(open)));
        mHighestPrice.setText(getContext().getString(R.string.highest_, MarketDataUtils.formatDollarWithSign(highest)));
        mLowestPrice.setText(getContext().getString(R.string.lowest_, MarketDataUtils.formatDollarWithSign(lowest)));
        mClosePrice.setText(getContext().getString(R.string.close_, MarketDataUtils.formatDollarWithSign(close)));
    }

    public void setDate(long date) {
        mDate.setText(DateUtil.format(date, "yyyy-MM-dd  HH:mm"));
    }
}
