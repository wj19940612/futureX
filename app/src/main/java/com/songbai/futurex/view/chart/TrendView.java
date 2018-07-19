package com.songbai.futurex.view.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbai.futurex.R;

import java.util.List;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class TrendView extends RelativeLayout {

    private View mMAsView;
    private TrendV mTrendV;

    private TextView mMa5;
    private TextView mMa60;
    private TextView mMa30;

    public TrendView(Context context) {
        super(context);
        init();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTrendV = new TrendV(getContext());
        mTrendV.setOnMADataChangedListener(new Kline.OnMADataChangedListener() {
            @Override
            public void onMADataChanged(Kline.Data data) {
                if (data != null) {
                    mMa60.setText(formatMaValue(60, data.getMas(60)));
                }
            }
        });

        mMAsView = LayoutInflater.from(getContext()).inflate(R.layout.view_moving_averages, null);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int rightMargin = (int) mTrendV.dp2Px(12);
        params.setMargins(0, 0, rightMargin, 0);
        params.addRule(ALIGN_PARENT_RIGHT);

        mMa5 = mMAsView.findViewById(R.id.ma5);
        mMa5.setVisibility(GONE);
        mMa60 = mMAsView.findViewById(R.id.ma10);
        mMa30 = mMAsView.findViewById(R.id.ma30);
        mMa30.setVisibility(GONE);

        addView(mTrendV);
        addView(mMAsView, params);
    }

    private String formatMaValue(int maKey, Float maValue) {
        if (maValue == null) return "MA" + maKey + ": --";
        return "MA" + maKey + ": " + mTrendV.formatNumber(maValue);
    }

    public void setDateFormatStr(String dateFormatStr) {
        mTrendV.setDateFormatStr(dateFormatStr);
    }

    public void setDataList(List<Kline.Data> dataList) {
        mTrendV.setDataList(dataList);
    }

    public ChartCfg getChartCfg() {
        return mTrendV.getChartCfg();
    }

    public ChartColor getChartColor() {
        return mTrendV.getChartColor();
    }

    public void setOnCrossLineAppearListener(Kline.OnCrossLineAppearListener listener) {
        mTrendV.setOnCrossLineAppearListener(listener);
    }

    public void setOnSidesReachedListener(Kline.OnSidesReachedListener onSidesReachedListener) {
        mTrendV.setOnSidesReachedListener(onSidesReachedListener);
    }

    public void reset() {
        mTrendV.reset();
    }

    public void setLastPrice(double lastPrice) {
        mTrendV.setLastPrice((float) lastPrice);
    }

    public void addHistoryData(List<Kline.Data> data) {
        mTrendV.addHistoryData(data);
    }

    public void flush() {
        mTrendV.flush();
    }
}
